package efrei.bankbackend.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

/**
 * Service class for managing JWT (JSON Web Token) operations such as generation, validation, blacklisting, and claim extraction.
 * This class is responsible for handling token creation, parsing, and verification using a secret key.
 */
@Service
public class JwtService {
    private static final String BLACKLIST_PREFIX = "jwt:blacklist:";

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static final String BEARER_PREFIX = "Bearer ";

    private static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public JwtService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Generates a signed JWT for the given user.
     *
     * @param userDetails authenticated user
     * @return signed JWT token
     */
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts the JWT token from an Authorization header.
     *
     * <p>Expected format:</p>
     * <pre>
     * Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
     * </pre>
     *
     * @param authorizationHeader raw header value
     * @return token if present, empty otherwise
     */
    public Optional<String> extractToken(String authorizationHeader) {

        if (authorizationHeader == null ||
                !authorizationHeader.startsWith(BEARER_PREFIX)) {
            return Optional.empty();
        }

        return Optional.of(authorizationHeader.substring(BEARER_PREFIX_LENGTH));
    }

    /**
     * Logs out a user by revoking the token contained in the header.
     *
     * <p>
     * The token is stored in Redis with a TTL matching its remaining lifetime,
     * ensuring immediate invalidation without memory leaks.
     * </p>
     *
     * @param authorizationHeader raw Authorization header
     */
    public void logout(String authorizationHeader) {
        extractToken(authorizationHeader).ifPresent(this::blacklistToken);
    }

    /**
     * Adds the token to the Redis blacklist.
     *
     * @param token JWT token to revoke
     */
    private void blacklistToken(String token) {
        Date expiration = extractExpiration(token);

        long ttlMillis = expiration.getTime() - System.currentTimeMillis();

        if (ttlMillis <= 0) return;

        redisTemplate.opsForValue().set(
                buildKey(token),
                "revoked",
                Duration.ofMillis(ttlMillis)
        );
    }

    /**
     * Checks whether a token has been revoked.
     *
     * @param token JWT token
     * @return true if blacklisted
     */
    private boolean isBlacklisted(String token) {
        Boolean exists = redisTemplate.hasKey(buildKey(token));
        return Boolean.TRUE.equals(exists);
    }

    /**
     * Validates the token against:
     * <ul>
     *     <li>Blacklist</li>
     *     <li>Username match</li>
     *     <li>Expiration</li>
     * </ul>
     *
     * @param token JWT token
     * @param userDetails expected user
     * @return true if valid
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        if (isBlacklisted(token)) return false;

        String username = extractUsername(token);

        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public long getExpirationTime() {
        return jwtExpiration;
    }

    private String buildKey(String token) {
        return BLACKLIST_PREFIX + token;
    }

    public static String getAuthorizationHeader() {
        return AUTHORIZATION_HEADER;
    }
}
package efrei.bankbackend.configuration.security.filters;

import efrei.bankbackend.exceptions.BaseException;
import efrei.bankbackend.exceptions.TokenInvalidException;
import io.jsonwebtoken.ExpiredJwtException;
import efrei.bankbackend.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Optional;

/**
 * Filter for JWT-based authentication.
 *
 * This filter intercepts incoming requests to extract and validate a JSON Web Token (JWT)
 * from the Authorization header. If the token is valid, it sets up the Spring Security
 * context with the authenticated user.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    /**
     * Constructor for JwtAuthenticationFilter.
     *
     * @param jwtService              Service for handling JWT operations such as parsing and validation.
     * @param userDetailsService      Service for loading user details from the database.
     * @param handlerExceptionResolver Resolver for handling exceptions during filtering.
     */
    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService,
            HandlerExceptionResolver handlerExceptionResolver
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    /**
     * Performs the JWT authentication process.
     *
     * This method extracts the JWT from the Authorization header, validates it,
     * and sets the authenticated user in the security context. If an exception occurs,
     * it is delegated to the exception resolver.
     *
     * @param request     The HTTP request.
     * @param response    The HTTP response.
     * @param filterChain The filter chain to proceed with the request.
     * @throws ServletException If an error occurs during filtering.
     * @throws IOException      If an I/O error occurs.
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String header = request.getHeader(JwtService.getAuthorizationHeader());

            Optional<String> existingToken = jwtService.extractToken(header);

            if (existingToken.isEmpty()) {
                filterChain.doFilter(request, response);
                return;
            }

            String jwt = existingToken.get();

            processJwtAuthentication(jwt, request);

        } catch (Exception exception) {
            handlerExceptionResolver.resolveException(request, response, null, exception);
            return;
        }

        filterChain.doFilter(request, response);
    }


    /**
     * Checks if the current security context has no authentication.
     *
     * This prevents overwriting an existing authenticated user in the context.
     *
     * @return {@code true} if the security context has no authentication, {@code false} otherwise.
     */
    private boolean isAuthenticationAbsent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null || !authentication.isAuthenticated();
    }

    /**
     * Processes the JWT for authentication.
     *
     * This method extracts the username from the token, loads the corresponding user details,
     * validates the token, and sets up the security context if valid.
     *
     * @param jwt     The JWT token.
     * @param request The HTTP request.
     *
     * @throws BaseException if the token is not valid.
     */
    private void processJwtAuthentication(String jwt, HttpServletRequest request) throws BaseException {
        String userEmail;
        try {
            userEmail = jwtService.extractUsername(jwt);

        } catch (ExpiredJwtException expiredJwtException) {
            throw new TokenInvalidException();
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

        if (!jwtService.isTokenValid(jwt, userDetails)) {
            throw new TokenInvalidException();
        }

        if (isAuthenticationAbsent()) {
            setAuthentication(userDetails, request);
        }
    }

    /**
     * Sets up the security context with the authenticated user details.
     *
     * Creates an authentication token and sets it in the Spring Security context.
     *
     * @param userDetails The authenticated user's details.
     * @param request     The HTTP request.
     */
    private void setAuthentication(UserDetails userDetails, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}
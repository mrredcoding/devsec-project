package efrei.bankbackend.configuration.security.filters;

import efrei.bankbackend.entities.RoleType;
import efrei.bankbackend.exceptions.TooManyRequestsException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiterFilter extends OncePerRequestFilter {

    private final HandlerExceptionResolver handlerExceptionResolver;

    private final Map<String, List<Instant>> userRequests;

    private final Map<String, Map<String, Integer>> endpointRoleLimits;

    private static final long WINDOW_SECONDS = 60;

    @Autowired
    public RateLimiterFilter(HandlerExceptionResolver handlerExceptionResolver) {
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.userRequests = new ConcurrentHashMap<>();

        this.endpointRoleLimits = new HashMap<>();

        this.endpointRoleLimits.put("/auth/login", Map.of(
                RoleType.ROLE_ANONYMOUS.name(), 5
        ));

        this.endpointRoleLimits.put("/bank/accounts/{id}/credit", Map.of(
                RoleType.ROLE_ADMIN.name(), 15,
                RoleType.ROLE_CLIENT.name(), 5
        ));

        this.endpointRoleLimits.put("/bank/accounts/{id}/debit", Map.of(
                RoleType.ROLE_ADMIN.name(), 15,
                RoleType.ROLE_CLIENT.name(), 5
        ));
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String normalizedPath = normalizeEndpoint(request.getRequestURI());

        if (!endpointRoleLimits.containsKey(normalizedPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String key;
        String role;

        if (authentication != null && authentication.isAuthenticated()) {
            key = authentication.getName();
            role = authentication.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse(RoleType.ROLE_ANONYMOUS.name());
        } else {
            key = request.getRemoteAddr();
            role = RoleType.ROLE_ANONYMOUS.name();
        }

        int maxRequests = endpointRoleLimits.get(normalizedPath)
                .getOrDefault(role, 10);

        try {
            checkRateLimit(key, maxRequests);
        } catch (Exception exception) {
            handlerExceptionResolver.resolveException(request, response, null, exception);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String normalizeEndpoint(String path) {
        return path.replaceAll("/[0-9a-fA-F\\-]{36}", "/{id}");
    }

    private void checkRateLimit(String key, int maxRequests) throws TooManyRequestsException {
        Instant now = Instant.now();
        List<Instant> requests = userRequests.computeIfAbsent(key, k -> new ArrayList<>());

        requests.removeIf(timestamp -> timestamp.isBefore(now.minusSeconds(WINDOW_SECONDS)));

        if (requests.size() >= maxRequests) {
            Instant earliest = Collections.min(requests);
            long retryAfter = WINDOW_SECONDS - (now.getEpochSecond() - earliest.getEpochSecond());
            throw new TooManyRequestsException(retryAfter);
        }

        requests.add(now);
    }
}
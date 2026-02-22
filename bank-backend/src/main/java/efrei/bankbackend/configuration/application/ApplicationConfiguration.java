package efrei.bankbackend.configuration.application;

import efrei.bankbackend.exceptions.BaseException;
import efrei.bankbackend.exceptions.InvalidPasswordOrEmailException;
import efrei.bankbackend.repositories.UserAccountRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Configuration class for application-level security and authentication setup.
 *
 * This class defines beans for managing user authentication, including user details service,
 * password encoding, and the authentication provider. It integrates with Spring Security to
 * provide authentication mechanisms.
 */
@Configuration
public class ApplicationConfiguration {

    private final UserAccountRepository userAccountRepository;

    /**
     * Constructor for ApplicationConfiguration.
     *
     * @param userAccountRepository The repository for managing client data.
     */
    public ApplicationConfiguration(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    /**
     * Bean definition for UserDetailsService.
     *
     * This service is used by Spring Security to load user-specific data during authentication.
     * It retrieves user details from the database based on the provided email (username).
     *
     * @return A UserDetailsService implementation that fetches user data from the database.
     * @throws BaseException If the user is not found in the database or the password is invalid.
     */
    @Bean
    UserDetailsService userDetailsService() throws BaseException {
        try {
            return email -> userAccountRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        } catch (UsernameNotFoundException e) {
            throw new InvalidPasswordOrEmailException();
        }
    }

    /**
     * Bean definition for BCryptPasswordEncoder.
     *
     * This encoder is used to hash passwords securely using the BCrypt hashing function.
     * It is recommended for storing passwords in a secure manner.
     *
     * @return A BCryptPasswordEncoder instance for password hashing.
     */
    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Bean definition for AuthenticationManager.
     *
     * This manager is responsible for processing authentication requests. It integrates with
     * Spring Security to delegate the authentication process to the appropriate provider.
     *
     * @param config The authentication configuration provided by Spring Security.
     * @return An AuthenticationManager instance for managing authentication.
     * @throws Exception If an error occurs while configuring the authentication manager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Bean definition for AuthenticationProvider.
     *
     * This provider is responsible for handling authentication by using the
     * {@link UserDetailsService} and {@link BCryptPasswordEncoder} for verifying credentials.
     * It is configured to authenticate users based on their email and password.
     *
     * @throws BaseException if authentication fails
     * @return A DaoAuthenticationProvider instance for authentication.
     */
    @Bean
    AuthenticationProvider authenticationProvider() throws BaseException {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService());

        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}
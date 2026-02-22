package efrei.bankbackend.services;

import efrei.bankbackend.contracts.LoginRequest;
import efrei.bankbackend.entities.UserAccount;
import efrei.bankbackend.exceptions.BaseException;
import efrei.bankbackend.exceptions.InvalidPasswordOrEmailException;
import efrei.bankbackend.exceptions.ResourceNotFoundException;
import efrei.bankbackend.repositories.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

/**
 * Service layer for handling client-related operations, such as authentication, registration,
 * and data retrieval. This class acts as a bridge between the controller and repository layers,
 * encapsulating business logic and data processing for clients.
 */
@Service
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final AuthenticationManager authenticationManager;

    /**
     * Constructs a new UserAccountService with the necessary dependencies.
     *
     * @param userAccountRepository    The repository for managing client data.
     * @param authenticationManager The manager for handling authentication processes.
     */
    @Autowired
    public UserAccountService(UserAccountRepository userAccountRepository, AuthenticationManager authenticationManager) {
        this.userAccountRepository = userAccountRepository;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Authenticates a client based on the provided login credentials.
     *
     * @param input The login request containing the client's email and password.
     * @return The authenticated ClientEntity.
     * @throws BaseException If authentication fails or the user is not found.
     */
    public UserAccount authenticate(LoginRequest input) throws BaseException {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            input.email(),
                            input.password()
                    )
            );
        } catch (Exception e) {
            throw new InvalidPasswordOrEmailException();
        }

        return userAccountRepository.findByEmail(input.email())
                .orElseThrow(InvalidPasswordOrEmailException::new);
    }

    /**
     * Retrieves a specific user by their unique email.
     *
     * @param userEmail The unique email of the user to be retrieved.
     * @return The corresponding UserAccount.
     * @throws ResourceNotFoundException If no client with the provided ID is found in the system.
     */
    public UserAccount getClientByEmail(String userEmail) throws ResourceNotFoundException {
        return userAccountRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("No user found for email '" + userEmail + "'."));
    }
}
package efrei.bankbackend.seeders;

import efrei.bankbackend.entities.BankAccount;
import efrei.bankbackend.entities.RoleType;
import efrei.bankbackend.entities.UserAccount;
import efrei.bankbackend.repositories.BankAccountRepository;
import efrei.bankbackend.repositories.UserAccountRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

/**
 * ClientSeeder is responsible for populating the database with predefined client data.
 * It seeds the database with a set of predefined user accounts.
 * It converts it into a collection of {@link UserAccount} objects, which are then stored in the database.
 */
@Component
public class BankSeeder implements CommandLineRunner {

    private final BankAccountRepository bankAccountRepository;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs a new ClientSeeder.
     *
     * @param bankAccountRepository the repository used for managing bank account entities
     * @param userAccountRepository the repository used for managing client entities
     * @param passwordEncoder  the encoder used for securely storing passwords
     */
    @Autowired
    public BankSeeder(BankAccountRepository bankAccountRepository, UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        this.bankAccountRepository = bankAccountRepository;
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Runs the seeder logic to populate the database with initial client data.
     * Deletes any existing client records before creating new predefined ones.
     *
     * @param args command-line arguments passed to the application
     */
    @Override
    public void run(String @NonNull ... args) {
        UserAccount cedric = createClient(
                "Cédric",
                "cedric.alonso@efrei.net",
                RoleType.ROLE_CLIENT,
                "securePasswordCedric123*"
        );

        UserAccount guillaume = createClient(
                "Guillaume",
                "guillaume.gomez@efrei.net",
                RoleType.ROLE_CLIENT,
                "securePasswordGuillaume123*"
        );

        createClient(
                "Admin",
                "admin@efrei.net",
                RoleType.ROLE_ADMIN,
                "securePasswordAdmin123*"
        );

        createBankAccount(guillaume);
        createBankAccount(cedric);
    }

    /**
     * Creates and persists a new user entity with the specified details.
     *
     * @param name      the name of the client
     * @param email     the email address of the client
     * @param role      the role type of the client (e.g., ROLE_CLIENT, ROLE_ADMIN)
     * @param password  the plaintext password of the client, which will be encoded before storage
     */
    private UserAccount createClient(String name, String email, RoleType role, String password) {
        return userAccountRepository.findByEmail(email)
                .orElseGet(() -> {
                    UserAccount userAccount = new UserAccount();
                    userAccount.setName(name);
                    userAccount.setEmail(email);
                    userAccount.setRole(role);
                    userAccount.setPassword(passwordEncoder.encode(password));
                    return userAccountRepository.save(userAccount);
                });
    }

    /**
     * Creates and persists a new bank account entity for the specified client.
     *
     * @param userAccount the user to persist their bank account
     */
    private void createBankAccount(UserAccount userAccount) {
        boolean exists = bankAccountRepository.existsByOwner(userAccount);
        if (!exists) {
            BankAccount bankAccount = new BankAccount();
            bankAccount.setOwner(userAccount);
            bankAccount.setBalance(BigDecimal.ZERO);
            bankAccountRepository.save(bankAccount);
        }
    }
}
package efrei.bankbackend.repositories;

import efrei.bankbackend.entities.BankAccount;
import efrei.bankbackend.entities.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {
    boolean existsByOwner(UserAccount owner);
    Optional<BankAccount> findByOwnerEmail(String ownerEmail);
}
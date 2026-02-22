package efrei.bankbackend.repositories;

import efrei.bankbackend.entities.RoleType;
import efrei.bankbackend.entities.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {
    Optional<UserAccount> findByEmail(String clientEmail);
    List<UserAccount> findAllByRoleIsLike(RoleType role);
}
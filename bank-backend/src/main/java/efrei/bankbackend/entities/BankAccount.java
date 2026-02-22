package efrei.bankbackend.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@Table(name = "bank_accounts")
public class BankAccount {

    @Id
    @Column(name = "account_id")
    private final UUID id = UUID.randomUUID();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_email", referencedColumnName = "user_email")
    private UserAccount owner;

    @Column(name = "balance")
    private BigDecimal balance;

    public void credit(BigDecimal amount) {
        balance = balance.add(amount);
    }

    public void debit(BigDecimal amount) {
        balance = balance.subtract(amount);
    }
}
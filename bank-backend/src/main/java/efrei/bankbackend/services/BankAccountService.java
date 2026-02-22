package efrei.bankbackend.services;

import efrei.bankbackend.entities.BankAccount;
import efrei.bankbackend.entities.RoleType;
import efrei.bankbackend.exceptions.BaseException;
import efrei.bankbackend.exceptions.ForbiddenOperationException;
import efrei.bankbackend.exceptions.ResourceAlreadyExistsException;
import efrei.bankbackend.exceptions.ResourceNotFoundException;
import efrei.bankbackend.repositories.BankAccountRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final UserAccountService userAccountService;

    private final static BigDecimal TRANSACTION_THRESHOLD = new BigDecimal(1000);

    public BankAccountService(BankAccountRepository bankAccountRepository, UserAccountService userAccountService) {
        this.bankAccountRepository = bankAccountRepository;
        this.userAccountService = userAccountService;
    }

    public List<BankAccount> all() {
        return bankAccountRepository.findAll();
    }

    @Transactional
    public BankAccount registerBankAccount(String ownerEmail) throws BaseException {
        var owner = userAccountService.getClientByEmail(ownerEmail);

        boolean existingBankAccount = bankAccountRepository.existsByOwner(owner);
        if (existingBankAccount)
            throw new ResourceAlreadyExistsException("The user '" + ownerEmail + "' already has a bank account.");

        BankAccount newBankAccount = new BankAccount();
        newBankAccount.setOwner(owner);
        newBankAccount.setBalance(BigDecimal.ZERO);

        bankAccountRepository.save(newBankAccount);
        return newBankAccount;
    }

    public BankAccount byOwner(String owner) throws BaseException {
        return bankAccountRepository.findByOwnerEmail(owner)
                .orElseThrow(() -> new ResourceNotFoundException("No account found for owner '" + owner + "'."));
    }

    @Transactional
    public BankAccount credit(UUID accountId, BigDecimal amount, Authentication authentication) throws BaseException {
        BankAccount account = getAccount(accountId);

        checkAmountByRole(amount, authentication);

        account.credit(amount);
        return account;
    }

    @Transactional
    public BankAccount debit(UUID accountId, BigDecimal amount, Authentication authentication) throws BaseException {
        BankAccount account = getAccount(accountId);

        checkAmountByRole(amount, authentication);

        account.debit(amount);
        return account;
    }

    private BankAccount getAccount(UUID accountId) throws ResourceNotFoundException {
        return bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("No account found for id '" + accountId + "'."));
    }

    private void checkAmountByRole(BigDecimal amount, Authentication authentication) throws ForbiddenOperationException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        boolean isAdmin = authorities
                .stream()
                .anyMatch(authority ->
                        Objects.requireNonNull(authority.getAuthority()).equals(RoleType.ROLE_ADMIN.name()));

        boolean isClient = authorities
                .stream()
                .anyMatch(authority ->
                        Objects.requireNonNull(authority.getAuthority()).equals(RoleType.ROLE_CLIENT.name()));

        if (isClient && amount.compareTo(TRANSACTION_THRESHOLD) > 0) {
            throw new ForbiddenOperationException("You cannot operate on amounts greater than " + TRANSACTION_THRESHOLD + " €.");
        }

        if (isAdmin && amount.compareTo(TRANSACTION_THRESHOLD) < 0) {
            throw new ForbiddenOperationException("You cannot operate on amounts less than " + TRANSACTION_THRESHOLD + " €.");
        }
    }
}
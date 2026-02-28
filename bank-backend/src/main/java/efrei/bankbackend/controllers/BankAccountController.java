package efrei.bankbackend.controllers;

import efrei.bankbackend.contracts.BankAccountResponse;
import efrei.bankbackend.entities.BankAccount;
import efrei.bankbackend.exceptions.BaseException;
import efrei.bankbackend.services.BankAccountService;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/bank/accounts")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @PostMapping("/create")
    public ResponseEntity<BankAccountResponse> createAccount(@RequestParam String ownerEmail) throws BaseException {
        log.info("Creating new bank account for owner={}", ownerEmail);

        BankAccount newBankAccount = bankAccountService.registerBankAccount(ownerEmail);

        log.info("Bank account created successfully: accountId={}", newBankAccount.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(newBankAccount));
    }

    @GetMapping("/all")
    public ResponseEntity<List<BankAccountResponse>> all() {
        log.info("Fetching all bank accounts");

        List<BankAccountResponse> allAccounts = bankAccountService.all()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        log.info("Total accounts fetched={}", allAccounts.size());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(allAccounts);
    }

    @GetMapping("/mine")
    @PreAuthorize("""
        hasAuthority(T(efrei.bankbackend.entities.RoleType).ROLE_ADMIN.name()) or
        hasAuthority(T(efrei.bankbackend.entities.RoleType).ROLE_CLIENT.name())
    """)
    public ResponseEntity<BankAccountResponse> mine(@NonNull Authentication authentication) throws BaseException {
        log.info("Fetching bank account for authenticated user={}", authentication.getName());

        BankAccount bankAccount = bankAccountService.byOwner(authentication.getName());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(toResponse(bankAccount));
    }

    @PatchMapping("/{bankAccountId}/credit")
    @PreAuthorize("""
        hasAuthority(T(efrei.bankbackend.entities.RoleType).ROLE_ADMIN.name()) or
        hasAuthority(T(efrei.bankbackend.entities.RoleType).ROLE_CLIENT.name())
    """)
    public ResponseEntity<BankAccountResponse> credit(@PathVariable UUID bankAccountId, @RequestParam BigDecimal amount) throws BaseException {
        log.info("Crediting account '{}': amount={}", bankAccountId, amount);

        BankAccount bankAccount = bankAccountService.credit(bankAccountId, amount);

        log.info("Bank account credited of {} € successfully: accountId={}, newBalance={}", amount, bankAccount.getId(), bankAccount.getBalance());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(toResponse(bankAccount));
    }

    @PatchMapping("/{bankAccountId}/debit")
    @PreAuthorize("""
        hasAuthority(T(efrei.bankbackend.entities.RoleType).ROLE_ADMIN.name()) or
        hasAuthority(T(efrei.bankbackend.entities.RoleType).ROLE_CLIENT.name())
    """)
    public ResponseEntity<BankAccountResponse> debit(@PathVariable UUID bankAccountId, @RequestParam BigDecimal amount) throws BaseException {
        log.info("Debiting account '{}': amount={}", bankAccountId, amount);

        BankAccount bankAccount = bankAccountService.debit(bankAccountId, amount);

        log.info("Bank account debited of {} € successfully: accountId={}, newBalance={}", amount, bankAccount.getId(), bankAccount.getBalance());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(toResponse(bankAccount));
    }

    private BankAccountResponse toResponse(BankAccount account) {
        return new BankAccountResponse(
                account.getId(),
                account.getOwner().getEmail(),
                account.getBalance()
        );
    }
}
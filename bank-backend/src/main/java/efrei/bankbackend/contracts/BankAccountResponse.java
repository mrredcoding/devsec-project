package efrei.bankbackend.contracts;

import java.math.BigDecimal;
import java.util.UUID;

public record BankAccountResponse(UUID id, String ownerEmail, BigDecimal balance) { }

package efrei.bankbackend.contracts;

public record LoginResponse(String token, long expiresIn) { }
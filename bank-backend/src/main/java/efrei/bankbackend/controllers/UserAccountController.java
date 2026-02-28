package efrei.bankbackend.controllers;

import efrei.bankbackend.contracts.LoginRequest;
import efrei.bankbackend.contracts.LoginResponse;
import efrei.bankbackend.contracts.UserResponse;
import efrei.bankbackend.entities.UserAccount;
import efrei.bankbackend.exceptions.BaseException;
import efrei.bankbackend.services.JwtService;
import efrei.bankbackend.services.UserAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class UserAccountController {
    private final JwtService jwtService;
    private final UserAccountService userAccountService;

    @Autowired
    public UserAccountController(JwtService jwtService, UserAccountService userAccountService) {
        this.jwtService = jwtService;
        this.userAccountService = userAccountService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) throws BaseException {
        log.info("Login attempt for email={}", loginRequest.email());

        UserAccount authenticatedUser = userAccountService.authenticate(loginRequest);
        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());

        log.info("Login successful");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(loginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(value = "Authorization", required = false) String header,
            Authentication authentication) {
        log.info("Logout attempt for email={}.", authentication.getName());

        jwtService.logout(header);

        log.info("Logout successful.");

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserAccount client = (UserAccount) authentication.getPrincipal();

        log.info("Fetching user information for authenticated user={}", authentication.getName());

        UserResponse response = new UserResponse(
                client.getId(),
                client.getName(),
                client.getEmail(),
                client.getRole().name()
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
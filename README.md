# DevSec BANK & Co

## Introduction

DevSec BANK & Co is an academic and learning application designed to
demonstrate secure application development using Spring Security. It
simulates a simple banking system with separate frontend and backend
components, built to illustrate best practices in security architecture.
The purpose of this project is to provide a hands-on example of applying
multiple layers of security---from TLS encryption and CORS policies to
JWT authentication and role-based access control---within a modern web
application. Based on a fictitious banking scenario, this project
highlights key security concepts.

## Architecture Overview

DevSec BANK & Co is composed of two independent parts: a backend REST
API and a frontend user interface. The backend is implemented in Spring
Boot (Java) and handles all the core business logic, data access, and
security concerns. The frontend is a ViteJS application written in
React, which provides the user interface. These two components
communicate over HTTPS through well-defined API endpoints. In practice,
the React UI makes HTTP requests to the Spring Boot API to perform
operations like logging in, creating accounts, and viewing balances.

Within the backend, controller classes map HTTP routes (for example,
`/auth/login` for authentication and `/bank/accounts/*` for account
operations) to service methods. The service layer encapsulates the core
business rules (such as transaction limits) and communicates with
repository classes that abstract the database access (for example,
fetching or updating account records). Spring Security configurations
and custom filters wrap these layers to enforce access control, handle
JWT tokens, and manage error handling uniformly via a global exception
handler.

## Architecture Diagram

``` mermaid
flowchart LR

%% ===============================
%% CLIENT
%% ===============================
subgraph CLIENT["Client Layer"]
    Browser["Web Browser"]
    Frontend["React + Vite Frontend"]
end

Browser --> Frontend

%% ===============================
%% GATEWAY / EDGE
%% ===============================
subgraph EDGE["Gateway Layer"]
    Gateway["API Gateway / Reverse Proxy"]
    TLS["TLS Termination (HTTPS)"]
    CORS["CORS Policy"]
end

Frontend -->|HTTPS REST| Gateway
Gateway --> TLS
TLS --> CORS

%% ===============================
%% SPRING SECURITY FILTER CHAIN
%% ===============================
subgraph SECURITY["Spring Security Filters"]
    RateLimiter["RateLimiter Filter"]
    JwtFilter["JWT Authentication Filter"]
    SecurityContext["Security Context"]
    AuthEntry["Authentication Entry Point"]
end

CORS --> RateLimiter
RateLimiter --> JwtFilter
JwtFilter --> SecurityContext
JwtFilter --> AuthEntry

%% ===============================
%% APPLICATION LAYER
%% ===============================
subgraph APP["Spring Boot Application"]

    subgraph CONTROLLERS["Controllers"]
        AuthController["Auth Controller"]
        BankController["Bank Controller"]
        UserController["User Controller"]
    end

    subgraph SERVICES["Services"]
        AuthService["JWT Service"]
        BankService["Bank Account Service"]
        UserService["User Service"]
    end

    MethodSecurity["Method Security @PreAuthorize"]

end

SecurityContext --> AuthController
SecurityContext --> BankController
SecurityContext --> UserController

AuthController --> AuthService
BankController --> BankService
UserController --> UserService

BankController --> MethodSecurity
UserController --> MethodSecurity

%% ===============================
%% DATA LAYER
%% ===============================
subgraph DATA["Repositories"]
    BankRepo["Bank Repository"]
    UserRepo["User Repository"]
end

BankService --> BankRepo
UserService --> UserRepo

%% ===============================
%% DATABASE
%% ===============================
Database[(Database)]

BankRepo --> Database
UserRepo --> Database

%% ===============================
%% ERROR HANDLING
%% ===============================
ExceptionHandler["Global Exception Handler"]

AuthController --> ExceptionHandler
BankController --> ExceptionHandler
UserController --> ExceptionHandler
JwtFilter --> ExceptionHandler
RateLimiter --> ExceptionHandler
```

## Security Layers

DevSec BANK & Co incorporates multiple security mechanisms at different
layers of the application to achieve a defense-in-depth posture. First,
all communication uses HTTPS with a self-signed certificate (configured
in `application.properties` with a PKCS12 keystore). This ensures that
data in transit is encrypted, although clients must trust the
certificate chain (browsers will flag it as untrusted by default).

Second, a strict CORS policy is enforced on the API: only requests from
the frontend origin (`http://localhost:5173`) are allowed, and only
specific HTTP methods (GET, POST, PATCH) and headers (`Authorization`,
`Content-Type`) are permitted.

Authentication is handled via JWT (JSON Web Tokens). When a user logs
in, the API issues a signed JWT token that the client includes in the
`Authorization` header of subsequent requests. The server uses this
token to authenticate the user and extract their roles. Sessions are
stateless (`SessionCreationPolicy.STATELESS`). Passwords for user
accounts are hashed using BCrypt.

Role-based access control is applied via security configuration and
annotations such as `@PreAuthorize`. The backend also includes a global
exception handler (`@ControllerAdvice`) to translate exceptions into
consistent HTTP error responses for the frontend.

## Business Logic and Role-Based Enforcement

In the service layer, DevSec BANK & Co enforces role-specific business
rules on transactions. A fixed threshold (1,000 €) distinguishes what
actions clients and admins can perform. Specifically: - `ROLE_CLIENT`
can only operate on their own account and cannot execute transactions
greater than 1,000 €. - `ROLE_ADMIN` cannot execute transactions below
that threshold (admins handle larger transactions).

Disallowed operations throw custom exceptions with meaningful messages,
which the global exception handler delivers to the frontend.

## Testing Notes and HTTPS Usage

The project uses version **26.2.1** for both components: - UI
service: `ui:26.2.1` - Backend API service: `api:26.2.1` for http requests.

If you want to test https, you would need to use `api:26.2.2`. Indeed with a self-signed certificate, a
regular browser may reject the connection (e.g.,`NET::ERR_CERT_AUTHORITY_INVALID`). 
To test the API, use Postman or a similar HTTP client where SSL verification can be disabled or the
certificate can be trusted.

## Conclusion

DevSec BANK & Co is built with a layered security architecture following
best practices. By combining transport security (HTTPS/TLS), strong
authentication (JWT with BCrypt password hashing), strict CORS policies,
stateless session management, and fine‑grained authorization checks, the
system implements a comprehensive defense-in-depth approach.

# Architecture

## Module Structure & Dependencies

```
Incoming Request → gateway (8080) → erp-hr (8081)
                                       → vacation (8082) → [OpenFeign] → erp-hr
                                       → db-manager (8083)
                                       → kerberos (8084)

All service modules → security (shared library)
```

- **`security`**: Pure library — no Spring Boot plugin applied. Provides shared Spring Security and OAuth2 Resource Server configuration. Dependencies declared with `api()` are transitively exposed to all modules.
- **`gateway`**: Spring Cloud Gateway (WebFlux-based). Validates Keycloak JWT tokens and routes to downstream services. Must remain WebFlux-only — do not mix with Servlet stack.
- **`erp-hr`**: Master data for employees, organization structure, and approval lines. Core module referenced by other services.
- **`vacation`**: Handles vacation requests and drafts. Calls `erp-hr` via OpenFeign to resolve organization and approval line data.
- **`db-manager`**: Database configuration management.
- **`kerberos`**: Company server information lookup.

## DDD Package Structure

Each service module follows Domain-Driven Design. The base package is `com.seohalabs.moduerp.{module}`.

```
com.seohalabs.moduerp.{module}/
├── domain/
│   ├── model/           # Aggregates, Entities, Value Objects
│   ├── repository/      # Repository interfaces (domain layer, no JPA)
│   └── service/         # Domain Services
├── application/
│   ├── command/         # Command handlers (write side)
│   └── query/           # Query handlers (read side)
├── infrastructure/
│   ├── persistence/     # JPA entities, QueryDSL repository implementations
│   └── client/          # Feign clients, external integrations
└── presentation/
    ├── rest/            # REST controllers
    └── dto/             # Request / Response DTOs
```

**Dependency direction**: `presentation` → `application` → `domain` ← `infrastructure`

- The `domain` layer must have zero dependency on Spring, JPA, or any framework.
- `infrastructure` implements interfaces defined in `domain.repository`.
- `application` orchestrates domain logic and coordinates infrastructure via domain interfaces.

## Dependency Version Management

- Dependencies managed by Spring BOM: declare artifact only, no version.
- Dependencies **not** managed by BOM: add a version variable to `gradle.properties` first, then reference it.
  ```properties
  # gradle.properties
  mapstructVersion=1.6.3
  ```
  ```groovy
  // module build.gradle
  implementation "org.mapstruct:mapstruct:${mapstructVersion}"
  ```

## Authentication & Authorization

- All services validate Keycloak JWTs as OAuth2 Resource Servers.
- Gateway performs primary token validation; each service enforces Role-based authorization.
- Keycloak Realm: `modu-erp` — configured via `KEYCLOAK_ISSUER_URI`.

## Inter-Service Communication

- Gradle project dependencies (`implementation project(':...')`) are only allowed pointing to `security`. Service-to-service calls must go over HTTP.
- `vacation` → `erp-hr` via OpenFeign (`@EnableFeignClients` is already applied on `VacationApplication`).

## Environment Variables

| Variable | Default | Description |
|---|---|---|
| `KEYCLOAK_ISSUER_URI` | `http://localhost:8180/realms/modu-erp` | Keycloak issuer URI |
| `DB_HOST` | `localhost` | PostgreSQL host |
| `DB_PORT` | `5432` | PostgreSQL port |
| `DB_USERNAME` | `postgres` | DB username |
| `DB_PASSWORD` | `postgres` | DB password |
| `HR_SERVICE_URL` | `http://localhost:8081` | erp-hr service URL |
| `VACATION_SERVICE_URL` | `http://localhost:8082` | vacation service URL |
| `DB_MANAGER_SERVICE_URL` | `http://localhost:8083` | db-manager service URL |
| `KERBEROS_SERVICE_URL` | `http://localhost:8084` | kerberos service URL |

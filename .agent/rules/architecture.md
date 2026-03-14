# Architecture

## Directory Layout

```
modu-erp/
├── apps/
│   ├── gateway/       (port 8080)
│   ├── organization/  (port 8081)
│   ├── vacation/      (port 8082)
│   ├── db-manager/    (port 8083)
│   └── kerberos/      (port 8084)
└── shared/
    └── security/      (library)
```

Gradle module names (settings.gradle):
- `:security` → `shared/security`
- `:gateway` → `apps/gateway`
- `:organization` → `apps/organization`
- `:vacation` → `apps/vacation`
- `:db-manager` → `apps/db-manager`
- `:kerberos` → `apps/kerberos`

## Module Responsibilities

```
Incoming Request → gateway (8080) → organization (8081)
                                   → vacation (8082) → [OpenFeign] → organization
                                   → db-manager (8083)
                                   → kerberos (8084)

All service modules → security (shared library)
```

- **`security`**: Pure library — no Spring Boot plugin applied. Provides shared Spring Security and OAuth2 Resource Server configuration. Dependencies declared with `api()` are transitively exposed to all modules.
- **`gateway`**: Spring Cloud Gateway (WebFlux-based). Validates Keycloak JWT tokens and routes to downstream services. Must remain WebFlux-only — do not mix with Servlet stack.
- **`organization`**: Master data for employees, departments, positions, roles, and approval lines. Core module referenced by other services.
- **`vacation`**: Handles vacation requests and drafts. Calls `organization` via OpenFeign to resolve organization and approval line data.
- **`db-manager`**: Database configuration management.
- **`kerberos`**: Company server information lookup.

## DDD Package Structure

Each service module is organized **domain-first**. The base package is `com.seohalabs.moduerp.{module}`.

```
com.seohalabs.moduerp.{module}/
├── {domain}/                        # e.g. employee, department, role, position
│   ├── domain/
│   │   ├── {Domain}Entity.java      # JPA entity + domain model in one class
│   │   ├── {Domain}Factory.java     # Factory for creating domain objects
│   │   ├── {Domain}Policy.java      # Domain rules / invariants (if needed)
│   │   └── {Domain}StatusType.java  # Enums with Type suffix
│   ├── application/
│   │   ├── {Domain}UseCase.java     # Facade — composes Services, entry point for Controller/gRPC
│   │   ├── {Action}Service.java     # Single-operation service (@Service), holds infra repos
│   │   ├── {Action}Command.java     # Write-side input DTO
│   │   ├── {Find}Query.java         # Read-side input DTO
│   │   ├── {Domain}Result.java      # Output DTO
│   │   └── {Domain}DomainMapper.java
│   ├── infrastructure/
│   │   └── persistence/
│   │       ├── {Domain}Repository.java       # extends JpaRepository
│   │       └── {Domain}QueryRepository.java  # QueryDSL (if needed)
│   └── presentation/
│       ├── {Domain}Controller.java
│       ├── {Domain}Mapper.java
│       ├── {Action}Request.java     # one file per operation
│       └── {Domain}Response.java    # one file per operation
└── shared/                          # Cross-domain infrastructure (keycloak, openfga, security, bootstrap)
    ├── infrastructure/
    │   ├── bootstrap/
    │   ├── keycloak/
    │   ├── openfga/
    │   └── security/
    └── mapping/
```

**Layer roles:**

- **domain** — JPA entity + domain model in one class (`@Entity` + domain logic together). State transition methods, value objects, enums (`Type` suffix), domain policy. Only `@Entity`/`@Table`/`@Column` JPA annotations allowed here.
- **domain** — JPA entity + domain model in one class (`@Entity` + domain logic together). State transition methods, value objects, enums (`Type` suffix), domain policy. Only `@Entity`/`@Table`/`@Column` JPA annotations allowed here.
- **application** — Two-level structure:
  - `{Domain}UseCase` — facade that composes related `Service` instances. This is what `Controller`/gRPC callers depend on. No business logic of its own.
  - `{Action}Service` — single-operation `@Service`. Owns the transaction boundary. Directly imports Spring Data JPA repository interfaces from `infrastructure.persistence`. No domain repository interfaces — no indirection layer.
- **infrastructure** — Spring Data JPA interfaces (`extends JpaRepository`), QueryDSL implementations, Keycloak/OpenFGA adapters.
- **presentation** — Controllers and gRPC handlers depend on `{Domain}UseCase`, never on individual `Service` classes. MapStruct mappers, per-operation request/response DTOs. No business logic.

**Dependency direction**: `presentation` → `application(UseCase)` → `application(Service)` → `infrastructure` + `domain`

**Cross-domain references**: A domain class may directly reference another domain's types (e.g. `employee/domain` imports `department/domain`). Keep references unidirectional — no circular domain dependencies.

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

- Gradle project dependencies (`implementation project(':...')`) are only allowed pointing to `:security`. Service-to-service calls must go over HTTP.
- `vacation` → `organization` via OpenFeign (`@EnableFeignClients` is already applied on `VacationApplication`).

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

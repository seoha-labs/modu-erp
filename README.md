# modu-erp

An open-source company ERP system built with Java 21 and Spring Boot, organized as a Gradle multi-module project. Each module is an independently deployable service with its own bounded context, following Domain-Driven Design principles.

## Modules

| Module | Port | Description |
|---|---|---|
| `erp-gateway` | 8080 | API Gateway — JWT validation and routing |
| `erp-hr` | 8081 | Human Resources — employees, departments, approval lines |
| `erp-vacation` | 8082 | Vacation & draft request management |
| `erp-db-manager` | 8083 | Database configuration management |
| `erp-kerberos` | 8084 | Company server information registry |
| `erp-common` | — | Shared library — security, OAuth2 configuration |

## Architecture

### Multi-Module Structure

```
Incoming Request
    │
    ▼
erp-gateway (8080)
    ├── /api/hr/**          → erp-hr (8081)
    ├── /api/vacation/**    → erp-vacation (8082)
    ├── /api/db-manager/**  → erp-db-manager (8083)
    └── /api/kerberos/**    → erp-kerberos (8084)

All service modules depend on erp-common (shared library)
erp-vacation calls erp-hr via OpenFeign (approval line resolution)
```

### DDD Package Structure

Each service module follows Domain-Driven Design with the following package layout:

```
com.seohalabs.moduerp.{module}/
├── domain/
│   ├── model/           # Aggregates, Entities, Value Objects
│   ├── repository/      # Repository interfaces (domain layer)
│   └── service/         # Domain Services
├── application/
│   ├── command/         # Command handlers (write side)
│   └── query/           # Query handlers (read side)
├── infrastructure/
│   ├── persistence/     # JPA entities, QueryDSL repositories
│   └── client/          # Feign clients, external integrations
└── presentation/
    ├── rest/            # REST controllers
    └── dto/             # Request / Response DTOs
```

**Dependency direction**: `presentation` → `application` → `domain` ← `infrastructure`

The `domain` layer has no dependency on any other layer. Infrastructure implements domain interfaces.

### Authentication & Authorization

All services act as OAuth2 Resource Servers, validating JWT tokens issued by Keycloak.

```
Client → erp-gateway (validates token) → Service (enforces roles)
                  │
                  ▼
            Keycloak (modu-erp realm)
```

## Tech Stack

| Category | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.4.x |
| Gateway | Spring Cloud Gateway |
| ORM | Spring Data JPA + QueryDSL 5.x |
| Database | PostgreSQL 16 |
| Auth | Keycloak 26 (OAuth2 / JWT) |
| Build | Gradle (multi-module) |
| Deployment | Docker / Kubernetes |

## Getting Started

### Prerequisites

- Java 21
- Docker & Docker Compose

### Run Local Infrastructure

```bash
docker-compose up -d
```

This starts:
- **PostgreSQL** at `localhost:5432` — databases are initialized via `scripts/init-db.sql`
- **Keycloak** at `http://localhost:8180` — admin credentials: `admin / admin`

### Keycloak Setup

1. Open `http://localhost:8180` and log in.
2. Create a Realm named `modu-erp`.
3. Create clients and roles as needed per service.

### Run a Service

```bash
./gradlew :erp-hr:bootRun
```

### Build All

```bash
./gradlew build
```

## Project Structure

```
modu-erp/
├── build.gradle              # Root build — shared plugin versions and BOM
├── settings.gradle           # Module registration
├── gradle.properties         # Version declarations (BOM-unmanaged libraries)
├── docker-compose.yml        # Local infrastructure
├── scripts/
│   └── init-db.sql           # PostgreSQL database initialization
├── erp-common/
├── erp-gateway/
├── erp-hr/
├── erp-vacation/
├── erp-db-manager/
└── erp-kerberos/
```

## Contributing

Contributions are welcome. Please open an issue first to discuss what you would like to change.

## License

[MIT](LICENSE)

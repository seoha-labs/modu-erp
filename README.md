# modu-erp

An open-source company ERP system built with Java 21 and Spring Boot, organized as a Gradle multi-module project. Each module is an independently deployable service with its own bounded context, following Domain-Driven Design principles.

## Modules

| Module | Port | Description |
|---|---|---|
| `erp-gateway` | 8080 | API Gateway — JWT validation and routing |
| `erp-organization` | 8081 | Organization — employees, departments, positions, roles |
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
    ├── /api/organization/**  → erp-organization (8081)
    ├── /api/vacation/**      → erp-vacation (8082)
    ├── /api/db-manager/**    → erp-db-manager (8083)
    └── /api/kerberos/**      → erp-kerberos (8084)

All service modules depend on erp-common (shared library)
erp-vacation calls erp-organization via OpenFeign (approval line resolution)
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
│   ├── persistence/     # Repository implementations
│   └── client/          # Feign clients, external integrations
└── presentation/
    ├── rest/            # REST controllers
    └── dto/             # Request / Response DTOs
```

**Dependency direction**: `presentation` → `application` → `domain` ← `infrastructure`

The `domain` layer has no dependency on any other layer. Infrastructure implements domain interfaces.

### Authentication & Authorization

All services act as OAuth2 Resource Servers, validating JWT tokens issued by Keycloak.
`erp-organization` uses OpenFGA for fine-grained Relationship-Based Access Control (ReBAC).

```
Client → erp-gateway (validates JWT) → Service (enforces roles)
                  │
                  ▼
            Keycloak (modu-erp realm)

erp-organization → OpenFGA (permission checks via @PreAuthorize)
```

## Tech Stack

| Category | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.4.x |
| Gateway | Spring Cloud Gateway (WebFlux) |
| Reactive Stack | Spring WebFlux + Spring Data R2DBC |
| Database | PostgreSQL 16 |
| Auth | Keycloak 26 (OAuth2 / JWT) |
| Authorization | OpenFGA (ReBAC) |
| Build | Gradle (multi-module, Groovy DSL) |
| Deployment | Docker / Kubernetes |

## Getting Started

### Prerequisites

- Java 21
- Docker & Docker Compose

### Run Local Infrastructure

```bash
cd infra
docker-compose up -d
```

This starts:
- **PostgreSQL** at `localhost:5432` — databases initialized via `infra/postgres/init-db.sql`
- **Keycloak** at `http://localhost:8180` — admin credentials: `admin / admin`
- **OpenFGA** at `http://localhost:8090`

### Keycloak Setup

1. Open `http://localhost:8180` and log in.
2. Create a Realm named `modu-erp`.
3. Create an `admin-cli` client with service account roles enabled.

### OpenFGA Setup

On first startup, `erp-organization` creates the FGA store and model automatically.
Follow the log output to set the required environment variables before the next start:

```
# First run — creates store
=== Initial startup. Set OPENFGA_STORE_ID=<id> as an environment variable and restart ===

# Second run — creates authorization model
=== Set OPENFGA_MODEL_ID=<id> as an environment variable and restart ===
```

### Run a Service

```bash
./gradlew :erp-organization:bootRun
```

With OpenFGA configured:

```bash
OPENFGA_STORE_ID=<id> OPENFGA_MODEL_ID=<id> ./gradlew :erp-organization:bootRun
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
├── erp-common/
├── erp-gateway/
├── erp-organization/
├── erp-vacation/
├── erp-db-manager/
├── erp-kerberos/
└── infra/
    ├── docker-compose.yml    # Local infrastructure (PostgreSQL, Keycloak, OpenFGA)
    ├── postgres/
    │   └── init-db.sql       # Database initialization
    └── helm/                 # Kubernetes Helm charts
```

## License

[Apache 2.0](LICENSE)

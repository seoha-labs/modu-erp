# modu-erp

An open-source company ERP system built with Java 21 and Spring Boot, organized as a Gradle multi-module project. Each module is an independently deployable service with its own bounded context, following Domain-Driven Design principles.

## Modules

| Module | Port | Description |
|---|---|---|
| `gateway` | 8080 | API Gateway — JWT validation and routing |
| `organization` | 8081 | Organization — employees, departments, positions, roles |
| `vacation` | 8082 | Vacation & draft request management |
| `db-manager` | 8083 | Database configuration management |
| `kerberos` | 8084 | Company server information registry |
| `security` | — | Shared library — security, OAuth2 configuration |

## Architecture

### Multi-Module Structure

```
Incoming Request
    │
    ▼
gateway (8080)
    ├── /api/organization/**  → organization (8081)
    ├── /api/vacation/**      → vacation (8082)
    ├── /api/db-manager/**    → db-manager (8083)
    └── /api/kerberos/**      → kerberos (8084)

All service modules depend on security (shared library)
vacation calls organization via OpenFeign (approval line resolution)
```

### Feature-Oriented Package Structure

Service modules can organize code by business feature first, then by layer inside each feature:

```
com.seohalabs.moduerp.{module}/
├── employee/
│   ├── domain/
│   ├── application/
│   ├── infrastructure/
│   └── presentation/
├── department/
│   ├── domain/
│   ├── application/
│   ├── infrastructure/
│   └── presentation/
├── role/
│   ├── domain/
│   ├── application/
│   ├── infrastructure/
│   └── presentation/
└── shared/
    └── infrastructure/
```

**Dependency direction**: `presentation` → `application` → `domain` ← `infrastructure`

This keeps each business area cohesive while preserving clear domain and infrastructure boundaries.

### Authentication & Authorization

All services act as OAuth2 Resource Servers, validating JWT tokens issued by Keycloak.
`organization` uses OpenFGA for fine-grained Relationship-Based Access Control (ReBAC).

```
Client → gateway (validates JWT) → Service (enforces roles)
                  │
                  ▼
            Keycloak (modu-erp realm)

organization → OpenFGA (permission checks via @PreAuthorize)
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

On first startup, `organization` creates the FGA store and model automatically.
Follow the log output to set the required environment variables before the next start:

```
# First run — creates store
=== Initial startup. Set OPENFGA_STORE_ID=<id> as an environment variable and restart ===

# Second run — creates authorization model
=== Set OPENFGA_MODEL_ID=<id> as an environment variable and restart ===
```

### Run a Service

```bash
./gradlew :organization:bootRun
```

With OpenFGA configured:

```bash
OPENFGA_STORE_ID=<id> OPENFGA_MODEL_ID=<id> ./gradlew :organization:bootRun
```

### Build All

```bash
./gradlew build
```

## Project Structure

```text
modu-erp/
├── apps/
│   ├── gateway/
│   ├── organization/
│   ├── vacation/
│   ├── db-manager/
│   └── kerberos/
├── shared/
│   └── security/
├── infra/
│   ├── docker-compose.yml    # Local infrastructure (PostgreSQL, Keycloak, OpenFGA)
│   ├── postgres/
│   │   └── init-db.sql       # Database initialization
│   └── helm/                 # Kubernetes Helm charts
├── build.gradle              # Root build — shared plugin versions and BOM
├── settings.gradle           # Module registration
└── gradle.properties         # Version declarations (BOM-unmanaged libraries)
```

## License

[Apache 2.0](LICENSE)

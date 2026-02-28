# Skill: Create a New Service Module

Use this when adding a new service to the modu-erp multi-module project.

## Steps

### 1. Register in `settings.gradle`
```groovy
include 'erp-{name}'
```

### 2. Create `erp-{name}/build.gradle`
```groovy
apply plugin: 'org.springframework.boot'

dependencies {
    implementation project(':erp-common')
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    runtimeOnly 'org.postgresql:postgresql'
}
```

### 3. Create the Application class
Path: `erp-{name}/src/main/java/com/modu/erp/{name}/{Name}Application.java`
```java
package com.modu.erp.{name};

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class {Name}Application {

    public static void main(String[] args) {
        SpringApplication.run({Name}Application.class, args);
    }
}
```

### 4. Create `application.yml`
Path: `erp-{name}/src/main/resources/application.yml`
```yaml
spring:
  application:
    name: erp-{name}
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/erp_{name}
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:postgres}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
    show-sql: false
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${KEYCLOAK_ISSUER_URI:http://localhost:8180/realms/modu-erp}

server:
  port: {port}  # next available: 8085, 8086, ...
```

### 5. Add DB to `scripts/init-db.sql`
```sql
CREATE DATABASE erp_{name};
```

### 6. Add Gateway route in `erp-gateway/src/main/resources/application.yml`
```yaml
- id: erp-{name}
  uri: ${"{NAME}_SERVICE_URL:http://localhost:{port}"}
  predicates:
    - Path=/api/{name}/**
```

### 7. Add environment variable to `.agent/rules/architecture.md`
Add the new service URL variable to the Environment Variables table.

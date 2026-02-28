# =============================================
# Stage 1: Build
# =============================================
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /workspace

COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle settings.gradle gradle.properties ./

COPY erp-common/build.gradle erp-common/
COPY erp-gateway/build.gradle erp-gateway/
COPY erp-hr/build.gradle erp-hr/
COPY erp-vacation/build.gradle erp-vacation/
COPY erp-db-manager/build.gradle erp-db-manager/
COPY erp-kerberos/build.gradle erp-kerberos/

RUN chmod +x gradlew && ./gradlew :erp-kerberos:dependencies --no-daemon

COPY erp-common/src erp-common/src
COPY erp-kerberos/src erp-kerberos/src

RUN ./gradlew :erp-kerberos:bootJar --no-daemon -x test

# =============================================
# Stage 2: Extract layers
# =============================================
FROM eclipse-temurin:21-jre-alpine AS layers
WORKDIR /app
COPY --from=builder /workspace/erp-kerberos/build/libs/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

# =============================================
# Stage 3: Runtime
# =============================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=layers /app/dependencies/ ./
COPY --from=layers /app/spring-boot-loader/ ./
COPY --from=layers /app/snapshot-dependencies/ ./
COPY --from=layers /app/application/ ./

EXPOSE 8084

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]

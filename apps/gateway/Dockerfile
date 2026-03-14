# =============================================
# Stage 1: Build
# =============================================
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /workspace

COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle settings.gradle gradle.properties ./

COPY shared/security/build.gradle shared/security/
COPY apps/gateway/build.gradle apps/gateway/
COPY apps/organization/build.gradle apps/organization/
COPY apps/vacation/build.gradle apps/vacation/
COPY apps/db-manager/build.gradle apps/db-manager/
COPY apps/kerberos/build.gradle apps/kerberos/

RUN chmod +x gradlew && ./gradlew :gateway:dependencies --no-daemon

COPY shared/security/src shared/security/src
COPY apps/gateway/src apps/gateway/src

RUN ./gradlew :gateway:bootJar --no-daemon -x test

# =============================================
# Stage 2: Extract layers
# =============================================
FROM eclipse-temurin:21-jre-alpine AS layers
WORKDIR /app
COPY --from=builder /workspace/apps/gateway/build/libs/*.jar app.jar
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

EXPOSE 8080

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]

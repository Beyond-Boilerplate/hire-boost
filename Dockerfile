# Stage 1: Build Stage
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

# Copy Gradle Wrapper & Dependency Files First (For Caching Dependencies)
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew && ./gradlew build --no-daemon || return 0

# Copy Source Code & Build Again
COPY src src
RUN ./gradlew bootJar --no-daemon

# Stage 2: Runtime Stage
FROM eclipse-temurin:17-jre AS runtime

WORKDIR /app

# Secure container by creating a non-root user
#RUN addgroup spring && adduser -D -G spring spring
#USER spring:spring

# Copy JAR from Builder Stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Expose Port & Run Application
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -jar app.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]

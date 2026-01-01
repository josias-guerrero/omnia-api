# Runtime stage
FROM eclipse-temurin:25-jre-alpine
LABEL authors="josias"

WORKDIR /app

# The application is already built by the CI runner (GitHub Actions), so we just copy the JAR.
# Expects the context to have build/libs/*.jar available.
COPY build/libs/*.jar app.jar

# Environment configuration
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SERVER_PORT=8080

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

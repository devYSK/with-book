# Java 23 이미지 기반 (공식 릴리스가 있다면 설정)
FROM eclipse-temurin:23-jdk-alpine

COPY build/libs/order-service.jar app.jar
COPY opentelemetry-javaagent-1.33.3.jar /workspace/BOOT-INF/lib/opentelemetry-javaagent-1.33.3.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]

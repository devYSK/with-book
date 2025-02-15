# Java 23 이미지 기반 (eclipse-temurin JDK)
FROM eclipse-temurin:23-jdk-alpine

# 애플리케이션 JAR 파일 복사
COPY build/libs/catalog-service.jar /app.jar

# OpenTelemetry Java 에이전트 파일 복사
COPY opentelemetry-javaagent-1.33.3.jar /workspace/BOOT-INF/lib/opentelemetry-javaagent-1.33.3.jar

# 환경 변수 설정
ENV BPL_JVM_THREAD_COUNT=50 \
    BPL_DEBUG_ENABLED=true \
    BPL_DEBUG_PORT=8001 \
    JAVA_TOOL_OPTIONS="-javaagent:/workspace/BOOT-INF/lib/opentelemetry-javaagent-1.33.3.jar" \
    OTEL_SERVICE_NAME="catalog-service" \
    OTEL_EXPORTER_OTLP_ENDPOINT="http://tempo:4317" \
    OTEL_METRICS_EXPORTER="none" \
    SPRING_CLOUD_CONFIG_URI="http://config-service:8888" \
#    SPRING_DATASOURCE_URL="jdbc:postgresql://polar-postgres:5432/polardb_catalog" \
    SPRING_PROFILES_ACTIVE="testdata" \
    SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI="http://host.docker.internal:8080/realms/PolarBookshop"

# 포트 노출 설정
EXPOSE 9001 8001

# ENTRYPOINT 설정
ENTRYPOINT ["java", "-jar", "/app.jar"]

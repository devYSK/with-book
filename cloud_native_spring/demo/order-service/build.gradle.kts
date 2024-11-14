import org.springframework.boot.gradle.tasks.bundling.BootJar

// 버전 정보 정의
val otelVersion = "1.33.3"
val springCloudVersion = "2023.0.0"
val testcontainersVersion = "1.19.0"
val testKeycloakVersion = "3.5.1"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.cloud:spring-cloud-stream-binder-rabbit")
    implementation("org.springframework.retry:spring-retry")

    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    runtimeOnly("io.opentelemetry.javaagent:opentelemetry-javaagent:$otelVersion")
    runtimeOnly("org.flywaydb:flyway-core")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("org.postgresql:r2dbc-postgresql")
    runtimeOnly("org.springframework:spring-jdbc")

    // Only on Apple Silicon. Why it's necessary: https://github.com/netty/netty/issues/11020
    // runtimeOnly("io.netty:netty-resolver-dns-native-macos:4.1.101.Final:osx-aarch_64")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("com.squareup.okhttp3:mockwebserver")
    testImplementation("com.github.dasniko:testcontainers-keycloak:$testKeycloakVersion")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:r2dbc")
    testImplementation("org.springframework.cloud:spring-cloud-stream") {
//        artifact {
//            name = "spring-cloud-stream"
//            extension = "jar"
//            type = "test-jar"
//            classifier = "test-binder"
//        }
    }
}

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:$testcontainersVersion")
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }
}

springBoot {
    buildInfo()
}

//tasks.bootBuildImage {
//    builder.set("docker.io/paketobuildpacks/builder-jammy-base")
//    imageName.set(project.name)
//    environment.set(mapOf("BP_JVM_VERSION" to "17"))
//
//    docker {
//        publishRegistry {
//            username.set(project.findProperty("registryUsername") as String?)
//            password.set(project.findProperty("registryToken") as String?)
//            url.set(project.findProperty("registryUrl") as String?)
//        }
//    }
//}

tasks.getByName<BootJar>("bootJar") {
    archiveFileName.set("order-service.jar")
    enabled = true
}

tasks.getByName("jar") {
    enabled = false
}

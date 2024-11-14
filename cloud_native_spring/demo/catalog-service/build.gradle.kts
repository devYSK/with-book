import org.springframework.boot.gradle.tasks.bundling.BootJar

description = "Provides functionality for managing the books in the catalog."

// 버전 정보 정의
val otelVersion = "1.33.3"
val springCloudVersion = "2023.0.0"
val testcontainersVersion = "1.19.0"
val testKeycloakVersion = "3.5.1"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
//    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.retry:spring-retry")
    // https://mvnrepository.com/artifact/org.flywaydb/flyway-core
    implementation("org.flywaydb:flyway-core:10.20.1")
    // https://mvnrepository.com/artifact/org.flywaydb/flyway-database-postgresql
    runtimeOnly("org.flywaydb:flyway-database-postgresql:10.20.1")

//    implementation("org.springframework.boot:spring-boot-starter-security:3.3.5")


    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    runtimeOnly("io.opentelemetry.javaagent:opentelemetry-javaagent:$otelVersion")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("com.github.dasniko:testcontainers-keycloak:$testKeycloakVersion")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
        mavenBom("org.testcontainers:testcontainers-bom:$testcontainersVersion")
    }
}

springBoot {
    buildInfo()
}

tasks.bootRun {
    systemProperty("spring.profiles.active", "testdata")
}

tasks.getByName<BootJar>("bootJar") {
    archiveFileName.set("catalog-service.jar")
    enabled = true
}
tasks.getByName("jar") {
    enabled = false
}

//tasks.bootBuildImage {
//    builder.set("docker.io/paketobuildpacks/builder-jammy-base")
//    imageName.set("devysk/mybooks")
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


tasks.named<Test>("test") {
    useJUnitPlatform()
}
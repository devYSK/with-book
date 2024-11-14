import org.springframework.boot.gradle.tasks.bundling.BootJar

// 버전 정보 정의
val otelVersion = "1.33.3"
val springCloudVersion = "2023.0.0"
val testcontainersVersion = "1.19.0"
val testKeycloakVersion = "3.5.1"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-reactor-resilience4j")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
    implementation("org.springframework.session:spring-session-data-redis")

    runtimeOnly("io.github.resilience4j:resilience4j-micrometer")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    runtimeOnly("io.opentelemetry.javaagent:opentelemetry-javaagent:$otelVersion")

    // Only on Apple Silicon. Why it's necessary: https://github.com/netty/netty/issues/11020
    // runtimeOnly("io.netty:netty-resolver-dns-native-macos:4.1.101.Final:osx-aarch_64")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.testcontainers:junit-jupiter")
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

//tasks.named<org.springframework.boot.gradle.tasks.bundling.BootBuildImage>("bootBuildImage") {
//    builder = "docker.io/paketobuildpacks/builder-jammy-base"
//    imageName = project.name
//    environment["BP_JVM_VERSION"] = "17"
//
//    docker {
//        publishRegistry {
//            username = project.findProperty("registryUsername") as String?
//            password = project.findProperty("registryToken") as String?
//            url = project.findProperty("registryUrl") as String?
//        }
//    }
//}

tasks.test {
    useJUnitPlatform()
}

tasks.getByName<BootJar>("bootJar") {
    archiveFileName.set("edge-service.jar")
    enabled = true
}
tasks.getByName("jar") {
    enabled = false
}

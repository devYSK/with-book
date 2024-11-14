import org.springframework.boot.gradle.tasks.bundling.BootJar

description = "Provides functionality for dispatching orders."
val otelVersion = "1.33.3"
val springCloudVersion = "2023.0.0"
val testcontainersVersion = "1.19.0"
val testKeycloakVersion = "3.5.1"

dependencies {
     implementation("org.springframework.cloud:spring-cloud-function-context")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.cloud:spring-cloud-stream-binder-rabbit")
    implementation("org.springframework.retry:spring-retry")

    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    runtimeOnly("io.opentelemetry.javaagent:opentelemetry-javaagent:$otelVersion")

    // Only on Apple Silicon. Why it's necessary: https://github.com/netty/netty/issues/11020
    // runtimeOnly("io.netty:netty-resolver-dns-native-macos:4.1.101.Final:osx-aarch_64")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    // https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-stream-test-binder
    testImplementation("org.springframework.cloud:spring-cloud-stream-test-binder")

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
    archiveFileName.set("dispatcher-service.jar")
    enabled = true
}
tasks.getByName("jar") {
    enabled = false
}

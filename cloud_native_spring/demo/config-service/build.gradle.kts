import org.springframework.boot.gradle.tasks.bundling.BootJar

extra["springCloudVersion"] = "2023.0.3"

description = "Provides functionality for centralizing the application configuration."


val otelVersion = "1.33.3"

dependencies {
    implementation("org.springframework.cloud:spring-cloud-config-server")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.cloud:spring-cloud-config-server")

    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    runtimeOnly("io.opentelemetry.javaagent:opentelemetry-javaagent:$otelVersion")

}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

springBoot {
    buildInfo()
}

//tasks.bootBuildImage {
//    builder.set("docker.io/paketobuildpacks/builder-jammy-base")
//    imageName.set(project.name)
//    environment.set(mapOf("BP_JVM_VERSION" to "23"))
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
    archiveFileName.set("config-service.jar")
    enabled = true
}
tasks.getByName("jar") {
    enabled = false
}

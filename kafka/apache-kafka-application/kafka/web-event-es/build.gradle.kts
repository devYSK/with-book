

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // https://mvnrepository.com/artifact/org.apache.kafka/connect-api
    implementation("org.apache.kafka:connect-api:3.9.0")
    implementation("org.slf4j:slf4j-simple:2.0.12")
    implementation("com.google.code.gson:gson:2.8.6")
    // https://mvnrepository.com/artifact/org.elasticsearch.client/elasticsearch-rest-high-level-client
    implementation("org.elasticsearch.client:elasticsearch-rest-high-level-client:7.17.26")


}

tasks.jar {
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

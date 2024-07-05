import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.8.0"
}

allprojects {
	group = "org.test"
	version = "1.0.0"

	repositories {
		mavenCentral()
	}
}

subprojects {
	apply(plugin = "org.jetbrains.kotlin.jvm")

	repositories {
		mavenCentral()
	}

	dependencies {
		implementation("org.jetbrains.kotlin:kotlin-reflect")
		implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
		// Coroutine
		implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
		implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
		implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm")
		implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j")

	}

	tasks.withType<KotlinCompile> {
		kotlinOptions {
			freeCompilerArgs = listOf("-Xjsr305=strict")
			jvmTarget = "17"
		}
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}

}
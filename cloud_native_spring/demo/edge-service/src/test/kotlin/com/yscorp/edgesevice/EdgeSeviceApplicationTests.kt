package com.yscorp.edgesevice

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
internal class EdgeServiceApplicationTests {
    @MockBean
    var clientRegistrationRepository: ReactiveClientRegistrationRepository? = null

    @Test
    fun verifyThatSpringContextLoads() {
    }

    companion object {
        private const val REDIS_PORT = 6379

        @Container
        var redis: GenericContainer<*> = GenericContainer(DockerImageName.parse("redis:7.2"))
            .withExposedPorts(REDIS_PORT)

        @DynamicPropertySource
        fun redisProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.redis.host") { redis.host }
            registry.add("spring.redis.port") {
                redis.getMappedPort(
                    REDIS_PORT
                )
            }
        }
    }
}

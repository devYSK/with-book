package com.yscorp.orderservice.order

import com.yscorp.orderservice.config.DataConfig
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.context.annotation.Import
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import reactor.test.StepVerifier
import java.util.*
import java.util.function.Predicate

@DataR2dbcTest
@Import(DataConfig::class)
@Testcontainers
internal class OrderRepositoryR2dbcTests {

    @Autowired
    lateinit var  orderRepository: OrderRepository

    @Test
    fun findOrderByIdWhenNotExisting() {
        StepVerifier.create(orderRepository!!.findById(394L))
            .expectNextCount(0)
            .verifyComplete()
    }

    @Test
    fun createRejectedOrder(): Unit = runBlocking {
        val rejectedOrder  = OrderService.buildRejectedOrder("1234567890", 3)
        StepVerifier.create(orderRepository!!.save(rejectedOrder))
            .expectNextMatches { order -> order.status.equals(OrderStatus.REJECTED) }
            .verifyComplete()


    }

    @Test
    fun whenCreateOrderNotAuthenticatedThenNoAuditMetadata() {
        val rejectedOrder  = OrderService.buildRejectedOrder("1234567890", 3)
        StepVerifier.create(orderRepository!!.save(rejectedOrder))
            .expectNextMatches { order ->
                Objects.isNull(order.createdBy) &&
                    Objects.isNull(order.lastModifiedBy)
            }
            .verifyComplete()
    }

    @Test
    @WithMockUser("marlena")
    fun whenCreateOrderAuthenticatedThenAuditMetadata() {
        val rejectedOrder =  OrderService.buildRejectedOrder("1234567890", 3)
        StepVerifier.create(orderRepository!!.save(rejectedOrder))
            .expectNextMatches { order ->
                order.createdBy.equals("marlena") &&
                    order.lastModifiedBy.equals("marlena")
            }
            .verifyComplete()
    }

    companion object {
        @Container
        var postgresql: PostgreSQLContainer<*> = PostgreSQLContainer(DockerImageName.parse("postgres:14.12"))

        @DynamicPropertySource
        fun postgresqlProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.r2dbc.url") { r2dbcUrl() }
            registry.add("spring.r2dbc.username") { postgresql.username }
            registry.add("spring.r2dbc.password") { postgresql.password }
            registry.add("spring.flyway.url") { postgresql.jdbcUrl }
        }

        private fun r2dbcUrl(): String {
            return String.format(
                "r2dbc:postgresql://%s:%s/%s", postgresql.host,
                postgresql.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT), postgresql.databaseName
            )
        }
    }

}

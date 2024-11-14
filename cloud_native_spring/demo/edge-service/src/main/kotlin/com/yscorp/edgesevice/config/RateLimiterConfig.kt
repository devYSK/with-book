package com.yscorp.edgesevice.config

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.server.ServerWebExchange
import java.security.Principal

@Configuration
class RateLimiterConfig {
    @Bean
    fun keyResolver(): KeyResolver {
        return KeyResolver { exchange: ServerWebExchange ->
            exchange.getPrincipal<Principal>()
                .map { obj: Principal -> obj.name }
                .defaultIfEmpty("anonymous")
        }
    }
}

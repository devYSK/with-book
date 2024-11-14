package com.yscorp.orderservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.ReactiveAuditorAware
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContext


@Configuration
@EnableR2dbcAuditing
class DataConfig {
    @Bean
    fun auditorAware(): ReactiveAuditorAware<String> {
        return ReactiveAuditorAware {
            ReactiveSecurityContextHolder.getContext()
                .map { obj: SecurityContext -> obj.authentication }
                .filter { obj: Authentication -> obj.isAuthenticated }
                .map { obj: Authentication -> obj.name }
        }
    }
}

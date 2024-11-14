package com.yscorp.catalog.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing

@Configuration
@EnableJdbcAuditing
class DataConfig {

//    @Bean
//    fun auditorAware(): AuditorAware<String> {
//        return AuditorAware {
//            Optional.ofNullable(
//                SecurityContextHolder.getContext()
//            )
//                .map { obj: SecurityContext -> obj.authentication }
//                .filter { obj: Authentication -> obj.isAuthenticated }
//                .map { obj: Authentication -> obj.name }
//        }
//    }

}

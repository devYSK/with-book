package com.yscorp.edgesevice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository
import org.springframework.security.oauth2.client.web.server.WebSessionServerOAuth2AuthorizedClientRepository
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository
import org.springframework.security.web.server.csrf.CsrfToken
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {

//    @Bean
//    fun authorizedClientRepository(): ServerOAuth2AuthorizedClientRepository {
//        return WebSessionServerOAuth2AuthorizedClientRepository()
//    }

    @Bean
    fun springSecurityFilterChain(
        http: ServerHttpSecurity,
//        clientRegistrationRepository: ReactiveClientRegistrationRepository
    ): SecurityWebFilterChain {
        return http
            .authorizeExchange { exchanges ->
                exchanges
                    .pathMatchers("/actuator/**").permitAll()
                    .pathMatchers("/**").permitAll()
                    .pathMatchers("/", "/*.css", "/*.js", "/favicon.ico").permitAll()
                    .pathMatchers(HttpMethod.GET, "/books/**").permitAll()
                    .anyExchange().authenticated()
            }
            .exceptionHandling { exceptions ->
                exceptions.authenticationEntryPoint(HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED))
            }
//            .oauth2Login { }  // Customizer.withDefaults()와 동일
//            .logout { logout ->
//                logout.logoutSuccessHandler(oidcLogoutSuccessHandler(clientRegistrationRepository))
//            }
            .csrf { csrf ->
                csrf.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse())
            }
            .build()
    }

    private fun oidcLogoutSuccessHandler(
        clientRegistrationRepository: ReactiveClientRegistrationRepository
    ): ServerLogoutSuccessHandler {
        val handler = OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository)
        handler.setPostLogoutRedirectUri("{baseUrl}")
        return handler
    }

    @Bean
    fun csrfWebFilter(): WebFilter {
        // Required because of https://github.com/spring-projects/spring-security/issues/5766
        return WebFilter { exchange: ServerWebExchange, chain: WebFilterChain ->
            exchange.response.beforeCommit {
                Mono.defer {
                    val csrfToken =
                        exchange.getAttribute<Mono<CsrfToken>>(
                            CsrfToken::class.java.name
                        )
                    csrfToken?.then() ?: Mono.empty()
                }
            }
            chain.filter(exchange)
        }
    }
}

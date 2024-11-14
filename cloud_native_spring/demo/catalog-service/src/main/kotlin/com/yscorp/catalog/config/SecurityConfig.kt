package com.yscorp.catalog.config

//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
//import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter

//@EnableWebSecurity
class SecurityConfig {
//
//    @Bean
//    fun corsConfigurationSource(): CorsConfigurationSource {
//        val config = corsConfiguration()
//
//        val source = UrlBasedCorsConfigurationSource()
//        source.registerCorsConfiguration("/**", config)
//        return source
//    }
//
//    @Bean
//    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
//        return http.securityMatcher("/**")
//            .httpBasic { it.disable() }            // 기본 인증 비활성화
//            .formLogin { it.disable() }            // 폼 로그인 비활성화
//            .logout { it.disable() }               // 로그아웃 비활성화
//            .csrf { it.disable() }
//            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
//            .authorizeHttpRequests {
//                it.requestMatchers("/**").permitAll()
//                    .anyRequest().permitAll()
////                    .requestMatchers(HttpMethod.OPTIONS, "/**/*").permitAll()
////                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
////                    .requestMatchers("/actuator/**").permitAll()
////                    .requestMatchers("/**").permitAll()
////                    .requestMatchers(HttpMethod.GET, "/", "/books/**").permitAll()
////                    .anyRequest().hasRole("employee")
////                    .anyRequest().authenticated()
//            }
////            .oauth2ResourceServer { oauth2 ->
////                oauth2.jwt(Customizer.withDefaults()) // 새로운 방식으로 jwt 설정
////            }
//            .build()
//    }
//
////    @Bean
////    fun webSecurityCustomizer(): WebSecurityCustomizer {
////
////        return WebSecurityCustomizer { web ->
////            web.ignoring()
////                .requestMatchers("/books", "/**")
////        }
////    }
//
////    @Bean
////    fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
////        val jwtGrantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()
////        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_")
////        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("roles")
////
////        val jwtAuthenticationConverter = JwtAuthenticationConverter()
////        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter)
////        return jwtAuthenticationConverter
////    }
//
//    private fun corsConfiguration(): CorsConfiguration {
//        val config = CorsConfiguration()
//
//        config.allowedOrigins = listOf("http://localhost:3000")
//        config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
//        config.allowedHeaders = listOf("*")
//        config.exposedHeaders = listOf("*")
//
//        config.allowedOriginPatterns = mutableListOf(
//            "http://localhost:3000",
//            "http://localhost",
//            "http://localhost:[*]/",
//        )
//        config.allowCredentials = true
//        config.exposedHeaders = mutableListOf(
//            HttpHeaders.AUTHORIZATION,
//            "*",
//            HttpHeaders.CONTENT_DISPOSITION
//        )
//
//        return config
//    }

}

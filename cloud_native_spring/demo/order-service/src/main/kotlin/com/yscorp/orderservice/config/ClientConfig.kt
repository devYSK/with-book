package com.yscorp.orderservice.config

import org.springframework.web.reactive.function.client.WebClient


@org.springframework.context.annotation.Configuration
class ClientConfig {
    @org.springframework.context.annotation.Bean
    fun webClient(
        clientProperties: ClientProperties,
        webClientBuilder: org.springframework.web.reactive.function.client.WebClient.Builder
    ): WebClient {
        return webClientBuilder
            .baseUrl(clientProperties.catalogServiceUri.toString())
            .build()
    }
}

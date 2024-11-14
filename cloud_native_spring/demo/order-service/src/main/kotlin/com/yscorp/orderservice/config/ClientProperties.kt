package com.yscorp.orderservice.config

import jakarta.validation.constraints.NotNull
import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URI


@ConfigurationProperties(prefix = "polar")
@JvmRecord
data class ClientProperties(
    @field:NotNull @param:NotNull val catalogServiceUri: URI
)

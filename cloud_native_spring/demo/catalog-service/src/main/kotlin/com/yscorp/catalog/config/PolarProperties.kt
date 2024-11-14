package com.yscorp.catalog.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "polar")
class PolarProperties(
    var greeting: String
) {
}

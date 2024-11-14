package com.yscorp.catalog

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties()
@ConfigurationPropertiesScan("com.yscorp")
class CatalogServiceApplication

fun main(args: Array<String>) {
    runApplication<CatalogServiceApplication>(*args)
}

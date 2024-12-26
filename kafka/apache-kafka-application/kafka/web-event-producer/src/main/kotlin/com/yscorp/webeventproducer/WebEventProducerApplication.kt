package com.yscorp.webeventproducer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WebEventProducerApplication

fun main(args: Array<String>) {
    runApplication<WebEventProducerApplication>(*args)
}

package com.yscorp.springkafka

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import kotlin.system.exitProcess

@SpringBootApplication
class SpringKafkaApplication

fun main(args: Array<String>) {
    runApplication<SpringKafkaApplication>(*args)
}

@Component
class SimpleRunner(
    private val simpleProducer: SimpleProducer
) : CommandLineRunner {
    private var TOPIC_NAME: String = "test"

    override fun run(vararg args: String) {
        for (i in 0..9) {
            simpleProducer.sendMessage(TOPIC_NAME, "test$i")
        }
        exitProcess(0)
    }

}
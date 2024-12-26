package com.yscorp.springkafka

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class SimpleProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>
) {

    fun sendMessage(topic: String, message: String) {
        kafkaTemplate.send(topic, message)
    }
}
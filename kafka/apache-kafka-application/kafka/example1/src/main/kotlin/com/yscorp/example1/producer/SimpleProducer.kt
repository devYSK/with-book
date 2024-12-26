package com.yscorp.example1.producer

import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*

object SimpleProducer {
    private val logger = KotlinLogging.logger {  }
    private const val TOPIC_NAME = "test"
    private const val BOOTSTRAP_SERVERS = "localhost:9092"

//    @JvmStatic
    fun main(args: Array<String>) {
        val configs = Properties()
        configs[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = BOOTSTRAP_SERVERS
        configs[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
        configs[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name

        val producer = KafkaProducer<String, String>(configs)

        val messageValue = "testMessage1234"
        for (i in 0..9) {
            val record = ProducerRecord<String, String>(TOPIC_NAME, messageValue + i)
            producer.send(record)
        }

//        logger.info("{}", record)
        producer.flush()
        producer.close()
    }
}
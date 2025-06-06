package com.yscorp.example1.producer

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*

object WithKeyValueProducer {
    private val TOPIC_NAME = "test"
    private val BOOTSTRAP_SERVERS = "localhost:9092"

    @JvmStatic
    fun main(args: Array<String>) {
        val configs = Properties()
        configs[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = BOOTSTRAP_SERVERS
        configs[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
        configs[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name

        val producer = KafkaProducer<String, String>(configs)

        val record = ProducerRecord(TOPIC_NAME, "Pangyo", "Pangyo")
        producer.send(record)
        val record2 = ProducerRecord(TOPIC_NAME, "Busan", "Busan")
        producer.send(record2)
        producer.flush()
        producer.close()
    }

}


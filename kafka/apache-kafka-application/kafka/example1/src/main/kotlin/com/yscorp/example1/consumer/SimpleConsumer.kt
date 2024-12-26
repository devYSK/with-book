package com.yscorp.example1.consumer

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*

object SimpleConsumer {
    private val logger: Logger = LoggerFactory.getLogger(SimpleConsumer::class.java)
    private const val TOPIC_NAME = "test"
    private const val BOOTSTRAP_SERVERS = "localhost:9092"
    private const val GROUP_ID = "test-group000"

    @JvmStatic
    fun main(args: Array<String>) {
        val configs = Properties()
        configs[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = BOOTSTRAP_SERVERS
        configs[ConsumerConfig.GROUP_ID_CONFIG] = GROUP_ID
        configs[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        configs[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        configs[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"

        val consumer = KafkaConsumer<String, String>(configs)

        consumer.subscribe(listOf(TOPIC_NAME))
        while (true) {
            val records = consumer.poll(Duration.ofSeconds(1))
            println(records.count())
            for (record in records) {
                logger.info("record:{}", record)
            }
        }

    }
}
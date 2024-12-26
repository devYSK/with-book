package com.yscorp.example1.consumer

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*

object ConsumerWithRebalanceListener {
    private val logger: Logger = LoggerFactory.getLogger(ConsumerWithRebalanceListener::class.java)
    private const val TOPIC_NAME = "test"
    private const val BOOTSTRAP_SERVERS = "my-kafka:9092"
    private const val GROUP_ID = "test-group"

    private lateinit var consumer: KafkaConsumer<String, String>

    @JvmStatic
    fun main(args: Array<String>) {
        val configs = Properties()
        configs[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = BOOTSTRAP_SERVERS
        configs[ConsumerConfig.GROUP_ID_CONFIG] = GROUP_ID
        configs[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        configs[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name

        consumer = KafkaConsumer(configs)
        consumer.subscribe(listOf(TOPIC_NAME), RebalanceListener())
        while (true) {
            val records = consumer!!.poll(Duration.ofSeconds(1))
            for (record in records) {
                logger.info("{}", record)
            }
        }
    }
}

class RebalanceListener : ConsumerRebalanceListener {
    override fun onPartitionsAssigned(partitions: Collection<TopicPartition>) {
        logger.warn("Partitions are assigned : $partitions")
    }

    override fun onPartitionsRevoked(partitions: Collection<TopicPartition>) {
        logger.warn("Partitions are revoked : $partitions")
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(RebalanceListener::class.java)
    }
}
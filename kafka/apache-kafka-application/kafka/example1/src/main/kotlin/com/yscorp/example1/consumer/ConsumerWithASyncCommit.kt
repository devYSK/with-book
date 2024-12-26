package com.yscorp.example1.consumer

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*

object ConsumerWithASyncCommit {
    private val logger: Logger = LoggerFactory.getLogger(ConsumerWithASyncCommit::class.java)
    private const val TOPIC_NAME = "test"
    private const val BOOTSTRAP_SERVERS = "my-kafka:9092"
    private const val GROUP_ID = "test-group"

    @JvmStatic
    fun main(args: Array<String>) {
        val configs = Properties()
        configs[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = BOOTSTRAP_SERVERS
        configs[ConsumerConfig.GROUP_ID_CONFIG] = GROUP_ID
        configs[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        configs[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        configs[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = false

        val consumer = KafkaConsumer<String, String>(configs)
        consumer.subscribe(Arrays.asList(TOPIC_NAME))

        while (true) {
            val records = consumer.poll(Duration.ofSeconds(1))
            for (record in records) {
                logger.info("record:{}", record)
            }
            consumer.commitAsync { offsets, e ->
                if (e != null) System.err.println("Commit failed")
                else println("Commit succeeded")
                if (e != null) logger.error("Commit failed for offsets {}", offsets, e)
            }
        }
    }
}
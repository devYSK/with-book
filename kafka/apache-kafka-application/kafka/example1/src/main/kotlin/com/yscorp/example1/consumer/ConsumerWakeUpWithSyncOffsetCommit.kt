package com.yscorp.example1.consumer

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.errors.WakeupException
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*

class ConsumerWakeUpWithSyncOffsetCommit {
    private val logger: Logger = LoggerFactory.getLogger(ConsumerWithSyncOffsetCommit::class.java)
    private val TOPIC_NAME: String = "test"
    private val BOOTSTRAP_SERVERS: String = "my-kafka:9092"
    private val GROUP_ID: String = "test-group"
    private var consumer: KafkaConsumer<String, String>? = null

    fun main() {
        Runtime.getRuntime().addShutdownHook(ShutdownThread())

        val configs = Properties()
        configs[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = BOOTSTRAP_SERVERS
        configs[ConsumerConfig.GROUP_ID_CONFIG] = GROUP_ID
        configs[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        configs[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        configs[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = false

        consumer = KafkaConsumer(configs)
        consumer!!.subscribe(Arrays.asList(TOPIC_NAME))

        try {
            while (true) {
                val records = consumer!!.poll(Duration.ofSeconds(1))
                for (record in records) {
                    logger.info("{}", record)
                }
                consumer!!.commitSync()
            }
        } catch (e: WakeupException) {
            logger.warn("Wakeup consumer")
        } finally {
            logger.warn("Consumer close")
            consumer!!.close()
        }
    }

    inner class ShutdownThread : Thread() {
        override fun run() {
            logger.info("Shutdown hook")
            consumer?.wakeup()
        }
    }
}
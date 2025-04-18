package com.yscorp.example1.multithread

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*
import java.util.concurrent.Executors

object MultiConsumerThread {
    private const val TOPIC_NAME = "test"
    private const val BOOTSTRAP_SERVERS = "my-kafka:9092"
    private const val GROUP_ID = "test-group"
    private const val CONSUMER_COUNT = 3

    @JvmStatic
    fun main(args: Array<String>) {
        val configs = Properties()
        configs[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = BOOTSTRAP_SERVERS
        configs[ConsumerConfig.GROUP_ID_CONFIG] = GROUP_ID
        configs[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        configs[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name

        val executorService = Executors.newCachedThreadPool()

        for (i in 0 until CONSUMER_COUNT) {
            val worker = MultiConsumerWorker(configs, TOPIC_NAME, i)
            executorService.execute(worker)
        }
    }
}

class MultiConsumerWorker internal constructor(
    private val prop: Properties,
    private val topic: String, number: Int
) : Runnable {
    private val threadName = "consumer-thread-$number"
    private var consumer: KafkaConsumer<String, String> = KafkaConsumer(prop)

    override fun run() {
        consumer.subscribe(listOf(topic))

        while (true) {
            val records = consumer.poll(Duration.ofSeconds(1))
            for (record in records) {
                logger.info("{}", record)
            }
            consumer.commitSync()
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(MultiConsumerWorker::class.java)
    }
}
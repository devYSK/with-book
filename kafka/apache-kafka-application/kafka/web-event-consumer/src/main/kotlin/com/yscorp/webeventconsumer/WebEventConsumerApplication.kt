package com.yscorp.webeventconsumer

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.Executors
import java.util.function.Consumer

object HdfsSinkApplication {
    private val logger: Logger = LoggerFactory.getLogger(HdfsSinkApplication::class.java)

    private const val BOOTSTRAP_SERVERS = "my-kafka:9092"
    private const val TOPIC_NAME = "select-color"
    private const val GROUP_ID = "color-hdfs-save-consumer-group"
    private const val CONSUMER_COUNT = 2
    private val workers: MutableList<ConsumerWorker> = ArrayList()

    @JvmStatic
    fun main(args: Array<String>) {
        Runtime.getRuntime().addShutdownHook(ShutdownThread())

        val configs = Properties()
        configs[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = BOOTSTRAP_SERVERS
        configs[ConsumerConfig.GROUP_ID_CONFIG] = GROUP_ID
        configs[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        configs[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name

        val executorService = Executors.newCachedThreadPool()
        for (i in 0 until CONSUMER_COUNT) {
            workers.add(ConsumerWorker(configs, TOPIC_NAME, i))
        }
        workers.forEach(Consumer { command: ConsumerWorker ->
            executorService.execute(
                command
            )
        })
    }

    internal class ShutdownThread : Thread() {
        override fun run() {
            logger.info("Shutdown hook")
            workers.forEach(ConsumerWorker::stopAndWakeup)
        }
    }
}
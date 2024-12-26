package com.yscorp.example1.multithread

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*
import java.util.concurrent.Executors

object ConsumerWithMultiWorkerThread {
    private val logger: Logger = LoggerFactory.getLogger(ConsumerWithMultiWorkerThread::class.java)
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
        configs[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = true
        configs[ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG] = 10000


        val consumer = KafkaConsumer<String, String>(configs)
        consumer.subscribe(Arrays.asList(TOPIC_NAME))
        val executorService = Executors.newCachedThreadPool()

        while (true) {
            val records = consumer.poll(Duration.ofSeconds(10))
            for (record in records) {
                val worker = ConsumerMultiWorker(record.value())
                executorService.execute(worker)
            }
        }
    }
}

class ConsumerMultiWorker internal constructor(private val recordValue: String) : Runnable {
    override fun run() {
        logger.info(
            "thread:{}\trecord:{}", Thread.currentThread().name,
            recordValue
        )
    }

    companion object {
        internal val logger: Logger = LoggerFactory.getLogger(ConsumerMultiWorker::class.java)
    }
}
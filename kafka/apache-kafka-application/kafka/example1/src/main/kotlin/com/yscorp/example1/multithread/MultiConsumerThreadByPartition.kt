package com.yscorp.example1.multithread

import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.ConfigEntry
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.config.ConfigResource
import org.apache.kafka.common.errors.WakeupException
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*
import java.util.concurrent.Executors


object MultiConsumerThreadByPartition {
    private val logger: Logger = LoggerFactory.getLogger(MultiConsumerThreadByPartition::class.java)

    private const val TOPIC_NAME = "test"
    private const val BOOTSTRAP_SERVERS = "my-kafka:9092"
    private const val GROUP_ID = "test-group"

    private val workerThreads: MutableList<MultiConsumerThreadByPartitionWorker> = ArrayList()

    @JvmStatic
    fun main(args: Array<String>) {
        Runtime.getRuntime().addShutdownHook(ShutdownThread())
        val configs = Properties()
        configs[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = BOOTSTRAP_SERVERS
        configs[ConsumerConfig.GROUP_ID_CONFIG] = GROUP_ID
        configs[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        configs[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        configs[ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG] = false

        val CONSUMER_COUNT = getPartitionSize(TOPIC_NAME)
        logger.info("Set thread count : {}", CONSUMER_COUNT)

        val executorService = Executors.newCachedThreadPool()
        for (i in 0 until CONSUMER_COUNT) {
            val worker = MultiConsumerThreadByPartitionWorker(configs, TOPIC_NAME, i)
            workerThreads.add(worker)
            executorService.execute(worker)
        }
    }

    fun getPartitionSize(topic: String): Int {
        logger.info("Get {} partition size", topic)
        var partitions: Int
        val adminConfigs = Properties()
        adminConfigs[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = BOOTSTRAP_SERVERS
        val admin = AdminClient.create(adminConfigs)
        try {
            val result = admin.describeTopics(Arrays.asList(topic))
            val values = result.values()
            val topicDescription = values[topic]!!
            partitions = topicDescription.get().partitions().size
        } catch (e: java.lang.Exception) {
            logger.error(e.message, e)
            partitions = defaultPartitionSize
        }
        admin.close()
        return partitions
    }

    val defaultPartitionSize: Int
        get() {
            logger.info("getDefaultPartitionSize")
            var partitions = 1
            val adminConfigs = Properties()
            adminConfigs[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = BOOTSTRAP_SERVERS
            val admin = AdminClient.create(adminConfigs)
            try {
                for (node in admin.describeCluster().nodes().get()) {
                    val cr = ConfigResource(ConfigResource.Type.BROKER, "0")
                    val describeConfigs = admin.describeConfigs(setOf(cr))
                    val cf = describeConfigs.all().get()[cr]
                    val optionalConfigEntry = cf!!.entries().stream()
                        .filter { v: ConfigEntry -> v.name() == "num.partitions" }.findFirst()
                    val numPartitionConfig = optionalConfigEntry.orElseThrow { Exception() }
                    partitions = Integer.getInteger(numPartitionConfig.value())
                }
            } catch (e: java.lang.Exception) {
                logger.error(e.message, e)
            }
            admin.close()
            return partitions
        }

    internal class ShutdownThread : Thread() {
        override fun run() {
            workerThreads.forEach(MultiConsumerThreadByPartitionWorker::shutdown)
            println("Bye")
        }
    }
}

class MultiConsumerThreadByPartitionWorker internal constructor(private val prop: Properties, private val topic: String, number: Int) :
    Runnable {
    private val threadName = "consumer-thread-$number"
    private var consumer: KafkaConsumer<String, String>? = null

    override fun run() {
        consumer = KafkaConsumer(prop)
        consumer!!.subscribe(Arrays.asList(topic))
        try {
            while (true) {
                val records = consumer!!.poll(Duration.ofSeconds(1))
                for (record in records) {
                    logger.info("{}", record)
                }
                consumer!!.commitSync()
            }
        } catch (e: WakeupException) {
            println("$threadName trigger WakeupException")
        } finally {
            consumer!!.commitSync()
            consumer!!.close()
        }
    }

    fun shutdown() {
        consumer!!.wakeup()
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(MultiConsumerThreadByPartitionWorker::class.java)
    }
}

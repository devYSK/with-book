package com.yscorp.example1.admin

import org.apache.kafka.clients.admin.*
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.config.ConfigResource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.function.Consumer


object KafkaAdminClient {
    private val logger: Logger = LoggerFactory.getLogger(KafkaAdminClient::class.java)
    private const val BOOTSTRAP_SERVERS = "localhost:9092"

    @Throws(java.lang.Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val configs = Properties()
        configs[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = BOOTSTRAP_SERVERS
        val admin = AdminClient.create(configs)

        logger.info("== Get broker information")

        for (node in admin.describeCluster().nodes().get()) {
            logger.info("node : {}", node)
            val cr = ConfigResource(ConfigResource.Type.BROKER, node.idString())
            val describeConfigs = admin.describeConfigs(setOf(cr))
            describeConfigs.all().get().forEach { (broker: ConfigResource?, config: Config) ->
                config.entries().forEach(
                    Consumer { configEntry: ConfigEntry ->
                        logger.info(
                            configEntry.name() + "= " + configEntry.value()
                        )
                    })
            }
        }

        logger.info("== Get default num.partitions")
        for (node in admin.describeCluster().nodes().get()) {
            val cr = ConfigResource(ConfigResource.Type.BROKER, node.idString())
            val describeConfigs = admin.describeConfigs(setOf(cr))
            val config = describeConfigs.all().get()[cr]
            val optionalConfigEntry =
                config!!.entries().stream().filter { v: ConfigEntry -> v.name() == "num.partitions" }
                    .findFirst()
            val numPartitionConfig = optionalConfigEntry.orElseThrow { Exception() }
            logger.info("{}", numPartitionConfig.value())
        }

        logger.info("== Topic list")
        for (topicListing in admin.listTopics().listings().get()) {
            logger.info("{}", topicListing.toString())
        }

        logger.info("== test topic information")
        val topicInformation = admin.describeTopics(listOf("test")).all().get()
        logger.info("{}", topicInformation)

        logger.info("== Consumer group list")
        val listConsumerGroups = admin.listConsumerGroups()
        listConsumerGroups.all().get().forEach(Consumer { v: ConsumerGroupListing? ->
            logger.info("{}", v)
        })

        admin.close()
    }
}
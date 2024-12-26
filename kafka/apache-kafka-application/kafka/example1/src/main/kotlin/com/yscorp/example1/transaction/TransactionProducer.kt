package com.yscorp.example1.transaction

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

object TransactionProducer {
    private val logger: Logger = LoggerFactory.getLogger(TransactionProducer::class.java)
    private const val TOPIC_NAME = "test"
    private const val BOOTSTRAP_SERVERS = "my-kafka:9092"

    @JvmStatic
    fun main(args: Array<String>) {
        val configs = Properties()
        configs[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = BOOTSTRAP_SERVERS
        configs[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
        configs[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
        configs[ProducerConfig.TRANSACTIONAL_ID_CONFIG] = "my-transaction-id"

        val producer = KafkaProducer<String, String>(configs)
        producer.initTransactions()
        producer.beginTransaction()
        val messageValue = "testMessage"
        val record = ProducerRecord<String, String>(TOPIC_NAME, messageValue)
        producer.send(record)
        logger.info("{}", record)
        producer.flush()


        producer.commitTransaction()

        //producer.abortTransaction();
        producer.close()
    }
}
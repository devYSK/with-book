package com.yscorp.example1.producer

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*

object ProducerWithCustomPartitioner {
    private const val TOPIC_NAME = "test"
    private const val BOOTSTRAP_SERVERS = "localhost:9092"

    @JvmStatic
    fun main(args: Array<String>) {
        val configs = Properties()
        configs[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = BOOTSTRAP_SERVERS
        configs[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
        configs[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
        configs[ProducerConfig.PARTITIONER_CLASS_CONFIG] = CustomPartitioner::class.java

        val producer = KafkaProducer<String, String>(configs)

        val record = ProducerRecord(TOPIC_NAME, "Pangyo", "Pangyo")
        val send = producer.send(record).get()

        producer.flush()
        producer.close()
        println(send.offset())
        println(send.topic())
        println(send.partition())
        println(send.timestamp())
    }
}

package com.yscorp.springkafka

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.PartitionOffset
import org.springframework.kafka.annotation.TopicPartition
import org.springframework.stereotype.Component

@Component
class SimpleConsumer {

    private val logger = org.slf4j.LoggerFactory.getLogger(SimpleConsumer::class.java)

    @KafkaListener(topics = ["test"], groupId = "test-group-00")
    fun recordListener(record: ConsumerRecord<String?, String?>) {
        logger.info(record.toString())
    }

    @KafkaListener(topics = ["test"], groupId = "test-group-01")
    fun singleTopicListener(messageValue: String?) {
        logger.info(messageValue)
    }

    @KafkaListener(
        topics = ["test"],
        groupId = "test-group-02",
        properties = ["max.poll.interval.ms:60000", "auto.offset.reset:earliest"
        ]
    )
    fun singleTopicWithPropertiesListener(messageValue: String?) {
        logger.info(messageValue)
    }

    @KafkaListener(topics = ["test"], groupId = "test-group-03", concurrency = "3")
    fun concurrentTopicListener(messageValue: String?) {
        logger.info(messageValue)
    }

    @KafkaListener(
        topicPartitions = [TopicPartition(
            topic = "test01",
            partitions = ["0", "1"]
        ), TopicPartition(
            topic = "test02",
            partitionOffsets = [PartitionOffset(partition = "0", initialOffset = "3")]
        )],
        groupId = "test-group-04"
    )
    fun listenSpecificPartition(record: ConsumerRecord<String?, String?>) {
        logger.info(record.toString())
    }

}
package com.yscorp.springkafka

import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.KafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import org.springframework.kafka.listener.ConsumerAwareRebalanceListener
import org.springframework.kafka.listener.ContainerProperties

@Configuration
class CustomListenerContainerConfiguration {
    @Bean
    fun customContainerFactory(): KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> {
        val props: MutableMap<String, Any> = HashMap()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "my-kafka:9092"
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java

        val cf =  DefaultKafkaConsumerFactory<Any, Any>(props)

        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.containerProperties.setConsumerRebalanceListener(object : ConsumerAwareRebalanceListener {
            override fun onPartitionsRevokedBeforeCommit(
                consumer: Consumer<*, *>,
                partitions: Collection<TopicPartition>
            ) {
            }

            override fun onPartitionsRevokedAfterCommit(
                consumer: Consumer<*, *>,
                partitions: Collection<TopicPartition>
            ) {
            }

            override fun onPartitionsAssigned(partitions: Collection<TopicPartition>) {
            }

            override fun onPartitionsLost(partitions: Collection<TopicPartition>) {
            }
        })
        factory.isBatchListener = false
        factory.containerProperties.ackMode = ContainerProperties.AckMode.RECORD
        factory.consumerFactory = cf
        return factory
    }
}
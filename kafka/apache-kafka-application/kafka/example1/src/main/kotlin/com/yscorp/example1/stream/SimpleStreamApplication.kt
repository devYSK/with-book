package com.yscorp.example1.stream

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import java.util.*

object SimpleStreamApplication {
    private const val APPLICATION_NAME = "streams-application"
    private const val BOOTSTRAP_SERVERS = "localhost:9092"
    private const val STREAM_LOG = "stream_log"
    private const val STREAM_LOG_COPY = "stream_log_copy"

    @JvmStatic
    fun main(args: Array<String>) {
        val props = Properties()
        props[StreamsConfig.APPLICATION_ID_CONFIG] = APPLICATION_NAME
        props[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = BOOTSTRAP_SERVERS
        props[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.String().javaClass
        props[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = Serdes.String().javaClass

        val builder = StreamsBuilder()
        val stream = builder.stream<String, String>(STREAM_LOG)

        stream.to(STREAM_LOG_COPY)

        val streams = KafkaStreams(builder.build(), props)
        streams.start()
    }
}
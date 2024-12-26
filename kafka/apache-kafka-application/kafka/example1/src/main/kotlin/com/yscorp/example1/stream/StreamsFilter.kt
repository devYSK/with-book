package com.yscorp.example1.stream

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import java.util.*

object StreamsFilter {
    private const val APPLICATION_NAME = "streams-filter-application"
    private const val BOOTSTRAP_SERVERS = "my-kafka:9092"
    private const val STREAM_LOG = "stream_log"
    private const val STREAM_LOG_FILTER = "stream_log_filter"

    @JvmStatic
    fun main(args: Array<String>) {
        val props = Properties()
        props[StreamsConfig.APPLICATION_ID_CONFIG] = APPLICATION_NAME
        props[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = BOOTSTRAP_SERVERS
        props[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = Serdes.String().javaClass
        props[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = Serdes.String().javaClass

        val builder = StreamsBuilder()
        val streamLog = builder.stream<String, String>(STREAM_LOG)

        //        KStream<String, String> filteredStream = streamLog.filter(
//                (key, value) -> value.length() > 5);
//        filteredStream.to(STREAM_LOG_FILTER);
        streamLog.filter { _: String?, value: String -> value.length > 5 }.to(STREAM_LOG_FILTER)
        val streams = KafkaStreams(builder.build(), props)
        streams.start()
    }
}
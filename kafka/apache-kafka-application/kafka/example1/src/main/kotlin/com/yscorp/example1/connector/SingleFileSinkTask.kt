package com.yscorp.example1.connector

import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.connect.errors.ConnectException
import org.apache.kafka.connect.sink.SinkRecord
import org.apache.kafka.connect.sink.SinkTask
import java.io.File
import java.io.FileWriter
import java.io.IOException


class SingleFileSinkTask : SinkTask() {
    private var config: SingleFileSinkConnectorConfig? = null
    private var file: File? = null
    private var fileWriter: FileWriter? = null

    override fun version(): String {
        return "1.0"
    }

    override fun start(props: Map<String, String>) {
        try {
            config = SingleFileSinkConnectorConfig(props)
            file = File(config!!.getString(SingleFileSinkConnectorConfig.DIR_FILE_NAME))
            fileWriter = FileWriter(file, true)
        } catch (e: Exception) {
            throw ConnectException(e.message, e)
        }
    }

    override fun put(records: Collection<SinkRecord>) {
        try {
            for (record in records) {
                fileWriter!!.write(record.value().toString() + "\n")
            }
        } catch (e: IOException) {
            throw ConnectException(e.message, e)
        }
    }

    override fun flush(offsets: Map<TopicPartition, OffsetAndMetadata>) {
        try {
            fileWriter!!.flush()
        } catch (e: IOException) {
            throw ConnectException(e.message, e)
        }
    }

    override fun stop() {
        try {
            fileWriter!!.close()
        } catch (e: IOException) {
            throw ConnectException(e.message, e)
        }
    }
}
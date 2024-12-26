package com.yscorp.example1.connector

import org.apache.kafka.common.config.ConfigDef
import org.apache.kafka.common.config.ConfigException
import org.apache.kafka.connect.connector.Task
import org.apache.kafka.connect.errors.ConnectException
import org.apache.kafka.connect.source.SourceConnector
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class SingleFileSourceConnector : SourceConnector() {
    private val logger: Logger = LoggerFactory.getLogger(SingleFileSourceConnector::class.java)

    private var configProperties: Map<String, String>? = null

    override fun version(): String {
        return "1.0"
    }

    override fun start(props: Map<String, String>) {
        this.configProperties = props
        try {
            SingleFileSourceConnectorConfig(props)
        } catch (e: ConfigException) {
            throw ConnectException(e.message, e)
        }
    }

    override fun taskClass(): Class<out Task?> {
        return SingleFileSourceTask::class.java
    }

    override fun taskConfigs(maxTasks: Int): List<Map<String, String>> {
        val taskConfigs: MutableList<Map<String, String>> = ArrayList()
        val taskProps: MutableMap<String, String> = HashMap()
        taskProps.putAll(configProperties!!)
        for (i in 0 until maxTasks) {
            taskConfigs.add(taskProps)
        }
        return taskConfigs
    }

    override fun config(): ConfigDef {
        return SingleFileSourceConnectorConfig.CONFIG
    }

    override fun stop() {
    }
}

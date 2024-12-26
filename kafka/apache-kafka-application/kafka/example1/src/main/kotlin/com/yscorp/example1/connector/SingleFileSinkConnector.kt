package com.yscorp.example1.connector

import org.apache.kafka.common.config.ConfigDef
import org.apache.kafka.common.config.ConfigException
import org.apache.kafka.connect.connector.Task
import org.apache.kafka.connect.errors.ConnectException
import org.apache.kafka.connect.sink.SinkConnector


class SingleFileSinkConnector : SinkConnector() {
    private lateinit var configProperties: Map<String, String>

    override fun version(): String {
        return "1.0"
    }

    override fun start(props: Map<String, String>) {
        this.configProperties = props
        try {
            SingleFileSinkConnectorConfig(props)
        } catch (e: ConfigException) {
            throw ConnectException(e.message, e)
        }
    }

    override fun taskClass(): Class<out Task?> {
        return SingleFileSinkTask::class.java
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
        return SingleFileSinkConnectorConfig.CONFIG
    }

    override fun stop() {
    }
}
package com.yscorp.example1.connector

import org.apache.kafka.common.config.AbstractConfig
import org.apache.kafka.common.config.ConfigDef
import org.apache.kafka.common.config.ConfigDef.Importance

class SingleFileSourceConnectorConfig(props: Map<String, String>) : AbstractConfig(CONFIG, props)
{

    companion object {
        const val DIR_FILE_NAME: String = "file"
        private const val DIR_FILE_NAME_DEFAULT_VALUE = "/tmp/kafka.txt"
        private const val DIR_FILE_NAME_DOC = "읽을 파일 경로와 이름"

        const val TOPIC_NAME: String = "topic"
        private const val TOPIC_DEFAULT_VALUE = "test"
        private const val TOPIC_DOC = "보낼 토픽명"

        var CONFIG: ConfigDef = ConfigDef()
            .define(
                DIR_FILE_NAME,
                ConfigDef.Type.STRING,
                DIR_FILE_NAME_DEFAULT_VALUE,
                Importance.HIGH,
                DIR_FILE_NAME_DOC
            )
            .define(
                TOPIC_NAME,
                ConfigDef.Type.STRING,
                TOPIC_DEFAULT_VALUE,
                Importance.HIGH,
                TOPIC_DOC
            )
    }
}

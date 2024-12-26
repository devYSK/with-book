package com.yscorp.example1.connector

import org.apache.kafka.common.config.AbstractConfig
import org.apache.kafka.common.config.ConfigDef
import org.apache.kafka.common.config.ConfigDef.Importance


class SingleFileSinkConnectorConfig(props: Map<String, String>) :
    AbstractConfig(CONFIG, props) {

    companion object {
        const val DIR_FILE_NAME: String = "file"
        private const val DIR_FILE_NAME_DEFAULT_VALUE = "/tmp/kafka.txt"
        private const val DIR_FILE_NAME_DOC = "저장할 디렉토리와 파일 이름"

        var CONFIG: ConfigDef = ConfigDef().define(
            DIR_FILE_NAME,
            ConfigDef.Type.STRING,
            DIR_FILE_NAME_DEFAULT_VALUE,
            Importance.HIGH,
            DIR_FILE_NAME_DOC
        )
    }
}

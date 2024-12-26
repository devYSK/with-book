package com.yscorp.webeventes

import org.apache.kafka.common.config.AbstractConfig
import org.apache.kafka.common.config.ConfigDef


class ElasticSearchSinkConnectorConfig(props: Map<String, String>) :
    AbstractConfig(CONFIG, props) {
    companion object {
        const val ES_CLUSTER_HOST: String = "es.host"
        private const val ES_CLUSTER_HOST_DEFAULT_VALUE = "localhost"
        private const val ES_CLUSTER_HOST_DOC = "엘라스틱서치 호스트를 입력"

        const val ES_CLUSTER_PORT: String = "es.port"
        private const val ES_CLUSTER_PORT_DEFAULT_VALUE = "9200"
        private const val ES_CLUSTER_PORT_DOC = "엘라스틱서치 포트를 입력"

        const val ES_INDEX: String = "es.index"
        private const val ES_INDEX_DEFAULT_VALUE = "kafka-connector-index"
        private const val ES_INDEX_DOC = "엘라스틱서치 인덱스를 입력"

        var CONFIG: ConfigDef = ConfigDef().define(
            ES_CLUSTER_HOST,
            ConfigDef.Type.STRING,
            ES_CLUSTER_HOST_DEFAULT_VALUE,
            ConfigDef.Importance.HIGH,
            ES_CLUSTER_HOST_DOC
        )
            .define(
                ES_CLUSTER_PORT,
                ConfigDef.Type.INT,
                ES_CLUSTER_PORT_DEFAULT_VALUE,
                ConfigDef.Importance.HIGH,
                ES_CLUSTER_PORT_DOC
            )
            .define(ES_INDEX, ConfigDef.Type.STRING, ES_INDEX_DEFAULT_VALUE, ConfigDef.Importance.HIGH, ES_INDEX_DOC)
    }
}

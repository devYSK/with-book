package com.yscorp.webeventes

import com.google.gson.Gson
import org.apache.http.HttpHost
import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.config.ConfigException
import org.apache.kafka.connect.errors.ConnectException
import org.apache.kafka.connect.sink.SinkRecord
import org.apache.kafka.connect.sink.SinkTask
import org.elasticsearch.action.ActionListener
import org.elasticsearch.action.bulk.BulkRequest
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.xcontent.XContentType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException

class ElasticSearchSinkTask : SinkTask() {
    private val logger: Logger = LoggerFactory.getLogger(ElasticSearchSinkTask::class.java)


    private lateinit var config: ElasticSearchSinkConnectorConfig
    private lateinit var esClient: RestHighLevelClient

    override fun version(): String {
        return "1.0"
    }

    override fun start(props: Map<String, String>) {
        try {
            config = ElasticSearchSinkConnectorConfig(props)
        } catch (e: ConfigException) {
            throw ConnectException(e.message, e)
        }

        esClient = RestHighLevelClient(
            RestClient.builder(
                HttpHost(
                    config.getString(ElasticSearchSinkConnectorConfig.ES_CLUSTER_HOST),
                    config.getInt(ElasticSearchSinkConnectorConfig.ES_CLUSTER_PORT)
                )
            )
        )
    }

    override fun put(records: Collection<SinkRecord>) {
        if (records.isNotEmpty()) {
            val bulkRequest = BulkRequest()
            for (record in records) {
                val gson = Gson()
                val map = gson.fromJson<Map<*, *>>(
                    record.value().toString(),
                    MutableMap::class.java
                )
                bulkRequest.add(
                    IndexRequest(config.getString(ElasticSearchSinkConnectorConfig.ES_INDEX))
                        .source(map, XContentType.JSON)
                )
                logger.info("record : {}", record.value())
            }

            esClient!!.bulkAsync(bulkRequest, RequestOptions.DEFAULT, object : ActionListener<BulkResponse> {
                override fun onResponse(bulkResponse: BulkResponse) {
                    if (bulkResponse.hasFailures()) {
                        logger.error(bulkResponse.buildFailureMessage())
                    } else {
                        logger.info("bulk save success")
                    }
                }

                override fun onFailure(e: Exception) {
                    logger.error(e.message, e)
                }
            })
        }
    }

    override fun flush(offsets: Map<TopicPartition, OffsetAndMetadata>) {
        logger.info("flush")
    }

    override fun stop() {
        try {
            esClient!!.close()
        } catch (e: IOException) {
            logger.info(e.message, e)
        }
    }
}

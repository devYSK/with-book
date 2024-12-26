package com.yscorp.example1.producer

import org.apache.kafka.clients.producer.Partitioner
import org.apache.kafka.common.Cluster
import org.apache.kafka.common.InvalidRecordException
import org.apache.kafka.common.utils.Utils

class CustomPartitioner : Partitioner {

    override fun partition(
        topic: String, key: Any, keyBytes: ByteArray, value: Any, valueBytes: ByteArray,
        cluster: Cluster
    ): Int {
        if (keyBytes == null) {
            throw InvalidRecordException("Need message key")
        }

        if ((key as String) == "Pangyo") return 0

        val partitions = cluster.partitionsForTopic(topic)
        val numPartitions = partitions.size
        return Utils.toPositive(Utils.murmur2(keyBytes)) % numPartitions
    }

    override fun configure(configs: Map<String?, *>?) {}

    override fun close() {}
}

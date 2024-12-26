package com.yscorp.example1.producer

import org.apache.kafka.clients.producer.Callback
import org.apache.kafka.clients.producer.RecordMetadata
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ProducerCallback : Callback {
    override fun onCompletion(recordMetadata: RecordMetadata, e: Exception?) {

        if (e != null) logger.error(e.message, e)

        else logger.info(recordMetadata.toString())
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ProducerCallback::class.java)
    }
}
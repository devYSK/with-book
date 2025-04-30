package com.yscorp.msapattern.ch06

import com.rabbitmq.client.ConnectionFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory


/**
 * @author [Binildas C. A.](mailto:biniljava<[@>.]yahoo.co.in)
 */
object Send {
    private val LOGGER: Logger = LoggerFactory.getLogger(Send::class.java)
    private const val QUEUE_NAME = "hello"

    @Throws(Exception::class)
    @JvmStatic
    fun main(argv: Array<String>) {
        LOGGER.info("Start")
        val factory = ConnectionFactory()
        factory.host = "localhost"
        val connection = factory.newConnection()
        val channel = connection.createChannel()

        channel.queueDeclare(QUEUE_NAME, false, false, false, null)
        val message = "Hello World!"
        channel.basicPublish("", QUEUE_NAME, null, message.toByteArray(charset("UTF-8")))
        LOGGER.debug(" [!] Sent '$message'")

        channel.close()
        connection.close()
        LOGGER.info("End")
    }

}
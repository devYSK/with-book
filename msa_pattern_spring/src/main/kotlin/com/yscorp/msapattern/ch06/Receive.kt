package com.yscorp.msapattern.ch06

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException

/**
 * @author [Binildas C. A.](mailto:biniljava<[@>.]yahoo.co.in)
 */
object Receive {
    private val LOGGER: Logger = LoggerFactory.getLogger(Receive::class.java)
    private const val QUEUE_NAME = "hello"

    @Throws(Exception::class)
    @JvmStatic
    fun main(argv: Array<String>) {
        LOGGER.info("Start")
        val factory: ConnectionFactory = ConnectionFactory()
        factory.host = "localhost"
        val connection = factory.newConnection()
        val channel = connection.createChannel()
        channel.queueDeclare(QUEUE_NAME, false, false, false, null)
        LOGGER.debug(" [!] Waiting for messages. To exit press CTRL+C")

        val consumer = object : DefaultConsumer(channel) {
            override fun handleDelivery(
                consumerTag: String?,
                envelope: Envelope?,
                properties: AMQP.BasicProperties?,
                body: ByteArray?,
            ) {
                val message = String(body!!, charset("UTF-8"))
                LOGGER.debug(" [x] Received '$message'")
            }
        }
        channel.basicConsume(QUEUE_NAME, true, consumer)
        LOGGER.info("End")
    }
}
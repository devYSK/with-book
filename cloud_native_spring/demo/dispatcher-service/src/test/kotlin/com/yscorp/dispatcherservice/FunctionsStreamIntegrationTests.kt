package com.yscorp.dispatcherservice

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.integration.support.MessageBuilder
import org.springframework.messaging.Message
import java.io.IOException
import org.springframework.cloud.stream.binder.test.InputDestination
import org.springframework.cloud.stream.binder.test.OutputDestination
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration


@SpringBootTest
@Import(TestChannelBinderConfiguration::class)
internal class FunctionsStreamIntegrationTests {
    @Autowired
    lateinit var input: InputDestination

    @Autowired
    lateinit var output: OutputDestination

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    @Throws(IOException::class)
    fun whenOrderAcceptedThenDispatched() {
        val orderId: Long = 121
        val inputMessage = MessageBuilder
            .withPayload<Any>(OrderAcceptedMessage(orderId)).build()
        val expectedOutputMessage = MessageBuilder
            .withPayload<Any>(OrderDispatchedMessage(orderId)).build()

        input.send(inputMessage)
        assertThat(objectMapper.readValue(output.receive().getPayload(), OrderDispatchedMessage::class.java))
            .isEqualTo(expectedOutputMessage.payload)
    }
}

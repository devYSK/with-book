package com.yscorp.webeventproducer

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.util.concurrent.ListenableFuture
import org.springframework.util.concurrent.ListenableFutureCallback
import org.springframework.web.bind.annotation.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CompletableFuture

@RestController
@CrossOrigin(origins = ["*"], allowedHeaders = ["*"])
class ProduceController(
    private val objectMapper: ObjectMapper,
    private val kafkaTemplate: KafkaTemplate<String, String>) {
    private val logger: Logger = LoggerFactory.getLogger(ProduceController::class.java)

    @GetMapping("/api/select")
    fun selectColor(
        @RequestHeader("user-agent") userAgentName: String,
        @RequestParam(value = "color") colorName: String,
        @RequestParam(value = "user") userName: String
    ) {
        val sdfDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ")
        val now = Date()
        val userEventVO = UserEventVO(
            timestamp = sdfDate.format(now),
            userAgent = userAgentName,
            colorName = colorName,
            userName = userName
        )

        val jsonColorLog = objectMapper.writeValueAsString(userEventVO)

        kafkaTemplate.send("select-color", jsonColorLog)
            .whenComplete { result, ex ->
                if (ex != null) {
                    println("Message failed: ${ex.message}")
                } else {
                    println("Message sent successfully to ${result?.recordMetadata?.topic()} at offset ${result?.recordMetadata?.offset()}")
                }
            }

    }

}

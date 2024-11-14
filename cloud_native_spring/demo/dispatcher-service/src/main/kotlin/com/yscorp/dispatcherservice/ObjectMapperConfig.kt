package com.yscorp.dispatcherservice

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ObjectMapperConfig {

    @Bean
    fun objectMapper(): ObjectMapper {
        return objectMapper
    }

}

fun ObjectMapper.pretty(data: Any?): String {
    if (data == null) {
        return ""
    }

    return this.writerWithDefaultPrettyPrinter().writeValueAsString(data)
}

val objectMapper: ObjectMapper = init()

private fun init(): ObjectMapper {
    val objectMapper = ObjectMapper()

    objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) // 모르는 프로퍼티 무시

    objectMapper.registerModules(
        KotlinModule.Builder()
            .withReflectionCacheSize(512)
            .configure(KotlinFeature.NullToEmptyCollection, false)
            .configure(KotlinFeature.NullToEmptyMap, false)
            .configure(KotlinFeature.NullIsSameAsDefault, false)
            .configure(KotlinFeature.SingletonSupport, true)
            .configure(KotlinFeature.StrictNullChecks, false)
            .build()
    )

    objectMapper.registerModule(JavaTimeModule())

    return objectMapper
}

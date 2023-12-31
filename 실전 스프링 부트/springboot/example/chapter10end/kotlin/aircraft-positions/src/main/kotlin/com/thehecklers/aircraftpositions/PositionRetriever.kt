package com.thehecklers.aircraftpositions

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlux

@Component
class PositionRetriever(private val repository: AircraftRepository, private val client: WebClient) {
    fun retrieveAircraftPositions(endpoint: String?): Iterable<Aircraft> {
        repository.deleteAll()

        client.get()
            .uri(if (!endpoint.isNullOrEmpty()) endpoint else "")
            .retrieve()
            .bodyToFlux<Aircraft>()
            .filter { !it.reg.isNullOrEmpty() }
            .toStream()
            .forEach { repository.save(it) }

        return repository.findAll()
    }
}

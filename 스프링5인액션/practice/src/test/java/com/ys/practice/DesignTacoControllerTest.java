package com.ys.practice;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ys.practice.domain.Ingredient;
import com.ys.practice.domain.Taco;
import com.ys.practice.webflux.DesignTacoController;
import com.ys.practice.webflux.TacoRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class DesignTacoControllerTest {

  @Test
  void shouldReturnRecentTacos() throws JsonProcessingException {
    Taco[] tacos = {
        testTaco(1L), testTaco(2L),
        testTaco(3L), testTaco(4L),
        testTaco(5L), testTaco(6L),
        testTaco(7L), testTaco(8L),
        testTaco(9L), testTaco(10L),
        testTaco(11L), testTaco(12L),
        testTaco(13L), testTaco(14L),
        testTaco(15L), testTaco(16L)};

    Flux<Taco> tacoFlux = Flux.just(tacos);

    TacoRepository tacoRepo = Mockito.mock(TacoRepository.class);
    when(tacoRepo.findAll()).thenReturn(tacoFlux);

    WebTestClient testClient = WebTestClient.bindToController(
        new DesignTacoController(tacoRepo))
        .build();

    var result = testClient.get().uri("/design/recent")
      .exchange()
      .expectStatus().isOk()
      .expectBody()
        .jsonPath("$").isArray()
        .jsonPath("$").isNotEmpty()
        .jsonPath("$[0].id").isEqualTo(tacos[0].getId().toString())
        .jsonPath("$[0].name").isEqualTo("Taco 1")
        .jsonPath("$[1].id").isEqualTo(tacos[1].getId().toString())
        .jsonPath("$[1].name").isEqualTo("Taco 2")
        .jsonPath("$[11].id").isEqualTo(tacos[11].getId().toString())
        .jsonPath("$[11].name").isEqualTo("Taco 12")
        .jsonPath("$[12]").doesNotExist()
						   .returnResult()
    ;

	  ObjectMapper objectMapper = new ObjectMapper();
	  objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
	  String responseBodyAsString = new String(result.getResponseBody(), StandardCharsets.UTF_8);
	  Object json = objectMapper.readValue(responseBodyAsString, Object.class);
	  System.out.println(objectMapper.writeValueAsString(json));
  }

  @Test
  public void shouldSaveATaco() {
    TacoRepository tacoRepo = Mockito.mock(
                TacoRepository.class);
    Mono<Taco> unsavedTacoMono = Mono.just(testTaco(null));
    Taco savedTaco = testTaco(null);
    Mono<Taco> savedTacoMono = Mono.just(savedTaco);

    when(tacoRepo.save(any())).thenReturn(savedTacoMono);

    WebTestClient testClient = WebTestClient.bindToController(
        new DesignTacoController(tacoRepo)).build();

    testClient.post()
        .uri("/design")
        .contentType(MediaType.APPLICATION_JSON)
        .body(unsavedTacoMono, Taco.class)
      .exchange()
      .expectStatus().isCreated()
      .expectBody(Taco.class)
        .isEqualTo(savedTaco);
  }

  private Taco testTaco(Long number) {
    Taco taco = new Taco();
    taco.setId(number != null ? number.toString(): "TESTID");
    taco.setName("Taco " + number);
    List<Ingredient> ingredients = new ArrayList<>();
    ingredients.add(
        new Ingredient("INGA", "Ingredient A", Ingredient.Type.WRAP));
    ingredients.add(
        new Ingredient("INGB", "Ingredient B", Ingredient.Type.PROTEIN));
    taco.setIngredients(ingredients);
    return taco;
  }

}

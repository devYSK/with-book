package com.ys.practice;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class DesignTacoControllerWebTest {

  @Autowired
  private WebTestClient testClient;

  @Test
  void shouldReturnRecentTacos() throws IOException {
    testClient.get().uri("/design/recent")
              .accept(MediaType.APPLICATION_JSON).exchange()
              .expectStatus().isOk()
              .expectBody()
              .jsonPath("$[?(@.id == 'TACO1')].name")
              .isEqualTo("Carnivore")
              .jsonPath("$[?(@.id == 'TACO2')].name")
              .isEqualTo("Bovine Bounty")
              .jsonPath("$[?(@.id == 'TACO3')].name")
              .isEqualTo("Veg-Out");
  }
  @DisplayName("")
  @Test
  void ff() {
      //given
    final var build = WebClient.builder()
                               .build();
    
    //when

      //then
  }
}
package com.yscorp.edgesevice

import com.yscorp.edgesevice.config.SecurityConfig
import com.yscorp.edgesevice.user.User
import com.yscorp.edgesevice.user.UserController
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.core.oidc.StandardClaimNames
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.OidcLoginMutator
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest(UserController::class)
@Import(SecurityConfig::class)
internal class UserControllerTests {

    @Autowired
    lateinit var webClient: WebTestClient

    @MockBean
    var clientRegistrationRepository: ReactiveClientRegistrationRepository? = null

    @Test
    fun whenNotAuthenticatedThen401() {
        webClient
            .get()
            .uri("/user")
            .exchange()
            .expectStatus().isUnauthorized()
    }

    @Test
    fun whenAuthenticatedThenReturnUser() {
        val expectedUser: User = User("jon.snow", "Jon", "Snow", listOf("employee", "customer"))

        webClient
            .mutateWith(configureMockOidcLogin(expectedUser))
            .get()
            .uri("/user")
            .exchange()
            .expectStatus().is2xxSuccessful()
            .expectBody(User::class.java)
            .value { user -> assertThat(user).isEqualTo(expectedUser) }
    }

    private fun configureMockOidcLogin(expectedUser: User): OidcLoginMutator {
        return SecurityMockServerConfigurers.mockOidcLogin().idToken { builder: OidcIdToken.Builder ->
            builder.claim(StandardClaimNames.PREFERRED_USERNAME, expectedUser.username)
            builder.claim(StandardClaimNames.GIVEN_NAME, expectedUser.firstName)
            builder.claim(StandardClaimNames.FAMILY_NAME, expectedUser.lastName)
            builder.claim("roles", expectedUser.roles)
        }
    }
}

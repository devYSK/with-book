package com.yscorp.edgesevice.user

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class UserController {

    @GetMapping("user")
    fun getUser(@AuthenticationPrincipal oidcUser: OidcUser): Mono<User> {
        log.info("Fetching information about the currently authenticated user")
        val user = User(
            oidcUser.preferredUsername,
            oidcUser.givenName,
            oidcUser.familyName,
            oidcUser.getClaimAsStringList("roles")
        )
        return Mono.just(user)
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(UserController::class.java)
    }

}

package com.mvnh.plugins

import com.auth0.jwt.JWT
import com.mvnh.utils.JWTConfig.AUDIENCE
import com.mvnh.utils.JWTConfig.ISSUER
import com.mvnh.utils.JWTConfig.REALM
import com.mvnh.utils.JWTConfig.algorithm
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respond

fun Application.configureSecurity() {
    authentication {
        jwt("auth-jwt") {
            realm = REALM
            verifier(
                JWT
                    .require(algorithm)
                    .withAudience(AUDIENCE)
                    .withIssuer(ISSUER)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(AUDIENCE)) JWTPrincipal(credential.payload) else null
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or expired")
            }
        }
    }
}

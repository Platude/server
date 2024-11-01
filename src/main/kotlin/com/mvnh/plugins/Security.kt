package com.mvnh.plugins

import com.auth0.jwt.JWT
import com.mvnh.auth.utils.JwtConfig.AUDIENCE
import com.mvnh.auth.utils.JwtConfig.ISSUER
import com.mvnh.auth.utils.JwtConfig.REALM
import com.mvnh.auth.utils.JwtConfig.algorithm
import com.mvnh.dto.BasicApiResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

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
                if (credential.payload.audience.contains(AUDIENCE) &&
                    credential.payload.issuer == ISSUER &&
                    credential.payload.expiresAt.time > System.currentTimeMillis()) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { _, _ ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    BasicApiResponse(
                        success = false,
                        message = "Token is invalid or expired",
                        data = null
                    )
                )
            }
        }
    }
}

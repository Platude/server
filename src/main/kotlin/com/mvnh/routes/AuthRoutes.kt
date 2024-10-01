package com.mvnh.routes

import com.mvnh.database.repositories.AuthRepository
import com.mvnh.dto.AccountCredentials
import com.mvnh.dto.ApiResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.authRoutes(repository: AuthRepository) {
    route("/auth") {
        post("/register") {
            val credentials = call.receive<AccountCredentials>()

            try {
                repository.register(credentials)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest,
                    ApiResponse(
                        success = false,
                        message = e.message
                    )
                )
            }

            call.respond(HttpStatusCode.Created, ApiResponse(success = true))
        }

        post("/login") {
            val credentials = call.receive<AccountCredentials>()

            try {
                val tokens = repository.login(credentials)
                call.respond(HttpStatusCode.OK, tokens)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest,
                    ApiResponse(
                        success = false,
                        message = e.message
                    )
                )
            }
        }

        post("/refresh") {
            val request = call.receive<Map<String, String>>()

            try {
                val oldRefreshToken = request["refreshToken"] ?: throw IllegalArgumentException("Refresh token not provided")
                val tokens = repository.refresh(oldRefreshToken)
                call.respond(HttpStatusCode.OK, tokens)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest,
                    ApiResponse(
                        success = false,
                        message = e.message
                    )
                )
            }
        }
    }
}
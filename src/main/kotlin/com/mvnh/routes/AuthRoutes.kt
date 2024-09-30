package com.mvnh.routes

import com.mvnh.db.repositories.AuthRepository
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
    }
}
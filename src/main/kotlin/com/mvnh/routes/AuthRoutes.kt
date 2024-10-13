package com.mvnh.routes

import com.mvnh.database.repositories.AuthRepository
import com.mvnh.dto.UserCredentials
import com.mvnh.dto.BasicApiResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.authRoutes(repository: AuthRepository) {
    route("/auth") {
        post("/register") {
            val credentials = call.receive<UserCredentials>()
            handleRequest(call) {
                repository.register(credentials)
                call.respond(HttpStatusCode.Created, BasicApiResponse(success = true))
            }
        }

        post("/login") {
            val credentials = call.receive<UserCredentials>()
            handleRequest(call) {
                val tokens = repository.login(credentials)
                call.respond(HttpStatusCode.OK, tokens)
            }
        }

        post("/refresh") {
            val request = call.receive<Map<String, String>>()
            handleRequest(call) {
                val oldRefreshToken = request["refreshToken"] ?: throw IllegalArgumentException("Refresh token not provided")
                val tokens = repository.refresh(oldRefreshToken)
                call.respond(HttpStatusCode.OK, tokens)
            }
        }

        post("/logout") {
            val request = call.receive<Map<String, String>>()
            handleRequest(call) {
                val refreshToken = request["refreshToken"] ?: throw IllegalArgumentException("Refresh token not provided")
                repository.logout(refreshToken)
                call.respond(HttpStatusCode.OK, BasicApiResponse(success = true))
            }
        }
    }
}

suspend fun handleRequest(call: ApplicationCall, block: suspend () -> Unit) {
    try {
        block()
    } catch (e: IllegalArgumentException) {
        call.respond(HttpStatusCode.BadRequest,
            BasicApiResponse(
                success = false,
                message = e.message
            )
        )
    }
}
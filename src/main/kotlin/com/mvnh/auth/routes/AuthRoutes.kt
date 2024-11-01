package com.mvnh.auth.routes

import com.mvnh.auth.dto.AuthTokens
import com.mvnh.auth.dto.RefreshTokenRequest
import com.mvnh.auth.dto.UserCredentials
import com.mvnh.auth.repositories.AuthRepository
import com.mvnh.dto.BasicApiResponse
import com.mvnh.utils.handleRequest
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes(repository: AuthRepository) {
    route("/auth") {
        authenticate("auth-jwt") {
            post("/register") {
                val credentials = call.receive<UserCredentials>()
                val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")

                handleRequest(call) {
                    require(token != null) { "Missing or invalid JWT token" }

                    repository.register(token, credentials)
                    call.respond(
                        HttpStatusCode.Created,
                        BasicApiResponse(
                            success = true,
                            message = "User registered successfully",
                            data = null
                        )
                    )
                }
            }
        }

        post("/login") {
            val credentials = call.receive<UserCredentials>()
            handleRequest(call) {
                val tokensMap = repository.login(credentials)
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse(
                        success = true,
                        message = "User logged in successfully",
                        data = AuthTokens(
                            tokensMap["accessToken"]!!,
                            tokensMap["refreshToken"]!!
                        )
                    )
                )
            }
        }

        post("/refresh") {
            val request = call.receive<RefreshTokenRequest>()
            handleRequest(call) {
                val tokensMap = repository.refresh(request.refreshToken)
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse(
                        success = true,
                        message = "Tokens refreshed successfully",
                        data = AuthTokens(
                            tokensMap["accessToken"]!!,
                            tokensMap["refreshToken"]!!
                        )
                    )
                )
            }
        }

        post("/logout") {
            val request = call.receive<RefreshTokenRequest>()
            handleRequest(call) {
                repository.logout(request.refreshToken)
                call.respond(
                    HttpStatusCode.OK,
                    BasicApiResponse(
                        success = true,
                        message = "User logged out successfully",
                        data = null
                    )
                )
            }
        }
    }
}
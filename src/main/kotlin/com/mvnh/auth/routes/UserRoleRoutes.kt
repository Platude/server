package com.mvnh.auth.routes

import com.mvnh.auth.dto.UsernameRequest
import com.mvnh.auth.repositories.UserRoleRepository
import com.mvnh.utils.handleRequest
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoleRoutes(repository: UserRoleRepository) {
    route("/auth/role") {
        authenticate("auth-jwt") {
            post("/promote") {
                val request = call.receive<UsernameRequest>()
                val username = request.username

                val adminToken = call.principal<JWTPrincipal>()!!.payload

//                handleRequest(call) {
//                    repository.promoteUserRole(username)
//                    call.respond(HttpStatusCode.OK)
//                }

                call.respond(HttpStatusCode.OK, adminToken.claims.mapValues { it.value.toString() })
            }

            post("/demote") {

            }

            get("/get") {

            }
        }
    }
}
package com.mvnh.auth.routes

import com.mvnh.auth.repositories.UserRoleRepository
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.userRoleRoutes(repository: UserRoleRepository) {
    route("/auth/role") {
        authenticate("auth-jwt") {
            post("/promote") {

            }

            post("/demote") {

            }

            get("/get") {

            }
        }
    }
}
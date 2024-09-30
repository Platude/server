package com.mvnh.plugins

import com.mvnh.database.repositories.impl.AuthRepositoryImpl
import com.mvnh.routes.authRoutes
import io.ktor.server.application.Application
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    val authRepository = AuthRepositoryImpl()

    routing {
        authRoutes(authRepository)
    }
}

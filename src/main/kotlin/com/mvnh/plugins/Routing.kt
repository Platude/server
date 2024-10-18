package com.mvnh.plugins

import com.mvnh.auth.repositories.impl.AuthRepositoryImpl
import com.mvnh.auth.repositories.impl.InviteCodeRepositoryImpl
import com.mvnh.auth.repositories.impl.UserRoleRepositoryImpl
import com.mvnh.auth.routes.authRoutes
import com.mvnh.auth.routes.inviteCodeRoutes
import com.mvnh.auth.routes.userRoleRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val authRepository = AuthRepositoryImpl()
    val inviteCodeRepository = InviteCodeRepositoryImpl()
    val userRoleRepository = UserRoleRepositoryImpl()

    routing {
        authRoutes(authRepository)
        inviteCodeRoutes(inviteCodeRepository)
        userRoleRoutes(userRoleRepository)
    }
}
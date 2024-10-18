package com.mvnh.auth.routes

import com.mvnh.auth.dao.UserRoleDao
import com.mvnh.auth.dto.InviteCodeGenerationRequest
import com.mvnh.auth.dto.InviteCodeRequest
import com.mvnh.auth.repositories.InviteCodeRepository
import com.mvnh.utils.handleRequest
import com.mvnh.utils.suspendTransaction
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.inviteCodeRoutes(repository: InviteCodeRepository) {
    route("/auth/invite") {
        authenticate("auth-jwt") {
            post("/generate") {
                val request = call.receive<InviteCodeGenerationRequest>()
                val roleToGrant = request.roleToGrant

                handleRequest(call) {
                    val adminToken = call.principal<JWTPrincipal>()!!.payload
                    val adminId = adminToken.getClaim("user_id").asString()

                    requireNotNull(adminId) { "Invalid admin ID" }
                    require(adminToken.getClaim("role_id").asInt() >= 3) { "Insufficient permissions" }
                    require(suspendTransaction { UserRoleDao.findById(roleToGrant) } != null) { "Invalid role" }

                    val inviteCode = repository.generateInviteCode(roleToGrant, adminId)
                    call.respond(HttpStatusCode.Created, mapOf("inviteCode" to inviteCode))
                }
            }
        }

        post("/redeem") {
            val request = call.receive<InviteCodeRequest>()
            val code = request.inviteCode

            handleRequest(call) {
                val temporaryToken = repository.redeemInviteCode(code)
                call.respond(HttpStatusCode.OK, mapOf("temporaryToken" to temporaryToken))
            }
        }
    }
}
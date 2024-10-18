package com.mvnh.utils

import com.mvnh.dto.BasicApiResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

suspend fun handleRequest(call: ApplicationCall, block: suspend () -> Unit) {
    try {
        block()
    } catch (e: IllegalArgumentException) {
        call.respond(
            HttpStatusCode.BadRequest,
            BasicApiResponse(
                success = false,
                message = e.message
            )
        )
    }
}
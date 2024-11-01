package com.mvnh.auth.dto

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class Username(val value: String)

@Serializable
data class UsernameRequest(val username: Username)
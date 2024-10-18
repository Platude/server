package com.mvnh.auth.dto

import kotlinx.serialization.Serializable

@Serializable
data class InviteCodeGenerationRequest(val roleToGrant: Int)
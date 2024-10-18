package com.mvnh.auth.dto

import kotlinx.serialization.Serializable

@Serializable
data class InviteCodeRequest(val inviteCode: String)
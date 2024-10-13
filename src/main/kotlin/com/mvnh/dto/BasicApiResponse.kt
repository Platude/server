package com.mvnh.dto

import kotlinx.serialization.Serializable

@Serializable
data class BasicApiResponse(
    val success: Boolean,
    val message: String? = null
)

package com.mvnh.dto

import kotlinx.serialization.Serializable

@Serializable
data class BasicApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null
)
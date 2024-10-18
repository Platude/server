package com.mvnh.auth.repositories

import com.mvnh.auth.dto.UserCredentials

interface AuthRepository {
    suspend fun register(temporaryToken: String, credentials: UserCredentials): Boolean
    suspend fun login(credentials: UserCredentials): Map<String, String>
    suspend fun refresh(oldRefreshToken: String): Map<String, String>
    suspend fun logout(refreshToken: String): Boolean
}
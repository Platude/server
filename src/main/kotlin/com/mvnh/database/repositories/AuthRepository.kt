package com.mvnh.database.repositories

import com.mvnh.dto.UserCredentials
import com.mvnh.dto.AuthTokens

interface AuthRepository {
    suspend fun register(credentials: UserCredentials): Boolean
    suspend fun login(credentials: UserCredentials): AuthTokens
    suspend fun refresh(refreshToken: String): AuthTokens
    suspend fun logout(refreshToken: String): Boolean
}
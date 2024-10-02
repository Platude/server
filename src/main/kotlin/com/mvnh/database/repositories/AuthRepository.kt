package com.mvnh.database.repositories

import com.mvnh.dto.AccountCredentials
import com.mvnh.dto.AuthToken

interface AuthRepository {
    suspend fun register(credentials: AccountCredentials): Boolean
    suspend fun login(credentials: AccountCredentials): AuthToken
    suspend fun refresh(refreshToken: String): AuthToken
    suspend fun logout(refreshToken: String): Boolean
}
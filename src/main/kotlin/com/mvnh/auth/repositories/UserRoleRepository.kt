package com.mvnh.auth.repositories

interface UserRoleRepository {
    suspend fun promoteUserRole(username: String): Boolean
    suspend fun demoteUserRole(username: String): Boolean
    suspend fun getUserRole(username: String): Int
}
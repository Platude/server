package com.mvnh.auth.tables

import org.jetbrains.exposed.dao.id.UUIDTable

object UsersTable : UUIDTable("users") {
    val username = varchar("username", 32).uniqueIndex()
    val password = varchar("password", 64)
    val refreshToken = text("refresh_token").nullable()
    val role = reference("role", UserRolesTable.id)
}
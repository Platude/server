package com.mvnh.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object UsersTable : IntIdTable("accounts") {
    val username = varchar("username", 32).uniqueIndex()
    val password = varchar("password", 64)
    val refreshToken = text("refresh_token").nullable()
}
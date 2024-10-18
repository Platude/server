package com.mvnh.auth.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object UserRolesTable : IntIdTable("roles") {
    val name = varchar("name", 32).uniqueIndex()
    val description = text("description").nullable()
}
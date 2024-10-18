package com.mvnh.auth.dao

import com.mvnh.auth.tables.UsersTable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class UserDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserDao>(UsersTable)

    var userId by UsersTable.id
    var username by UsersTable.username
    var password by UsersTable.password
    var refreshToken by UsersTable.refreshToken
    var role by UsersTable.role
}
package com.mvnh.database.dao

import com.mvnh.database.tables.UsersTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UserDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserDao>(UsersTable)

    var username by UsersTable.username
    var password by UsersTable.password
    var refreshToken by UsersTable.refreshToken
}
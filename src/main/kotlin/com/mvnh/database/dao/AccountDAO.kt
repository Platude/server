package com.mvnh.database.dao

import com.mvnh.database.tables.AccountsTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class AccountDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AccountDAO>(AccountsTable)

    var username by AccountsTable.username
    var password by AccountsTable.password
    var refreshToken by AccountsTable.refreshToken
}
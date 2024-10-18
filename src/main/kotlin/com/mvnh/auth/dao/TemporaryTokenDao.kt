package com.mvnh.auth.dao

import com.mvnh.auth.tables.TemporaryTokensTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class TemporaryTokenDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TemporaryTokenDao>(TemporaryTokensTable)

    var token by TemporaryTokensTable.token
    var inviteCode by TemporaryTokensTable.inviteCode
}
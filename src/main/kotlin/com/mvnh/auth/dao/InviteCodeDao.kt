package com.mvnh.auth.dao

import com.mvnh.auth.tables.InviteCodesTable
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class InviteCodeDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<InviteCodeDao>(InviteCodesTable)

    var inviteCode by InviteCodesTable.id
    var createdAt by InviteCodesTable.createdAt
    var usedBy by InviteCodesTable.usedBy
    var roleToGrant by InviteCodesTable.roleToGrant
    var createdBy by InviteCodesTable.createdBy
}
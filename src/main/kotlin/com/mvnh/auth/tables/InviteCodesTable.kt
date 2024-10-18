package com.mvnh.auth.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime

object InviteCodesTable : UUIDTable("invite_codes") {
    val roleToGrant = integer("role_to_grant")
    val usedBy = reference("used_by", UsersTable.id).nullable()
    val createdBy = reference("created_by", UsersTable.id)
    val createdAt = datetime("created_at")
}
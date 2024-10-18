package com.mvnh.auth.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object TemporaryTokensTable : IntIdTable("temporary_tokens") {
    val token = text("token").uniqueIndex()
    val inviteCode = reference("invite_code", InviteCodesTable)
}
package com.mvnh.auth.repositories.impl

import com.mvnh.auth.dao.InviteCodeDao
import com.mvnh.auth.dao.TemporaryTokenDao
import com.mvnh.auth.repositories.InviteCodeRepository
import com.mvnh.auth.tables.TemporaryTokensTable
import com.mvnh.utils.suspendTransaction
import com.mvnh.auth.tables.UsersTable
import com.mvnh.auth.utils.JwtConfig.generateTemporaryToken
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import java.time.LocalDateTime
import java.util.*

class InviteCodeRepositoryImpl : InviteCodeRepository {
    override suspend fun generateInviteCode(roleToGrant: Int, createdById: String): String = suspendTransaction {
        val code = InviteCodeDao.new {
            createdAt = LocalDateTime.now()
            this.roleToGrant = roleToGrant
            createdBy = EntityID(UUID.fromString(createdById), UsersTable)
        }

        code.inviteCode.toString()
    }

    override suspend fun redeemInviteCode(inviteCode: String): String = suspendTransaction {
        val code = InviteCodeDao.findById(UUID.fromString(inviteCode))

        requireNotNull(code) { "Invalid invite code" }
        require(code.usedBy == null) { "Invite code already used" }

        val temporaryToken = generateTemporaryToken(inviteCode)

        TemporaryTokenDao.find { TemporaryTokensTable.token eq temporaryToken }.firstOrNull()?.apply {
            this.inviteCode = code.id
        } ?: TemporaryTokenDao.new {
            token = temporaryToken
            this.inviteCode = code.id
        }

        temporaryToken
    }
}
package com.mvnh.auth.repositories

interface InviteCodeRepository {
    suspend fun generateInviteCode(roleToGrant: Int, createdById: String): String
    suspend fun redeemInviteCode(inviteCode: String): String
}
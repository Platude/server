package com.mvnh.auth.repositories.impl

import com.mvnh.auth.dao.InviteCodeDao
import com.mvnh.auth.dao.TemporaryTokenDao
import com.mvnh.auth.dao.UserDao
import com.mvnh.auth.repositories.AuthRepository
import com.mvnh.utils.suspendTransaction
import com.mvnh.auth.tables.TemporaryTokensTable
import com.mvnh.auth.tables.UserRolesTable
import com.mvnh.auth.tables.UsersTable
import com.mvnh.auth.dto.UserCredentials
import com.mvnh.auth.utils.JwtConfig
import com.mvnh.auth.utils.JwtConfig.generateToken
import com.mvnh.auth.utils.JwtConfig.verifyToken
import org.jetbrains.exposed.dao.id.EntityID
import org.mindrot.jbcrypt.BCrypt

class AuthRepositoryImpl : AuthRepository {
    override suspend fun register(temporaryToken: String, credentials: UserCredentials): Boolean = suspendTransaction {
        require(credentials.username.length in 4..32) { "Username must be between 4 and 32 characters" }
        require(credentials.password.length in 8..64) { "Password must be between 8 and 64 characters" }

        val user = UserDao.find {
            UsersTable.username eq credentials.username
        }.firstOrNull()
        val tempToken = TemporaryTokenDao.find {
            TemporaryTokensTable.token eq temporaryToken
        }.firstOrNull()

        require(user == null) { "Username already taken" }
        requireNotNull(tempToken) { "Invalid temporary token" }

        val inviteCode = InviteCodeDao.findById(tempToken.inviteCode)

        requireNotNull(inviteCode) { "Invalid temporary token" }
        require(inviteCode.usedBy == null) { "Temporary token already used" }

        val newUser = UserDao.new {
            username = credentials.username
            password = BCrypt.hashpw(credentials.password, BCrypt.gensalt())
            role = EntityID(inviteCode.roleToGrant, UserRolesTable)
        }

        inviteCode.usedBy = newUser.id
//        inviteCode.flush()

        true
    }

    override suspend fun login(credentials: UserCredentials): Map<String, String> = suspendTransaction {
        val account = UserDao.find { UsersTable.username eq credentials.username }.firstOrNull()

        requireNotNull(account) { "Account not found" }
        require(BCrypt.checkpw(credentials.password, account.password)) { "Invalid credentials" }

        val accessTokenToGrant = generateToken(
            userId = account.userId.toString(),
            username = credentials.username,
            roleId = account.role.value,
            isAccessToken = true,
        )
        val refreshTokenToGrant = account.refreshToken ?: run {
            val newRefreshToken = generateToken(
                userId = account.userId.toString(),
                username = credentials.username,
                roleId = account.role.value,
                isAccessToken = false,
            )

            account.refreshToken = newRefreshToken
//            account.flush()

            newRefreshToken
        }

        mapOf(
            "accessToken" to accessTokenToGrant,
            "refreshToken" to refreshTokenToGrant
        )
    }

    override suspend fun refresh(oldRefreshToken: String): Map<String, String> = suspendTransaction {
        val account = UserDao.find { UsersTable.refreshToken eq oldRefreshToken }.firstOrNull()

        requireNotNull(account) { "Invalid credentials" }

        val accountRefreshToken = verifyToken(oldRefreshToken, JwtConfig.refreshAlgorithm)

        require(
            accountRefreshToken
                .getClaim("username")
                .asString() == account.username
        ) { "Invalid credentials" }
        require(accountRefreshToken.expiresAt.time > System.currentTimeMillis()) {
            account.refreshToken = null
//            account.flush()

            "Refresh token expired, please login again"
        }

        val newAccessToken = generateToken(
            userId = account.userId.toString(),
            username = account.username,
            roleId = account.role.value,
            isAccessToken = true,
        )
        val newRefreshToken = generateToken(
            userId = account.userId.toString(),
            username = account.username,
            roleId = account.role.value,
            isAccessToken = false,
        )

        account.refreshToken = newRefreshToken
//        account.flush()

        mapOf(
            "accessToken" to newAccessToken,
            "refreshToken" to newRefreshToken
        )
    }

    override suspend fun logout(refreshToken: String): Boolean = suspendTransaction {
        val account = UserDao.find { UsersTable.refreshToken eq refreshToken }.firstOrNull()

        requireNotNull(account) { "Invalid credentials" }

        account.refreshToken = null
//        account.flush()

        true
    }
}
package com.mvnh.database.repositories.impl

import com.mvnh.database.dao.AccountDAO
import com.mvnh.database.repositories.AuthRepository
import com.mvnh.database.suspendTransaction
import com.mvnh.database.tables.AccountsTable
import com.mvnh.dto.AccountCredentials
import com.mvnh.dto.AuthToken
import com.mvnh.utils.JWTConfig
import com.mvnh.utils.JWTConfig.generateAccessToken
import com.mvnh.utils.JWTConfig.generateRefreshToken
import com.mvnh.utils.JWTConfig.verifyToken
import org.jetbrains.exposed.sql.update
import org.mindrot.jbcrypt.BCrypt

class AuthRepositoryImpl : AuthRepository {
    override suspend fun register(credentials: AccountCredentials): Boolean = suspendTransaction {
        val existingAccount = AccountDAO.find {
            AccountsTable.username eq credentials.username
        }.firstOrNull()

        require(existingAccount == null) { "Username already exists" }
        require(credentials.username.length in 4..32) { "Username must be between 4 and 32 characters" }
        require(credentials.password.length in 8..64) { "Password must be between 8 and 64 characters" }

        AccountDAO.new {
            username = credentials.username
            password = BCrypt.hashpw(credentials.password, BCrypt.gensalt())
        }

        true
    }

    override suspend fun login(credentials: AccountCredentials): AuthToken = suspendTransaction {
        val account = AccountDAO.find {
            AccountsTable.username eq credentials.username
        }.firstOrNull()

        require(account != null) { "Account not found" }
        require(BCrypt.checkpw(credentials.password, account.password)) { "Invalid password" }

        val accessTokenToGrant = generateAccessToken(credentials.username)
        val refreshTokenToGrant = account.refreshToken ?: run {
            val newRefreshToken = generateRefreshToken(credentials.username)
            AccountsTable.update({ AccountsTable.username eq credentials.username }) {
                it[refreshToken] = newRefreshToken
            }
            newRefreshToken
        }

        AuthToken(
            accessToken = accessTokenToGrant,
            refreshToken = refreshTokenToGrant
        )
    }

    override suspend fun refresh(oldRefreshToken: String): AuthToken = suspendTransaction {
        val account = AccountDAO.find {
            AccountsTable.refreshToken eq oldRefreshToken
        }.firstOrNull()
        require(account != null) { "Invalid refresh token" }

        val accountRefreshToken = verifyToken(oldRefreshToken, JWTConfig.refreshAlgorithm)
        require(
            accountRefreshToken
                .getClaim("username")
                .asString() == account.username
        ) { "Invalid refresh token" }
        require(accountRefreshToken.expiresAt.time > System.currentTimeMillis()) { "Refresh token expired" }

        val newAccessToken = generateAccessToken(account.username)
        val newRefreshToken = generateRefreshToken(account.username)
        AccountsTable.update({ AccountsTable.username eq account.username }) {
            it[refreshToken] = newRefreshToken
        }

        AuthToken(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }

    override suspend fun logout(refreshToken: String): Boolean {
        return false
    }
}
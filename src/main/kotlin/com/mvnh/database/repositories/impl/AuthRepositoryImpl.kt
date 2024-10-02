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
    override suspend fun register(credentials: AccountCredentials): Boolean {
        require(credentials.username.length in 4..32) { "Username must be between 4 and 32 characters" }
        require(credentials.password.length in 8..64) { "Password must be between 8 and 64 characters" }

        return suspendTransaction {
            val existingAccount = AccountDAO.find {
                AccountsTable.username eq credentials.username
            }.firstOrNull()

            require(existingAccount == null) { "Username already exists" }

            AccountDAO.new {
                username = credentials.username
                password = BCrypt.hashpw(credentials.password, BCrypt.gensalt())
            }

            true
        }
    }

    override suspend fun login(credentials: AccountCredentials): AuthToken {
        val account = suspendTransaction {
            AccountDAO.find {
                AccountsTable.username eq credentials.username
            }.firstOrNull()
        }

        require(account != null) { "Account not found" }
        require(BCrypt.checkpw(credentials.password, account.password)) { "Invalid password" }

        val accessTokenToGrant = generateAccessToken(credentials.username)
        val refreshTokenToGrant = account.refreshToken ?: run {
            val newRefreshToken = generateRefreshToken(credentials.username)
            suspendTransaction {
                AccountsTable.update({ AccountsTable.username eq credentials.username }) {
                    it[refreshToken] = newRefreshToken
                }
            }
            newRefreshToken
        }

        return AuthToken( //
            accessToken = accessTokenToGrant,
            refreshToken = refreshTokenToGrant
        )
    }

    override suspend fun refresh(oldRefreshToken: String): AuthToken {
        val account = suspendTransaction {
            AccountDAO.find {
                AccountsTable.refreshToken eq oldRefreshToken
            }.firstOrNull()
        }
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

        suspendTransaction {
            AccountsTable.update({ AccountsTable.username eq account.username }) {
                it[refreshToken] = newRefreshToken
            }
        }

        return AuthToken(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }

    override suspend fun logout(refreshToken: String): Boolean {
        val account = suspendTransaction {
            AccountDAO.find {
                AccountsTable.refreshToken eq refreshToken
            }.firstOrNull()
        }

        require(account != null) { "Invalid refresh token" }

        suspendTransaction {
            AccountsTable.update({ AccountsTable.username eq account.username }) {
                it[this.refreshToken] = null
            }
        }

        return true
    }
}
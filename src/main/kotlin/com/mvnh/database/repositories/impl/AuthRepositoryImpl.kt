package com.mvnh.database.repositories.impl

import com.mvnh.database.dao.UserDao
import com.mvnh.database.repositories.AuthRepository
import com.mvnh.database.suspendTransaction
import com.mvnh.database.tables.UsersTable
import com.mvnh.dto.UserCredentials
import com.mvnh.dto.AuthTokens
import com.mvnh.utils.JwtConfig
import com.mvnh.utils.JwtConfig.generateToken
import com.mvnh.utils.JwtConfig.verifyToken
import org.jetbrains.exposed.sql.update
import org.mindrot.jbcrypt.BCrypt

class AuthRepositoryImpl : AuthRepository {
    override suspend fun register(credentials: UserCredentials): Boolean {
        require(credentials.username.length in 4..32) { "Username must be between 4 and 32 characters" }
        require(credentials.password.length in 8..64) { "Password must be between 8 and 64 characters" }

        return suspendTransaction {
            val existingAccount = UserDao.find {
                UsersTable.username eq credentials.username
            }.firstOrNull()

            require(existingAccount == null) { "Username already exists" }

            UserDao.new {
                username = credentials.username
                password = BCrypt.hashpw(credentials.password, BCrypt.gensalt())
            }

            true
        }
    }

    override suspend fun login(credentials: UserCredentials): AuthTokens {
        val account = suspendTransaction {
            UserDao.find {
                UsersTable.username eq credentials.username
            }.firstOrNull()
        }

        require(account != null) { "Account not found" }
        require(BCrypt.checkpw(credentials.password, account.password)) { "Invalid password" }

        val accessTokenToGrant = generateToken(credentials.username, isAccessToken = true)
        val refreshTokenToGrant = account.refreshToken ?: run {
            val newRefreshToken = generateToken(credentials.username, isAccessToken = false)
            suspendTransaction {
                UsersTable.update({ UsersTable.username eq credentials.username }) {
                    it[refreshToken] = newRefreshToken
                }
            }
            newRefreshToken
        }

        return AuthTokens(
            accessToken = accessTokenToGrant,
            refreshToken = refreshTokenToGrant
        )
    }

    override suspend fun refresh(refreshToken: String): AuthTokens {
        val account = suspendTransaction {
            UserDao.find {
                UsersTable.refreshToken eq refreshToken
            }.firstOrNull()
        }
        require(account != null) { "Invalid refresh token" }

        val accountRefreshToken = verifyToken(refreshToken, JwtConfig.refreshAlgorithm)
        require(
            accountRefreshToken
                .getClaim("username")
                .asString() == account.username
        ) { "Invalid refresh token" }
        require(accountRefreshToken.expiresAt.time > System.currentTimeMillis()) {
            suspendTransaction {
                UsersTable.update({ UsersTable.username eq account.username }) {
                    it[this.refreshToken] = null
                }
            }

            "Refresh token expired, please login again"
        }

        val newAccessToken = generateToken(account.username, isAccessToken = true)
        val newRefreshToken = generateToken(account.username, isAccessToken = false)

        suspendTransaction {
            UsersTable.update({ UsersTable.username eq account.username }) {
                it[this.refreshToken] = newRefreshToken
            }
        }

        return AuthTokens(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }

    override suspend fun logout(refreshToken: String): Boolean {
        val account = suspendTransaction {
            UserDao.find {
                UsersTable.refreshToken eq refreshToken
            }.firstOrNull()
        }

        require(account != null) { "Invalid refresh token" }

        suspendTransaction {
            UsersTable.update({ UsersTable.username eq account.username }) {
                it[this.refreshToken] = null
            }
        }

        return true
    }
}
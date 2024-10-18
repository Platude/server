package com.mvnh.auth.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import java.util.*

object JwtConfig {
    private val SECRET: String = System.getenv("JWT_SECRET") ?: throw IllegalStateException("JWT_SECRET environment variable not set")
    private val REFRESH_SECRET: String = System.getenv("JWT_REFRESH_SECRET") ?: throw IllegalStateException("JWT_REFRESH_SECRET environment variable not set")

    const val ISSUER = "https://platude.com/auth"
    const val AUDIENCE = "https://platude.com"
    const val REALM = "Platude"

    private const val ACCESS_TOKEN_VALIDITY = 60 * 60 * 1000L // 1 hour
    private const val REFRESH_TOKEN_VALIDITY = 60 * 24 * 60 * 60 * 1000L // 60 days

    val algorithm: Algorithm = Algorithm.HMAC256(SECRET)
    val refreshAlgorithm: Algorithm = Algorithm.HMAC256(REFRESH_SECRET)

    fun generateToken(
        userId: String,
        username: String,
        roleId: Int,
        isAccessToken: Boolean
    ): String {
        val validity = if (isAccessToken) ACCESS_TOKEN_VALIDITY else REFRESH_TOKEN_VALIDITY
        val algorithmToUse = if (isAccessToken) algorithm else refreshAlgorithm

        return JWT.create()
            .withAudience(AUDIENCE)
            .withIssuer(ISSUER)
            .withClaim("user_id", userId)
            .withClaim("username", username)
            .withClaim("role_id", roleId)
            .withExpiresAt(Date(System.currentTimeMillis() + validity))
            .sign(algorithmToUse)
    }

    fun generateTemporaryToken(inviteCode: String): String = JWT.create()
        .withAudience(AUDIENCE)
        .withIssuer(ISSUER)
        .withClaim("invite_code", inviteCode)
        .withExpiresAt(Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY))
        .sign(algorithm)

    fun verifyToken(
        token: String,
        algorithm: Algorithm
    ): DecodedJWT = JWT.require(algorithm)
        .withAudience(AUDIENCE)
        .withIssuer(ISSUER)
        .build()
        .verify(token)
}
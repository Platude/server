package com.mvnh.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import java.util.*

object JWTConfig {
    private val SECRET: String = System.getenv("JWT_SECRET") ?: throw IllegalStateException("JWT_SECRET environment variable not set")
    private val REFRESH_SECRET: String = System.getenv("JWT_REFRESH_SECRET") ?: throw IllegalStateException("JWT_REFRESH_SECRET environment variable not set")

    const val ISSUER = "https://platude.com/auth"
    const val AUDIENCE = "https://platude.com"
    const val REALM = "Platude"

    private const val ACCESS_TOKEN_VALIDITY = 60 * 60 * 1000L // 1 hour
    private const val REFRESH_TOKEN_VALIDITY = 60 * 24 * 60 * 60 * 1000L // 60 days

    val algorithm: Algorithm = Algorithm.HMAC256(SECRET)
    val refreshAlgorithm: Algorithm = Algorithm.HMAC256(REFRESH_SECRET)

    fun generateAccessToken(username: String): String = JWT.create()
        .withAudience(AUDIENCE)
        .withIssuer(ISSUER)
        .withClaim("username", username)
        .withExpiresAt(Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY))
        .sign(algorithm)

    fun generateRefreshToken(username: String): String = JWT.create()
        .withAudience(AUDIENCE)
        .withIssuer(ISSUER)
        .withClaim("username", username)
        .withExpiresAt(Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY))
        .sign(refreshAlgorithm)

    fun verifyToken(token: String, algorithm: Algorithm): DecodedJWT = JWT.require(algorithm)
        .withAudience(AUDIENCE)
        .withIssuer(ISSUER)
        .build()
        .verify(token)
}
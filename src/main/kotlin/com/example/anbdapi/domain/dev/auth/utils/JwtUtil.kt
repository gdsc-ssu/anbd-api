package com.example.anbdapi.domain.dev.auth.utils

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtUtil {

    @Value("\${jwt.secret}")
    private lateinit var secret: String

    @Value("\${jwt.access-expiration-in-ms}")
    private var accessTokenExpirationInMs: Long = 0

    @Value("\${jwt.refresh-expiration-in-ms}")
    private var refreshTokenExpirationInMs: Long = 0

    private fun getSigningKey(): SecretKey {
        return Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))
    }

    fun generateAccessToken(email: String): String {
        val now = Date()
        val expiryDate = Date(now.time + accessTokenExpirationInMs)
        // accessToken과 refershToken이 같은 쌍인지 확인하기 위한 uuid
        val jti = UUID.randomUUID().toString()

        return Jwts.builder()
            .subject(email)
            .issuedAt(now)
            .expiration(expiryDate)
            .claim("jti", jti)
            .signWith(getSigningKey(), Jwts.SIG.HS256)
            .compact()
    }

    fun generateRefreshToken(email: String, jtiOfAccessToken: String): String {
        val now = Date()
        val expiryDate = Date(now.time + refreshTokenExpirationInMs)

        return Jwts.builder()
            .subject(email)
            .issuedAt(now)
            .expiration(expiryDate)
            .claim("jti", jtiOfAccessToken) // Access Token과 동일한 jti로 Claim
            .signWith(getSigningKey(), Jwts.SIG.HS256)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
            true
        } catch (e: JwtException) {
            throw JwtException("유효하지 않거나 만료된 토큰입니다.")
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("잘못된 토큰 형식입니다.")
        }
    }

    fun getJtiFromToken(token: String): String {
        val claims: Claims = Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .payload
        return claims["jti", String::class.java] ?: throw IllegalArgumentException("JTI not found in token")
    }

    fun getEmailFromToken(token: String): String {
        val claims: Claims = Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .payload
        return claims.subject ?: throw IllegalArgumentException("Email not found in token")
    }
}
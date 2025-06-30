package com.example.anbdapi.support.utils.jwt

import com.example.anbdapi.domain.auth.exception.TokenExpiredException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
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

    fun generateAccessToken(userId: Long): String {
        val now = Date()
        val expiryDate = Date(now.time + accessTokenExpirationInMs)
        // accessToken과 refershToken이 같은 쌍인지 확인하기 위한 uuid
        val jti = UUID.randomUUID().toString()

        return Jwts.builder()
            .subject(userId.toString())
            .issuedAt(now)
            .expiration(expiryDate)
            .claim("jti", jti)
            .signWith(getSigningKey(), Jwts.SIG.HS256)
            .compact()
    }

    fun generateRefreshToken(userId: Long, jtiOfAccessToken: String): String {
        val now = Date()
        val expiryDate = Date(now.time + refreshTokenExpirationInMs)

        return Jwts.builder()
            .subject(userId.toString())
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
        } catch (e: ExpiredJwtException) {
            throw TokenExpiredException("Token has expired.")
        } catch (e: JwtException) {
            throw JwtException("Invalid token.")
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid token format.")
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

    fun getUserIdFromToken(token: String): String {
        val claims: Claims = Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .payload
        return claims.subject ?: throw IllegalArgumentException("UserId not found in token")
    }

    fun getUserIdFromExpiredToken(token: String): String {
        try {
            val claims: Claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .payload
            return claims.subject ?: throw IllegalArgumentException("UserId not found in token")
        } catch (e: ExpiredJwtException) {
            return e.claims.subject ?: throw IllegalArgumentException("UserId not found in token")
        } catch (e: Exception) {
            throw JwtException("Invalid token format")
        }
    }

    fun getJtiFromExpiredToken(token: String): String {
        try {
            val claims: Claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .payload
            return claims["jti", String::class.java] ?: throw IllegalArgumentException("JTI not found in token")
        } catch (e: ExpiredJwtException) {
            return e.claims["jti", String::class.java] ?: throw IllegalArgumentException("JTI not found in token")
        } catch (e: Exception) {
            throw JwtException("Invalid token format")
        }
    }

    fun validateAndExtractClaims(token: String): Claims {
        return try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: ExpiredJwtException) {
            throw TokenExpiredException("Token has expired.")
        } catch (e: JwtException) {
            throw JwtException("Invalid token.")
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid token format.")
        }
    }
}
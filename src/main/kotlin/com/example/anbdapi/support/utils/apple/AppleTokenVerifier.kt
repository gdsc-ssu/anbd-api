package com.example.anbdapi.support.utils.apple

import com.example.anbdapi.domain.auth.exception.AppleAuthException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class AppleTokenVerifier(
    private val objectMapper: ObjectMapper,
    private val applePublicKeyService: ApplePublicKeyService
) {
    companion object {
        private const val APPLE_ISS = "https://appleid.apple.com"
    }

    @Value("\${apple.client-id}")
    private lateinit var appleClientId: String

    fun verifyToken(idToken: String): Map<String, Any> {
        return try {
            // 1. JWT 헤더에서 kid 추출
            val chunks = idToken.split(".")
            if (chunks.size != 3) {
                throw AppleAuthException("Invalid JWT format")
            }

            val header = String(Base64.getUrlDecoder().decode(chunks[0]))
            val headerMap: Map<String, Any> = objectMapper.readValue(header)
            val kid = headerMap["kid"] as String
            val alg = headerMap["alg"] as String

            val applePublicKey = applePublicKeyService.getPublicKey(kid, alg)

            // 2. JWT 검증 및 클레임 추출
            val claims = Jwts.parser()
                .verifyWith(applePublicKey)
                .build()
                .parseSignedClaims(idToken)
                .payload

            // 3. 검증 조건 확인
            validateClaims(claims)

            claims.toMap()
        } catch (e: AppleAuthException) {
            throw e
        } catch (e: Exception) {
            throw AppleAuthException("Error verifying Apple token: ${e.message}")
        }
    }

    private fun validateClaims(claims: Claims) {
        if (claims.issuer != APPLE_ISS) {
            throw AppleAuthException("Invalid issuer: ${claims.issuer}")
        }

        val audience = claims.audience
        if (!audience.contains(appleClientId)) {
            throw AppleAuthException("Invalid audience: $audience, expected: $appleClientId")
        }

        val now = Date()
        val expiration = claims.expiration
        if (expiration != null && expiration.before(now)) {
            throw AppleAuthException("Token has expired.")
        }

        val issuedAt = claims.issuedAt
        if (issuedAt != null && issuedAt.after(now)) {
            throw AppleAuthException("Token issued in the future.")
        }
    }
}
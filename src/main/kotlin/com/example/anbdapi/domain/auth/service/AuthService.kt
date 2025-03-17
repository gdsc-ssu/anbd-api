package com.example.anbdapi.domain.auth.service

import com.example.anbdapi.domain.auth.dto.request.RefreshRequest
import com.example.anbdapi.domain.auth.dto.response.TokenResponse
import com.example.anbdapi.domain.auth.exception.GoogleAuthException
import com.example.anbdapi.domain.user.dto.response.LoginResponse
import com.example.anbdapi.domain.user.service.UserService
import com.example.anbdapi.support.utils.jwt.JwtUtil
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.JwtException
import jakarta.transaction.Transactional
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.springframework.stereotype.Service


@Service
class AuthService(
    private val userService: UserService,
    private val jwtUtil: JwtUtil
) {
    private val GOOGLE_USER_INFO_URL = "https://www.googleapis.com/oauth2/v3/userinfo"

    @Transactional
    fun refreshAccessToken(accessToken: String, request: RefreshRequest): TokenResponse {

        val userId = jwtUtil.getUserIdFromExpiredToken(accessToken)

        val user = userService.getUserById(userId.toLong())

        val storedRefreshToken = user.refreshToken
        if (storedRefreshToken == null || storedRefreshToken != request.refreshToken) {
            throw JwtException("Refresh Token mismatch")
        }

        if (!jwtUtil.validateToken(request.refreshToken)) {
            throw JwtException("Refresh Token is invalid or expired")
        }

        // 같은 쌍의 token인지 확인하기 위해 jti를 비교
        val accessTokenJti = jwtUtil.getJtiFromExpiredToken(accessToken)
        val refreshTokenJti = jwtUtil.getJtiFromToken(request.refreshToken)
        if (accessTokenJti != refreshTokenJti) {
            throw JwtException("jti mismatch between Access and Refresh Tokens")
        }

        // refresh 될 때마다 같은 쌍의 accessToken, refreshToken을 생성
        val accessToken = jwtUtil.generateAccessToken(user.id!!)
        val newJti = jwtUtil.getJtiFromToken(accessToken)
        val refreshToken = jwtUtil.generateRefreshToken(user.id, newJti)

        user.refreshToken = refreshToken
        userService.save(user)

        return TokenResponse(accessToken, refreshToken)
    }

    @Transactional
    fun processMobileGoogleLogin(accessToken: String): LoginResponse {

        val userInfo = verifyGoogleToken(accessToken)

        val providerId = userInfo["sub"] as String
        val email = userInfo["email"] as String
        val nickname = userInfo["name"] as? String ?: "Unknown"
        val profileImage = userInfo["picture"] as? String ?: ""

        val user = userService.findOrCreateUser("GOOGLE", providerId, email, nickname, profileImage)

        val jwtAccessToken = jwtUtil.generateAccessToken(user.id!!)
        val jti = jwtUtil.getJtiFromToken(jwtAccessToken)
        val refreshToken = jwtUtil.generateRefreshToken(user.id, jti)

        userService.updateRefreshToken(user.id, refreshToken)

        return LoginResponse(jwtAccessToken, refreshToken, user.isProfileCompleted)
    }

    private fun verifyGoogleToken(accessToken: String): Map<String, Any> {
        val httpClient = HttpClients.createDefault()
        val request = HttpGet(GOOGLE_USER_INFO_URL)
        request.addHeader("Authorization", "Bearer $accessToken")

        return try {
            val response = httpClient.execute(request)
            val entity = response.entity
            val content = EntityUtils.toString(entity)

            if (response.statusLine.statusCode != 200) {
                throw JwtException("Failed to verify Google token: $content")
            }

            val objectMapper = ObjectMapper()
            @Suppress("UNCHECKED_CAST")
            objectMapper.readValue(content, Map::class.java) as Map<String, Any>
        } catch (e: Exception) {
            throw GoogleAuthException("Error verifying Google token: ${e.message}")
        } finally {
            httpClient.close()
        }
    }
}
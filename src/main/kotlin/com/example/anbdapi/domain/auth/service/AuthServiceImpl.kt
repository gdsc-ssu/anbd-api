package com.example.anbdapi.domain.auth.service

import com.example.anbdapi.support.utils.JwtUtil
import org.springframework.stereotype.Service

@Service
class AuthServiceImpl(
    private val userService: UserService,
    private val jwtUtil: JwtUtil
) : AuthService {

    override fun refreshAccessToken(request: com.example.anbdapi.domain.auth.dto.request.RefreshRequest): com.example.anbdapi.domain.auth.dto.response.TokenResponse {
        val user = userService.findByEmail(request.email)
            ?: throw RuntimeException("User not found")

        val storedRefreshToken = user.refreshToken
        if (storedRefreshToken == null || storedRefreshToken != request.refreshToken) {
            throw RuntimeException("Refresh Token mismatch")
        }

        if (!jwtUtil.validateToken(request.refreshToken)) {
            throw com.example.anbdapi.domain.auth.exception.TokenExpiredException("Refresh Token is invalid or expired")
        }

        if (!jwtUtil.validateToken(request.accessToken)) {
            throw com.example.anbdapi.domain.auth.exception.TokenExpiredException("Access Token is expired")
        }

        // 같은 쌍의 token인지 확인하기 위해 jti를 비교
        val accessTokenJti = jwtUtil.getJtiFromToken(request.accessToken)
        val refreshTokenJti = jwtUtil.getJtiFromToken(request.refreshToken)
        if (accessTokenJti != refreshTokenJti) {
            throw RuntimeException("jti mismatch between Access and Refresh Tokens")
        }

        // refresh 될 때마다 같은 쌍의 accessToken, refreshToken을 생성
        val accessToken = jwtUtil.generateAccessToken(user.email)
        val newJti = jwtUtil.getJtiFromToken(accessToken)
        val refreshToken = jwtUtil.generateRefreshToken(user.email, newJti)

        user.refreshToken = refreshToken
        userService.save(user)

        return com.example.anbdapi.domain.auth.dto.response.TokenResponse(accessToken, refreshToken)
    }
}
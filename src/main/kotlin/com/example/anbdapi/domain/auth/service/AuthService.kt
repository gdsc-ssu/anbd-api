package com.example.anbdapi.domain.auth.service

import com.example.anbdapi.domain.auth.dto.request.RefreshRequest
import com.example.anbdapi.domain.auth.dto.response.TokenResponse
import com.example.anbdapi.domain.auth.exception.TokenExpiredException
import com.example.anbdapi.domain.user.exception.UserNotFoundException
import com.example.anbdapi.domain.user.service.UserService
import com.example.anbdapi.support.utils.JwtUtil
import io.jsonwebtoken.JwtException
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userService: UserService,
    private val jwtUtil: JwtUtil
) {

    fun refreshAccessToken(request: RefreshRequest): TokenResponse {
        val user = userService.findByEmail(request.email)
            ?: throw UserNotFoundException("User not found")

        val storedRefreshToken = user.refreshToken
        if (storedRefreshToken == null || storedRefreshToken != request.refreshToken) {
            throw JwtException("Refresh Token mismatch")
        }

        if (!jwtUtil.validateToken(request.refreshToken)) {
            throw TokenExpiredException("Refresh Token is invalid or expired")
        }

        if (!jwtUtil.validateToken(request.accessToken)) {
            throw TokenExpiredException("Access Token is expired")
        }

        // 같은 쌍의 token인지 확인하기 위해 jti를 비교
        val accessTokenJti = jwtUtil.getJtiFromToken(request.accessToken)
        val refreshTokenJti = jwtUtil.getJtiFromToken(request.refreshToken)
        if (accessTokenJti != refreshTokenJti) {
            throw JwtException("jti mismatch between Access and Refresh Tokens")
        }

        // refresh 될 때마다 같은 쌍의 accessToken, refreshToken을 생성
        val accessToken = jwtUtil.generateAccessToken(user.email)
        val newJti = jwtUtil.getJtiFromToken(accessToken)
        val refreshToken = jwtUtil.generateRefreshToken(user.email, newJti)

        user.refreshToken = refreshToken
        userService.save(user)

        return TokenResponse(accessToken, refreshToken)
    }
}
package com.example.anbdapi.support.global

import com.example.anbdapi.domain.dev.auth.dto.response.LoginResponse
import com.example.anbdapi.domain.dev.auth.service.UserService
import com.example.anbdapi.support.utils.JwtUtil
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class OAuth2AuthenticationSuccessHandler(
    private val jwtUtil: JwtUtil,
    private val objectMapper: ObjectMapper,
    private val userService: UserService
) : AuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val oAuth2User = authentication.principal as OAuth2User
        val email = oAuth2User.attributes["email"] as? String
            ?: throw IllegalArgumentException("Email not found in authentication data")

        val accessToken = jwtUtil.generateAccessToken(email)
        val jti = jwtUtil.getJtiFromToken(accessToken)
        val refreshToken = jwtUtil.generateRefreshToken(email, jti)

        userService.updateRefreshToken(email, refreshToken)

        val user = userService.findByEmail(email) ?: throw RuntimeException("User not found")
        // 가입 후 최초 로그인 시 프로필 미완료 상태로 간주
        val profileCompleteFlag = if (user.isProfileCompleted) 1 else 0
        val loginResponse = LoginResponse(accessToken, refreshToken, profileCompleteFlag)

        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"
        response.writer.write(objectMapper.writeValueAsString(loginResponse))
    }
}
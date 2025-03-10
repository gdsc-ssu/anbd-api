package com.example.anbdapi.support.global

import com.example.anbdapi.domain.user.dto.response.LoginResponse
import com.example.anbdapi.domain.user.exception.UserNotFoundException
import com.example.anbdapi.domain.user.service.UserService
import com.example.anbdapi.domain.userSocialAccount.service.UserSocialAccountService
import com.example.anbdapi.support.enums.Provider
import com.example.anbdapi.support.utils.jwt.JwtUtil
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class OAuth2AuthenticationSuccessHandler(
    private val jwtUtil: JwtUtil,
    private val objectMapper: ObjectMapper,
    private val userService: UserService,
    private val userSocialAccountService: UserSocialAccountService
) : AuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val oAuth2User = authentication.principal as OAuth2User
        val providerId = oAuth2User.attributes["sub"] as? String
            ?: throw IllegalArgumentException("providerId not found in authentication attributes")

        // OAuth2AuthenticationToken에서 provider 정보 가져오기
        val authToken = authentication as OAuth2AuthenticationToken
        val provider = Provider.valueOf(authToken.authorizedClientRegistrationId.uppercase())

        // UserSocialAccount를 통해 User 찾기
        val userSocialAccount = userSocialAccountService.getUserSocialAccount(provider, providerId)
            ?: throw UserNotFoundException("User social account not found")

        val user = userSocialAccount.user

        val accessToken = jwtUtil.generateAccessToken(user.id!!)
        val jti = jwtUtil.getJtiFromToken(accessToken)
        val refreshToken = jwtUtil.generateRefreshToken(user.id, jti)

        userService.updateRefreshToken(user.id, refreshToken)

        // 가입 후 최초 로그인 시 프로필 미완료 상태로 간주
        val loginResponse = LoginResponse(accessToken, refreshToken, user.isProfileCompleted)

        response.contentType = "application/json"
        response.characterEncoding = "UTF-8"
        response.writer.write(objectMapper.writeValueAsString(loginResponse))
    }
}
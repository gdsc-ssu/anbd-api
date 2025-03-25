package com.example.anbdapi.domain.dev.service

import com.example.anbdapi.domain.dev.dto.DevLoginRequest
import com.example.anbdapi.domain.dev.dto.DevUserRequest
import com.example.anbdapi.domain.user.dto.response.LoginResponse
import com.example.anbdapi.domain.user.entity.User
import com.example.anbdapi.domain.user.repository.UserRepository
import com.example.anbdapi.domain.userSocialAccount.entity.UserSocialAccount
import com.example.anbdapi.support.enums.Gender
import com.example.anbdapi.support.enums.Provider
import com.example.anbdapi.support.utils.jwt.JwtUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class DevService(
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil
) {

    @Transactional
    fun createDevUser(request: DevUserRequest): LoginResponse {

        val existingUser = userRepository.findByEmail(request.email)
        if (existingUser != null) {
            throw IllegalArgumentException("Email already registered: ${request.email}")
        }

        val user = User(
            nickname = request.nickname,
            email = request.email,
            profileImage = null,
            gender = Gender.OTHER,
            birthDate = LocalDate.of(2000, 1, 1),
            // TODO : 동네 default 서울로 설정
            neighborhood = "서울",
            shareCategories = mutableListOf(),
            reliability = 0,
            refreshToken = null,
            isProfileCompleted = true,
            deletedAt = null
        )

        userRepository.save(user)

        val socialAccount = UserSocialAccount(
            user = user,
            provider = Provider.DEV,
            providerId = request.email,
            deletedAt = null
        )

        user.socialAccounts.add(socialAccount)

        val accessToken = jwtUtil.generateAccessToken(user.id!!)
        val jti = jwtUtil.getJtiFromToken(accessToken)
        val refreshToken = jwtUtil.generateRefreshToken(user.id, jti)

        user.refreshToken = refreshToken
        userRepository.save(user)

        return LoginResponse.from(accessToken, refreshToken, user)
    }

    @Transactional
    fun loginDevUser(request: DevLoginRequest): LoginResponse {

        val user = userRepository.findByEmail(request.email)
            ?: throw IllegalArgumentException("User with email not found: ${request.email}")

        val hasDevProvider = user.socialAccounts.any { it.provider == Provider.DEV }
        if (!hasDevProvider) {
            throw IllegalArgumentException("Not registered as a developer account.")
        }

        // TODO : 동네가 설정되어 있지 않은 경우 "서울"로 설정
        if (user.neighborhood == null) {
            user.neighborhood = "서울"
        }

        val accessToken = jwtUtil.generateAccessToken(user.id!!)
        val jti = jwtUtil.getJtiFromToken(accessToken)
        val refreshToken = jwtUtil.generateRefreshToken(user.id, jti)

        user.refreshToken = refreshToken
        userRepository.save(user)

        return LoginResponse.from(accessToken, refreshToken, user)
    }
}
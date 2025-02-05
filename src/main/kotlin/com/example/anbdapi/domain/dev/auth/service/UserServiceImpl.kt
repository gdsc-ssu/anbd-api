package com.example.anbdapi.domain.dev.auth.service

import com.example.anbdapi.domain.dev.auth.entity.Gender
import com.example.anbdapi.domain.dev.auth.entity.Provider
import com.example.anbdapi.domain.dev.auth.dto.request.ProfileUpdateRequest
import com.example.anbdapi.domain.dev.auth.entity.User
import com.example.anbdapi.domain.dev.auth.entity.UserSocialAccount
import com.example.anbdapi.domain.dev.auth.repository.UserRepository
import com.example.anbdapi.domain.dev.auth.repository.UserSocialAccountRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val userSocialAccountRepository: UserSocialAccountRepository
) : UserService {

    /**
     * OAuth2 로그인 시 호출.
     *
     * 1. 공급자(registrationId)와 공급자 아이디(providerId)로 기존 소셜 계정이 있는지 확인
     * 2. 없으면 email로 기존 유저가 있는지 확인한 후, 있으면 소셜 계정만 추가
     * 3. 유저가 없으면 기본값(예: gender=OTHER, birthDate=2000-01-01 등)을 사용해 신규 유저와 소셜 계정 생성
     */
    override fun findOrCreateUser(
        registrationId: String,
        providerId: String,
        email: String,
        name: String,
        pictureUrl: String
    ): User {
        val providerEnum = Provider.valueOf(registrationId.uppercase())
        val existingSocialAccount = userSocialAccountRepository.findByProviderAndProviderId(providerEnum, providerId)
        if (existingSocialAccount != null) {
            return existingSocialAccount.user
        }

        val existingUser = userRepository.findByEmail(email)
        if (existingUser != null) {
            val newSocialAccount = UserSocialAccount(
                user = existingUser,
                provider = providerEnum,
                providerId = providerId,
                deletedAt = null
            )
            userSocialAccountRepository.save(newSocialAccount)
            return existingUser
        }

        // 신규 유저 생성 (필수값에 기본값 사용)
        val newUser = User(
            nickname = name,
            email = email,
            profileImage = pictureUrl,
            gender = Gender.OTHER,
            birthDate = LocalDate.of(2000, 1, 1),
            shareCategory = null,
            reliability = 0,
            refreshToken = null,
            deletedAt = null
        )
        userRepository.save(newUser)

        val newSocialAccount = UserSocialAccount(
            user = newUser,
            provider = providerEnum,
            providerId = providerId,
            deletedAt = null
        )
        userSocialAccountRepository.save(newSocialAccount)

        return newUser
    }

    override fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }

    override fun save(user: User) {
        userRepository.save(user)
    }

    override fun logoutUser(email: String): String {
        val user = userRepository.findByEmail(email) ?: throw RuntimeException("User not found")
        user.refreshToken = null
        userRepository.save(user)
        return "Logout success"
    }

    override fun updateUserProfile(email: String, request: ProfileUpdateRequest): User {
        val user = userRepository.findByEmail(email) ?: throw RuntimeException("User not found")
        user.gender = request.gender
        user.birthDate = request.birthDate

        // 선택적으로 닉네임, 프로필 이미지 업데이트
        request.nickname?.let { user.nickname = it }
        request.profileImage?.let { user.profileImage = it }
        request.shareCategory?.let { user.shareCategory = it }

        user.isProfileCompleted = true

        return userRepository.save(user)
    }

    override fun updateRefreshToken(email: String, refreshToken: String) {
        val user = userRepository.findByEmail(email) ?: throw RuntimeException("User not found")
        user.refreshToken = refreshToken

        userRepository.save(user)
    }

    override fun withdrawUser(email: String): String {
        val user = userRepository.findByEmail(email) ?: throw RuntimeException("User not found")
        userRepository.delete(user)
        return "Withdrawal successful"
    }
}
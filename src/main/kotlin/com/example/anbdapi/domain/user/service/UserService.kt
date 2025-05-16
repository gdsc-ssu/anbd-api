package com.example.anbdapi.domain.user.service

import com.example.anbdapi.domain.user.dto.request.ProfileUpdateRequest
import com.example.anbdapi.domain.user.dto.response.UserInformationResponse
import com.example.anbdapi.domain.user.dto.response.UserProfileResponse
import com.example.anbdapi.domain.user.entity.User
import com.example.anbdapi.domain.user.exception.UserNotFoundException
import com.example.anbdapi.domain.user.repository.UserRepository
import com.example.anbdapi.domain.userSocialAccount.entity.UserSocialAccount
import com.example.anbdapi.domain.userSocialAccount.repository.UserSocialAccountRepository
import com.example.anbdapi.support.enums.Gender
import com.example.anbdapi.support.enums.Provider
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.DefaultOAuth2User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userSocialAccountRepository: UserSocialAccountRepository
) {

    /**
     * OAuth2 로그인 시 호출.
     *
     * 1. 공급자(registrationId)와 공급자 아이디(providerId)로 기존 소셜 계정이 있는지 확인
     * 2. 없으면 email로 기존 유저가 있는지 확인한 후, 있으면 소셜 계정만 추가
     * 3. 유저가 없으면 기본값(예: gender=OTHER, birthDate=2000-01-01 등)을 사용해 신규 유저와 소셜 계정 생성
     */
    @Transactional
    fun findOrCreateUser(
        registrationId: String,
        providerId: String,
        email: String,
        nickname: String,
        profileImage: String
    ): User {

        val existingSocialAccount = userSocialAccountRepository.findSocialAccountWithUserByProviderAndProviderId(
            registrationId.uppercase(), providerId
        )

        if (existingSocialAccount != null) {
            val socialAccountId = existingSocialAccount.socialId
                ?: throw IllegalArgumentException("Social account ID is missing")
            val socialDeletedAt = existingSocialAccount.socialDeletedAt
            val userId = existingSocialAccount.userId
                ?: throw IllegalArgumentException("User ID is missing for social account")
            val userDeletedAt = existingSocialAccount.userDeletedAt

            if (socialDeletedAt != null || userDeletedAt != null) {
                userSocialAccountRepository.restoreById(socialAccountId)
                userRepository.restoreById(userId)
            }

            return userRepository.findById(userId).orElse(null)
                ?: throw UserNotFoundException("User not found")
        }

        val providerEnum = Provider.valueOf(registrationId.uppercase())

        val existingUser = userRepository.findByEmailIncludingDeleted(email)
        if (existingUser != null) {

            if (existingUser.deletedAt != null) {
                existingUser.deletedAt = null
                userRepository.save(existingUser)
            }

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
            nickname = nickname,
            email = email,
            profileImage = profileImage,
            gender = Gender.OTHER,
            birthDate = LocalDate.of(2000, 1, 1),
            shareCategories = mutableListOf(),
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

    @Transactional
    fun save(user: User) {
        userRepository.save(user)
    }

    @Transactional
    fun logoutUser(userId : Long): String {

        val user = userRepository.findById(userId).orElse(null)
            ?: throw UserNotFoundException("User not found")

        user.refreshToken = null

        userRepository.save(user)
        return "Logout success"
    }

    @Transactional
    fun updateUserProfile(userId: Long, request: ProfileUpdateRequest): User {
        val user = userRepository.findById(userId).orElse(null)
            ?: throw UserNotFoundException("User not found")

        user.gender = request.gender
        user.birthDate = request.birthDate
        user.neighborhood = request.neighborhood
        user.shareCategories = request.shareCategories

        user.isProfileCompleted = true

        return userRepository.save(user)
    }

    @Transactional
    fun updateRefreshToken(userId: Long, refreshToken: String) {
        val user = userRepository.findById(userId).orElse(null)
            ?: throw UserNotFoundException("User not found")
        user.refreshToken = refreshToken

        userRepository.save(user)
    }

    @Transactional
    fun deleteUser(userId : Long): String {

        val user = userRepository.findById(userId).orElse(null)
            ?: throw UserNotFoundException("User not found")

        userRepository.delete(user)

        return "Withdrawal successful"
    }

    fun getUserInfo(userId: Long): UserInformationResponse {
        val user = userRepository.findById(userId).orElse(null)
            ?: throw UserNotFoundException("User not found")

        return UserInformationResponse.from(user)
    }

    fun getCurrentUserNotNull(authentication: Authentication): User {
        require(authentication.isAuthenticated) { "Authentication Invalid." }

        val principal = authentication.principal as? DefaultOAuth2User
            ?: throw SecurityException("Invalid authentication principal")

        val userId = principal.attributes["userId"] as? String
            ?: throw SecurityException("UserId not found in authentication attributes.")

        return userRepository.findById(userId.toLong()).orElse(null)
            ?: throw UserNotFoundException("User not found")
    }

    fun getUserById(userId: Long): User {
        return userRepository.findById(userId).orElse(null)
            ?: throw UserNotFoundException("User not found")
    }

    @Transactional
    fun updateProfileImageAndNickname(userId: Long, nickname: String?, imageUrl: String?): User {
        val user = userRepository.findById(userId).orElse(null)
            ?: throw UserNotFoundException("User not found")

        nickname?.let {
            user.nickname = it
        }

        imageUrl?.let {
            user.profileImage = it
        }

        return userRepository.save(user)
    }

    fun getUserProfile(userId: Long): UserProfileResponse {
        val user = userRepository.findById(userId).orElse(null)
            ?: throw UserNotFoundException("User not found")

        return UserProfileResponse.from(user)
    }
}

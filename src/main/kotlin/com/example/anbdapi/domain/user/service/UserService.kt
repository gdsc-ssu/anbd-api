package com.example.anbdapi.domain.user.service

import com.example.anbdapi.domain.user.dto.request.ProfileUpdateRequest
import com.example.anbdapi.domain.user.dto.response.UserInformationResponse
import com.example.anbdapi.domain.user.entity.User
import com.example.anbdapi.domain.user.exception.UserNotFoundException
import com.example.anbdapi.domain.user.repository.UserRepository
import com.example.anbdapi.domain.userSocialAccount.entity.UserSocialAccount
import com.example.anbdapi.domain.userSocialAccount.repository.UserSocialAccountRepository
import com.example.anbdapi.support.enums.Gender
import com.example.anbdapi.support.enums.Provider
import org.springframework.stereotype.Service
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
    fun findOrCreateUser(
        registrationId: String,
        providerId: String,
        email: String,
        nickname: String,
        profileImage: String
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
            nickname = nickname,
            email = email,
            profileImage = profileImage,
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

    fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }

    fun save(user: User) {
        userRepository.save(user)
    }

    fun logoutUser(email: String): String {
        val user = userRepository.findByEmail(email) ?: throw UserNotFoundException("User not found")
        user.refreshToken = null
        userRepository.save(user)
        return "Logout success"
    }

    fun updateUserProfile(email: String, request: ProfileUpdateRequest): User {
        val user = userRepository.findByEmail(email) ?: throw UserNotFoundException("User not found")
        user.gender = request.gender
        user.birthDate = request.birthDate
        user.nickname = request.nickname;
        // 선택적으로 프로필 이미지, 관심사 업데이트
        request.profileImage?.let { user.profileImage = it }
        request.shareCategory?.let { user.shareCategory = it }

        user.isProfileCompleted = true

        return userRepository.save(user)
    }

    fun updateRefreshToken(email: String, refreshToken: String) {
        val user = userRepository.findByEmail(email) ?: throw UserNotFoundException("User not found")
        user.refreshToken = refreshToken

        userRepository.save(user)
    }

    fun withdrawUser(email: String): String {
        val user = userRepository.findByEmail(email) ?: throw UserNotFoundException("User not found")
        userRepository.delete(user)
        return "Withdrawal successful"
    }

    fun getUserInfo(email: String): UserInformationResponse {
        val user = userRepository.findByEmail(email) ?: throw UserNotFoundException("User not found")
        return UserInformationResponse(
                nickname = user.nickname,
                email = user.email,
                profileImage = user.profileImage,
                gender = user.gender,
                birthDate = user.birthDate,
                shareCategory = user.shareCategory,
                reliability = user.reliability
            )
    }
}

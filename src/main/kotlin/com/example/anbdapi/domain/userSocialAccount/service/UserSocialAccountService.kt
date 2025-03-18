package com.example.anbdapi.domain.userSocialAccount.service

import com.example.anbdapi.domain.userSocialAccount.entity.UserSocialAccount
import com.example.anbdapi.domain.userSocialAccount.repository.UserSocialAccountRepository
import com.example.anbdapi.support.enums.Provider
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class UserSocialAccountService(
    private val userSocialAccountRepository: UserSocialAccountRepository) {
    @Transactional
    fun getUserSocialAccount(provider: Provider, providerId: String): UserSocialAccount? {
        return userSocialAccountRepository.findByProviderAndProviderId(provider, providerId)
    }
}
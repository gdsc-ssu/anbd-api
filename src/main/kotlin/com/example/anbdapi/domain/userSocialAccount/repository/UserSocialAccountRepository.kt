package com.example.anbdapi.domain.userSocialAccount.repository

import com.example.anbdapi.domain.userSocialAccount.entity.UserSocialAccount
import com.example.anbdapi.support.enums.Provider
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserSocialAccountRepository : JpaRepository<UserSocialAccount, Long> {
    fun findByProviderAndProviderId(provider: Provider, providerId: String): UserSocialAccount?
}
package com.example.anbdapi.domain.auth.repository

import com.example.anbdapi.domain.auth.entity.Provider
import com.example.anbdapi.domain.auth.entity.UserSocialAccount
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserSocialAccountRepository : JpaRepository<UserSocialAccount, Long> {
    fun findByProviderAndProviderId(provider: Provider, providerId: String): UserSocialAccount?
}
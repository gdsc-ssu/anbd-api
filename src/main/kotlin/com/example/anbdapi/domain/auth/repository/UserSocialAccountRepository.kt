package com.example.anbdapi.domain.auth.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserSocialAccountRepository : JpaRepository<com.example.anbdapi.domain.auth.entity.UserSocialAccount, Long> {
    fun findByProviderAndProviderId(provider: com.example.anbdapi.domain.auth.entity.Provider, providerId: String): com.example.anbdapi.domain.auth.entity.UserSocialAccount?
}
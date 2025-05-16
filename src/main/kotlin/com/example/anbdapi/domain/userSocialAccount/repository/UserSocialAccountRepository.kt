package com.example.anbdapi.domain.userSocialAccount.repository

import com.example.anbdapi.domain.user.dto.response.SocialAccountUserResponse
import com.example.anbdapi.domain.userSocialAccount.entity.UserSocialAccount
import com.example.anbdapi.support.enums.Provider
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserSocialAccountRepository : JpaRepository<UserSocialAccount, Long> {
    fun findByProviderAndProviderId(provider: Provider, providerId: String): UserSocialAccount?

    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE user_social_accounts SET deleted_at = NULL WHERE id = :id", nativeQuery = true)
    fun restoreById(@Param("id") id: Long)

    // TODO : 추후 QueryDSL로 바꾸기
    @Query("""
    SELECT  sa.id   AS socialId,
            sa.deleted_at AS socialDeletedAt,
            u.id    AS userId,
            u.deleted_at  AS userDeletedAt
    FROM    user_social_accounts sa
    JOIN    users u ON sa.user_id = u.id
    WHERE   sa.provider = :provider AND sa.provider_id = :providerId
    """, nativeQuery = true)
    fun findSocialAccountWithUserByProviderAndProviderId(
        @Param("provider") provider: String,
        @Param("providerId") providerId: String
    ): SocialAccountUserResponse?
}
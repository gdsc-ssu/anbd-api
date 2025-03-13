package com.example.anbdapi.domain.user.dto.response

import com.example.anbdapi.domain.user.entity.User

data class UserProfileResponse(
    val userId: Long,
    val nickname: String,
    val profileImage: String?,
    val neighborhood: String?,
    val reliability: Int
) {
    companion object {
        fun from(user: User): UserProfileResponse {
            return UserProfileResponse(
                userId = user.id!!,
                nickname = user.nickname,
                profileImage = user.profileImage,
                neighborhood = user.neighborhood,
                reliability = user.reliability
            )
        }
    }
}
package com.example.anbdapi.domain.user.dto.response

import com.example.anbdapi.domain.user.entity.User

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val profileComplete: Boolean
) {
    companion object {
        fun from(accessToken: String, refreshToken: String, user: User): LoginResponse {
            return LoginResponse(
                accessToken = accessToken,
                refreshToken = refreshToken,
                profileComplete = user.isProfileCompleted
            )
        }
    }
}
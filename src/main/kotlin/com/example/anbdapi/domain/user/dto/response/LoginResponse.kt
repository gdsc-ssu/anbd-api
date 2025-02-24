package com.example.anbdapi.domain.user.dto.response

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val profileComplete: Int
)
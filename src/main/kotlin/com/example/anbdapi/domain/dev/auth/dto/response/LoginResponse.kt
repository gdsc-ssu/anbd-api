package com.example.anbdapi.domain.dev.auth.dto.response

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val profileComplete: Int
)
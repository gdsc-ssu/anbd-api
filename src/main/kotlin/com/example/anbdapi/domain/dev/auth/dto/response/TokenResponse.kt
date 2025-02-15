package com.example.anbdapi.domain.dev.auth.dto.response

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String
)
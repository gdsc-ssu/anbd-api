package com.example.anbdapi.domain.auth.dto.response

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String
)
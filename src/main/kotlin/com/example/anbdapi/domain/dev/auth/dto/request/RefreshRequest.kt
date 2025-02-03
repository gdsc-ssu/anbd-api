package com.example.anbdapi.domain.dev.auth.dto.request

data class RefreshRequest(
    val email: String,
    val accessToken: String,
    val refreshToken: String
)
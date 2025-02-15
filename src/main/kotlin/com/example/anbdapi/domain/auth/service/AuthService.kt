package com.example.anbdapi.domain.auth.service

interface AuthService {
    fun refreshAccessToken(request: com.example.anbdapi.domain.auth.dto.request.RefreshRequest): com.example.anbdapi.domain.auth.dto.response.TokenResponse
}

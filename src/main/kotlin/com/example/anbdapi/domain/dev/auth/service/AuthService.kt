package com.example.anbdapi.domain.dev.auth.service

import com.example.anbdapi.domain.dev.auth.dto.request.RefreshRequest
import com.example.anbdapi.domain.dev.auth.dto.response.TokenResponse

interface AuthService {
    fun refreshAccessToken(request: RefreshRequest): TokenResponse
}

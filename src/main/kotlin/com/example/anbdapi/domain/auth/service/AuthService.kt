package com.example.anbdapi.domain.auth.service

import com.example.anbdapi.domain.auth.dto.request.RefreshRequest
import com.example.anbdapi.domain.auth.dto.response.TokenResponse

interface AuthService {
    fun refreshAccessToken(request: RefreshRequest): TokenResponse
}

package com.example.anbdapi.domain.auth.controller

import com.example.anbdapi.domain.auth.dto.request.RefreshRequest
import com.example.anbdapi.domain.auth.dto.response.TokenResponse
import com.example.anbdapi.support.response.AnbdApiResponse
import org.springframework.web.bind.annotation.RequestBody

interface AuthController {

    fun refreshAccessToken(@RequestBody request: RefreshRequest): AnbdApiResponse<TokenResponse>
}
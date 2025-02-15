package com.example.anbdapi.domain.dev.auth.controller

import com.example.anbdapi.domain.dev.auth.dto.request.RefreshRequest
import com.example.anbdapi.domain.dev.auth.dto.response.TokenResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody

interface AuthController {

    fun refreshAccessToken(@RequestBody request: RefreshRequest): ResponseEntity<TokenResponse>
}
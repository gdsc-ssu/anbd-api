package com.example.anbdapi.domain.auth.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody

interface AuthController {

    fun refreshAccessToken(@RequestBody request: com.example.anbdapi.domain.auth.dto.request.RefreshRequest): ResponseEntity<com.example.anbdapi.domain.auth.dto.response.TokenResponse>
}
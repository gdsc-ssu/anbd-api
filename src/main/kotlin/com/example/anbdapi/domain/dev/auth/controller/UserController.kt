package com.example.anbdapi.domain.dev.auth.controller

import com.example.anbdapi.domain.dev.auth.dto.request.LogoutRequest
import com.example.anbdapi.domain.dev.auth.dto.request.ProfileUpdateRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.RequestBody

interface UserController {

    fun logout(@RequestBody request: LogoutRequest): ResponseEntity<String>

    fun updateProfile(
        @AuthenticationPrincipal oAuth2User: OAuth2User,
        @RequestBody request: ProfileUpdateRequest
    ): ResponseEntity<String>

    fun withdraw(@AuthenticationPrincipal oAuth2User: OAuth2User): ResponseEntity<String>
}
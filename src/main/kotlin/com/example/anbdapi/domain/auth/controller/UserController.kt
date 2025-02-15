package com.example.anbdapi.domain.auth.controller

import com.example.anbdapi.domain.auth.dto.request.LogoutRequest
import com.example.anbdapi.domain.auth.dto.request.ProfileUpdateRequest
import com.example.anbdapi.support.response.AnbdApiResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.RequestBody

interface UserController {

    fun logout(@RequestBody request: LogoutRequest): AnbdApiResponse<String>

    fun updateProfile(
        @AuthenticationPrincipal oAuth2User: OAuth2User,
        @RequestBody request: ProfileUpdateRequest
    ): AnbdApiResponse<String>

    fun withdraw(@AuthenticationPrincipal oAuth2User: OAuth2User): AnbdApiResponse<String>
}
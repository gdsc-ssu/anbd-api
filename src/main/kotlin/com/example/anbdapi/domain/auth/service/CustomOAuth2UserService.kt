package com.example.anbdapi.domain.auth.service

import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User

interface CustomOAuth2UserService {
    fun loadUser(userRequest: OAuth2UserRequest): OAuth2User
}

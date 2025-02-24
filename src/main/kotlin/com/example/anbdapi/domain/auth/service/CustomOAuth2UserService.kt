package com.example.anbdapi.domain.auth.service

import com.example.anbdapi.domain.user.service.UserService
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOAuth2UserService(
    private val userService: UserService
) : DefaultOAuth2UserService() {

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(userRequest)
        // registrationId는 GOOGLE 혹은 APPLE
        val registrationId = userRequest.clientRegistration.registrationId

        val attributes = oAuth2User.attributes

        val providerId = attributes["sub"] as? String ?: attributes["id"] as? String
        ?: throw IllegalArgumentException("Provider ID not found")
        val email = attributes["email"] as? String
            ?: throw IllegalArgumentException("Email not found in OAuth2 attributes")
        val nickname = attributes["name"] as? String ?: "Unknown"
        val profileImage = attributes["picture"] as? String ?: ""

        userService.findOrCreateUser(registrationId, providerId, email, nickname, profileImage)
        return oAuth2User
    }
}
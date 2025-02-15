package com.example.anbdapi.domain.auth.service

import com.example.anbdapi.domain.auth.dto.request.ProfileUpdateRequest
import com.example.anbdapi.domain.auth.entity.User


interface UserService {
    fun findOrCreateUser(
        registrationId: String,
        providerId: String,
        email: String,
        name: String,
        pictureUrl: String
    ): User

    fun findByEmail(email: String): User?
    fun save(user: User)
    fun logoutUser(email: String): String
    fun updateUserProfile(email: String, request: ProfileUpdateRequest): User
    fun updateRefreshToken(email: String, refreshToken: String)
    fun withdrawUser(email: String): String
}
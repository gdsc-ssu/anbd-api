package com.example.anbdapi.domain.auth.service


interface UserService {
    fun findOrCreateUser(
        registrationId: String,
        providerId: String,
        email: String,
        name: String,
        pictureUrl: String
    ): com.example.anbdapi.domain.auth.entity.User

    fun findByEmail(email: String): com.example.anbdapi.domain.auth.entity.User?
    fun save(user: com.example.anbdapi.domain.auth.entity.User)
    fun logoutUser(email: String): String
    fun updateUserProfile(email: String, request: com.example.anbdapi.domain.auth.dto.request.ProfileUpdateRequest): com.example.anbdapi.domain.auth.entity.User
    fun updateRefreshToken(email: String, refreshToken: String)
    fun withdrawUser(email: String): String
}
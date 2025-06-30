package com.example.anbdapi.domain.user.dto.response

import java.time.LocalDateTime

interface SocialAccountUserResponse {
    val socialId: Long?
    val socialDeletedAt: LocalDateTime?
    val userId: Long?
    val userDeletedAt: LocalDateTime?
}
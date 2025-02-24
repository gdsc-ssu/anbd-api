package com.example.anbdapi.domain.auth.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class RefreshRequest(

    @field:NotBlank(message = "이메일은 필수 입력입니다.")
    @field:Email(message = "유효한 이메일 형식이어야 합니다.")
    val email: String,

    @field:NotBlank(message = "Access Token은 필수 입력입니다.")
    val accessToken: String,

    @field:NotBlank(message = "Refresh Token은 필수 입력입니다.")
    val refreshToken: String
)
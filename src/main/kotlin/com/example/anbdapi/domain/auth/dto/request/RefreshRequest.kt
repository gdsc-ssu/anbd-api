package com.example.anbdapi.domain.auth.dto.request

import jakarta.validation.constraints.NotBlank

data class RefreshRequest(

    @field:NotBlank(message = "Refresh Token은 필수 입력입니다.")
    val refreshToken: String
)
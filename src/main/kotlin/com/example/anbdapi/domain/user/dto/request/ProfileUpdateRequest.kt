package com.example.anbdapi.domain.user.dto.request

import com.example.anbdapi.support.enums.Gender
import com.example.anbdapi.support.enums.ShareCategory
import java.time.LocalDate

data class ProfileUpdateRequest(
    val gender: Gender,
    val birthDate: LocalDate,
    val nickname: String,
    val profileImage: String? = null,
    val shareCategory: ShareCategory? = null
)
package com.example.anbdapi.domain.dev.auth.dto.request

import com.example.anbdapi.domain.dev.auth.entity.Gender
import com.example.anbdapi.domain.dev.auth.entity.ShareCategory
import java.time.LocalDate

data class ProfileUpdateRequest(
    val gender: Gender,          // 예: MALE, FEMALE, OTHER 등 (enum으로 정의되어 있다고 가정)
    val birthDate: LocalDate,    // 예: 1990-05-20
    val nickname: String? = null,      // 선택적: 기존 닉네임을 변경하고 싶을 경우
    val profileImage: String? = null,   // 선택적: 프로필 이미지 URL 업데이트 등
    val shareCategory: ShareCategory? = null
)
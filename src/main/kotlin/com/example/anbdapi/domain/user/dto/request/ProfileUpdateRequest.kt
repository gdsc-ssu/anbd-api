package com.example.anbdapi.domain.user.dto.request

import com.example.anbdapi.support.enums.Gender
import com.example.anbdapi.support.enums.ShareCategory
import java.time.LocalDate

data class ProfileUpdateRequest(
    val gender: Gender,
    val birthDate: LocalDate,
    val neighborhood: String,
    val shareCategories: MutableList<ShareCategory> = mutableListOf()
) {
    companion object {
        fun from(gender: Gender, birthDate: LocalDate, neighborhood: String,
                 shareCategories: MutableList<ShareCategory> = mutableListOf()): ProfileUpdateRequest {
            return ProfileUpdateRequest(
                gender = gender,
                birthDate = birthDate,
                neighborhood = neighborhood,
                shareCategories = shareCategories
            )
        }
    }
}
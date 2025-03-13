package com.example.anbdapi.domain.user.dto.response

import com.example.anbdapi.domain.user.entity.User
import com.example.anbdapi.support.enums.Gender
import com.example.anbdapi.support.enums.ShareCategory
import java.time.LocalDate

data class UserInformationResponse(
    val userId: Long,
    val nickname: String,
    val email: String,
    val profileImage: String?,
    val gender: Gender,
    val birthDate: LocalDate,
    val neighborhood: String?,
    val shareCategories: List<ShareCategory>,
    val reliability: Int
) {
    companion object {
        fun from(user: User): UserInformationResponse {
            return UserInformationResponse(
                userId = user.id!!,
                nickname = user.nickname,
                email = user.email,
                profileImage = user.profileImage,
                gender = user.gender,
                birthDate = user.birthDate,
                neighborhood = user.neighborhood,
                shareCategories = user.shareCategories,
                reliability = user.reliability
            )
        }
    }
}
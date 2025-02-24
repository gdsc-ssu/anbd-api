package com.example.anbdapi.domain.user.dto.response

import com.example.anbdapi.support.enums.Gender
import com.example.anbdapi.support.enums.ShareCategory
import java.time.LocalDate

data class UserInformationResponse (
    val nickname : String,
    val email : String,
    val profileImage : String?,
    val gender : Gender,
    val birthDate : LocalDate,
    val shareCategory: ShareCategory?,
    val reliability: Int
)
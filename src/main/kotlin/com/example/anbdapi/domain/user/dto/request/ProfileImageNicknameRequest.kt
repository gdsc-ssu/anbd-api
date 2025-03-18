package com.example.anbdapi.domain.user.dto.request

import org.springframework.web.multipart.MultipartFile

data class ProfileImageNicknameRequest(
    val nickname: String?,
    val profileImage: MultipartFile?
) {
    companion object {
        fun from(nickname: String?, profileImage: MultipartFile?): ProfileImageNicknameRequest {
            return ProfileImageNicknameRequest(
                nickname = nickname,
                profileImage = profileImage
            )
        }
    }
}
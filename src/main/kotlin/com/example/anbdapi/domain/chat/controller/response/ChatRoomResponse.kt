package com.example.anbdapi.domain.chat.controller.response

import com.example.anbdapi.domain.sharepost.controller.response.SharePostResponse
import com.example.anbdapi.domain.user.dto.response.UserProfileResponse

data class ChatRoomResponse(
    val id: Long,
    val partner: UserProfileResponse,
    val sharePost: SharePostResponse,
) {
    companion object {
        fun from(
            id: Long,
            partner: UserProfileResponse,
            sharePost: SharePostResponse,
        ): ChatRoomResponse {
            return ChatRoomResponse(
                id = id,
                partner = partner,
                sharePost = sharePost,
            )
        }
    }
}
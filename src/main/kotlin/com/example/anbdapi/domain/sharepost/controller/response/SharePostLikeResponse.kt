package com.example.anbdapi.domain.sharepost.controller.response

import com.example.anbdapi.domain.sharepost.entity.SharePostLike
import java.time.LocalDateTime

data class SharePostLikeResponse(
    val id: Long,
    val userId: Long,
    val postId: Long,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(like: SharePostLike): SharePostLikeResponse {
            return SharePostLikeResponse(
                id = like.id!!,
                userId = like.user.id!!,
                postId = like.sharePost.id!!,
                createdAt = like.createdAt,
                updatedAt = like.updatedAt
            )
        }
    }
}

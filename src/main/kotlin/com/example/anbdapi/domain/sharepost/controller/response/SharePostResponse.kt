package com.example.anbdapi.domain.sharepost.controller.response

import com.example.anbdapi.domain.sharepost.entity.SharePost
import com.example.anbdapi.domain.sharepost.entity.SharePostLike
import com.example.anbdapi.support.enums.ShareCategory
import com.example.anbdapi.support.enums.ShareType
import java.time.LocalDateTime

data class SharePostResponse(
    val id: Long,
    val userId: Long,
    val title: String,
    val category: ShareCategory,
    val content: String,
    val images: List<String>,
    val type: ShareType,
    val description: String?,
    val location: String?,
    val isSold: Boolean,
    val hits: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val likeCount: Int,
    val isLiked: Boolean
) {
    companion object {
        fun from(post: SharePost, currentUserId: Long?, likes: List<SharePostLike>): SharePostResponse {
            return SharePostResponse(
                id = post.id!!,
                userId = post.user.id!!,
                title = post.title,
                category = post.category,
                content = post.content,
                images = post.imageUrls,
                type = post.type,
                description = post.description,
                location = post.location,
                isSold = post.isSold,
                hits = post.hits,
                createdAt = post.createdAt,
                updatedAt = post.updatedAt,
                likeCount = likes.size,
                isLiked = currentUserId?.let { userId ->
                    likes.any { it.user.id == userId }
                } ?: false
            )
        }

        fun from(post: SharePost): SharePostResponse {
            return SharePostResponse(
                id = post.id!!,
                userId = post.user.id!!,
                title = post.title,
                category = post.category,
                content = post.content,
                images = post.imageUrls,
                type = post.type,
                description = post.description,
                location = post.location,
                isSold = post.isSold,
                hits = post.hits,
                createdAt = post.createdAt,
                updatedAt = post.updatedAt,
                likeCount = 0,
                isLiked = false
            )
        }
    }
}

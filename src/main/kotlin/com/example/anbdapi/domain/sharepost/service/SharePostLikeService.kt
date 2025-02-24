package com.example.anbdapi.domain.sharepost.service

import com.example.anbdapi.domain.sharepost.controller.response.SharePostLikeResponse
import com.example.anbdapi.domain.sharepost.entity.SharePostLike
import com.example.anbdapi.domain.sharepost.exception.SharePostLikeBadRequestException
import com.example.anbdapi.domain.sharepost.exception.SharePostNotFoundException
import com.example.anbdapi.domain.sharepost.repository.SharePostLikeRepository
import com.example.anbdapi.domain.sharepost.repository.SharePostRepository
import com.example.anbdapi.domain.user.exception.UserNotFoundException
import com.example.anbdapi.domain.user.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class SharePostLikeService(
    private val sharePostLikeRepository: SharePostLikeRepository,
    private val sharePostRepository: SharePostRepository,
    private val userRepository: UserRepository
) {

    @Transactional
    fun addLike(email: String, postId: Long): SharePostLikeResponse {
        val user = userRepository.findByEmail(email)
            ?: throw UserNotFoundException("User not found")

        val post = sharePostRepository.findByIdOrNull(postId)
            ?: throw SharePostNotFoundException("Post not found")

        if (sharePostLikeRepository.existsByUserAndSharePost(user, post)) {
            throw SharePostLikeBadRequestException("You have already liked this post")
        }

        val like = SharePostLike(
            user = user,
            sharePost = post
        )

        val savedLike = sharePostLikeRepository.save(like)

        return SharePostLikeResponse.from(savedLike)
    }

    @Transactional
    fun removeLike(email: String, postId: Long) {
        val user = userRepository.findByEmail(email)
            ?: throw UserNotFoundException("User not found")

        val post = sharePostRepository.findByIdOrNull(postId)
            ?: throw SharePostNotFoundException("Post not found")

        val like = sharePostLikeRepository.findByUserAndSharePost(user, post)
            ?: throw SharePostLikeBadRequestException("You have not liked this post")

        sharePostLikeRepository.delete(like)
    }
}

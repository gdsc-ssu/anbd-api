package com.example.anbdapi.domain.sharepost.service

import com.example.anbdapi.domain.sharepost.controller.response.SharePostLikeResponse
import com.example.anbdapi.domain.sharepost.entity.SharePostLike
import com.example.anbdapi.domain.sharepost.exception.SharePostLikeBadRequestException
import com.example.anbdapi.domain.sharepost.exception.SharePostNotFoundException
import com.example.anbdapi.domain.sharepost.repository.SharePostLikeRepository
import com.example.anbdapi.domain.sharepost.repository.SharePostRepository
import com.example.anbdapi.domain.user.repository.UserRepository
import com.example.anbdapi.domain.user.service.UserApplicationService
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class SharePostLikeService(
    private val sharePostLikeRepository: SharePostLikeRepository,
    private val sharePostRepository: SharePostRepository,
    private val userApplicationService: UserApplicationService,
    private val userRepository: UserRepository
) {

    @Transactional
    fun addLike(authentication: Authentication, postId: Long): SharePostLikeResponse {
        val currentUser = userApplicationService.getCurrentUser(authentication)

        val post = sharePostRepository.findByIdOrNull(postId)
            ?: throw SharePostNotFoundException("Post not found")

        if (sharePostLikeRepository.existsByUserAndSharePost(currentUser, post)) {
            throw SharePostLikeBadRequestException("You have already liked this post")
        }

        val like = SharePostLike(
            user = currentUser,
            sharePost = post
        )

        val savedLike = sharePostLikeRepository.save(like)

        return SharePostLikeResponse.from(savedLike)
    }

    @Transactional
    fun removeLike(authentication: Authentication, postId: Long) {
        val currentUser = userApplicationService.getCurrentUser(authentication)

        val post = sharePostRepository.findByIdOrNull(postId)
            ?: throw SharePostNotFoundException("Post not found")

        val like = sharePostLikeRepository.findByUserAndSharePost(currentUser, post)
            ?: throw SharePostLikeBadRequestException("You have not liked this post")

        sharePostLikeRepository.delete(like)
    }
}

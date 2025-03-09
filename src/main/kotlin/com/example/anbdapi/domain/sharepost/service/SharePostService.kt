package com.example.anbdapi.domain.sharepost.service

import com.example.anbdapi.domain.sharepost.controller.request.SharePostRequest
import com.example.anbdapi.domain.sharepost.controller.response.SharePostResponse
import com.example.anbdapi.domain.sharepost.entity.SharePost
import com.example.anbdapi.domain.sharepost.exception.SharePostNotFoundException
import com.example.anbdapi.domain.sharepost.repository.SharePostLikeRepository
import com.example.anbdapi.domain.sharepost.repository.SharePostRepository
import com.example.anbdapi.domain.user.exception.UserNotFoundException
import com.example.anbdapi.domain.user.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class SharePostService(
    private val sharePostRepository: SharePostRepository,
    private val sharePostLikeRepository: SharePostLikeRepository,
    private val userRepository: UserRepository
) {
    @Transactional
    fun createPost(userId: Long, request: SharePostRequest): SharePost {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw UserNotFoundException("User not found")

        val post = SharePost(
            user = user,
            title = request.title,
            category = request.category,
            content = request.content,
            images = request.images,
            type = request.type,
            description = request.description
        )

        return sharePostRepository.save(post)
    }

    fun getPostById(email: String, postId: Long): SharePostResponse {
        val currentUser = userRepository.findByEmail(email)
            ?: throw UserNotFoundException("Current user not found")

        val post = sharePostRepository.findByIdOrNull(postId)
            ?: throw SharePostNotFoundException("Post not found")

        val likes = sharePostLikeRepository.findBySharePost(post)

        return SharePostResponse.from(post, currentUser.id, likes)
    }

    fun getUserPosts(email: String, userId: Long, pageable: Pageable): Page<SharePostResponse> {
        val currentUser = userRepository.findByEmail(email)
            ?: throw UserNotFoundException("Current user not found")

        val user = userRepository.findByIdOrNull(userId)
            ?: throw UserNotFoundException("User not found")

        val posts = sharePostRepository.findByUser(user, pageable)
        val postIds =  posts.map { it.id!!}.content.toList()

        val allLikes = sharePostLikeRepository.findBySharePostIdIn(postIds)

        return posts.map { post ->
            val postLikes = allLikes.filter { it.sharePost.id == post.id }
            SharePostResponse.from(post, currentUser.id, postLikes)
        }
    }

    @Transactional
    fun updatePost(email: String, postId: Long, request: SharePostRequest): SharePostResponse {
        val currentUser = userRepository.findByEmail(email)
            ?: throw UserNotFoundException("Current user not found")

        val post = sharePostRepository.findByIdOrNull(postId)
            ?: throw SharePostNotFoundException("Post not found")

        val likes = sharePostLikeRepository.findBySharePost(post)

        post.title = request.title
        post.category = request.category
        post.content = request.content
        post.images = request.images
        post.type = request.type
        post.description = request.description

        return SharePostResponse.from(post, currentUser.id, likes)
    }

    @Transactional
    fun deletePost(postId: Long) {
        if (!sharePostRepository.existsById(postId)) {
            throw SharePostNotFoundException("Post not found")
        }
        sharePostRepository.deleteById(postId)
    }
}

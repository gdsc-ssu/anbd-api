package com.example.anbdapi.domain.sharepost.service

import com.example.anbdapi.domain.sharepost.controller.request.SharePostRequest
import com.example.anbdapi.domain.sharepost.controller.response.SharePostResponse
import com.example.anbdapi.domain.sharepost.entity.SharePost
import com.example.anbdapi.domain.sharepost.exception.SharePostNotFoundException
import com.example.anbdapi.domain.sharepost.repository.SharePostLikeRepository
import com.example.anbdapi.domain.sharepost.repository.SharePostRepository
import com.example.anbdapi.domain.user.exception.UserNotFoundException
import com.example.anbdapi.domain.user.repository.UserRepository
import com.example.anbdapi.domain.user.service.UserApplicationService
import com.example.anbdapi.domain.user.service.UserImageService
import com.example.anbdapi.support.enums.ShareCategory
import com.example.anbdapi.support.enums.ShareType
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class SharePostService(
    private val userApplicationService: UserApplicationService,
    private val userImageService: UserImageService,
    private val sharePostRepository: SharePostRepository,
    private val sharePostLikeRepository: SharePostLikeRepository,
    private val userRepository: UserRepository
) {
    @Transactional
    fun createPost(authentication: Authentication, request: SharePostRequest): SharePost {
        val currentUser = userApplicationService.getCurrentUser(authentication)

        val imageUrls: MutableList<String> = mutableListOf()
        request.images.map {
            imageUrls.add(userImageService.uploadSharePostImage(currentUser, it))
        }

        val post = SharePost(
            user = currentUser,
            title = request.title,
            category = request.category,
            content = request.content,
            imageUrls = imageUrls,
            type = request.type,
            location = currentUser.neighborhood,    // TODO: 사용자가 인증한 동네로 변경
            description = request.description
        )

        return sharePostRepository.save(post)
    }

    fun getPostById(authentication: Authentication, postId: Long): SharePostResponse {
        val currentUserId = userApplicationService.getCurrentUserId(authentication)

        val post = sharePostRepository.findByIdOrNull(postId)
            ?: throw SharePostNotFoundException("Post not found")

        post.hits += 1

        val likes = sharePostLikeRepository.findBySharePost(post)

        return SharePostResponse.from(post, currentUserId, likes)
    }

    fun getPosts(authentication: Authentication, keyword: String?, location: String?, category: ShareCategory?, type: ShareType?, pageable: Pageable): Page<SharePostResponse> {
        val currentUserId = userApplicationService.getCurrentUserId(authentication)

        val posts = sharePostRepository.findPosts(
            keyword = keyword,
            location = location,
            category = category,
            type = type,
            pageable = pageable
        )

        val postIds =  posts.map { it.id!!}.content.toList()

        val allLikes = sharePostLikeRepository.findBySharePostIdIn(postIds)

        return posts.map { post ->
            val postLikes = allLikes.filter { it.sharePost.id == post.id }
            SharePostResponse.from(post, currentUserId, postLikes)
        }
    }

    fun getUserPosts(authentication: Authentication, userId: Long, pageable: Pageable): Page<SharePostResponse> {
        val currentUserId = userApplicationService.getCurrentUserId(authentication)

        val user = userRepository.findByIdOrNull(userId)
            ?: throw UserNotFoundException("User not found")

        val posts = sharePostRepository.findByUser(user, pageable)
        val postIds =  posts.map { it.id!!}.content.toList()

        val allLikes = sharePostLikeRepository.findBySharePostIdIn(postIds)

        return posts.map { post ->
            val postLikes = allLikes.filter { it.sharePost.id == post.id }
            SharePostResponse.from(post, currentUserId, postLikes)
        }
    }

    @Transactional
    fun updatePost(authentication: Authentication, postId: Long, request: SharePostRequest): SharePostResponse {
        val currentUser = userApplicationService.getCurrentUser(authentication)

        val post = sharePostRepository.findByIdOrNull(postId)
            ?: throw SharePostNotFoundException("Post not found")

        val likes = sharePostLikeRepository.findBySharePost(post)

        // TODO: 삭제 할 이미지만 삭제하고 추가할 이미지만 추가하도록 수정
        post.imageUrls.map {
            userImageService.deleteImage(it)
        }

        val imageUrls: MutableList<String> = mutableListOf()
        request.images.map {
            imageUrls.add(userImageService.uploadSharePostImage(currentUser, it))
        }

        post.title = request.title
        post.category = request.category
        post.content = request.content
        post.imageUrls = imageUrls
        post.type = request.type
        post.description = request.description

        return SharePostResponse.from(post, currentUser.id!!, likes)
    }

    @Transactional
    fun deletePost(postId: Long) {
        if (!sharePostRepository.existsById(postId)) {
            throw SharePostNotFoundException("Post not found")
        }
        sharePostRepository.deleteById(postId)
    }
}

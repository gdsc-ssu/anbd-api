package com.example.anbdapi.domain.sharepost.service

import com.example.anbdapi.domain.sharepost.controller.request.SharePostRequest
import com.example.anbdapi.domain.sharepost.controller.response.SharePostResponse
import com.example.anbdapi.domain.sharepost.entity.SharePost
import com.example.anbdapi.domain.sharepost.exception.SharePostNotFoundException
import com.example.anbdapi.domain.sharepost.repository.SharePostLikeRepository
import com.example.anbdapi.domain.sharepost.repository.SharePostQuerydslRepository
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
    private val sharePostQuerydslRepository: SharePostQuerydslRepository,
    private val sharePostLikeRepository: SharePostLikeRepository,
    private val userRepository: UserRepository,
    private val sharePostDescriptionGenerator: SharePostDescriptionGenerator,
    private val sharePostCategoryGenerator: SharePostCategoryGenerator

) {
    @Transactional
    fun createPost(authentication: Authentication, request: SharePostRequest): SharePost {
        val currentUser = userApplicationService.getCurrentUser(authentication)

        val imageUrls: MutableList<String> = mutableListOf()
        request.images.map {
            imageUrls.add(userImageService.uploadSharePostImage(currentUser, it))
        }

        // TODO: title, content 기반 gemini ai 활용 문구 생성
        val description = sharePostDescriptionGenerator.generateDescription(request.title, request.content)

        // TODO: title, content 기반 gemini 활용하여 category type 결정
        val category = sharePostCategoryGenerator.categorizeItem(request.title, request.content)



        val post = SharePost(
            user = currentUser,
            title = request.title,
            category = category,
            content = request.content,
            imageUrls = imageUrls,
            type = request.type,
            neighborhood = currentUser.neighborhood!!,    // TODO: 사용자가 인증한 동네로 변경
            description = description
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
        val currentUser = userApplicationService.getCurrentUser(authentication)

        val posts = sharePostQuerydslRepository.findPosts(
            keyword = keyword,
            location = location ?: currentUser.neighborhood!!,    // TODO: 사용자가 인증한 동네로 변경
            category = category,
            type = type,
            pageable = pageable
        )

        val postIds =  posts.map { it.id!!}.content.toList()

        val allLikes = sharePostLikeRepository.findBySharePostIdIn(postIds)

        return posts.map { post ->
            val postLikes = allLikes.filter { it.sharePost.id == post.id }
            SharePostResponse.from(post, currentUser.id!!, postLikes)
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

        // TODO: title과 content update시 gemini ai 활용 설명도 update
        val updatedDescription = sharePostDescriptionGenerator.generateDescription(request.title, request.content)

        // TODO: title과 content update시 gemini 활용 category type도 update
        val updatedCategory = sharePostCategoryGenerator.categorizeItem(request.title, request.content)


        post.title = request.title
        post.category = updatedCategory
        post.content = request.content
        post.imageUrls = imageUrls
        post.type = request.type
        post.description = updatedDescription

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

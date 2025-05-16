package com.example.anbdapi.domain.user.service

import com.example.anbdapi.domain.sharepost.controller.response.SharePostResponse
import com.example.anbdapi.domain.sharepost.repository.BiddingRepository
import com.example.anbdapi.domain.sharepost.repository.SharePostLikeRepository
import com.example.anbdapi.domain.sharepost.repository.SharePostRepository
import com.example.anbdapi.domain.user.entity.User
import com.example.anbdapi.domain.user.exception.UserNotFoundException
import com.example.anbdapi.domain.user.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class UserContentService(
    private val userRepository: UserRepository,
    private val sharePostLikeRepository: SharePostLikeRepository,
    private val sharePostRepository: SharePostRepository,
    private val biddingRepository: BiddingRepository,
) {

    fun getLikedPosts(userId: Long, pageable: Pageable): Page<SharePostResponse> {
        val user = userRepository.findById(userId).orElse(null)
            ?: throw UserNotFoundException("User not found")

        val likedPosts = sharePostLikeRepository.findByUser(user, pageable)

        val postIds = likedPosts.content.map { it.sharePost.id!! }

        val allLikes = if (postIds.isEmpty()) {
            emptyList()
        } else {
            sharePostLikeRepository.findBySharePostIdIn(postIds)
        }
        val allBiddings = biddingRepository.findAllByUser(user)

        return likedPosts.map { like ->
            val post = like.sharePost
            val postLikes = allLikes.filter { it.sharePost.id == post.id }
            val postBidding = allBiddings.firstOrNull { it.sharePost.id == post.id }
            SharePostResponse.from(post, userId, postLikes, postBidding)
        }
    }

    fun getMySharedPosts(userId: Long, pageable: Pageable): Page<SharePostResponse> {
        val user = userRepository.findById(userId).orElse(null)
            ?: throw UserNotFoundException("User not found")

        val sharedPosts = sharePostRepository.findByUser(user, pageable)

        val postIds = sharedPosts.content.map { it.id!! }

        val allLikes = if (postIds.isEmpty()) {
            emptyList()
        } else {
            sharePostLikeRepository.findBySharePostIdIn(postIds)
        }

        return sharedPosts.map { post ->
            val postLikes = allLikes.filter { it.sharePost.id == post.id }
            SharePostResponse.from(post, userId, postLikes)
        }
    }

    fun updateNeighborhood(currentUser: User, newNeighborhood: String) {

        if (currentUser.neighborhood != newNeighborhood) {
            currentUser.neighborhood = newNeighborhood
            userRepository.save(currentUser)
        }
    }
}
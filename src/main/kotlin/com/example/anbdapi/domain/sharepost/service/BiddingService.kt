package com.example.anbdapi.domain.sharepost.service

import com.example.anbdapi.domain.sharepost.controller.request.BiddingRequest
import com.example.anbdapi.domain.sharepost.entity.Bidding
import com.example.anbdapi.domain.sharepost.entity.SharePost
import com.example.anbdapi.domain.sharepost.exception.BiddingBadRequestException
import com.example.anbdapi.domain.sharepost.exception.SharePostNotFoundException
import com.example.anbdapi.domain.sharepost.repository.BiddingRepository
import com.example.anbdapi.domain.sharepost.repository.SharePostRepository
import com.example.anbdapi.domain.user.entity.User
import com.example.anbdapi.domain.user.service.UserApplicationService
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class BiddingService(
    private val userApplicationService: UserApplicationService,
    private val sharePostRepository: SharePostRepository,
    private val biddingRepository: BiddingRepository,
) {
    fun getBidding(biddingId: Long): Bidding {
        return biddingRepository.findByIdOrNull(biddingId)
            ?: throw SharePostNotFoundException("Bidding not found")
    }

    fun getBiddingByUserAndSharePost(user: User, sharePost: SharePost): Bidding? {
        val bidding = biddingRepository.findByUserAndSharePost(user, sharePost)

        return bidding
    }

    fun getBiddings(postId: Long): List<Bidding> {
        val sharePost = sharePostRepository.findByIdOrNull(postId)
            ?: throw SharePostNotFoundException("SharePost not found")
        return biddingRepository.findAllBySharePost(sharePost)
    }

    @Transactional
    fun create(authentication: Authentication, postId: Long, request: BiddingRequest): Bidding {
        if (request.donation > 5000) {
            throw BiddingBadRequestException("Donation must be less than 5000")
        }

        val currentUser = userApplicationService.getCurrentUser(authentication)
        val sharePost = sharePostRepository.findByIdOrNull(postId)
            ?: throw SharePostNotFoundException("SharePost not found")

        if (sharePost.user.id == currentUser.id) {
            throw BiddingBadRequestException("Can't bid on your own post")
        }

        val isExisted = biddingRepository.existsByUserAndSharePost(currentUser, sharePost)
        if (isExisted) {
            throw BiddingBadRequestException("Already bid on this post")
        }

        val bidding = Bidding(
            user = currentUser,
            sharePost = sharePost,
            content = request.content,
            donation =  request.donation,
        )

        return biddingRepository.save(bidding)
    }

    @Transactional
    fun update(authentication: Authentication, biddingId: Long, request: BiddingRequest): Bidding {
        if (request.donation > 5000) {
            throw BiddingBadRequestException("Donation must be less than 5000")
        }

        val currentUser = userApplicationService.getCurrentUser(authentication)
        val bidding = biddingRepository.findByIdOrNull(biddingId)
            ?: throw SharePostNotFoundException("Bidding not found")

        if (bidding.user.id != currentUser.id) {
            throw SharePostNotFoundException("Can't update other user's bidding")
        }

        bidding.content = request.content
        bidding.donation = request.donation

        return biddingRepository.save(bidding)
    }

    @Transactional
    fun delete(authentication: Authentication, biddingId: Long) {
        val currentUser = userApplicationService.getCurrentUser(authentication)
        val bidding = biddingRepository.findByIdOrNull(biddingId)
            ?: throw SharePostNotFoundException("Bidding not found")

        if (bidding.user.id != currentUser.id) {
            throw SharePostNotFoundException("Can't delete other user's bidding")
        }

        biddingRepository.delete(bidding)
    }

    @Transactional
    fun completeBid(authentication: Authentication, postId: Long, biddingId: Long) {
        val currentUser = userApplicationService.getCurrentUser(authentication)

        val sharePost = sharePostRepository.findByIdOrNull(postId)
            ?: throw SharePostNotFoundException("SharePost not found")

        if (sharePost.user.id != currentUser.id) {
            throw SharePostNotFoundException("Can't complete other user's sharing.")
        }

        // TODO: 거래 완료는 추후 채팅까지 완료되면 변경하기
        biddingRepository.findByIdOrNull(biddingId)
            ?: throw SharePostNotFoundException("Bidding not found")

        val biddings = biddingRepository.findAllBySharePost(sharePost)
        biddings.map {
            it.isSelected = it.id == biddingId
        }

        sharePost.isSold = true

        sharePostRepository.save(sharePost)
        biddingRepository.saveAll(biddings)
    }
}

package com.example.anbdapi.domain.sharepost.repository

import com.example.anbdapi.domain.sharepost.entity.Bidding
import com.example.anbdapi.domain.sharepost.entity.SharePost
import com.example.anbdapi.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BiddingRepository : JpaRepository<Bidding, Long> {
    fun findAllBySharePost(sharePost: SharePost): List<Bidding>

    fun existsByUserAndSharePost(user: User, sharePost: SharePost): Boolean
}
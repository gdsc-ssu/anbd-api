package com.example.anbdapi.domain.sharepost.repository

import com.example.anbdapi.domain.sharepost.entity.SharePost
import com.example.anbdapi.domain.sharepost.entity.SharePostLike
import com.example.anbdapi.domain.user.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SharePostLikeRepository : JpaRepository<SharePostLike, Long> {

    fun countBySharePost(sharePost: SharePost): Int

    fun findBySharePost(sharePost: SharePost): List<SharePostLike>

    fun findBySharePostIdIn(sharePostIds: List<Long>): List<SharePostLike>

    fun existsByUserAndSharePost(user: User, sharePost: SharePost): Boolean

    fun findByUserAndSharePost(user: User, sharePost: SharePost): SharePostLike?

    fun findByUser(user: User, pageable: Pageable): Page<SharePostLike>
}


package com.example.anbdapi.domain.sharepost.entity

import com.example.anbdapi.domain.user.entity.User
import com.example.anbdapi.support.utils.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "share_post_like")
class SharePostLike(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "share_post_id", nullable = false)
    val sharePost: SharePost
) : BaseEntity()

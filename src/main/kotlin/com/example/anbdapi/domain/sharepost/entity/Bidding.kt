package com.example.anbdapi.domain.sharepost.entity

import com.example.anbdapi.domain.user.entity.User
import com.example.anbdapi.support.utils.BaseEntity
import jakarta.persistence.*
import org.hibernate.annotations.Filter
import org.hibernate.annotations.SQLDelete
import java.time.LocalDateTime

@Entity
@Table(name = "bidding")
@SQLDelete(sql = "UPDATE bidding SET deleted_at = NOW() WHERE id = ?")
@Filter(name = "deletedFilter", condition = "deleted_at IS NULL")
class Bidding(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "share_post_id", nullable = false)
    val sharePost: SharePost,

    @Column(name = "content", length = 512)
    var content: String? = null,

    @Column(name = "donation", nullable = false)
    var donation: Int,

    @Column(name = "is_selected")
    var isSelected: Boolean? = null,

    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null
) : BaseEntity()

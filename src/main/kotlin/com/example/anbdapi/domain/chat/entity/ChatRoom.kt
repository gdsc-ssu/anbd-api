package com.example.anbdapi.domain.chat.entity

import com.example.anbdapi.domain.sharepost.entity.SharePost
import com.example.anbdapi.domain.user.entity.User
import com.example.anbdapi.support.utils.BaseEntity
import jakarta.persistence.*
import org.hibernate.annotations.Filter
import org.hibernate.annotations.SQLDelete
import java.time.LocalDateTime

@Entity
@Table(name = "chat_room")
@SQLDelete(sql = "UPDATE chat_room SET deleted_at = NOW() WHERE id = ?")
@Filter(name = "deletedFilter", condition = "deleted_at IS NULL")
data class ChatRoom(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val partner: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "share_post_id", nullable = false)
    val sharePost: SharePost,

    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null
): BaseEntity()
package com.example.anbdapi.domain.chat.entity

import com.example.anbdapi.domain.user.entity.User
import com.example.anbdapi.support.utils.BaseEntity
import jakarta.persistence.*
import org.hibernate.annotations.Filter
import org.hibernate.annotations.SQLDelete
import java.time.LocalDateTime

@Entity
@Table(name = "chat_message")
@SQLDelete(sql = "UPDATE chat_message SET deleted_at = NOW() WHERE id = ?")
@Filter(name = "deletedFilter", condition = "deleted_at IS NULL")
data class ChatMessage(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    val chatRoom: ChatRoom,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val sender: User,

    @Column(name = "message", nullable = false)
    val message: String,

    @Column(name = "timestamp", nullable = false)
    val timestamp: LocalDateTime = LocalDateTime.now(),

    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null
): BaseEntity()
package com.example.anbdapi.domain.chat.repository

import com.example.anbdapi.domain.chat.entity.ChatRoom
import com.example.anbdapi.domain.sharepost.entity.SharePost
import com.example.anbdapi.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatRoomRepository: JpaRepository<ChatRoom, Long> {
    fun findAllBySharePost(sharePost: SharePost): List<ChatRoom>

    fun findAllByPartner(partner: User): List<ChatRoom>

    fun findFirstByPartnerAndSharePost(partner: User, sharePost: SharePost): ChatRoom?

    fun findBySharePostUserOrPartner(sharePostUser: User, partner: User): List<ChatRoom>
}
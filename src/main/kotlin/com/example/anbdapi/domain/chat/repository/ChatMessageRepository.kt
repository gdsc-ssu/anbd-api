package com.example.anbdapi.domain.chat.repository

import com.example.anbdapi.domain.chat.entity.ChatMessage
import com.example.anbdapi.domain.chat.entity.ChatRoom
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatMessageRepository : JpaRepository<ChatMessage, Long> {
    fun findAllByChatRoomOrderByTimestampAsc(chatRoom: ChatRoom, pageable: Pageable): Page<ChatMessage>
}

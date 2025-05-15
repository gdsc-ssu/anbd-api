package com.example.anbdapi.domain.chat.controller.response

import java.time.LocalDateTime

data class ChatMessageResponse(
    val id: Long,
    val chatRoomId: Long,
    val senderId: Long,
    val message: String,
    val timestamp: LocalDateTime,
) {
    companion object {
        fun from(
            id: Long,
            chatRoomId: Long,
            senderId: Long,
            message: String,
            timestamp: LocalDateTime,
        ) = ChatMessageResponse(
            id = id,
            chatRoomId = chatRoomId,
            senderId = senderId,
            message = message,
            timestamp = timestamp
        )
    }
}
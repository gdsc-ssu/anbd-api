package com.example.anbdapi.domain.chat.controller

import com.example.anbdapi.domain.chat.dto.ChattingMessageDto
import com.example.anbdapi.domain.chat.service.ChatService
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpAttributesContextHolder
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller

@Controller
class ChatMessageController(
    val template: SimpMessagingTemplate,
    val chatService: ChatService
) {
    @MessageMapping("/chat/message")
    fun message(message: ChattingMessageDto) {
        val simpAttributes = SimpAttributesContextHolder.currentAttributes()
        val userId = simpAttributes.getAttribute("user-id") as String?
            ?: throw IllegalArgumentException("User ID not found in attributes")

        chatService.saveMessage(message.roomId, message.message, userId.toLong())
        message.writer = userId.toLong()

        template.convertAndSend("/sub/chat/room/${message.roomId}", message)
    }
}
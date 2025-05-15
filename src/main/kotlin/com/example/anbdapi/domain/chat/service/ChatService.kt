package com.example.anbdapi.domain.chat.service

import com.example.anbdapi.domain.chat.controller.response.ChatMessageResponse
import com.example.anbdapi.domain.chat.controller.response.ChatRoomResponse
import com.example.anbdapi.domain.chat.entity.ChatMessage
import com.example.anbdapi.domain.chat.entity.ChatRoom
import com.example.anbdapi.domain.chat.repository.ChatMessageRepository
import com.example.anbdapi.domain.chat.repository.ChatRoomRepository
import com.example.anbdapi.domain.sharepost.controller.response.SharePostResponse
import com.example.anbdapi.domain.sharepost.exception.SharePostNotFoundException
import com.example.anbdapi.domain.sharepost.repository.SharePostRepository
import com.example.anbdapi.domain.user.dto.response.UserProfileResponse
import com.example.anbdapi.domain.user.exception.UserNotFoundException
import com.example.anbdapi.domain.user.repository.UserRepository
import com.example.anbdapi.domain.user.service.UserApplicationService
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class ChatService(
    private val userApplicationService: UserApplicationService,
    private val userRepository: UserRepository,
    private val sharePostRepository: SharePostRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val chatMessageRepository: ChatMessageRepository,
) {
    @Transactional
    fun createChatRoom(partnerId: Long, sharePostId: Long, authentication: Authentication): ChatRoomResponse {
        val currentUser = userApplicationService.getCurrentUser(authentication)
        val sharePost = sharePostRepository.findByIdOrNull(sharePostId)
            ?: throw SharePostNotFoundException("Share post not found with id: $sharePostId")
        val partnerUser =  userRepository.findByIdOrNull(partnerId) ?:  throw UserNotFoundException("Partener User not found with id: $partnerId")

        if (currentUser.id != sharePost.user.id) {
            throw IllegalArgumentException("Current user is not the owner of the share post")
        }

        if (currentUser.id == partnerId) {
            throw IllegalArgumentException("Cannot create a chat room with yourself")
        }

        val existingRoom = chatRoomRepository.findFirstByPartnerAndSharePost(currentUser, sharePost)

        if (existingRoom != null) return ChatRoomResponse.from(
            id = existingRoom.id!!,
            partner = UserProfileResponse.from(partnerUser),
            sharePost = SharePostResponse.from(sharePost),
        )

        val chatRoom = ChatRoom(
            partner = partnerUser,
            sharePost = sharePost,
        )

        val room = chatRoomRepository.save(chatRoom)

        return ChatRoomResponse.from(
            id = room.id!!,
            partner = UserProfileResponse.from(currentUser),
            sharePost = SharePostResponse.from(sharePost),
        )
    }

    fun getMyRooms(authentication: Authentication): List<ChatRoomResponse> {
        val user = userApplicationService.getCurrentUser(authentication)
        val chatRooms = chatRoomRepository.findBySharePostUserOrPartner(user, user)

        return chatRooms.map { room ->
            ChatRoomResponse.from(
                id = room.id!!,
                partner = UserProfileResponse.from(room.partner),
                sharePost = SharePostResponse.from(room.sharePost),
            )
        }
    }

    @Transactional
    fun saveMessage(roomId: Long, message: String, senderId: Long) {
        val chatRoom = findById(roomId)

        val sender = userRepository.findByIdOrNull(senderId)
            ?: throw IllegalArgumentException("Sender not found with id: $senderId")

        val chatMessage = ChatMessage(
            chatRoom = chatRoom,
            sender = sender,
            message = message,
        )

        chatMessageRepository.save(chatMessage)
    }

    fun getChatMessages(roomId: Long, authentication: Authentication, pageable: Pageable): Page<ChatMessageResponse> {
        val user = userApplicationService.getCurrentUser(authentication)
        val chatRoom = findById(roomId)

        if (chatRoom.partner != user && chatRoom.sharePost.user != user) {
            throw IllegalArgumentException("User is not a participant in this chat room")
        }

        val messages = chatMessageRepository.findAllByChatRoomOrderByTimestampDesc(chatRoom, pageable)

        return messages.map { chatMessage ->
            ChatMessageResponse.from(
                id = chatMessage.id!!,
                chatRoomId = chatMessage.chatRoom.id!!,
                senderId = chatMessage.sender.id!!,
                message = chatMessage.message,
                timestamp = chatMessage.timestamp,
            )
        }
    }

    fun findById(id: Long): ChatRoom {
        return chatRoomRepository.findByIdOrNull(id)
            ?: throw IllegalArgumentException("Chat room not found with id: $id")
    }
}

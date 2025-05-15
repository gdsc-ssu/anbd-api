package com.example.anbdapi.support.configuration

import com.example.anbdapi.domain.chat.service.ChatService
import com.example.anbdapi.support.utils.jwt.JwtUtil
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.SimpAttributesContextHolder
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageBuilder
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class StompWebSocketConfig(
    private val jwtUtil: JwtUtil,
    private val chatService: ChatService
): WebSocketMessageBrokerConfigurer {

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/stomp/chat")
            .setAllowedOriginPatterns("*")
    }

    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        // TODO: Change to RabbitMQ
        config.enableSimpleBroker("/sub")
        config.setApplicationDestinationPrefixes("/pub")
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(object : ChannelInterceptor {
            override fun preSend(message: Message<*>, channel: MessageChannel): Message<*> {
                val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)!!

                when (accessor.command) {
                    StompCommand.CONNECT -> {
                        val authToken = accessor.getNativeHeader("Authorization")?.firstOrNull()

                        if (authToken != null && authToken.startsWith("Bearer ")) {
                            val token = authToken.removePrefix("Bearer ")
                            val userId = jwtUtil.getUserIdFromToken(token)

                            val simpAttributes = SimpAttributesContextHolder.currentAttributes()
                            simpAttributes.setAttribute("user-id", userId)

                            return MessageBuilder.createMessage(message.payload, accessor.messageHeaders)
                        }
                    }

                    // TODO: 프론트에서 SUB할 때도 토큰 보내게 해야함. 근데 테스트 툴에서 SUB에 토큰을 못 보냄.
                    StompCommand.SUBSCRIBE -> {
                        val destination = accessor.destination
                        val authToken = accessor.getNativeHeader("Authorization")?.firstOrNull()

                        if (authToken != null && authToken.startsWith("Bearer ")) {
                            val token = authToken.removePrefix("Bearer ")
                            val userId = jwtUtil.getUserIdFromToken(token).toLong()

                            val roomId = destination?.substringAfterLast("/")?.toLongOrNull()
                                ?: throw IllegalArgumentException("Invalid destination")

                            val room = chatService.findById(roomId)
                            if (room.partner.id!! != userId && room.sharePost.user.id!! != userId)
                                throw IllegalArgumentException("구매자와 판매자만 채팅에 참여할 수 있습니다.")
                        }
                    }

                    else -> {}
                }
                return message
            }
        })
    }
}
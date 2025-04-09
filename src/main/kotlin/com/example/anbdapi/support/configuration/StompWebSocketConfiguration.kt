package com.example.anbdapi.support.configuration

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
                    else -> {}
                }
                return message
            }
        })
    }
}
package com.example.anbdapi.domain.chat.controller

import com.example.anbdapi.domain.chat.controller.request.ChatRoomRequest
import com.example.anbdapi.domain.chat.controller.response.ChatMessageResponse
import com.example.anbdapi.domain.chat.controller.response.ChatRoomResponse
import com.example.anbdapi.domain.chat.service.ChatService
import com.example.anbdapi.support.logging.TraceIdResolver
import com.example.anbdapi.support.response.AnbdApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@Tag(name = "📨 Chat API", description = "채팅 관련 API (채팅방 관리, 메시지 등)")
@RequestMapping("/v1/chat")
class ChatController(
    private val traceIdResolver: TraceIdResolver,
    private val chatService: ChatService,
) {
    @Operation(
        summary = "채팅방 생성",
        description = "채팅방을 생성합니다. 자신이 작성한 나눔글에서만 채팅을 열 수 있습니다.\n" +
                "채팅방이 이미 존재하는 경우, 기존 채팅방을 반환합니다.\n" +
                "채팅방 생성 시, 상대방의 프로필과 나눔글 정보를 함께 반환합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "채팅방 생성 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청")
        ]
    )
    @PostMapping("/room")
    fun createRoom(
        authentication: Authentication,
        @RequestBody chatRoomRequest: ChatRoomRequest
    ): AnbdApiResponse<ChatRoomResponse> {
        val chatRoom = chatService.createChatRoom(
            partnerId = chatRoomRequest.partnerId,
            sharePostId =  chatRoomRequest.sharePostId,
            authentication = authentication
        )

        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = chatRoom
        )
    }

    @Operation(
        summary = "나의 채팅방 정보 조회",
        description = "내가 참여하고 있는 채팅방을 불러옵니다"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "채팅방 조회 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청")
        ]
    )
    @GetMapping("/rooms")
    fun getMyRooms(
        authentication: Authentication,
    ): AnbdApiResponse<List<ChatRoomResponse>> {
        val chatRoom = chatService.getMyRooms(authentication)

        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = chatRoom
        )
    }

    @Operation(
        summary = "특정 채팅방의 메시지 조회",
        description = "특정 채팅방의 메시시를 최신 순으로 불러옵니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "메시지 조회 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청")
        ]
    )
    @GetMapping("/rooms/{roomId}/messages")
    fun getChatMessages(
        authentication: Authentication,
        @PathVariable roomId: Long,
        pageable: Pageable
    ): AnbdApiResponse<Page<ChatMessageResponse>> {
        val chatRoom = chatService.getChatMessages(roomId, authentication, pageable)

        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = chatRoom
        )
    }
}

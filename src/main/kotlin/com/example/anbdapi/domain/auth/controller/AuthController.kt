package com.example.anbdapi.domain.auth.controller

import com.example.anbdapi.domain.auth.dto.request.RefreshRequest
import com.example.anbdapi.domain.auth.dto.response.TokenResponse
import com.example.anbdapi.domain.auth.service.AuthService
import com.example.anbdapi.support.logging.TraceIdResolver
import com.example.anbdapi.support.response.AnbdApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/auth")
@Tag(name = "🔑 Auth API", description = "인증 관련 API")
class AuthController(
    private val traceIdResolver: TraceIdResolver,
    private val authService: AuthService
) {

    @Operation(
        summary = "사용자 토큰 리프레시",
        description = "406에러를 반환받을 때 리프레시 토큰을 이용하여 새로운 액세스 토큰을 발급합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "토큰 갱신 성공"),
            ApiResponse(responseCode = "401", description = "잘못된 요청 또는 토큰 오류"),
        ]
    )
    @PostMapping("/refresh")
    fun refreshAccessToken(
        @RequestHeader("Authorization") authHeader: String,
        @RequestBody request: RefreshRequest): AnbdApiResponse<TokenResponse> {

        val accessToken = authHeader.removePrefix("Bearer ").trim()

        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = authService.refreshAccessToken(accessToken, request)
        )
    }
}
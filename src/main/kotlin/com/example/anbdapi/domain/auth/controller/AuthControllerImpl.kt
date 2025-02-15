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
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth API", description = "인증 관련 API")
class AuthControllerImpl(
    private val traceIdResolver: TraceIdResolver,
    private val authService: AuthService
) : AuthController {

    @Operation(
        summary = "Refresh Access Token",
        description = "리프레시 토큰을 이용하여 새로운 액세스 토큰을 발급합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "토큰 갱신 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청 또는 토큰 오류"),
            ApiResponse(responseCode = "406", description = "만료된 토큰 요청")
        ]
    )
    @PostMapping("/refresh")
    override fun refreshAccessToken(@RequestBody request: RefreshRequest): AnbdApiResponse<TokenResponse> {
        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = authService.refreshAccessToken(request)
        )
    }
}
package com.example.anbdapi.domain.dev.controller

import com.example.anbdapi.domain.dev.dto.DevLoginRequest
import com.example.anbdapi.domain.dev.dto.DevUserRequest
import com.example.anbdapi.domain.dev.service.DevService
import com.example.anbdapi.domain.user.dto.response.LoginResponse
import com.example.anbdapi.support.logging.TraceIdResolver
import com.example.anbdapi.support.response.AnbdApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/dev")
@Tag(name = "💻 개발 전용 API", description = "개발 전용 API")
@Validated
class DevController(
    private val traceIdResolver: TraceIdResolver,
    private val devService: DevService
) {
    @Operation(
        summary = "ping",
        description = "핑 테스트 API.",
        responses = [
            ApiResponse(responseCode = "200", description = "OK"),
            ApiResponse(responseCode = "500", description = "Internal Server Error", content = arrayOf(Content(schema = Schema(hidden = true)))),
        ],
    )
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun ping(): AnbdApiResponse<String> {
        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = "pong"
        )
    }

    @Operation(
        summary = "개발자 계정 생성",
        description = "개발 및 테스트 목적으로 직접 사용자 계정을 생성합니다. 이 API는 개발 환경에서만 사용해야 합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "계정 생성 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청")
        ]
    )
    @PostMapping("/register")
    fun createDevUser(
        @Valid @RequestBody request: DevUserRequest
    ): AnbdApiResponse<LoginResponse> {
        val response = devService.createDevUser(request)
        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = response
        )
    }

    @Operation(
        summary = "개발자 계정 로그인",
        description = "개발 및 테스트 목적으로 이메일만으로 간편하게 로그인합니다. 이 API는 개발 환경에서만 사용해야 합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "로그인 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청"),
            ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
        ]
    )
    @PostMapping("/login")
    fun loginDevUser(
        @Valid @RequestBody request: DevLoginRequest
    ): AnbdApiResponse<LoginResponse> {
        val response = devService.loginDevUser(request)
        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = response
        )
    }
}
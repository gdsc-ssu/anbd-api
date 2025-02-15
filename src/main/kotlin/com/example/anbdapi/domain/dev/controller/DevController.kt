package com.example.anbdapi.domain.dev.controller

import com.example.anbdapi.support.logging.TraceIdResolver
import com.example.anbdapi.support.response.AnbdApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/dev")
@Tag(name = "💻 개발 전용 API", description = "개발 전용 API")
@Validated
class DevController(
    private val traceIdResolver: TraceIdResolver
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
}
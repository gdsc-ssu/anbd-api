package com.example.anbdapi.domain.dev.auth.global

import com.example.anbdapi.support.logging.TraceIdResolver
import com.example.anbdapi.support.response.AnbdApiResponse
import com.example.anbdapi.support.response.CommonResponseCode
import com.example.anbdapi.support.response.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler(
    private val traceIdResolver: TraceIdResolver,
) {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(RuntimeException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleRuntimeException(ex: RuntimeException): AnbdApiResponse<ErrorResponse> {
        log.error("RuntimeException 발생: {}", ex.message, ex)

        val message = ex.message ?: "알 수 없는 오류가 발생했습니다."
        val body = ErrorResponse(message)

        return AnbdApiResponse.of(
            traceId = traceIdResolver.getTraceId(),
            status = HttpStatus.BAD_REQUEST,
            code = CommonResponseCode.COMMON_01,
            body = body
        )
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleException(ex: Exception): AnbdApiResponse<ErrorResponse> {
        log.error("Exception 발생: {}", ex.message, ex)

        val message = ex.message ?: "예기치 않은 오류가 발생했습니다."
        val body = ErrorResponse(message)

        return AnbdApiResponse.of(
            traceId = traceIdResolver.getTraceId(),
            status = HttpStatus.INTERNAL_SERVER_ERROR,
            code = CommonResponseCode.COMMON_02,
            body = body
        )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): AnbdApiResponse<ErrorResponse> {
        log.error("IllegalArgumentException 발생: {}", ex.message, ex)

        val message = ex.message ?: "잘못된 입력값이 제공되었습니다."
        val body = ErrorResponse(message)

        return AnbdApiResponse.of(
            traceId = traceIdResolver.getTraceId(),
            status = HttpStatus.BAD_REQUEST,
            code = CommonResponseCode.COMMON_03,
            body = body
        )
    }
}
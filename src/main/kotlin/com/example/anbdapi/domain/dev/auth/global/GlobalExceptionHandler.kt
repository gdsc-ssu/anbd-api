package com.example.anbdapi.domain.dev.auth.global

import com.example.anbdapi.domain.dev.auth.exception.TokenExpiredException
import io.jsonwebtoken.JwtException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    data class ErrorResponse(
        val status: Int,
        val error: String,
        val message: String
    )

    /**
     * 토큰 만료 핸들러
     *
     * 만약 accessToken에 대한 토큰 만료일 때, 최초 1회는 /auth/refresh 호출
     */
    @ExceptionHandler(TokenExpiredException::class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    fun handleTokenExpiredException(ex: TokenExpiredException): ErrorResponse {
        log.error("TokenExpiredException 발생: {}", ex.message, ex)
        return ErrorResponse(
            status = HttpStatus.NOT_ACCEPTABLE.value(),
            error = "토큰 만료",
            message = ex.message ?: "토큰이 만료되었거나 올바르지 않습니다."
        )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ErrorResponse {
        log.error("IllegalArgumentException 발생: {}", ex.message, ex)
        return ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "잘못된 요청",
            message = ex.message ?: "잘못된 입력값이 제공되었습니다."
        )
    }

    @ExceptionHandler(RuntimeException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleRuntimeException(ex: RuntimeException): ErrorResponse {
        log.error("RuntimeException 발생: {}", ex.message, ex)
        return ErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = "잘못된 요청",
            message = ex.message ?: "알 수 없는 오류가 발생했습니다."
        )
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleException(ex: Exception): ErrorResponse {
        log.error("Exception 발생: {}", ex.message, ex)
        return ErrorResponse(
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = "서버 내부 오류",
            message = "예기치 않은 서버 오류가 발생했습니다."
        )
    }

    @ExceptionHandler(JwtException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleJwtException(ex: JwtException): ErrorResponse {
        log.error("JwtException 발생: {}", ex.message, ex)
        return ErrorResponse(
            status = HttpStatus.UNAUTHORIZED.value(),
            error = "JWT 오류",
            message = ex.message ?: "유효하지 않은 JWT입니다."
        )
    }
}
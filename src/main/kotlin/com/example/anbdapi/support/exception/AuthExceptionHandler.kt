package com.example.anbdapi.support.exception

import com.example.anbdapi.domain.dev.auth.exception.TokenExpiredException
import com.example.anbdapi.support.logging.TraceIdResolver
import com.example.anbdapi.support.response.AnbdApiResponse
import com.example.anbdapi.support.response.AuthResponseCode
import com.example.anbdapi.support.response.ErrorResponse
import io.jsonwebtoken.JwtException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class AuthExceptionHandler(
    private val traceIdResolver: TraceIdResolver,
) {

    private val log = LoggerFactory.getLogger(AuthExceptionHandler::class.java)

    /**
     * 토큰 만료 핸들러
     *
     * 만약 accessToken에 대한 토큰 만료일 때, 최초 1회는 /auth/refresh 호출
     */
    @ExceptionHandler(TokenExpiredException::class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    fun handleTokenExpiredException(ex: TokenExpiredException): AnbdApiResponse<ErrorResponse> {
        log.error("TokenExpiredException 발생: {}", ex.message, ex)

        val message = ex.message ?: "토큰이 만료되었거나 올바르지 않습니다."
        val body = ErrorResponse(message)

        return AnbdApiResponse.of(
            traceId = traceIdResolver.getTraceId(),
            status = HttpStatus.NOT_ACCEPTABLE,
            code = AuthResponseCode.AUTH_01,
            body = body
        )
    }

    @ExceptionHandler(JwtException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleJwtException(ex: JwtException): AnbdApiResponse<ErrorResponse> {
        log.error("JwtException 발생: {}", ex.message, ex)

        val message = ex.message ?: "유효하지 않은 JWT입니다."
        val body = ErrorResponse(message)

        return AnbdApiResponse.of(
            traceId = traceIdResolver.getTraceId(),
            status = HttpStatus.UNAUTHORIZED,
            code = AuthResponseCode.AUTH_02,
            body = body
        )
    }
}
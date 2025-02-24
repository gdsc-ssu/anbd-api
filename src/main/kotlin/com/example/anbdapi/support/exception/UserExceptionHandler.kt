package com.example.anbdapi.support.exception

import com.example.anbdapi.domain.user.exception.UserNotFoundException
import com.example.anbdapi.support.logging.TraceIdResolver
import com.example.anbdapi.support.response.AnbdApiResponse
import com.example.anbdapi.support.response.ErrorResponse
import com.example.anbdapi.support.response.UserResponseCode
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class UserExceptionHandler(
    private val traceIdResolver: TraceIdResolver,
) {

    private val log = LoggerFactory.getLogger(AuthExceptionHandler::class.java)
    @ExceptionHandler(UserNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleUserNotFoundException(ex: UserNotFoundException): AnbdApiResponse<ErrorResponse> {
        log.error("UserNotFound 발생: {}", ex.message, ex)

        val message = ex.message ?: "사용자를 찾을 수 없습니다."
        val body = ErrorResponse(message)

        return AnbdApiResponse.of(
            traceId = traceIdResolver.getTraceId(),
            status = HttpStatus.NOT_FOUND,
            code = UserResponseCode.USER_01,
            body = body
        )
    }
}
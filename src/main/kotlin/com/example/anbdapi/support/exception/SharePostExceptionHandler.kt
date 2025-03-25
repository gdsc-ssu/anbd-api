package com.example.anbdapi.support.exception

import com.example.anbdapi.domain.sharepost.exception.*
import com.example.anbdapi.support.logging.TraceIdResolver
import com.example.anbdapi.support.response.AnbdApiResponse
import com.example.anbdapi.support.response.ErrorResponse
import com.example.anbdapi.support.response.SharePostResponseCode
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class SharePostExceptionHandler(
    private val traceIdResolver: TraceIdResolver,
) {

    private val log = LoggerFactory.getLogger(AuthExceptionHandler::class.java)

    @ExceptionHandler(SharePostNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleSharePostNotFoundException(ex: SharePostNotFoundException): AnbdApiResponse<ErrorResponse> {
        log.error("SharePostNotFound 발생: {}", ex.message, ex)

        val message = ex.message ?: "나눔글을 찾을 수 없습니다."
        val body = ErrorResponse(message)

        return AnbdApiResponse.of(
            traceId = traceIdResolver.getTraceId(),
            status = HttpStatus.NOT_FOUND,
            code = SharePostResponseCode.SHAREPOST_01,
            body = body
        )
    }

    @ExceptionHandler(SharePostLikeBadRequestException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleSharePostBadRequestException(ex: SharePostLikeBadRequestException): AnbdApiResponse<ErrorResponse> {
        log.error("SharePostLikeBadRequest 발생: {}", ex.message, ex)

        val message = ex.message ?: "좋아요를 실행/취소 할 수 없습니다."
        val body = ErrorResponse(message)

        return AnbdApiResponse.of(
            traceId = traceIdResolver.getTraceId(),
            status = HttpStatus.BAD_REQUEST,
            code = SharePostResponseCode.SHAREPOST_02,
            body = body
        )
    }

    @ExceptionHandler(SharePostGeminiException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleSharePostGeminiException(ex: SharePostGeminiException): AnbdApiResponse<ErrorResponse> {
        log.error("SharePostGemini 오류 발생: {}", ex.message, ex)

        val message = ex.message ?: "나눔글 설명 생성 중 오류가 발생했습니다."
        val body = ErrorResponse(message)

        return AnbdApiResponse.of(
            traceId = traceIdResolver.getTraceId(),
            status = HttpStatus.BAD_REQUEST,
            code = SharePostResponseCode.SHAREPOST_03,
            body = body
        )
    }

    @ExceptionHandler(BiddingNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleBiddingNotFoundException(ex: BiddingNotFoundException): AnbdApiResponse<ErrorResponse> {
        log.error("BiddingNotFound 발생: {}", ex.message, ex)

        val message = ex.message ?: "입찰글을 찾을 수 없습니다."
        val body = ErrorResponse(message)

        return AnbdApiResponse.of(
            traceId = traceIdResolver.getTraceId(),
            status = HttpStatus.NOT_FOUND,
            code = SharePostResponseCode.SHAREPOST_04,
            body = body
        )
    }

    @ExceptionHandler(BiddingBadRequestException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBiddingBadRequestException(ex: BiddingBadRequestException): AnbdApiResponse<ErrorResponse> {
        log.error("BiddingBadRequestException 발생: {}", ex.message, ex)

        val message = ex.message ?: "잘못된 입찰 정보입니다."
        val body = ErrorResponse(message)

        return AnbdApiResponse.of(
            traceId = traceIdResolver.getTraceId(),
            status = HttpStatus.BAD_REQUEST,
            code = SharePostResponseCode.SHAREPOST_05,
            body = body
        )
    }
}
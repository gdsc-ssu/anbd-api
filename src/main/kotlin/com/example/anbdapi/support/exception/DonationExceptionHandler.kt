package com.example.anbdapi.support.exception

import com.example.anbdapi.infra.vision.donation.exception.DonationVerificationException
import com.example.anbdapi.support.logging.TraceIdResolver
import com.example.anbdapi.support.response.AnbdApiResponse
import com.example.anbdapi.support.response.DonationResponseCode
import com.example.anbdapi.support.response.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class DonationExceptionHandler(
    private val traceIdResolver: TraceIdResolver,
) {

    private val log = LoggerFactory.getLogger(DonationExceptionHandler::class.java)

    @ExceptionHandler(DonationVerificationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleDonationVerificationException(ex: DonationVerificationException): AnbdApiResponse<ErrorResponse> {
        log.error("DonationVerificationException 발생: {}", ex.message, ex)

        val message = ex.message ?: "기부금 영수증 인증 중 오류가 발생했습니다."
        val body = ErrorResponse(message)

        return AnbdApiResponse.of(
            traceId = traceIdResolver.getTraceId(),
            status = HttpStatus.BAD_REQUEST,
            code = DonationResponseCode.DONATION_01,
            body = body
        )
    }
}
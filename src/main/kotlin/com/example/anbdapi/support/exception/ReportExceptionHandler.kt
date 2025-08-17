package com.example.anbdapi.support.exception

import com.example.anbdapi.domain.report.exception.ReportDuplicateException
import com.example.anbdapi.domain.report.exception.ReportSelfException
import com.example.anbdapi.support.logging.TraceIdResolver
import com.example.anbdapi.support.response.AnbdApiResponse
import com.example.anbdapi.support.response.ErrorResponse
import com.example.anbdapi.support.response.ReportResponseCode
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

class ReportExceptionHandler(
    private val traceIdResolver: TraceIdResolver,
) {

    private val log = LoggerFactory.getLogger(ReportExceptionHandler::class.java)

    @ExceptionHandler(ReportDuplicateException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleReportDuplicateException(ex: ReportDuplicateException): AnbdApiResponse<ErrorResponse> {
        log.error("ReportDuplicateException 발생: {}", ex.message, ex)

        val message = ex.message ?: "이미 신고한 게시글입니다."
        val body = ErrorResponse(message)

        return AnbdApiResponse.of(
            traceId = traceIdResolver.getTraceId(),
            status = HttpStatus.CONFLICT,
            code = ReportResponseCode.REPORT_01,
            body = body
        )
    }

    @ExceptionHandler(ReportSelfException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleReportSelfException(ex: ReportSelfException): AnbdApiResponse<ErrorResponse> {
        log.error("ReportSelfException 발생: {}", ex.message, ex)

        val message = ex.message ?: "자신의 게시글은 신고할 수 없습니다."
        val body = ErrorResponse(message)

        return AnbdApiResponse.of(
            traceId = traceIdResolver.getTraceId(),
            status = HttpStatus.BAD_REQUEST,
            code = ReportResponseCode.REPORT_03,
            body = body
        )
    }
}
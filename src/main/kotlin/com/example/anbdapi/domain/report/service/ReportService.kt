package com.example.anbdapi.domain.report.service

import com.example.anbdapi.domain.report.dto.request.ReportRequest
import com.example.anbdapi.domain.report.dto.response.ReportResponse
import com.example.anbdapi.domain.report.entity.Report
import com.example.anbdapi.domain.report.exception.ReportDuplicateException
import com.example.anbdapi.domain.report.exception.ReportSelfException
import com.example.anbdapi.domain.report.repository.ReportRepository
import com.example.anbdapi.domain.sharepost.exception.SharePostNotFoundException
import com.example.anbdapi.domain.sharepost.repository.SharePostRepository
import com.example.anbdapi.domain.user.service.UserApplicationService
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class ReportService(
    private val reportRepository: ReportRepository,
    private val sharePostRepository: SharePostRepository,
    private val userApplicationService: UserApplicationService
) {

    @Transactional
    fun createReport(
        authentication: Authentication,
        postId: Long,
        request: ReportRequest
    ): ReportResponse {
        val currentUser = userApplicationService.getCurrentUser(authentication)

        val sharePost = sharePostRepository.findByIdOrNull(postId)
            ?: throw SharePostNotFoundException("나눔글을 찾을 수 없습니다.")

        // 자신의 나눔글은 신고할 수 없음
        if (sharePost.user.id == currentUser.id) {
            throw ReportSelfException("자신의 나눔글은 신고할 수 없습니다.")
        }

        // 중복 신고 방지
        if (reportRepository.existsByReporterAndSharePost(currentUser, sharePost)) {
            throw ReportDuplicateException("이미 나눔글을 신고하셨습니다.")
        }

        val report = Report(
            reporter = currentUser,
            sharePost = sharePost,
            category = request.category,
            description = request.description
        )

        val savedReport = reportRepository.save(report)
        return ReportResponse.from(savedReport)
    }
}
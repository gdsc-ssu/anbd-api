package com.example.anbdapi.domain.report.dto.response

import com.example.anbdapi.domain.report.entity.Report
import com.example.anbdapi.support.enums.ReportCategory
import java.time.LocalDateTime

data class ReportResponse(
    val id: Long,
    val reporterId: Long,
    val sharePostId: Long,
    val category: ReportCategory,
    val categoryDescription: String,
    val description: String?,
    val isProcessed: Boolean,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(report: Report): ReportResponse {
            return ReportResponse(
                id = report.id!!,
                reporterId = report.reporter.id!!,
                sharePostId = report.sharePost.id!!,
                category = report.category,
                categoryDescription = report.category.description,
                description = report.description,
                isProcessed = report.isProcessed,
                createdAt = report.createdAt
            )
        }
    }
}
package com.example.anbdapi.domain.report.dto.request

import com.example.anbdapi.support.enums.ReportCategory

data class ReportRequest(
    val category: ReportCategory,
    val description: String?
)
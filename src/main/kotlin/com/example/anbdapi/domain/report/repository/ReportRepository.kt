package com.example.anbdapi.domain.report.repository

import com.example.anbdapi.domain.report.entity.Report
import com.example.anbdapi.domain.sharepost.entity.SharePost
import com.example.anbdapi.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReportRepository : JpaRepository<Report, Long> {

    // 특정 사용자가 특정 게시글을 이미 신고했는지 확인
    fun existsByReporterAndSharePost(reporter: User, sharePost: SharePost): Boolean
}
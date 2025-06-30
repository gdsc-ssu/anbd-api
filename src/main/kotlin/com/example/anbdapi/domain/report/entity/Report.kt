package com.example.anbdapi.domain.report.entity

import com.example.anbdapi.domain.sharepost.entity.SharePost
import com.example.anbdapi.domain.user.entity.User
import com.example.anbdapi.support.enums.ReportCategory
import com.example.anbdapi.support.utils.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "report")
class Report(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    val reporter: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "share_post_id", nullable = false)
    val sharePost: SharePost,

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    val category: ReportCategory,

    @Column(name = "description", columnDefinition = "TEXT")
    val description: String? = null,

    @Column(name = "is_processed", nullable = false)
    var isProcessed: Boolean = false
) : BaseEntity() {
}
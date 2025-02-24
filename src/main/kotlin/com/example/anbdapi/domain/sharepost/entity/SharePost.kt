package com.example.anbdapi.domain.sharepost.entity

import com.example.anbdapi.domain.user.entity.User
import com.example.anbdapi.support.enums.ShareCategory
import com.example.anbdapi.support.enums.ShareType
import com.example.anbdapi.support.utils.BaseEntity
import jakarta.persistence.*
import org.hibernate.annotations.Filter
import org.hibernate.annotations.SQLDelete
import java.time.LocalDateTime

@Entity
@Table(name = "share_post")
@SQLDelete(sql = "UPDATE share_post SET deleted_at = NOW() WHERE id = ?")
// FilterName 중복 오류
//@FilterDef(name = "deletedFilter")
@Filter(name = "deletedFilter", condition = "deleted_at IS NULL")
class SharePost(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(name = "title", nullable = false, length = 512)
    var title: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    var category: ShareCategory,

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    var content: String,

    @ElementCollection
    @CollectionTable(name = "share_post_images", joinColumns = [JoinColumn(name = "share_post_id")])
    @Column(name = "image_url", length = 2048)
    var images: List<String> = listOf(),

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var type: ShareType,

    @Column(name = "description", columnDefinition = "TEXT")
    var description: String? = null,

    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null
) : BaseEntity()

package com.example.anbdapi.domain.dev.auth.entity

import com.example.anbdapi.support.utils.BaseTimeEntity
import jakarta.persistence.*
import org.hibernate.annotations.Filter
import org.hibernate.annotations.SQLDelete
import java.time.LocalDateTime

@Entity
@Table(name = "user_social_accounts")
@SQLDelete(sql = "UPDATE user_social_accounts SET deleted_at = NOW() WHERE id = ?")
// Hibernate Filter 정의: deleted_at 이 null 인 것만 조회
@Filter(name = "deletedFilter", condition = "deleted_at IS NULL")
class UserSocialAccount(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(name = "provider", nullable = false)
    @Enumerated(EnumType.STRING)
    val provider: Provider,

    @Column(name = "provider_id", nullable = false, unique = true, length = 128)
    val providerId: String,

    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null
) : BaseTimeEntity()
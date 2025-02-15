package com.example.anbdapi.domain.dev.auth.entity

import com.example.anbdapi.support.utils.BaseTimeEntity
import jakarta.persistence.*
import org.hibernate.annotations.Filter
import org.hibernate.annotations.FilterDef
import org.hibernate.annotations.SQLDelete
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "users")
@SQLDelete(sql = "UPDATE users SET deleted_at = NOW() WHERE id = ?")
// Hibernate Filter 정의: deleted_at 이 null 인 것만 조회
@FilterDef(name = "deletedFilter")
@Filter(name = "deletedFilter", condition = "deleted_at IS NULL")
class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long? = null,

    @Column(name = "nickname", nullable = false, unique = true, length = 64)
    var nickname: String,

    @Column(name = "email", nullable = false, unique = true, length = 64)
    val email: String,

    @Column(name = "profile_image", length = 2048)
    var profileImage: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    var gender: Gender,

    @Column(name = "birth_date", nullable = false)
    var birthDate: LocalDate,

    @Enumerated(EnumType.STRING)
    @Column(name = "share_category")
    var shareCategory: ShareCategory? = null,

    @Column(name = "reliability", nullable = false)
    var reliability: Int = 0,

    @Column(name = "refresh_token", length = 512)
    var refreshToken: String? = null,

    @Column(name = "is_profile_completed")
    var isProfileCompleted: Boolean = false,

    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null
) : BaseTimeEntity()
{
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    var socialAccounts: MutableList<UserSocialAccount> = mutableListOf()
}
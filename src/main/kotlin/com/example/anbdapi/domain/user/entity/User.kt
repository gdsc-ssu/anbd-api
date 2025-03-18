package com.example.anbdapi.domain.user.entity

import com.example.anbdapi.domain.userSocialAccount.entity.UserSocialAccount
import com.example.anbdapi.support.enums.Gender
import com.example.anbdapi.support.enums.ShareCategory
import com.example.anbdapi.support.utils.BaseEntity
import jakarta.persistence.*
import org.hibernate.annotations.FilterDef
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "users")
@SQLDelete(sql = "UPDATE users SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
@FilterDef(name = "deletedFilter")
class User(

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

    @Column(name = "neighborhood", length = 100)
    var neighborhood: String? = null,

    @ElementCollection
    @CollectionTable(
        name = "user_share_categories", joinColumns = [JoinColumn(name = "user_id")]
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "share_category")
    var shareCategories: MutableList<ShareCategory> = mutableListOf(),

    @Column(name = "reliability", nullable = false)
    var reliability: Int = 0,

    @Column(name = "refresh_token", length = 512)
    var refreshToken: String? = null,

    @Column(name = "is_profile_completed")
    var isProfileCompleted: Boolean = false,

    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime? = null
) : BaseEntity()
{
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    var socialAccounts: MutableList<UserSocialAccount> = mutableListOf()
}
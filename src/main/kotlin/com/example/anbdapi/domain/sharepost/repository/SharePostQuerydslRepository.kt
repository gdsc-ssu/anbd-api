package com.example.anbdapi.domain.sharepost.repository

import com.example.anbdapi.domain.sharepost.entity.QSharePost
import com.example.anbdapi.domain.sharepost.entity.SharePost
import com.example.anbdapi.support.enums.ShareCategory
import com.example.anbdapi.support.enums.ShareType
import com.querydsl.core.BooleanBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class SharePostQuerydslRepository(
    private val entityManager: EntityManager
) {
    private val sharePost = QSharePost.sharePost

    fun findPosts(
        keyword: String?,
        location: String,
        category: ShareCategory?,
        type: ShareType?,
        isSold: Boolean?,
        pageable: Pageable
    ): Page<SharePost> {
        val query = JPAQueryFactory(entityManager)

        val whereClause = BooleanBuilder()
            .and(sharePost.isSold.eq(false))
            .and(sharePost.deletedAt.isNull)
            .and(sharePost.neighborhood.eq(location))

        if (keyword != null) {
            whereClause.and(
                sharePost.title.like("%$keyword%")
                    .or(sharePost.content.like("%$keyword%"))
            )
        }

        if (category != null) {
            whereClause.and(sharePost.category.eq(category))
        }

        if (type != null) {
            whereClause.and(sharePost.type.eq(type))
        }

        if (isSold != null) {
            whereClause.and(sharePost.isSold.eq(isSold))
        }

        val count = query
            .select(QSharePost.sharePost.count())
            .from(QSharePost.sharePost)
            .where(whereClause)
            .fetchOne() ?: 0L

        val content = query
            .selectFrom(sharePost)
            .where(whereClause)
            .offset(pageable.offset)
            .orderBy(sharePost.createdAt.desc())
            .limit(pageable.pageSize.toLong())
            .fetch()

        return PageImpl(
            content,
            pageable,
            count
        )
    }
}

package com.example.anbdapi.domain.sharepost.repository

import com.example.anbdapi.domain.sharepost.entity.SharePost
import com.example.anbdapi.domain.user.entity.User
import com.example.anbdapi.support.enums.ShareCategory
import com.example.anbdapi.support.enums.ShareType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface SharePostRepository : JpaRepository<SharePost, Long> {

    // 특정 사용자가 작성한 게시글 조회
    fun findByUser(user: User): List<SharePost>

    // 특정 카테고리의 게시글 조회
    fun findByCategory(category: ShareCategory): List<SharePost>

    // 제목 또는 내용에 특정 키워드 포함된 게시글 검색 (대소문자 구분 없이 검색)
    fun findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(title: String, content: String): List<SharePost>

    // 특정 사용자의 게시글을 페이징 조회
    fun findByUser(user: User, pageable: Pageable): Page<SharePost>

    // TODO: QueryDSL로 리팩토링
    @Query("""
        SELECT sp FROM SharePost sp 
        WHERE sp.isSold = false
        AND (sp.deletedAt IS NULL)
        AND (:keyword IS NULL OR sp.title LIKE %:keyword% OR sp.content LIKE %:keyword%)
        AND (sp.location = :location)
        AND (:category IS NULL OR sp.category = :category)
        AND sp.type = :type
    """)
    fun findPosts(
        @Param("keyword") keyword: String?,
        @Param("location") location: String,
        @Param("category") category: ShareCategory?,
        @Param("type") type: ShareType?,
        pageable: Pageable
    ): Page<SharePost>
}

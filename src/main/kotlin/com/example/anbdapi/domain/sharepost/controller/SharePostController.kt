package com.example.anbdapi.domain.sharepost.controller

import com.example.anbdapi.domain.sharepost.controller.request.SharePostRequest
import com.example.anbdapi.domain.sharepost.controller.response.SharePostLikeResponse
import com.example.anbdapi.domain.sharepost.controller.response.SharePostResponse
import com.example.anbdapi.domain.sharepost.service.SharePostLikeService
import com.example.anbdapi.domain.sharepost.service.SharePostService
import com.example.anbdapi.support.enums.ShareCategory
import com.example.anbdapi.support.enums.ShareType
import com.example.anbdapi.support.logging.TraceIdResolver
import com.example.anbdapi.support.response.AnbdApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.*

@RestController
@Tag(name = "📮 SharePost API", description = "나눔글 관련 API (생성, 조회, 수정, 삭제)")
@RequestMapping("/v1/share-posts")
class SharePostController(
    private val traceIdResolver: TraceIdResolver,
    private val sharePostService: SharePostService,
    private val sharePostLikeService: SharePostLikeService
) {
    @Operation(
        summary = "나눔글 생성",
        description = "나눔글을 생성합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "나눔글 생성 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청")
        ]
    )
    @PostMapping
    fun createPost(
        @RequestParam userId: Long,
        @RequestBody request: SharePostRequest
    ): AnbdApiResponse<SharePostResponse> {
        val post = sharePostService.createPost(userId, request)

        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = SharePostResponse.from(post)
        )
    }

    @Operation(
        summary = "나눔글ID로 조회",
        description = "나눔글을 조회합니다.."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "나눔글 조회 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청")
        ]
    )
    @GetMapping("/{postId}")
    fun getPost(
        @AuthenticationPrincipal oAuth2User: OAuth2User,
        @PathVariable postId: Long
    ): AnbdApiResponse<SharePostResponse> {
        val email = oAuth2User.attributes["email"] as? String
            ?: throw IllegalArgumentException("Email not found in authentication data")

        val post = sharePostService.getPostById(email, postId)

        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = post
        )
    }

    @Operation(
        summary = "나눔글 전체 조회",
        description = "나눔글을 전체 조회합니다. 키워드, 카테고리 등을 설정할 수 있습니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "나눔글 조회 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청")
        ]
    )
    @GetMapping
    fun getPosts(
        @AuthenticationPrincipal oAuth2User: OAuth2User,
        @RequestParam(required = false) keyword: String?,
        @RequestParam(required = false) location: String?,
        @RequestParam(required = false) category: ShareCategory?,
        @RequestParam(required = false) type: ShareType?,
        pageable: Pageable
    ): AnbdApiResponse<Page<SharePostResponse>> {
        val email = oAuth2User.attributes["email"] as? String
            ?: throw IllegalArgumentException("Email not found in authentication data")

        val posts = sharePostService.getPosts(email, keyword, location, category, type, pageable)

        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = posts
        )
    }

    @Operation(
        summary = "특정 유저의 나눔글 조회",
        description = "특정 유저의 나눔글을 생성합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "나눔글 조회 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청")
        ]
    )
    @GetMapping("/user/{userId}")
    fun getUserPosts(
        @AuthenticationPrincipal oAuth2User: OAuth2User,
        @PathVariable userId: Long,
        pageable: Pageable
    ): AnbdApiResponse<Page<SharePostResponse>> {
        val email = oAuth2User.attributes["email"] as? String
            ?: throw IllegalArgumentException("Email not found in authentication data")

        val posts = sharePostService.getUserPosts(email, userId, pageable)

        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = posts
        )
    }

    @Operation(
        summary = "나눔글 수정",
        description = "나눔글을 수정합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "나눔글 수정 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청")
        ]
    )
    @PutMapping("/{postId}")
    fun updatePost(
        @AuthenticationPrincipal oAuth2User: OAuth2User,
        @PathVariable postId: Long,
        @RequestBody request: SharePostRequest
    ): AnbdApiResponse<SharePostResponse> {
        val email = oAuth2User.attributes["email"] as? String
            ?: throw IllegalArgumentException("Email not found in authentication data")

        val updatedPost = sharePostService.updatePost(email, postId, request)

        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = updatedPost
        )
    }

    @Operation(
        summary = "나눔글 삭제",
        description = "나눔글을 삭제합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "나눔글 삭제 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청")
        ]
    )
    @DeleteMapping("/{postId}")
    fun deletePost(@PathVariable postId: Long): AnbdApiResponse<String> {
        sharePostService.deletePost(postId)

        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = AnbdApiResponse.SUCCESS
        )
    }

    @Operation(
        summary = "나눔글 좋아요",
        description = "나눔글을 좋아요."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "나눔글 좋아요 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청")
        ]
    )
    @PostMapping("/like")
    fun addLike(
        @AuthenticationPrincipal oAuth2User: OAuth2User,
        @RequestParam postId: Long
    ): AnbdApiResponse<SharePostLikeResponse> {
        val email = oAuth2User.attributes["email"] as? String
            ?: throw IllegalArgumentException("Email not found in authentication data")

        val like = sharePostLikeService.addLike(email, postId)

        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = like
        )
    }

    @Operation(
        summary = "나눔글 좋아요 취소",
        description = "나눔글을 좋아요 취소."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "나눔글 좋아요 취소 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청")
        ]
    )
    @DeleteMapping("/like")
    fun removeLike(
        @AuthenticationPrincipal oAuth2User: OAuth2User,
        @RequestParam postId: Long
    ): AnbdApiResponse<String> {
        val email = oAuth2User.attributes["email"] as? String
            ?: throw IllegalArgumentException("Email not found in authentication data")

        sharePostLikeService.removeLike(email, postId)

        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = AnbdApiResponse.SUCCESS
        )
    }
}

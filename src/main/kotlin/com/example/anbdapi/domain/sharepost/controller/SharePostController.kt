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
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

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
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createPost(
        authentication: Authentication,
        @RequestPart title: String,
        @RequestParam category: ShareCategory,
        @RequestParam content: String,
        @RequestPart images: List<MultipartFile>,
        @RequestParam type: ShareType,
        @RequestParam description: String?
    ): AnbdApiResponse<SharePostResponse> {
        val request = SharePostRequest.from(title, category, content, images, type, description)

        val post = sharePostService.createPost(authentication, request)

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
        authentication: Authentication,
        @PathVariable postId: Long
    ): AnbdApiResponse<SharePostResponse> {
        val post = sharePostService.getPostById(authentication, postId)

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
        authentication: Authentication,
        @RequestParam(required = false) keyword: String?,
        @RequestParam(required = false) location: String?,
        @RequestParam(required = false) category: ShareCategory?,
        @RequestParam(required = false) type: ShareType?,
        pageable: Pageable
    ): AnbdApiResponse<Page<SharePostResponse>> {
        val posts = sharePostService.getPosts(authentication, keyword, location, category, type, pageable)

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
        authentication: Authentication,
        @PathVariable userId: Long,
        pageable: Pageable
    ): AnbdApiResponse<Page<SharePostResponse>> {
        val posts = sharePostService.getUserPosts(authentication, userId, pageable)

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
        authentication: Authentication,
        @PathVariable postId: Long,
        @RequestParam title: String,
        @RequestParam category: ShareCategory,
        @RequestParam content: String,
        @RequestPart images: List<MultipartFile>,
        @RequestParam type: ShareType,
        @RequestParam description: String?
    ): AnbdApiResponse<SharePostResponse> {
        val request = SharePostRequest(title, category, content, images, type, description)

        val updatedPost = sharePostService.updatePost(authentication, postId, request)

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
        authentication: Authentication,
        @RequestParam postId: Long
    ): AnbdApiResponse<SharePostLikeResponse> {
        val like = sharePostLikeService.addLike(authentication, postId)

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
        authentication: Authentication,
        @RequestParam postId: Long
    ): AnbdApiResponse<String> {
        sharePostLikeService.removeLike(authentication, postId)

        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = AnbdApiResponse.SUCCESS
        )
    }
}

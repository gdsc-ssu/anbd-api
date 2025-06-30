package com.example.anbdapi.domain.sharepost.controller

import com.example.anbdapi.domain.report.dto.request.ReportRequest
import com.example.anbdapi.domain.report.dto.response.ReportResponse
import com.example.anbdapi.domain.report.service.ReportService
import com.example.anbdapi.domain.sharepost.controller.request.BiddingRequest
import com.example.anbdapi.domain.sharepost.controller.request.SharePostRequest
import com.example.anbdapi.domain.sharepost.controller.response.BiddingResponse
import com.example.anbdapi.domain.sharepost.controller.response.SharePostLikeResponse
import com.example.anbdapi.domain.sharepost.controller.response.SharePostResponse
import com.example.anbdapi.domain.sharepost.service.BiddingService
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
@Tag(name = "📮 SharePost API", description = "나눔글 관련 API (CRUD, 좋아요, 입찰, 신고 등)")
@RequestMapping("/v1/share-posts")
class SharePostController(
    private val traceIdResolver: TraceIdResolver,
    private val sharePostService: SharePostService,
    private val sharePostLikeService: SharePostLikeService,
    private val biddingService: BiddingService,
    private val reportService: ReportService,
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
        @RequestParam title: String,
        @RequestParam content: String,
        @RequestPart images: List<MultipartFile>,
        @RequestParam type: ShareType
    ): AnbdApiResponse<SharePostResponse> {
        val request = SharePostRequest.from(title, content, images, type)

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
        @RequestParam(required = false) isSold: Boolean?,
        pageable: Pageable
    ): AnbdApiResponse<Page<SharePostResponse>> {
        val posts = sharePostService.getPosts(authentication, keyword, location, category, type, isSold, pageable)

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
    @PutMapping("/{postId}", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updatePost(
        authentication: Authentication,
        @PathVariable postId: Long,
        @RequestParam title: String,
        @RequestParam content: String,
        @RequestPart images: List<MultipartFile>,
        @RequestParam type: ShareType
    ): AnbdApiResponse<SharePostResponse> {
        val request = SharePostRequest(title, content, images, type)

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
    @PostMapping("/{postId}/like")
    fun addLike(
        authentication: Authentication,
        @PathVariable postId: Long
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
    @DeleteMapping("/{postId}/like")
    fun removeLike(
        authentication: Authentication,
        @PathVariable postId: Long
    ): AnbdApiResponse<String> {
        sharePostLikeService.removeLike(authentication, postId)

        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = AnbdApiResponse.SUCCESS
        )
    }

    @Operation(
        summary = "공유 글 입찰하기",
        description = "공유 글 입찰하기."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "공유 글 입찰하기 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청")
        ]
    )
    @PostMapping("/{postId}/bid")
    fun bidPost(
        authentication: Authentication,
        @PathVariable postId: Long,
        @RequestBody request: BiddingRequest
    ): AnbdApiResponse<BiddingResponse> {
        val bidding = biddingService.create(authentication, postId, request)

        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = BiddingResponse.from(bidding)
        )
    }

    @Operation(
        summary = "공유 글 입찰 수정",
        description = "공유 글 입찰 수정."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "공유 글 입찰 수정 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청")
        ]
    )
    @PutMapping("/bid/{biddingId}")
    fun updateBid(
        authentication: Authentication,
        @PathVariable biddingId: Long,
        @RequestBody request: BiddingRequest
    ): AnbdApiResponse<BiddingResponse> {
        val bidding = biddingService.update(authentication, biddingId, request)

        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = BiddingResponse.from(bidding)
        )
    }

    @Operation(
        summary = "공유 글 입찰 삭제",
        description = "공유 글 입찰 삭제."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "공유 글 입찰 삭제 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청")
        ]
    )
    @DeleteMapping("/bid/{biddingId}")
    fun deleteBid(
        authentication: Authentication,
        @PathVariable biddingId: Long
    ): AnbdApiResponse<String> {
        biddingService.delete(authentication, biddingId)

        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = AnbdApiResponse.SUCCESS
        )
    }

    @Operation(
        summary = "공유 글 입찰 전체 조회",
        description = "공유 글 입찰 전체 조회."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "공유 글 입찰 조회 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청")
        ]
    )
    @GetMapping("/{postId}/bids")
    fun getBids(
        @PathVariable postId: Long
    ): AnbdApiResponse<List<BiddingResponse>> {
        val biddings = biddingService.getBiddings(postId)

        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = biddings.map { BiddingResponse.from(it) }
        )
    }

    @Operation(
        summary = "공유 글 입찰 단일 조회",
        description = "공유 글 입찰 단일 조회."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "공유 글 입찰 조회 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청")
        ]
    )
    @GetMapping("/bid/{biddingId}")
    fun getBid(
        @PathVariable biddingId: Long
    ): AnbdApiResponse<BiddingResponse> {
        val bidding = biddingService.getBidding(biddingId)

        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = BiddingResponse.from(bidding)
        )
    }

    @Operation(
        summary = "낙찰자 선택 및 거래 완료 처리(임시)",
        description = "낙찰자 선택 및 거래 완료 처리(임시)"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "공유 글 입찰 조회 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청")
        ]
    )
    @PostMapping("/{postId}/bid/{biddingId}/complete")
    fun completeBid(
        authentication: Authentication,
        @PathVariable postId: Long,
        @PathVariable biddingId: Long
    ): AnbdApiResponse<String> {
        biddingService.completeBid(authentication, postId, biddingId)

        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = AnbdApiResponse.SUCCESS
        )
    }

    @Operation(
        summary = "나눔글 신고하기",
        description = "나눔글을 신고합니다. 카테고리와 설명을 포함할 수 있습니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "신고 접수 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청"),
            ApiResponse(responseCode = "409", description = "중복 신고")
        ]
    )
    @PostMapping("/{postId}/report")
    fun reportPost(
        authentication: Authentication,
        @PathVariable postId: Long,
        @RequestBody request: ReportRequest
    ): AnbdApiResponse<ReportResponse> {
        val report = reportService.createReport(authentication, postId, request)

        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = report
        )
    }

    @Operation(
        summary = "기부금 영수증 인증 + 낙찰 확정",
        description = "영수증을 업로드해 인증이 완료되면 해당 입찰을 낙찰자로 확정하고 거래를 완료합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "영수증 인증 & 낙찰 확정 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청")
        ]
    )
    @PostMapping("/{postId}/bid/{biddingId}/verify",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun verifyAndFinish(
        authentication: Authentication,
        @PathVariable postId: Long,
        @PathVariable biddingId: Long,
        @RequestPart("receiptImage") receipt: MultipartFile
    ): AnbdApiResponse<String> {

        biddingService.verifyReceiptAndComplete(authentication, postId, biddingId, receipt)

        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = AnbdApiResponse.SUCCESS
        )
    }
}
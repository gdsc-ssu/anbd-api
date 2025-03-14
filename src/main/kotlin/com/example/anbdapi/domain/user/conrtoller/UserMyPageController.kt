package com.example.anbdapi.domain.user.conrtoller

import com.example.anbdapi.domain.sharepost.controller.response.SharePostResponse
import com.example.anbdapi.domain.user.dto.request.ProfileImageNicknameRequest
import com.example.anbdapi.domain.user.dto.response.UserProfileResponse
import com.example.anbdapi.domain.user.exception.UserProfileImageNicknameException
import com.example.anbdapi.domain.user.service.UserApplicationService
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
@RequestMapping("/v1/users/me")
@Tag(name = "👤 MyPage API", description = "마이페이지 관련 API (활동 내역, 좋아요 목록 등)")
class UserMyPageController(
    private val traceIdResolver: TraceIdResolver,
    private val userApplicationService: UserApplicationService
) {
    @Operation(
        summary = "현재 사용자 관심 나눔글 목록 조회",
        description = "현재 로그인한 사용자가 좋아요한 나눔글 목록(관심 목록)을 조회합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "나눔글 조회 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청"),
            ApiResponse(responseCode = "401", description = "인증 실패")
        ]
    )
    @GetMapping("/liked-posts")
    fun getLikedPosts(
        authentication: Authentication,
        pageable: Pageable
    ): AnbdApiResponse<Page<SharePostResponse>> {

        val likedPosts = userApplicationService.getLikedPosts(authentication, pageable)

        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = likedPosts
        )
    }

    @Operation(
        summary = "현재 사용자 프로필 정보 조회",
        description = "현재 로그인한 사용자의 닉네임, 프로필 이미지, 신뢰도 정보를 조회합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "프로필 조회 성공"),
            ApiResponse(responseCode = "401", description = "인증 실패")
        ]
    )
    @GetMapping("/profile")
    fun getUserProfile(authentication: Authentication): AnbdApiResponse<UserProfileResponse> {

        val userProfile = userApplicationService.getMyProfile(authentication)

        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = userProfile
        )
    }

    @Operation(
        summary = "현재 사용자 프로필 이미지 및 닉네임 업데이트",
        description = "마이페이지에서 사용자의 프로필 이미지와 닉네임을 업데이트합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "프로필 업데이트 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청"),
            ApiResponse(responseCode = "401", description = "인증 실패")
        ]
    )
    @PatchMapping("/profile", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateProfileImageAndNickname(
        authentication: Authentication,
        @RequestPart(value = "nickname", required = false) nickname: String?,
        @RequestPart(value = "profileImage", required = false) profileImage: MultipartFile?
    ): AnbdApiResponse<String> {

        if (nickname == null && (profileImage == null || profileImage.isEmpty)) {
            throw UserProfileImageNicknameException("프로필 이미지나 닉네임 중 하나는 제공해야 합니다.")
        }

        val request = ProfileImageNicknameRequest.from(nickname, profileImage)

        val result = userApplicationService.updateProfileImageAndNickname(authentication, request)

        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = result
        )
    }

    @Operation(
        summary = "현재 사용자가 등록한 나눔글 목록 조회",
        description = "현재 로그인한 사용자가 등록한 나눔글 목록을 조회합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "나눔글 조회 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청"),
            ApiResponse(responseCode = "401", description = "인증 실패")
        ]
    )
    @GetMapping("/shared-posts")
    fun getMySharedPosts(
        authentication: Authentication,
        pageable: Pageable
    ): AnbdApiResponse<Page<SharePostResponse>> {

        val sharedPosts = userApplicationService.getMySharedPosts(authentication, pageable)

        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = sharedPosts
        )
    }
}
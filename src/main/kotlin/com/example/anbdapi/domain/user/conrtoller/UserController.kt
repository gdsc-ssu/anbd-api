package com.example.anbdapi.domain.user.conrtoller

import com.example.anbdapi.domain.user.dto.request.ProfileUpdateRequest
import com.example.anbdapi.domain.user.dto.response.UserInformationResponse
import com.example.anbdapi.domain.user.dto.response.UserProfileResponse
import com.example.anbdapi.domain.user.service.UserApplicationService
import com.example.anbdapi.support.logging.TraceIdResolver
import com.example.anbdapi.support.response.AnbdApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/users")
@Tag(name = "😀 User API", description = "사용자 관련 API (로그아웃, 프로필 업데이트, 회원 탈퇴)")
class UserController(
    private val traceIdResolver: TraceIdResolver,
    private val userApplicationService: UserApplicationService
) {

    @Operation(
        summary = "사용자 로그아웃",
        description = "사용자를 로그아웃 처리합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청")
        ]
    )
    @PostMapping("/logout")
    fun logout(authentication: Authentication): AnbdApiResponse<String> {

        val result = userApplicationService.logout(authentication)

        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = result
        )
    }

    @Operation(
        summary = "최초 회원가입시 사용자 프로필 업데이트",
        description = "최초로 사용자가 회원가입할 때 추가로 받은사용자의 성별, 생년월일, 동네, 관심 카테고리 정보를 업데이트합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "프로필 업데이트 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청"),
            ApiResponse(responseCode = "401", description = "인증 실패")
        ]
    )
    @PutMapping("/profiles")
    fun updateProfile(
        authentication: Authentication,
        @Valid @RequestBody request: ProfileUpdateRequest
    ): AnbdApiResponse<String> {

        val result = userApplicationService.updateProfile(authentication, request)

        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = result
        )
    }

    @Operation(
        summary = "사용자 계정 삭제",
        description = "사용자 계정을 탈퇴(삭제) 처리합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "회원 탈퇴 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청"),
            ApiResponse(responseCode = "401", description = "인증 실패")
        ]
    )
    @DeleteMapping("/withdraw")
    fun withdraw(authentication: Authentication): AnbdApiResponse<String> {

        val result = userApplicationService.withdrawUser(authentication)

        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = result
        )
    }

    @Operation(
        summary = "현재 사용자 정보 반환",
        description = "현재 로그인한 사용자의 정보를 반환합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "회원 정보 요청 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청"),
            ApiResponse(responseCode = "401", description = "인증 실패")
        ]
    )
    @GetMapping("/me")
    fun me(authentication: Authentication): AnbdApiResponse<UserInformationResponse> {

        val result = userApplicationService.getMyInfo(authentication)

        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = result
        )
    }

    @Operation(
        summary = "특정 사용자 정보 조회",
        description = "사용자 ID를 통해 특정 사용자의 전체 정보를 반환합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청"),
            ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
        ]
    )
    @GetMapping("/{userId}")
    fun getUserInformation(
        @PathVariable userId: Long
    ): AnbdApiResponse<UserInformationResponse> {

        val result = userApplicationService.getUserInfo(userId)

        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = result
        )
    }

    @Operation(
        summary = "특정 사용자 프로필 조회",
        description = "사용자 ID를 통해 특정 사용자의 프로필 정보를 반환합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "프로필 조회 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청"),
            ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
        ]
    )
    @GetMapping("/{userId}/profile")
    fun getUserProfile(
        @PathVariable userId: Long
    ): AnbdApiResponse<UserProfileResponse> {

        val result = userApplicationService.getUserProfile(userId)

        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = result
        )
    }
}
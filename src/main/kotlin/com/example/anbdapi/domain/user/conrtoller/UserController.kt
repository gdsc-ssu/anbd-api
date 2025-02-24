package com.example.anbdapi.domain.user.conrtoller

import com.example.anbdapi.domain.user.dto.request.ProfileUpdateRequest
import com.example.anbdapi.domain.user.dto.response.UserInformationResponse
import com.example.anbdapi.domain.user.service.UserService
import com.example.anbdapi.support.logging.TraceIdResolver
import com.example.anbdapi.support.response.AnbdApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User API", description = "사용자 관련 API (로그아웃, 프로필 업데이트, 회원 탈퇴)")
class UserController(
    private val traceIdResolver: TraceIdResolver,
    private val userService: UserService
) {

    @Operation(
        summary = "Logout",
        description = "사용자를 로그아웃 처리합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청")
        ]
    )
    @PostMapping("/logout")
    fun logout(@AuthenticationPrincipal oAuth2User: OAuth2User): AnbdApiResponse<String> {
        val email = oAuth2User.attributes["email"] as? String
            ?: throw IllegalArgumentException("Email not found in authentication data")
        val result = userService.logoutUser(email)
        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = result
        )
    }

    @Operation(
        summary = "Update Profile",
        description = "사용자의 프로필 정보를 업데이트합니다."
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
        @AuthenticationPrincipal oAuth2User: OAuth2User,
        @Valid @RequestBody request: ProfileUpdateRequest
    ): AnbdApiResponse<String> {
        val email = oAuth2User.attributes["email"] as? String
            ?: throw IllegalArgumentException("Email not found in authentication data")

        userService.updateUserProfile(email, request)
        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = "Profile updated successfully"
        )
    }

    @Operation(
        summary = "Withdraw",
        description = "사용자 계정을 탈퇴(삭제) 처리합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "회원 탈퇴 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청"),
            ApiResponse(responseCode = "401", description = "인증 실패")
        ]
    )
    @PatchMapping("/withdraw")
    fun withdraw(@AuthenticationPrincipal oAuth2User: OAuth2User): AnbdApiResponse<String> {
        val email = oAuth2User.attributes["email"] as? String
            ?: throw IllegalArgumentException("Email not found in authentication data")

        val result = userService.withdrawUser(email)
        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = result
        )
    }

    @Operation(
        summary = "Get Current User Information",
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
    fun me(@AuthenticationPrincipal oAuth2User: OAuth2User): AnbdApiResponse<UserInformationResponse> {
        val email = oAuth2User.attributes["email"] as? String
            ?: throw IllegalArgumentException("Email not found in authentication data")

        val result = userService.getUserInfo(email)
        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = result
        )
    }
}
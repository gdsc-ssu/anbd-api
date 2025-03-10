package com.example.anbdapi.domain.user.conrtoller

import com.example.anbdapi.domain.user.dto.request.ProfileUpdateRequest
import com.example.anbdapi.domain.user.dto.response.UserInformationResponse
import com.example.anbdapi.domain.user.exception.UserNotFoundException
import com.example.anbdapi.domain.user.service.UserImageService
import com.example.anbdapi.domain.user.service.UserService
import com.example.anbdapi.support.logging.TraceIdResolver
import com.example.anbdapi.support.response.AnbdApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/v1/users")
@Tag(name = "😀 User API", description = "사용자 관련 API (로그아웃, 프로필 업데이트, 회원 탈퇴)")
class UserController(
    private val traceIdResolver: TraceIdResolver,
    private val userService: UserService,
    private val userImageService: UserImageService
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

        val user = userService.getCurrentUser(authentication)
            ?: throw UserNotFoundException("User not found")

        val result = userService.logoutUser(user.id!!)

        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = result
        )
    }

    @Operation(
        summary = "사용자 프로필 업데이트",
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
        authentication: Authentication,
        @Valid @RequestBody request: ProfileUpdateRequest
    ): AnbdApiResponse<String> {

        val user = userService.getCurrentUser(authentication)
            ?: throw UserNotFoundException("User not found")

        userService.updateUserProfile(user.id!!, request)

        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = "Profile updated successfully"
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
    @PatchMapping("/withdraw")
    fun withdraw(authentication: Authentication): AnbdApiResponse<String> {

        val user = userService.getCurrentUser(authentication)
            ?: throw UserNotFoundException("User not found")

        val result = userService.deleteUser(user.id!!)

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

        val user = userService.getCurrentUser(authentication)
            ?: throw UserNotFoundException("User not found")

        val result = userService.getUserInfo(user.id!!)

        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = result
        )
    }

    @Operation(
        summary = "사용자 프로필 이미지 생성/업데이트",
        description = "사용자의 프로필 이미지를 업로드합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "이미지 업로드 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청"),
            ApiResponse(responseCode = "401", description = "인증 실패")
        ]
    )
    @PostMapping("/profile-image", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadProfileImage(
        authentication: Authentication,
        @RequestParam("image") file: MultipartFile
    ): AnbdApiResponse<String> {

        val user = userService.getCurrentUser(authentication)
            ?: throw UserNotFoundException("User not found")

        val imageUrl = userImageService.uploadImage(user.id!!, file)

        return AnbdApiResponse.success(
            traceId = traceIdResolver.getTraceId(),
            body = imageUrl
        )
    }
}
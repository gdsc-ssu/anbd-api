package com.example.anbdapi.domain.dev.auth.controller

import com.example.anbdapi.domain.dev.auth.dto.request.LogoutRequest
import com.example.anbdapi.domain.dev.auth.dto.request.ProfileUpdateRequest
import com.example.anbdapi.domain.dev.auth.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User API", description = "사용자 관련 API (로그아웃, 프로필 업데이트, 회원 탈퇴)")
class UserControllerImpl(
    private val userService: UserService
) : UserController {

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
    override fun logout(@RequestBody request: LogoutRequest): ResponseEntity<String> {
        val result = userService.logoutUser(request.email)
        return ResponseEntity.ok(result)
    }

    @Operation(
        summary = "Update Profile",
        description = "사용자의 프로필 정보를 업데이트합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "프로필 업데이트 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청")
        ]
    )
    @PutMapping("/profile/update")
    override fun updateProfile(
        @AuthenticationPrincipal oAuth2User: OAuth2User,
        @RequestBody request: ProfileUpdateRequest
    ): ResponseEntity<String> {
        val email = oAuth2User.attributes["email"] as? String
            ?: throw IllegalArgumentException("Email not found in authentication data")

        userService.updateUserProfile(email, request)
        return ResponseEntity.ok("Profile updated successfully")
    }

    @Operation(
        summary = "Withdraw",
        description = "사용자 계정을 탈퇴(삭제) 처리합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "회원 탈퇴 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청")
        ]
    )
    @DeleteMapping("/withdraw")
    override fun withdraw(@AuthenticationPrincipal oAuth2User: OAuth2User): ResponseEntity<String> {
        val email = oAuth2User.attributes["email"] as? String
            ?: throw IllegalArgumentException("Email not found in authentication data")

        val result = userService.withdrawUser(email)
        return ResponseEntity.ok(result)
    }
}
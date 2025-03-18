package com.example.anbdapi.domain.user.service

import com.example.anbdapi.domain.sharepost.controller.response.SharePostResponse
import com.example.anbdapi.domain.user.dto.request.ProfileImageNicknameRequest
import com.example.anbdapi.domain.user.dto.request.ProfileUpdateRequest
import com.example.anbdapi.domain.user.dto.response.UserInformationResponse
import com.example.anbdapi.domain.user.dto.response.UserProfileResponse
import com.example.anbdapi.domain.user.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class UserApplicationService(
    private val userService: UserService,
    private val userContentService: UserContentService,
    private val userImageService: UserImageService
) {
    // 현재 사용자 조회
    fun getCurrentUser(authentication: Authentication): User {
        val user = userService.getCurrentUserNotNull(authentication)
        return user
    }

    // 현재 사용자 ID 조회
    fun getCurrentUserId(authentication: Authentication): Long {
        return getCurrentUser(authentication).id!!
    }

    // 사용자 전체 정보 조회
    fun getMyInfo(authentication: Authentication): UserInformationResponse {
        val user = getCurrentUser(authentication)
        return userService.getUserInfo(user.id!!)
    }

    // 특정 사용자 전체 정보 조회
    fun getUserInfo(userId: Long): UserInformationResponse {
        return userService.getUserInfo(userId)
    }

    // 최초 회원가입시 프로필 업데이트
    fun updateProfile(authentication: Authentication, request: ProfileUpdateRequest): String {
        val user = getCurrentUser(authentication)
        userService.updateUserProfile(user.id!!, request)
        return "Profile updated successfully"
    }

    // 로그아웃
    fun logout(authentication: Authentication): String {
        val user = getCurrentUser(authentication)
        return userService.logoutUser(user.id!!)
    }

    // 회원 탈퇴
    fun withdrawUser(authentication: Authentication): String {
        val user = getCurrentUser(authentication)
        return userService.deleteUser(user.id!!)
    }

    // 현재 사용자 관심 목록 조회
    fun getLikedPosts(authentication: Authentication, pageable: Pageable): Page<SharePostResponse> {
        val userId = getCurrentUserId(authentication)
        return userContentService.getLikedPosts(userId, pageable)
    }

    // 마이페이지 프로필 이미지 및 닉네임 업데이트
    fun updateProfileImageAndNickname(authentication: Authentication, request: ProfileImageNicknameRequest): String {
        val user = getCurrentUser(authentication)

        var imageUrl: String? = null
        if (request.profileImage != null && !request.profileImage.isEmpty) {
            imageUrl = userImageService.uploadUserImage(user.id!!, request.profileImage)
        }

        userService.updateProfileImageAndNickname(user.id!!, request.nickname, imageUrl)

        return "Profile updated successfully"
    }

    // 내가 등록한 나눔글 목록 조회
    fun getMySharedPosts(authentication: Authentication, pageable: Pageable): Page<SharePostResponse> {
        val userId = getCurrentUserId(authentication)
        return userContentService.getMySharedPosts(userId, pageable)
    }

    // 마이페이지 프로필 정보(ID, 닉네임, 이미지, 신뢰도) 조회
    fun getMyProfile(authentication: Authentication): UserProfileResponse {
        val user = getCurrentUser(authentication)
        return userService.getUserProfile(user.id!!)
    }

    // 특정 사용자 프로필 정보(ID, 닉네임, 이미지, 신뢰도) 조회
    fun getUserProfile(userId: Long): UserProfileResponse {
        return userService.getUserProfile(userId)
    }
}
package com.example.anbdapi.domain.sharepost.controller.request

import com.example.anbdapi.support.enums.ShareCategory
import com.example.anbdapi.support.enums.ShareType
import org.springframework.web.multipart.MultipartFile

data class SharePostRequest(
    val title: String,
    val content: String,
    val images: List<MultipartFile>,
    val type: ShareType
) {
    companion object {
        fun from(
            title: String,
            content: String,
            images: List<MultipartFile>,
            type: ShareType
        ): SharePostRequest {
            return SharePostRequest(
                title = title,
                content = content,
                images = images,
                type = type
            )
        }
    }
}
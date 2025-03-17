package com.example.anbdapi.domain.sharepost.controller.request

import com.example.anbdapi.support.enums.ShareCategory
import com.example.anbdapi.support.enums.ShareType
import org.springframework.web.multipart.MultipartFile

data class SharePostRequest(
    val title: String,
    val category: ShareCategory,
    val content: String,
    val images: List<MultipartFile>,
    val type: ShareType,
    val description: String?
) {
    companion object {
        fun from(
            title: String,
            category: ShareCategory,
            content: String,
            images: List<MultipartFile>,
            type: ShareType,
            description: String?
        ): SharePostRequest {
            return SharePostRequest(
                title = title,
                category = category,
                content = content,
                images = images,
                type = type,
                description = description
            )
        }
    }
}
package com.example.anbdapi.domain.sharepost.controller.request

import com.example.anbdapi.support.enums.ShareCategory
import com.example.anbdapi.support.enums.ShareType

data class SharePostRequest(
    val title: String,
    val category: ShareCategory,
    val content: String,
    val images: List<String>,
    val type: ShareType,
    val description: String?
)
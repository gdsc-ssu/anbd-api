package com.example.anbdapi.domain.sharepost.service

import com.example.anbdapi.support.enums.ShareCategory

interface SharePostCategoryGenerator {
    fun categorizeItem(title: String, content: String): ShareCategory
}
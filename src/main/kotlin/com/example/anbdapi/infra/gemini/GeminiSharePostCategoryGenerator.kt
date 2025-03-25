package com.example.anbdapi.infra.gemini

import com.example.anbdapi.domain.sharepost.exception.SharePostGeminiException
import com.example.anbdapi.domain.sharepost.service.SharePostCategoryGenerator
import com.example.anbdapi.support.enums.ShareCategory

class GeminiSharePostCategoryGenerator(private val geminiClient: GeminiClient) : SharePostCategoryGenerator {

    override fun categorizeItem(title: String, content: String): ShareCategory {

        val prompt = """
            다음 물품 정보를 분석하여 가장 적합한 카테고리를 선택해주세요.
            
            제목: "$title"
            내용: "$content"
            
            다음 카테고리 중 정확히 하나만 선택하세요(다른 설명 없이 카테고리명만 대문자로 반환):
            - FOOD
            - DIGITAL
            - HOME_APPLIANCE
            - FURNITURE_INTERIOR
            - WOMAN_ACCESSORY
            - MAN_FASHION
            - LIVING_KITCHEN
            - SPORT_LEISURE
            - HOBBY_GAME_MUSIC
            - BEAUTY_COSMETIC
            - PLANT
            - BOOK
        """.trimIndent()

        val response = geminiClient.callGeminiApi(prompt)

        try {
            val normalizedResponse = response.trim().uppercase()

            ShareCategory.entries.forEach { category ->
                if (normalizedResponse == category.name) {
                    return category
                }
            }

            throw SharePostGeminiException("Share post category mapping error : $response")
        } catch (e: Exception) {
            // Default category
            return ShareCategory.DIGITAL
        }
    }
}
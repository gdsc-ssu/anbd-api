package com.example.anbdapi.infra.gemini

import com.example.anbdapi.domain.sharepost.service.SharePostDescriptionGenerator

class GeminiSharePostDescriptionGenerator(private val geminiClient: GeminiClient) : SharePostDescriptionGenerator {

    private val maxDescriptionLength = 50
    private val descriptionPromptTemplate = """
        제목: "%s"과 내용: "%s"을 바탕으로 한 이 물품의 창의적인 재사용과 reform 아이디어를 활용하여
         해양 생태계를 보호하는 영향을 공백 포함 50자 이내로 써주세요. 
    """

    override fun generateDescription(title: String, content: String): String {
        val prompt = String.format(descriptionPromptTemplate, title, content).trimIndent()
        val response = geminiClient.callGeminiApi(prompt)
        return response.take(maxDescriptionLength)
    }
}
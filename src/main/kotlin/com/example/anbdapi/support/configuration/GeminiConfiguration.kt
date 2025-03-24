package com.example.anbdapi.support.configuration

import com.example.anbdapi.domain.sharepost.service.SharePostCategoryGenerator
import com.example.anbdapi.domain.sharepost.service.SharePostDescriptionGenerator
import com.example.anbdapi.infra.gemini.GeminiClient
import com.example.anbdapi.infra.gemini.GeminiSharePostCategoryGenerator
import com.example.anbdapi.infra.gemini.GeminiSharePostDescriptionGenerator
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GeminiConfiguration {
    @Value("\${gemini.api.key}")
    private lateinit var apiKey: String

    @Bean
    fun geminiClient(): GeminiClient {
        return GeminiClient(apiKey)
    }

    @Bean
    fun SharePostDescriptionGenerator(geminiClient: GeminiClient): SharePostDescriptionGenerator {
        return GeminiSharePostDescriptionGenerator(geminiClient)
    }

    @Bean
    fun SharePostCategoryGenerator(geminiClient: GeminiClient): SharePostCategoryGenerator {
        return GeminiSharePostCategoryGenerator(geminiClient)
    }
}
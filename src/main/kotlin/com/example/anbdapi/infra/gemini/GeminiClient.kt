package com.example.anbdapi.infra.gemini

import com.example.anbdapi.domain.sharepost.exception.SharePostGeminiException
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpStatus
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class GeminiClient(private val apiKey: String) {

    private val client = HttpClient.newBuilder().build()
    private val objectMapper = ObjectMapper()
    private val apiBaseUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent"

    fun callGeminiApi(prompt: String, temperature: Double = 0.7, maxTokens: Int = 100): String {

        val requestBody = mapOf(
            "contents" to listOf(
                mapOf("parts" to listOf(mapOf("text" to prompt)))
            ),
            "generationConfig" to mapOf(
                "temperature" to temperature,
                "maxOutputTokens" to maxTokens
            )
        )

        val request = HttpRequest.newBuilder()
            .uri(URI.create("$apiBaseUrl?key=$apiKey"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestBody)))
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        if (response.statusCode() != HttpStatus.OK.value()) {
            throw SharePostGeminiException("SharePostGemini error : Status code ${response.statusCode()}")
        }

        val responseJson = objectMapper.readTree(response.body())
        val error = responseJson.path("error")

        if (!error.isMissingNode) {
            val errorMessage = error.path("message").asText("Unknown error")
            throw SharePostGeminiException("SharePostGemini error: $errorMessage")
        }

        return responseJson
            .path("candidates")
            .path(0)
            .path("content")
            .path("parts")
            .path(0)
            .path("text")
            .asText()
    }
}
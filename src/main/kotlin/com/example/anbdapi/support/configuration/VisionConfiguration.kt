package com.example.anbdapi.support.configuration

import com.google.cloud.vision.v1.ImageAnnotatorClient
import com.google.cloud.vision.v1.ImageAnnotatorSettings
import com.google.auth.oauth2.GoogleCredentials
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import java.io.IOException

@Configuration
class VisionConfiguration {

    @Value("\${google.cloud.vision.credentials-path}")
    private lateinit var credentialsPath: String

    @Bean
    fun imageAnnotatorClient(): ImageAnnotatorClient {
        try {
            val resource = ClassPathResource(credentialsPath)
            val credentials = GoogleCredentials.fromStream(resource.inputStream)
                .createScoped(listOf("https://www.googleapis.com/auth/cloud-platform"))

            val settings = ImageAnnotatorSettings.newBuilder()
                .setCredentialsProvider { credentials }
                .build()

            return ImageAnnotatorClient.create(settings)
        } catch (e: IOException) {
            throw RuntimeException("Google Vision API client error: ${e.message}", e)
        }
    }
}
package com.example.anbdapi.support.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import java.io.IOException

@Configuration
class GcsConfiguration {

    @Value("\${gcs.bucket-name}")
    private lateinit var bucketName: String

    @Value("\${gcs.credentials-path}")
    private lateinit var credentialsPath: String

    @Bean
    fun storage(): Storage {
        try {
            val credentials = GoogleCredentials.fromStream(
                ClassPathResource(credentialsPath).inputStream
            )
            return StorageOptions.newBuilder()
                .setCredentials(credentials)
                .build()
                .service
        } catch (e: IOException) {
            throw RuntimeException("Gcs initialization error", e)
        }
    }

    @Bean
    fun bucketName(): String {
        return bucketName
    }
}
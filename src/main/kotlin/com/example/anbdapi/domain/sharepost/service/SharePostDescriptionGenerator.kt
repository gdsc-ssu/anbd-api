package com.example.anbdapi.domain.sharepost.service

interface SharePostDescriptionGenerator {
    fun generateDescription(title: String, content: String): String
}
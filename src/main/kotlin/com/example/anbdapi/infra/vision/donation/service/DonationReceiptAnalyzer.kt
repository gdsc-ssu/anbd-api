package com.example.anbdapi.infra.vision.donation.service

import com.example.anbdapi.infra.vision.VisionClient
import com.example.anbdapi.infra.vision.donation.exception.DonationVerificationException
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.util.regex.Pattern

@Component
class DonationReceiptAnalyzer(private val visionClient: VisionClient) {

    fun analyzeReceipt(imageFile: MultipartFile): DonationReceiptData {
        val textList = visionClient.detectText(imageFile)
        val fullText = textList.joinToString("\n")
        val amount = extractDonationAmount(fullText)

        return DonationReceiptData(amount, fullText)
    }

    private fun extractDonationAmount(text: String): Int {
        if (text.isBlank()) return 0

        // 우선순위에 따라 추출 시도
        return findAmountByKeywords(text)
            ?: findAmountByGeneralPatterns(text)
            ?: findAmountBySimplePattern(text)
            ?: 0
    }

    // 1. 명시적 키워드로 금액 찾기
    private fun findAmountByKeywords(text: String): Int? {
        val patterns = listOf(
            "(?:후원\\s*금액|기부\\s*금액|금액|donation)[\\s:]*(\\d{1,3}(?:,\\d{3})*|\\d+)\\s*원",
            "(?:후원|기부)[\\s:]*(\\d{1,3}(?:,\\d{3})*|\\d+)\\s*원"
        )

        patterns.forEach { patternStr ->
            val matcher = Pattern.compile(patternStr).matcher(text)
            if (matcher.find()) {
                val amountStr = matcher.group(1)?.replace(",", "")
                val amount = amountStr?.toIntOrNull()
                if (amount != null && amount > 0) return amount
            }
        }

        return null
    }

    // 2. 일반 패턴으로 금액 찾기
    private fun findAmountByGeneralPatterns(text: String): Int? {
        val patterns = mapOf(
            "(?i)(기부|기부금|후원금?|donation)[\\s\\S]{0,30}(\\d{1,3}(?:,\\d{3})*|\\d+)\\s*원" to 2,
            "(?i)(\\d{1,3}(?:,\\d{3})*|\\d+)\\s*원.{0,30}(?:기부|기부금|후원)" to 1,
            "(?i)(?:amount|sum|total|금액)[\\s\\S]{0,30}(\\d{1,3}(?:,\\d{3})*|\\d+)\\s*원" to 1,
            "(?i)(?:₩|￦|KRW)\\s*(\\d{1,3}(?:,\\d{3})*|\\d+)" to 1
        )

        patterns.forEach { (patternStr, groupIdx) ->
            val matcher = Pattern.compile(patternStr).matcher(text)
            if (matcher.find()) {
                try {
                    val amountStr = matcher.group(groupIdx)?.replace(",", "")
                    val amount = amountStr?.toIntOrNull()
                    if (amount != null && amount > 0) return amount
                } catch (e: Exception) {
                    throw DonationVerificationException("Extract Error in General Pattern.")
                }
            }
        }

        return null
    }

    // 3. 단순 숫자 + '원' 패턴으로 금액 찾기
    private fun findAmountBySimplePattern(text: String): Int? {
        val pattern = "(\\d{1,3}(?:,\\d{3})*|\\d+)\\s*원"
        val matcher = Pattern.compile(pattern).matcher(text)

        val amounts = mutableListOf<Int>()
        while (matcher.find()) {
            val amountStr = matcher.group(1)?.replace(",", "")
            val amount = amountStr?.toIntOrNull()
            if (amount != null && amount > 0) amounts.add(amount)
        }

        return amounts.firstOrNull()
    }

    data class DonationReceiptData(
        val amount: Int,
        val fullText: String
    )
}
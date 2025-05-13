package com.example.anbdapi.infra.vision.donation.dto.response

import com.example.anbdapi.infra.vision.donation.service.DonationReceiptAnalyzer

data class DonationVerificationResponse(
    val amount: Int,
    val isVerified: Boolean,
    val fullText: String
) {
    companion object {
        fun from(donationReceiptData: DonationReceiptAnalyzer.DonationReceiptData, isVerified: Boolean): DonationVerificationResponse {
            return DonationVerificationResponse(
                amount = donationReceiptData.amount,
                isVerified = isVerified,
                fullText = donationReceiptData.fullText
            )
        }
    }
}
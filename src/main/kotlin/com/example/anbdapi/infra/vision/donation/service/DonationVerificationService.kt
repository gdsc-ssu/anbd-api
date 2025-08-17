package com.example.anbdapi.infra.vision.donation.service

import com.example.anbdapi.infra.vision.donation.dto.response.DonationVerificationResponse
import com.example.anbdapi.infra.vision.donation.exception.DonationVerificationException
import com.example.anbdapi.domain.sharepost.repository.BiddingRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class DonationVerificationService(
    private val donationReceiptAnalyzer: DonationReceiptAnalyzer,
    private val biddingRepository: BiddingRepository
) {

    fun verifyDonationReceipt(receiptImage: MultipartFile, biddingId: Long): DonationVerificationResponse {
        if (receiptImage.isEmpty) {
            throw DonationVerificationException("Receipt Image Empty.")
        }

        val receiptData = donationReceiptAnalyzer.analyzeReceipt(receiptImage)

        val bidding = biddingRepository.findByIdOrNull(biddingId)
            ?: throw DonationVerificationException("Bidding not found")

        if (bidding.donation != receiptData.amount) {
            throw DonationVerificationException(
                "Donation amount mismatch. Bidding: ${bidding.donation}, Receipt: ${receiptData.amount}"
            )
        }

        return DonationVerificationResponse.from(
            donationReceiptData = receiptData,
            isVerified = true
        )
    }
}
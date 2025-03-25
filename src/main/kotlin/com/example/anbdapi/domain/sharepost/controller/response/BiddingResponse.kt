package com.example.anbdapi.domain.sharepost.controller.response

import com.example.anbdapi.domain.sharepost.entity.Bidding
import com.example.anbdapi.domain.user.dto.response.UserInformationResponse
import java.time.LocalDateTime

data class BiddingResponse(
    val id: Long,
    val content: String,
    val donation: Int,
    val isSelected: Boolean? = null,
    val user: UserInformationResponse,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(bidding: Bidding): BiddingResponse {
            return BiddingResponse(
                id = bidding.id!!,
                content = bidding.content ?: "",
                donation = bidding.donation,
                isSelected = bidding.isSelected,
                user = UserInformationResponse.from(bidding.user),
                createdAt = bidding.createdAt,
                updatedAt = bidding.updatedAt
            )
        }
    }
}

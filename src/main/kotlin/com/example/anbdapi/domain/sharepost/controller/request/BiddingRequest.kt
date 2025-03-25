package com.example.anbdapi.domain.sharepost.controller.request

data class BiddingRequest(
    val content: String,
    val donation: Int,
) {
    companion object {
        fun from(
            content: String,
            donation: Int
        ): BiddingRequest {
            return BiddingRequest(
                content = content,
                donation = donation
            )
        }
    }
}
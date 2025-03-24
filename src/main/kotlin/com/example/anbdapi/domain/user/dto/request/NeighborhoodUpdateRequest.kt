package com.example.anbdapi.domain.user.dto.request

data class NeighborhoodUpdateRequest(
    val neighborhood: String
) {
    companion object {
        fun from(neighborhood: String): NeighborhoodUpdateRequest {
            return NeighborhoodUpdateRequest(neighborhood)
        }
    }
}
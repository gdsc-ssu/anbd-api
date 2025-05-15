package com.example.anbdapi.domain.chat.dto

data class ChattingMessageDto(
    var roomId : Long,
    var writer : Long? = null,
    var message : String,
)
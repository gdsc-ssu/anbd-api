package com.example.anbdapi.support.enums

enum class ReportCategory(val description: String) {
    PROHIBITED_ITEM("거래 금지 물품이에요"),
    NOT_SHARE_POST("나눔글 게시글이 아니에요"),
    DISPUTE_OCCURRED("거래 중 분쟁이 발생했어요"),
    SUSPECTED_FRAUD("사기인 것 같아요"),
    REPORT_AUTHOR("작성자 신고하기")
}
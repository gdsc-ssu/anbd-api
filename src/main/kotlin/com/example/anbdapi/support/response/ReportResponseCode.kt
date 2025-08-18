package com.example.anbdapi.support.response

class ReportResponseCode {
    companion object {
        const val REPORT_01 = "Report-001" // ReportDuplicateException
        const val REPORT_02 = "Report-002" // ReportNotFoundException
        const val REPORT_03 = "Report-003" // ReportSelfException
    }
}
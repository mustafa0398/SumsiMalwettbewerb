package com.example.sumsimalwettbewerb

data class SubmissionResponse(
    val status: String,
    val status_code: Int,
    val message: String,
    val data: Any?
)

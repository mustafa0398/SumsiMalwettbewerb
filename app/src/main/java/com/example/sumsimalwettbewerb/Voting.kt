package com.example.sumsimalwettbewerb

data class VoteBody(
    val email: String
)

data class VoteResponse(
    val status: String,
    val status_code: Int,
    val message: String,
    val data: VoteData
)

data class VoteData(
    val votes: Int
)

data class Vote(
    val id: String,
    val email: String,
    val submissionId: String,
    val createdAt: String?,
    val updatedAt: String?
)

data class VoteCountResponse(
    val status: String,
    val status_code: Int,
    val message: String,
    val data: VoteCountData
)

data class VoteCountData(
    val votes: Int
)

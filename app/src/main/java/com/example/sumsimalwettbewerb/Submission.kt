package com.example.sumsimalwettbewerb

data class ApiResult(
    val status: String,
    val status_code: Int,
    val message: String,
    val data: List<Submission>
)

data class Image(
    val id: Int,
    val name: String,
    val extension: String,
    val size: Int,
    val location: String,
    val public_location: String,
    val imageable_type: String,
    val imageable_id: String,
    val created_at: String,
    val updated_at: String
)
data class Submission(
    val id: String,
    val legalguardian_firstname: String,
    val legalguardian_lastname: String,
    val email: String,
    val child_firstname: String,
    val child_age: String,
    val approval_privacypolicy: Int,
    val approval_participation: Int,
    val approval_mailnotification: Int,
    val created_at: String?,
    val updated_at: String?,
    val image: Image?,
    val votings: List<Voting> = emptyList()
) {
    class Voting {

    }
}

data class SubmissionResponse(

    val status: String,
    val status_code: Int,
    val message: String,
    val data: Any?

)


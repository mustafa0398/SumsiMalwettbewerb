package com.example.sumsimalwettbewerb

data class Photo(
    val id: String,
    val imageUrl: String,
    var voteCount: Int,
    val votes: MutableList<Vote>,
    var hasVoted: Boolean,
    val localImagePath: String?,
    val childName: String,
    val childAge: String
)

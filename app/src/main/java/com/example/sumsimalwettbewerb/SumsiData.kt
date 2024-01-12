package com.example.sumsimalwettbewerb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName ="sumsi_data")
data class SumsiData(
    @PrimaryKey
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
    val imageId: Int,
    val votingCount:Int,
    val localImagePath:String?
)
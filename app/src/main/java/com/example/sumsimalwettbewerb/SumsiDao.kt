package com.example.sumsimalwettbewerb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SumsiDao {
    @Query("SELECT * FROM sumsi_data")
    fun getAllSumsiData(): LiveData<List<SumsiData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSumsiData(data:SumsiData)

    @Query("SELECT * FROM sumsi_data WHERE id = :imageId LIMIT 1")
    fun getSumsiDataById(imageId: Int): SumsiData?
}
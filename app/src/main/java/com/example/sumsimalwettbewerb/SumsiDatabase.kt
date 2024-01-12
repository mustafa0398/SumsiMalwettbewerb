package com.example.sumsimalwettbewerb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [SumsiData::class], version = 1, exportSchema = false)
abstract class SumsiDatabase : RoomDatabase() {
    abstract fun sumsiDao(): SumsiDao

    companion object {
        @Volatile
        private var INSTANCE: SumsiDatabase? = null

        fun getDatabase(context: Context): SumsiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SumsiDatabase::class.java,
                    "sumsi_data"
                ).fallbackToDestructiveMigration().build()


                INSTANCE = instance
                instance
            }
        }
    }
}
package com.example.pokersolverGTO.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pokersolverGTO.trainer.data.Converters
import com.example.pokersolverGTO.trainer.data.PlayerStatsDao
import com.example.pokersolverGTO.trainer.data.TrainingHandDao
import com.example.pokersolverGTO.trainer.models.PlayerStats
import com.example.pokersolverGTO.trainer.models.TrainingHandResult

@Database(
    entities = [
        TrainingStats::class,
        TrainingHandResult::class,
        PlayerStats::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trainingStatsDao(): TrainingStatsDao
    abstract fun trainingHandDao(): TrainingHandDao
    abstract fun playerStatsDao(): PlayerStatsDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun get(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "poker_solver.db"
            ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
        }
    }
}


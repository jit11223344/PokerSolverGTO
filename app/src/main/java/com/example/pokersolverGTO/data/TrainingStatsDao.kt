package com.example.pokersolverGTO.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingStatsDao {
    @Query("SELECT * FROM training_stats WHERE id = 1")
    fun observe(): Flow<TrainingStats?>

    @Query("SELECT * FROM training_stats WHERE id = 1")
    suspend fun get(): TrainingStats?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stats: TrainingStats)

    @Update
    suspend fun update(stats: TrainingStats)
}


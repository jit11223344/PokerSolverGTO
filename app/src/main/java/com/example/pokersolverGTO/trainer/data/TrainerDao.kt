package com.example.pokersolverGTO.trainer.data

import androidx.room.*
import com.example.pokersolverGTO.trainer.models.PlayerStats
import com.example.pokersolverGTO.trainer.models.TrainingHandResult
import com.example.pokersolverGTO.trainer.models.TrainingMode
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingHandDao {
    @Query("SELECT * FROM training_hands ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentHands(limit: Int = 50): Flow<List<TrainingHandResult>>

    @Query("SELECT * FROM training_hands WHERE mode = :mode ORDER BY timestamp DESC")
    fun getHandsByMode(mode: TrainingMode): Flow<List<TrainingHandResult>>

    @Query("SELECT * FROM training_hands WHERE DATE(timestamp/1000, 'unixepoch') = DATE('now') ORDER BY timestamp DESC")
    fun getTodayHands(): Flow<List<TrainingHandResult>>

    @Query("SELECT AVG(evLoss) FROM training_hands WHERE mode = :mode")
    suspend fun getAverageEVLoss(mode: TrainingMode): Float?

    @Query("SELECT COUNT(*) FROM training_hands WHERE isCorrect = 1 AND mode = :mode")
    suspend fun getCorrectCount(mode: TrainingMode): Int

    @Query("SELECT COUNT(*) FROM training_hands WHERE mode = :mode")
    suspend fun getTotalCount(mode: TrainingMode): Int

    @Insert
    suspend fun insert(hand: TrainingHandResult): Long

    @Query("DELETE FROM training_hands")
    suspend fun deleteAll()
}

@Dao
interface PlayerStatsDao {
    @Query("SELECT * FROM player_stats WHERE id = 1")
    fun getStats(): Flow<PlayerStats>

    @Query("SELECT * FROM player_stats WHERE id = 1")
    suspend fun getStatsOnce(): PlayerStats?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stats: PlayerStats)

    @Update
    suspend fun update(stats: PlayerStats)

    @Query("UPDATE player_stats SET totalXP = totalXP + :xp WHERE id = 1")
    suspend fun addXP(xp: Int)

    @Query("UPDATE player_stats SET currentStreak = currentStreak + 1, bestStreak = MAX(currentStreak + 1, bestStreak) WHERE id = 1")
    suspend fun incrementStreak()

    @Query("UPDATE player_stats SET currentStreak = 0 WHERE id = 1")
    suspend fun resetStreak()
}


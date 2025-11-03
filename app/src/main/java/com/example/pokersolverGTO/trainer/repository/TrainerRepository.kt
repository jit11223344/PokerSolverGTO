package com.example.pokersolverGTO.trainer.repository

import com.example.pokersolverGTO.data.AppDatabase
import com.example.pokersolverGTO.trainer.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*

class TrainerRepository(private val database: AppDatabase) {

    private val handDao = database.trainingHandDao()
    private val statsDao = database.playerStatsDao()

    // Hand operations
    suspend fun saveHandResult(result: TrainingHandResult) {
        handDao.insert(result)
        updatePlayerStats(result)
    }

    fun getRecentHands(limit: Int = 50): Flow<List<TrainingHandResult>> =
        handDao.getRecentHands(limit)

    fun getHandsByMode(mode: TrainingMode): Flow<List<TrainingHandResult>> =
        handDao.getHandsByMode(mode)

    fun getTodayHands(): Flow<List<TrainingHandResult>> =
        handDao.getTodayHands()

    suspend fun getAccuracyForMode(mode: TrainingMode): Float {
        val correct = handDao.getCorrectCount(mode)
        val total = handDao.getTotalCount(mode)
        return if (total == 0) 0f else correct.toFloat() / total
    }

    // Stats operations
    fun getPlayerStats(): Flow<PlayerStats> = statsDao.getStats()

    suspend fun getPlayerStatsOnce(): PlayerStats {
        return statsDao.getStatsOnce() ?: PlayerStats().also { statsDao.insert(it) }
    }

    private suspend fun updatePlayerStats(result: TrainingHandResult) {
        val stats = statsDao.getStatsOnce() ?: PlayerStats()

        // Update basic counters
        val newTotalHands = stats.totalHands + 1
        val newCorrect = if (result.isCorrect) stats.correctDecisions + 1 else stats.correctDecisions
        val newEVLoss = stats.totalEVLoss + result.evLoss
        val newXP = stats.totalXP + result.xpEarned

        // Update streak
        val newStreak = if (result.isCorrect) stats.currentStreak + 1 else 0
        val newBestStreak = maxOf(stats.bestStreak, newStreak)

        // Check if date changed
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val handsToday = if (stats.lastPlayedDate == today) stats.handsPlayedToday + 1 else 1

        // Calculate level
        val newLevel = (newXP / 100) + 1

        // Update mode-specific accuracy
        val preflopAcc = if (result.mode == TrainingMode.PREFLOP) {
            getAccuracyForMode(TrainingMode.PREFLOP)
        } else stats.preflopAccuracy

        val postflopAcc = if (result.mode == TrainingMode.POSTFLOP) {
            getAccuracyForMode(TrainingMode.POSTFLOP)
        } else stats.postflopAccuracy

        // Calculate average decision time
        val totalTime = stats.averageDecisionTime * stats.totalHands + result.timeToDecide
        val newAvgTime = totalTime / newTotalHands

        // Check for achievements
        val newAchievements = stats.achievements.toMutableList()
        checkAndAddAchievements(newAchievements, stats, newTotalHands, newStreak, newLevel)

        val updatedStats = stats.copy(
            totalHands = newTotalHands,
            correctDecisions = newCorrect,
            totalEVLoss = newEVLoss,
            totalXP = newXP,
            currentLevel = newLevel,
            currentStreak = newStreak,
            bestStreak = newBestStreak,
            handsPlayedToday = handsToday,
            lastPlayedDate = today,
            preflopAccuracy = preflopAcc,
            postflopAccuracy = postflopAcc,
            averageDecisionTime = newAvgTime,
            achievements = newAchievements
        )

        statsDao.update(updatedStats)
    }

    private fun checkAndAddAchievements(
        achievements: MutableList<String>,
        stats: PlayerStats,
        newTotal: Int,
        newStreak: Int,
        newLevel: Int
    ) {
        // First hand
        if (newTotal == 1 && "first_hand" !in achievements) {
            achievements.add("first_hand")
        }

        // Milestones
        if (newTotal == 100 && "century" !in achievements) {
            achievements.add("century")
        }
        if (newTotal == 1000 && "thousand" !in achievements) {
            achievements.add("thousand")
        }

        // Streaks
        if (newStreak == 10 && "streak_10" !in achievements) {
            achievements.add("streak_10")
        }
        if (newStreak == 50 && "streak_50" !in achievements) {
            achievements.add("streak_50")
        }

        // Level milestones
        if (newLevel == 10 && "level_10" !in achievements) {
            achievements.add("level_10")
        }
        if (newLevel == 50 && "level_50" !in achievements) {
            achievements.add("level_50")
        }

        // Accuracy achievements
        if (stats.accuracy >= 0.9f && stats.totalHands >= 50 && "high_accuracy" !in achievements) {
            achievements.add("high_accuracy")
        }
    }

    suspend fun resetProgress() {
        handDao.deleteAll()
        statsDao.insert(PlayerStats())
    }

    // Analytics
    suspend fun getSessionSummary(): SessionSummary {
        val todayHands = handDao.getTodayHands().first()
        val stats = getPlayerStatsOnce()

        return SessionSummary(
            handsPlayed = todayHands.size,
            correctDecisions = todayHands.count { it.isCorrect },
            totalEVLoss = todayHands.sumOf { it.evLoss.toDouble() }.toFloat(),
            xpEarned = todayHands.sumOf { it.xpEarned },
            averageDecisionTime = if (todayHands.isEmpty()) 0L
                else todayHands.map { it.timeToDecide }.average().toLong(),
            currentStreak = stats.currentStreak
        )
    }
}

data class SessionSummary(
    val handsPlayed: Int,
    val correctDecisions: Int,
    val totalEVLoss: Float,
    val xpEarned: Int,
    val averageDecisionTime: Long,
    val currentStreak: Int
) {
    val accuracy: Float
        get() = if (handsPlayed == 0) 0f else correctDecisions.toFloat() / handsPlayed
}

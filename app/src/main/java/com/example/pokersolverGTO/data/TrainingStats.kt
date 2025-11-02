package com.example.pokersolverGTO.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "training_stats")
data class TrainingStats(
    @PrimaryKey val id: Int = 1,
    val totalQuestions: Int = 0,
    val correctAnswers: Int = 0,
    val lastTimestamp: Long = System.currentTimeMillis()
) {
    val accuracy: Float get() = if (totalQuestions == 0) 0f else correctAnswers.toFloat() / totalQuestions
}


package com.example.pokersolverGTO.trainer.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.pokersolverGTO.trainer.data.Converters

// Core poker models for the trainer
enum class PokerPosition {
    UTG, MP, CO, BTN, SB, BB
}

enum class Street {
    PREFLOP, FLOP, TURN, RIVER
}

enum class PokerAction {
    FOLD, CHECK, CALL, RAISE, ALL_IN
}

data class PokerHand(
    val card1: com.example.pokersolverGTO.model.Card,
    val card2: com.example.pokersolverGTO.model.Card
) {
    fun toNotation(): String {
        val suited = if (card1.suit == card2.suit) "s" else "o"
        val rank1 = card1.rank.symbol
        val rank2 = card2.rank.symbol
        return if (card1.rank.value > card2.rank.value) "$rank1$rank2$suited"
        else "$rank2$rank1$suited"
    }
}

data class Board(
    val flop: List<com.example.pokersolverGTO.model.Card> = emptyList(),
    val turn: com.example.pokersolverGTO.model.Card? = null,
    val river: com.example.pokersolverGTO.model.Card? = null
) {
    val cards: List<com.example.pokersolverGTO.model.Card>
        get() = flop + listOfNotNull(turn, river)

    val street: Street
        get() = when {
            flop.isEmpty() -> Street.PREFLOP
            turn == null -> Street.FLOP
            river == null -> Street.TURN
            else -> Street.RIVER
        }
}

data class ActionDecision(
    val action: PokerAction,
    val amount: Int = 0 // For raises/bets
)

data class GTORecommendation(
    val optimalAction: PokerAction,
    val actionFrequencies: Map<PokerAction, Float>, // 0.0 to 1.0
    val evDifference: Float, // How much EV is lost vs optimal
    val explanation: String,
    val rangeAdvice: String? = null
)

data class HandScenario(
    val heroHand: PokerHand,
    val position: PokerPosition,
    val board: Board,
    val potSize: Int,
    val stackSize: Int,
    val facingBet: Int = 0,
    val actionHistory: List<ActionDecision> = emptyList()
)

@Entity(tableName = "training_hands")
@TypeConverters(Converters::class)
data class TrainingHandResult(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val mode: TrainingMode,
    val heroHandNotation: String,
    val position: PokerPosition,
    val street: Street,
    val playerAction: PokerAction,
    val optimalAction: PokerAction,
    val evLoss: Float, // Negative = good, positive = mistake
    val isCorrect: Boolean,
    val potSize: Int,
    val xpEarned: Int,
    val timeToDecide: Long // milliseconds
)

enum class TrainingMode {
    PREFLOP, POSTFLOP, QUIZ, SANDBOX
}

@Entity(tableName = "player_stats")
data class PlayerStats(
    @PrimaryKey val id: Int = 1,
    val totalHands: Int = 0,
    val correctDecisions: Int = 0,
    val totalEVLoss: Float = 0f,
    val totalXP: Int = 0,
    val currentLevel: Int = 1,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val handsPlayedToday: Int = 0,
    val lastPlayedDate: String = "",
    val preflopAccuracy: Float = 0f,
    val postflopAccuracy: Float = 0f,
    val averageDecisionTime: Long = 0L,
    val achievements: List<String> = emptyList()
) {
    val accuracy: Float
        get() = if (totalHands == 0) 0f else correctDecisions.toFloat() / totalHands

    val averageEVLoss: Float
        get() = if (totalHands == 0) 0f else totalEVLoss / totalHands

    val xpToNextLevel: Int
        get() = currentLevel * 100

    val levelProgress: Float
        get() = (totalXP % xpToNextLevel).toFloat() / xpToNextLevel
}

data class RankInfo(
    val level: Int,
    val title: String,
    val color: Long
)

object PlayerRanks {
    fun getRank(level: Int): RankInfo = when {
        // Changed default lowest-level title from "Fish" to "Shark" per request
        level < 5 -> RankInfo(level, "Shark", 0xFF9E9E9E)
        level < 10 -> RankInfo(level, "Calling Station", 0xFF8BC34A)
        level < 20 -> RankInfo(level, "Rock", 0xFF2196F3)
        level < 30 -> RankInfo(level, "Shark", 0xFF9C27B0)
        level < 50 -> RankInfo(level, "Pro", 0xFFFF9800)
        else -> RankInfo(level, "GTO Master", 0xFFFFD700)
    }
}

package com.example.pokersolverGTO.engine

import com.example.pokersolverGTO.model.Card
import com.example.pokersolverGTO.model.Rank

enum class Position { UTG, MP, CO, BTN, SB, BB }

enum class Action { FOLD, CALL, RAISE_2_5X, RAISE_3X, ALL_IN }

object GTOStrategy {
    // Simplified opening/response guidance
    fun getGTORecommendation(cards: List<Card>, position: Position): String {
        val ranks = cards.map { it.rank }.sortedByDescending { it.value }
        val premium = ranks.first().value >= Rank.QUEEN.value && ranks.last().value >= Rank.TEN.value
        return when (position) {
            Position.UTG, Position.MP -> if (premium) "Open 2.5x" else "Mostly fold; mix calls with suited connectors"
            Position.CO -> if (ranks.first().value >= Rank.TEN.value) "Open 2.5x - 3x" else "Fold or call suited"
            Position.BTN -> "Open wide: 50%+; raise 2.5x"
            Position.SB -> "Tight vs BB; complete/call more; 3-bet premium"
            Position.BB -> "Defend wide vs small opens; 3-bet premium"
        }
    }

    fun isPreflopActionCorrect(
        cards: List<Card>,
        position: Position,
        action: Action,
        facingRaise: Boolean
    ): Pair<Boolean, String> {
        val high = cards.maxOf { it.rank.value }
        val pair = cards[0].rank == cards[1].rank
        val suited = false // simplified; not tracking suit here

        val strong = pair && cards[0].rank.value >= Rank.TEN.value || high >= Rank.ACE.value
        val medium = high >= Rank.JACK.value || pair

        val correct = when {
            facingRaise && strong && (action == Action.RAISE_3X || action == Action.ALL_IN) -> true
            !facingRaise && position in setOf(Position.CO, Position.BTN) && medium && (action == Action.RAISE_2_5X || action == Action.RAISE_3X) -> true
            action == Action.CALL && medium -> true
            action == Action.FOLD && !medium -> true
            else -> false
        }
        val explain = if (correct) "Fits simplified GTO range for $position"
                       else "Deviates from simplified GTO for $position"
        return correct to explain
    }
}


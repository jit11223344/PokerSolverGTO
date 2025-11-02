package com.example.pokersolverGTO.engine

import com.example.pokersolverGTO.model.*

object HandEvaluator {
    // Very compact 7-card evaluator returning best 5-card hand rank with tie-break cards
    fun evaluateHand(cards: List<Card>): HandEvaluation {
        require(cards.size >= 5) { "Need at least 5 cards" }
        val all = cards
        val ranks = Rank.values().associateWith { r -> all.count { it.rank == r } }
        val suits = Suit.values().associateWith { s -> all.filter { it.suit == s } }

        // Check flush
        val flushSuit = suits.maxByOrNull { it.value.size }?.takeIf { it.value.size >= 5 }?.key
        val flushCards = flushSuit?.let { s -> all.filter { it.suit == s }.sortedByDescending { it.rank.value } }

        // Straight helper over unique ranks
        fun bestStraight(from: List<Card>): List<Card>? {
            val unique = from.map { it.rank.value }.toSet().toMutableSet()
            // Wheel straight A-2-3-4-5
            if (14 in unique) unique.add(1)
            val sorted = unique.sortedDescending()
            var run = mutableListOf<Int>()
            var prev: Int? = null
            for (v in sorted) {
                if (prev == null || prev - 1 == v) run.add(v) else run = mutableListOf(v)
                if (run.size >= 5) break
                prev = v
            }
            if (run.size >= 5) {
                val target = run.take(5).map { if (it == 1) 14 else it }
                return target.map { v -> from.filter { it.rank.value == v }.maxBy { it.rank.value } }
            }
            return null
        }

        // Straight flush / Royal flush
        val straightFlush = flushCards?.let { bestStraight(it) }
        if (straightFlush != null) {
            val top = straightFlush.maxOf { it.rank.value }
            return if (top == 14) HandEvaluation(HandRank.ROYAL_FLUSH, straightFlush, "Royal Flush")
            else HandEvaluation(HandRank.STRAIGHT_FLUSH, straightFlush, "Straight Flush to ${straightFlush.first().rank}")
        }

        // Four of a kind
        ranks.entries.firstOrNull { it.value == 4 }?.let { four ->
            val kickers = all.filter { it.rank != four.key }.sortedByDescending { it.rank.value }.take(1)
            val best = List(4) { Card(four.key, Suit.SPADES) } // rank only matters
            return HandEvaluation(HandRank.FOUR_OF_A_KIND, best + kickers, "Four of a Kind ${four.key}")
        }

        // Full House
        val trips = ranks.filter { it.value == 3 }.keys.sortedByDescending { it.value }
        val pairs = ranks.filter { it.value >= 2 && it.key !in trips }.keys.sortedByDescending { it.value }
        if (trips.isNotEmpty() && (pairs.isNotEmpty() || ranks.any { it.value >= 2 && it.key == trips.drop(1).firstOrNull() })) {
            val t = trips.first()
            val p = (pairs.firstOrNull() ?: trips.drop(1).first())
            val best = List(3) { Card(t, Suit.SPADES) } + List(2) { Card(p, Suit.SPADES) }
            return HandEvaluation(HandRank.FULL_HOUSE, best, "Full House ${t}s over ${p}s")
        }

        // Flush
        if (flushCards != null) {
            val best = flushCards.take(5)
            return HandEvaluation(HandRank.FLUSH, best, "Flush ${flushSuit}")
        }

        // Straight
        val straight = bestStraight(all.sortedByDescending { it.rank.value })
        if (straight != null) {
            return HandEvaluation(HandRank.STRAIGHT, straight, "Straight to ${straight.first().rank}")
        }

        // Trips
        ranks.entries.firstOrNull { it.value == 3 }?.let { trip ->
            val kickers = all.filter { it.rank != trip.key }.sortedByDescending { it.rank.value }.take(2)
            val best = List(3) { Card(trip.key, Suit.SPADES) } + kickers
            return HandEvaluation(HandRank.THREE_OF_A_KIND, best, "Trip ${trip.key}s")
        }

        // Two Pair / One Pair
        val pairRanks = ranks.filter { it.value == 2 }.keys.sortedByDescending { it.value }
        if (pairRanks.size >= 2) {
            val top2 = pairRanks.take(2)
            val kick = all.filter { it.rank !in top2 }.sortedByDescending { it.rank.value }.first()
            val best = List(2) { Card(top2[0], Suit.SPADES) } + List(2) { Card(top2[1], Suit.SPADES) } + kick
            return HandEvaluation(HandRank.TWO_PAIR, best, "Two Pair ${top2[0]} and ${top2[1]}")
        }
        if (pairRanks.size == 1) {
            val pair = pairRanks.first()
            val kickers = all.filter { it.rank != pair }.sortedByDescending { it.rank.value }.take(3)
            val best = List(2) { Card(pair, Suit.SPADES) } + kickers
            return HandEvaluation(HandRank.ONE_PAIR, best, "Pair of ${pair}s")
        }

        // High card
        val bestHigh = all.sortedByDescending { it.rank.value }.take(5)
        return HandEvaluation(HandRank.HIGH_CARD, bestHigh, "High Card ${bestHigh.first().rank}")
    }
}


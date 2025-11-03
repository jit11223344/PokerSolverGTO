package com.example.pokersolverGTO.equity.logic

import com.example.pokersolverGTO.equity.models.*

class HandEvaluator {

    fun evaluateHand(cards: List<Card>): HandResult {
        require(cards.size >= 5) { "Need at least 5 cards to evaluate" }

        return if (cards.size == 7) {
            findBest5CardHand(cards)
        } else {
            evaluateFiveCards(cards.take(5))
        }
    }

    private fun findBest5CardHand(sevenCards: List<Card>): HandResult {
        val combinations = getCombinations(sevenCards, 5)
        return combinations.maxOf { evaluateFiveCards(it) }
    }

    private fun getCombinations(cards: List<Card>, k: Int): List<List<Card>> {
        if (k == 0) return listOf(emptyList())
        if (cards.isEmpty()) return emptyList()

        val head = cards.first()
        val tail = cards.drop(1)

        val withHead = getCombinations(tail, k - 1).map { listOf(head) + it }
        val withoutHead = getCombinations(tail, k)

        return withHead + withoutHead
    }

    private fun evaluateFiveCards(cards: List<Card>): HandResult {
        val sortedCards = cards.sortedByDescending { it.rank.value }
        val ranks = sortedCards.map { it.rank.value }
        val suits = sortedCards.map { it.suit }

        val isFlush = suits.toSet().size == 1
        val isStraight = checkStraight(ranks)

        val rankCounts = ranks.groupingBy { it }.eachCount()
        val counts = rankCounts.values.sortedDescending()

        return when {
            isStraight && isFlush && ranks.first() == 12 ->
                HandResult(HandRank.ROYAL_FLUSH, sortedCards, ranks)

            isStraight && isFlush ->
                HandResult(HandRank.STRAIGHT_FLUSH, sortedCards, ranks)

            counts == listOf(4, 1) -> {
                val quadRank = rankCounts.entries.first { it.value == 4 }.key
                val kicker = rankCounts.entries.first { it.value == 1 }.key
                HandResult(HandRank.FOUR_OF_A_KIND, sortedCards, listOf(quadRank, kicker))
            }

            counts == listOf(3, 2) -> {
                val tripRank = rankCounts.entries.first { it.value == 3 }.key
                val pairRank = rankCounts.entries.first { it.value == 2 }.key
                HandResult(HandRank.FULL_HOUSE, sortedCards, listOf(tripRank, pairRank))
            }

            isFlush ->
                HandResult(HandRank.FLUSH, sortedCards, ranks)

            isStraight ->
                HandResult(HandRank.STRAIGHT, sortedCards, ranks)

            counts == listOf(3, 1, 1) -> {
                val tripRank = rankCounts.entries.first { it.value == 3 }.key
                val kickers = rankCounts.entries.filter { it.value == 1 }.map { it.key }.sortedDescending()
                HandResult(HandRank.THREE_OF_A_KIND, sortedCards, listOf(tripRank) + kickers)
            }

            counts == listOf(2, 2, 1) -> {
                val pairs = rankCounts.entries.filter { it.value == 2 }.map { it.key }.sortedDescending()
                val kicker = rankCounts.entries.first { it.value == 1 }.key
                HandResult(HandRank.TWO_PAIR, sortedCards, pairs + kicker)
            }

            counts == listOf(2, 1, 1, 1) -> {
                val pairRank = rankCounts.entries.first { it.value == 2 }.key
                val kickers = rankCounts.entries.filter { it.value == 1 }.map { it.key }.sortedDescending()
                HandResult(HandRank.PAIR, sortedCards, listOf(pairRank) + kickers)
            }

            else ->
                HandResult(HandRank.HIGH_CARD, sortedCards, ranks)
        }
    }

    private fun checkStraight(ranks: List<Int>): Boolean {
        val sortedRanks = ranks.sorted()

        // Check for A-2-3-4-5 (wheel)
        if (sortedRanks == listOf(0, 1, 2, 3, 12)) return true

        // Check for consecutive ranks
        for (i in 0 until sortedRanks.size - 1) {
            if (sortedRanks[i + 1] != sortedRanks[i] + 1) return false
        }

        return true
    }
}


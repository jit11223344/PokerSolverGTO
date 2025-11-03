package com.example.pokersolverGTO.equity.logic

import com.example.pokersolverGTO.equity.models.*

class EquityCalculator(private val handEvaluator: HandEvaluator = HandEvaluator()) {

    data class EquityResult(
        val player1WinPercent: Float,
        val player2WinPercent: Float,
        val tiePercent: Float
    )

    fun calculateEquity(
        player1Cards: List<Card>,
        player2Cards: List<Card>,
        communityCards: List<Card>,
        iterations: Int = 10000
    ): EquityResult {
        require(player1Cards.size == 2) { "Player 1 must have 2 cards" }
        require(player2Cards.size == 2) { "Player 2 must have 2 cards" }
        require(communityCards.size <= 5) { "Maximum 5 community cards" }

        val usedCards = (player1Cards + player2Cards + communityCards).toSet()
        val remainingCards = createDeck().filter { it !in usedCards }

        var player1Wins = 0
        var player2Wins = 0
        var ties = 0

        repeat(iterations) {
            val simulatedBoard = simulateBoard(communityCards, remainingCards)

            val player1Hand = handEvaluator.evaluateHand(player1Cards + simulatedBoard)
            val player2Hand = handEvaluator.evaluateHand(player2Cards + simulatedBoard)

            when {
                player1Hand > player2Hand -> player1Wins++
                player2Hand > player1Hand -> player2Wins++
                else -> ties++
            }
        }

        return EquityResult(
            player1WinPercent = (player1Wins.toFloat() / iterations) * 100f,
            player2WinPercent = (player2Wins.toFloat() / iterations) * 100f,
            tiePercent = (ties.toFloat() / iterations) * 100f
        )
    }

    /**
     * Public helper to deterministically evaluate a completed board (5 community cards)
     * Returns 100/0/0, 0/100/0 or 0/0/100 depending on winner/tie.
     */
    fun evaluateFinalHands(
        player1Cards: List<Card>,
        player2Cards: List<Card>,
        communityCards: List<Card>
    ): EquityResult {
        require(player1Cards.size == 2) { "Player 1 must have 2 cards" }
        require(player2Cards.size == 2) { "Player 2 must have 2 cards" }
        require(communityCards.size == 5) { "Community must have 5 cards for final evaluation" }

        val p1 = handEvaluator.evaluateHand(player1Cards + communityCards)
        val p2 = handEvaluator.evaluateHand(player2Cards + communityCards)

        return when {
            p1 > p2 -> EquityResult(100f, 0f, 0f)
            p2 > p1 -> EquityResult(0f, 100f, 0f)
            else -> EquityResult(0f, 0f, 100f)
        }
    }

    private fun simulateBoard(currentBoard: List<Card>, availableCards: List<Card>): List<Card> {
        val cardsNeeded = 5 - currentBoard.size
        if (cardsNeeded == 0) return currentBoard

        val shuffled = availableCards.shuffled()
        return currentBoard + shuffled.take(cardsNeeded)
    }

    private fun createDeck(): List<Card> {
        val deck = mutableListOf<Card>()
        for (rank in Rank.entries) {
            for (suit in Suit.entries) {
                deck.add(Card(rank, suit))
            }
        }
        return deck
    }
}

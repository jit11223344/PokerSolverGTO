package com.example.pokersolverGTO.engine

import com.example.pokersolverGTO.model.Card
import com.example.pokersolverGTO.model.Deck
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object MonteCarloSimulator {
    suspend fun calculateEquity(
        heroCards: List<Card>,
        boardCards: List<Card>,
        numOpponents: Int = 1,
        simulations: Int = 10000
    ): Float = withContext(Dispatchers.Default) {
        var wins = 0
        var ties = 0
        repeat(simulations) {
            val deck = Deck().apply {
                remove(heroCards)
                remove(boardCards)
                shuffle()
            }
            val board = boardCards + deck.deal(5 - boardCards.size)
            val opponents = List(numOpponents) { deck.deal(2) }

            val heroEval = HandEvaluator.evaluateHand(heroCards + board)
            val oppEvals = opponents.map { HandEvaluator.evaluateHand(it + board) }
            val bestOpp = oppEvals.maxOrNull()!!
            when {
                heroEval > bestOpp -> wins++
                heroEval == bestOpp -> ties++
            }
        }
        ((wins + ties * 0.5f) / simulations.toFloat()) * 100f
    }
}


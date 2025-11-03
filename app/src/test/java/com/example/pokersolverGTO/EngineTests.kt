package com.example.pokersolverGTO

import com.example.pokersolverGTO.engine.HandEvaluator
import com.example.pokersolverGTO.model.Card
import com.example.pokersolverGTO.model.Rank
import com.example.pokersolverGTO.model.Suit
import org.junit.Assert.assertTrue
import org.junit.Test

class EngineTests {
    @Test
    fun testHandEvaluatorStraight() {
        val cards = listOf(
            Card(Rank.TEN, Suit.HEARTS),
            Card(Rank.JACK, Suit.SPADES),
            Card(Rank.QUEEN, Suit.CLUBS),
            Card(Rank.KING, Suit.DIAMONDS),
            Card(Rank.NINE, Suit.HEARTS),
            Card(Rank.TWO, Suit.CLUBS),
            Card(Rank.ACE, Suit.SPADES)
        )
        val eval = HandEvaluator.evaluateHand(cards)
        assertTrue(eval.handRank.value >= 5)
    }
}


package com.example.pokersolverGTO.model

class Deck {
    private val cards: MutableList<Card> = mutableListOf()

    init {
        reset()
    }

    fun reset() {
        cards.clear()
        for (suit in Suit.values()) {
            for (rank in Rank.values()) {
                cards.add(Card(rank, suit))
            }
        }
    }

    fun shuffle() {
        cards.shuffle()
    }

    fun size(): Int = cards.size

    fun deal(n: Int): List<Card> {
        val count = n.coerceAtMost(cards.size)
        val dealt = cards.take(count)
        repeat(count) { cards.removeAt(0) }
        return dealt
    }

    fun remove(toRemove: List<Card>) {
        toRemove.forEach { card -> cards.remove(card) }
    }

    fun remaining(): List<Card> = cards.toList()
}


package com.example.pokersolverGTO.model

enum class Rank(val value: Int, val symbol: String) {
    TWO(2, "2"),
    THREE(3, "3"),
    FOUR(4, "4"),
    FIVE(5, "5"),
    SIX(6, "6"),
    SEVEN(7, "7"),
    EIGHT(8, "8"),
    NINE(9, "9"),
    TEN(10, "10"),
    JACK(11, "J"),
    QUEEN(12, "Q"),
    KING(13, "K"),
    ACE(14, "A")
}

enum class Suit(val symbol: String, val color: String) {
    HEARTS("♥", "red"),
    DIAMONDS("♦", "red"),
    CLUBS("♣", "black"),
    SPADES("♠", "black")
}

data class Card(val rank: Rank, val suit: Suit) {
    override fun toString(): String = "${rank.symbol}${suit.symbol}"

    fun getDisplayString(): String = rank.symbol
    fun getSuitSymbol(): String = suit.symbol
    fun isRed(): Boolean = suit.color == "red"
}

enum class HandRank(val value: Int, val displayName: String) {
    HIGH_CARD(1, "High Card"),
    ONE_PAIR(2, "One Pair"),
    TWO_PAIR(3, "Two Pair"),
    THREE_OF_A_KIND(4, "Three of a Kind"),
    STRAIGHT(5, "Straight"),
    FLUSH(6, "Flush"),
    FULL_HOUSE(7, "Full House"),
    FOUR_OF_A_KIND(8, "Four of a Kind"),
    STRAIGHT_FLUSH(9, "Straight Flush"),
    ROYAL_FLUSH(10, "Royal Flush")
}

data class HandEvaluation(
    val handRank: HandRank,
    val cards: List<Card>,
    val description: String
) : Comparable<HandEvaluation> {
    override fun compareTo(other: HandEvaluation): Int {
        if (handRank != other.handRank) {
            return handRank.value.compareTo(other.handRank.value)
        }
        // Compare kickers
        for (i in cards.indices) {
            if (i >= other.cards.size) return 1
            val cmp = cards[i].rank.value.compareTo(other.cards[i].rank.value)
            if (cmp != 0) return cmp
        }
        return 0
    }
}

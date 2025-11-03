package com.example.pokersolverGTO.equity.models

data class Card(
    val rank: Rank,
    val suit: Suit
) {
    override fun toString(): String = "${rank.symbol}${suit.symbol}"

    fun toInt(): Int = rank.value * 4 + suit.ordinal

    companion object {
        fun fromInt(value: Int): Card {
            val rankValue = value / 4
            val suitOrdinal = value % 4
            return Card(
                Rank.values().first { it.value == rankValue },
                Suit.values()[suitOrdinal]
            )
        }
    }
}

enum class Rank(val value: Int, val symbol: String) {
    TWO(0, "2"),
    THREE(1, "3"),
    FOUR(2, "4"),
    FIVE(3, "5"),
    SIX(4, "6"),
    SEVEN(5, "7"),
    EIGHT(6, "8"),
    NINE(7, "9"),
    TEN(8, "10"),
    JACK(9, "J"),
    QUEEN(10, "Q"),
    KING(11, "K"),
    ACE(12, "A");

    companion object {
        fun fromSymbol(symbol: String): Rank? = values().find { it.symbol == symbol }
    }
}

enum class Suit(val symbol: String, val color: Long) {
    SPADE("♠", 0xFF000000),
    DIAMOND("♦", 0xFFE74C3C),
    CLUB("♣", 0xFF000000),
    HEART("♥", 0xFFE74C3C);

    companion object {
        fun fromSymbol(symbol: String): Suit? = values().find { it.symbol == symbol }
    }
}

enum class HandRank(val value: Int) {
    HIGH_CARD(0),
    PAIR(1),
    TWO_PAIR(2),
    THREE_OF_A_KIND(3),
    STRAIGHT(4),
    FLUSH(5),
    FULL_HOUSE(6),
    FOUR_OF_A_KIND(7),
    STRAIGHT_FLUSH(8),
    ROYAL_FLUSH(9)
}

data class HandResult(
    val handRank: HandRank,
    val handCards: List<Card>,
    val kickers: List<Int> = emptyList()
) : Comparable<HandResult> {
    override fun compareTo(other: HandResult): Int {
        if (handRank.value != other.handRank.value) {
            return handRank.value.compareTo(other.handRank.value)
        }

        for (i in kickers.indices) {
            if (i >= other.kickers.size) return 1
            val cmp = kickers[i].compareTo(other.kickers[i])
            if (cmp != 0) return cmp
        }

        return 0
    }
}


"""
Hand evaluation for poker.
"""

from collections import Counter
from enum import IntEnum
from typing import List, Tuple
from pokersolver.card import Card


class HandRank(IntEnum):
    """Poker hand rankings."""
    HIGH_CARD = 1
    PAIR = 2
    TWO_PAIR = 3
    THREE_OF_A_KIND = 4
    STRAIGHT = 5
    FLUSH = 6
    FULL_HOUSE = 7
    FOUR_OF_A_KIND = 8
    STRAIGHT_FLUSH = 9


class HandEvaluator:
    """Evaluates poker hands."""
    
    @staticmethod
    def evaluate(cards: List[Card]) -> Tuple[int, List[int]]:
        """
        Evaluate a poker hand.
        
        Args:
            cards: List of cards (typically 5-7 cards for Texas Hold'em)
            
        Returns:
            Tuple of (hand_rank, kickers) where kickers are used for tie-breaking
        """
        if len(cards) < 5:
            raise ValueError("Need at least 5 cards to evaluate a hand")
        
        # For 7 cards, find the best 5-card combination
        if len(cards) == 7:
            return HandEvaluator._best_five_card_hand(cards)
        
        # Evaluate 5-card hand
        ranks = sorted([c.rank for c in cards], reverse=True)
        suits = [c.suit for c in cards]
        
        is_flush = len(set(suits)) == 1
        is_straight = HandEvaluator._is_straight(ranks)
        
        if is_straight and is_flush:
            return (HandRank.STRAIGHT_FLUSH, [max(ranks)])
        
        rank_counts = Counter(ranks)
        counts = sorted(rank_counts.items(), key=lambda x: (x[1], x[0]), reverse=True)
        
        # Four of a kind
        if counts[0][1] == 4:
            return (HandRank.FOUR_OF_A_KIND, [counts[0][0], counts[1][0]])
        
        # Full house
        if counts[0][1] == 3 and counts[1][1] == 2:
            return (HandRank.FULL_HOUSE, [counts[0][0], counts[1][0]])
        
        # Flush
        if is_flush:
            return (HandRank.FLUSH, ranks)
        
        # Straight
        if is_straight:
            # Handle A-2-3-4-5 straight (wheel)
            if ranks == [14, 5, 4, 3, 2]:
                return (HandRank.STRAIGHT, [5])
            return (HandRank.STRAIGHT, [max(ranks)])
        
        # Three of a kind
        if counts[0][1] == 3:
            kickers = [counts[1][0], counts[2][0]]
            return (HandRank.THREE_OF_A_KIND, [counts[0][0]] + kickers)
        
        # Two pair
        if counts[0][1] == 2 and counts[1][1] == 2:
            pairs = sorted([counts[0][0], counts[1][0]], reverse=True)
            return (HandRank.TWO_PAIR, pairs + [counts[2][0]])
        
        # One pair
        if counts[0][1] == 2:
            kickers = [c[0] for c in counts[1:]]
            return (HandRank.PAIR, [counts[0][0]] + kickers)
        
        # High card
        return (HandRank.HIGH_CARD, ranks)
    
    @staticmethod
    def _is_straight(ranks: List[int]) -> bool:
        """Check if ranks form a straight."""
        if len(set(ranks)) != 5:
            return False
        
        # Check for A-2-3-4-5 straight (wheel)
        if sorted(ranks) == [2, 3, 4, 5, 14]:
            return True
        
        # Check for regular straight
        return max(ranks) - min(ranks) == 4
    
    @staticmethod
    def _best_five_card_hand(cards: List[Card]) -> Tuple[int, List[int]]:
        """Find the best 5-card hand from 7 cards."""
        from itertools import combinations
        
        best_hand = None
        best_eval = (0, [])
        
        for combo in combinations(cards, 5):
            evaluation = HandEvaluator.evaluate(list(combo))
            if evaluation > best_eval:
                best_eval = evaluation
                best_hand = combo
        
        return best_eval
    
    @staticmethod
    def compare_hands(hand1: List[Card], hand2: List[Card]) -> int:
        """
        Compare two poker hands.
        
        Args:
            hand1: First hand
            hand2: Second hand
            
        Returns:
            1 if hand1 wins, -1 if hand2 wins, 0 if tie
        """
        eval1 = HandEvaluator.evaluate(hand1)
        eval2 = HandEvaluator.evaluate(hand2)
        
        if eval1 > eval2:
            return 1
        elif eval1 < eval2:
            return -1
        else:
            return 0
    
    @staticmethod
    def hand_rank_name(rank: int) -> str:
        """Get the name of a hand rank."""
        names = {
            HandRank.HIGH_CARD: "High Card",
            HandRank.PAIR: "Pair",
            HandRank.TWO_PAIR: "Two Pair",
            HandRank.THREE_OF_A_KIND: "Three of a Kind",
            HandRank.STRAIGHT: "Straight",
            HandRank.FLUSH: "Flush",
            HandRank.FULL_HOUSE: "Full House",
            HandRank.FOUR_OF_A_KIND: "Four of a Kind",
            HandRank.STRAIGHT_FLUSH: "Straight Flush",
        }
        return names.get(rank, "Unknown")

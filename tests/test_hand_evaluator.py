"""Tests for hand evaluator."""

import pytest
from pokersolver.card import Card
from pokersolver.hand_evaluator import HandEvaluator, HandRank


class TestHandEvaluator:
    """Test HandEvaluator class."""
    
    def test_straight_flush(self):
        """Test straight flush detection."""
        cards = [Card.from_string(c) for c in ["Ah", "Kh", "Qh", "Jh", "Th"]]
        rank, kickers = HandEvaluator.evaluate(cards)
        assert rank == HandRank.STRAIGHT_FLUSH
    
    def test_four_of_a_kind(self):
        """Test four of a kind detection."""
        cards = [Card.from_string(c) for c in ["As", "Ah", "Ad", "Ac", "Kh"]]
        rank, kickers = HandEvaluator.evaluate(cards)
        assert rank == HandRank.FOUR_OF_A_KIND
        assert kickers[0] == 14  # Aces
    
    def test_full_house(self):
        """Test full house detection."""
        cards = [Card.from_string(c) for c in ["Ks", "Kh", "Kd", "Qc", "Qh"]]
        rank, kickers = HandEvaluator.evaluate(cards)
        assert rank == HandRank.FULL_HOUSE
    
    def test_flush(self):
        """Test flush detection."""
        cards = [Card.from_string(c) for c in ["Ah", "Kh", "9h", "7h", "2h"]]
        rank, kickers = HandEvaluator.evaluate(cards)
        assert rank == HandRank.FLUSH
    
    def test_straight(self):
        """Test straight detection."""
        cards = [Card.from_string(c) for c in ["9h", "8d", "7c", "6s", "5h"]]
        rank, kickers = HandEvaluator.evaluate(cards)
        assert rank == HandRank.STRAIGHT
    
    def test_wheel_straight(self):
        """Test A-2-3-4-5 straight (wheel)."""
        cards = [Card.from_string(c) for c in ["Ah", "2d", "3c", "4s", "5h"]]
        rank, kickers = HandEvaluator.evaluate(cards)
        assert rank == HandRank.STRAIGHT
        assert kickers[0] == 5  # Wheel is considered 5-high
    
    def test_three_of_a_kind(self):
        """Test three of a kind detection."""
        cards = [Card.from_string(c) for c in ["Ks", "Kh", "Kd", "Ac", "Qh"]]
        rank, kickers = HandEvaluator.evaluate(cards)
        assert rank == HandRank.THREE_OF_A_KIND
    
    def test_two_pair(self):
        """Test two pair detection."""
        cards = [Card.from_string(c) for c in ["Ks", "Kh", "Qd", "Qc", "Ah"]]
        rank, kickers = HandEvaluator.evaluate(cards)
        assert rank == HandRank.TWO_PAIR
    
    def test_one_pair(self):
        """Test one pair detection."""
        cards = [Card.from_string(c) for c in ["Ks", "Kh", "Ad", "Qc", "Jh"]]
        rank, kickers = HandEvaluator.evaluate(cards)
        assert rank == HandRank.PAIR
    
    def test_high_card(self):
        """Test high card detection."""
        cards = [Card.from_string(c) for c in ["As", "Kh", "Qd", "Jc", "9h"]]
        rank, kickers = HandEvaluator.evaluate(cards)
        assert rank == HandRank.HIGH_CARD
    
    def test_seven_card_hand(self):
        """Test evaluating 7-card hand."""
        cards = [Card.from_string(c) for c in ["As", "Ah", "Kd", "Kc", "Qh", "Jh", "2c"]]
        rank, kickers = HandEvaluator.evaluate(cards)
        assert rank == HandRank.TWO_PAIR  # AA KK
    
    def test_compare_hands(self):
        """Test comparing hands."""
        hand1 = [Card.from_string(c) for c in ["As", "Ah", "Kd", "Kc", "Qh"]]
        hand2 = [Card.from_string(c) for c in ["Ks", "Kh", "Qd", "Qc", "Ah"]]
        
        result = HandEvaluator.compare_hands(hand1, hand2)
        assert result == 1  # hand1 wins (AA KK vs KK QQ)
    
    def test_hand_rank_name(self):
        """Test getting hand rank names."""
        assert HandEvaluator.hand_rank_name(HandRank.STRAIGHT_FLUSH) == "Straight Flush"
        assert HandEvaluator.hand_rank_name(HandRank.PAIR) == "Pair"

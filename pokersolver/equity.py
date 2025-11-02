"""
Equity calculator for poker hands.
"""

from typing import List, Dict, Optional, Tuple
import random
from pokersolver.card import Card, Deck
from pokersolver.hand_evaluator import HandEvaluator


class EquityCalculator:
    """Calculates equity between poker hands."""
    
    @staticmethod
    def calculate_equity(
        hands: List[List[Card]],
        board: Optional[List[Card]] = None,
        num_simulations: int = 10000,
        dead_cards: Optional[List[Card]] = None
    ) -> Dict[int, float]:
        """
        Calculate equity for multiple hands using Monte Carlo simulation.
        
        Args:
            hands: List of hands (each hand is a list of 2 cards for Texas Hold'em)
            board: Community cards (0-5 cards)
            num_simulations: Number of Monte Carlo simulations to run
            dead_cards: Known cards that are out of play
            
        Returns:
            Dictionary mapping hand index to win/tie equity percentage
        """
        if not hands:
            raise ValueError("Need at least one hand")
        
        if board is None:
            board = []
        
        if not (0 <= len(board) <= 5):
            raise ValueError("Board must have 0-5 cards")
        
        for hand in hands:
            if len(hand) != 2:
                raise ValueError("Each hand must have exactly 2 cards")
        
        # Track all known cards
        all_known_cards = []
        for hand in hands:
            all_known_cards.extend(hand)
        all_known_cards.extend(board)
        if dead_cards:
            all_known_cards.extend(dead_cards)
        
        # Check for duplicate cards
        if len(all_known_cards) != len(set(all_known_cards)):
            raise ValueError("Duplicate cards detected")
        
        wins = [0] * len(hands)
        ties = [0] * len(hands)
        
        for _ in range(num_simulations):
            # Create deck excluding known cards
            deck = Deck(exclude_cards=all_known_cards)
            deck.shuffle()
            
            # Complete the board
            cards_needed = 5 - len(board)
            simulated_board = board + deck.deal(cards_needed)
            
            # Evaluate all hands
            evaluations = []
            for hand in hands:
                full_hand = hand + simulated_board
                eval_result = HandEvaluator.evaluate(full_hand)
                evaluations.append(eval_result)
            
            # Find winner(s)
            best_eval = max(evaluations)
            winners = [i for i, e in enumerate(evaluations) if e == best_eval]
            
            if len(winners) == 1:
                wins[winners[0]] += 1
            else:
                # Tie
                for winner in winners:
                    ties[winner] += 1
        
        # Calculate equity percentages
        equity = {}
        for i in range(len(hands)):
            # Equity = (wins + ties/num_winners) / total_simulations
            equity[i] = (wins[i] + ties[i] / len(hands)) / num_simulations * 100
        
        return equity
    
    @staticmethod
    def hand_vs_hand(
        hand1: List[Card],
        hand2: List[Card],
        board: Optional[List[Card]] = None,
        num_simulations: int = 10000
    ) -> Tuple[float, float, float]:
        """
        Calculate equity between two hands.
        
        Args:
            hand1: First hand (2 cards)
            hand2: Second hand (2 cards)
            board: Community cards (0-5 cards)
            num_simulations: Number of simulations
            
        Returns:
            Tuple of (hand1_equity, hand2_equity, tie_equity)
        """
        equity = EquityCalculator.calculate_equity(
            [hand1, hand2],
            board,
            num_simulations
        )
        
        # For heads-up, calculate tie equity separately
        all_known = hand1 + hand2 + (board if board else [])
        wins1 = 0
        wins2 = 0
        ties = 0
        
        for _ in range(num_simulations):
            deck = Deck(exclude_cards=all_known)
            deck.shuffle()
            
            cards_needed = 5 - len(board if board else [])
            simulated_board = (board if board else []) + deck.deal(cards_needed)
            
            eval1 = HandEvaluator.evaluate(hand1 + simulated_board)
            eval2 = HandEvaluator.evaluate(hand2 + simulated_board)
            
            if eval1 > eval2:
                wins1 += 1
            elif eval2 > eval1:
                wins2 += 1
            else:
                ties += 1
        
        return (
            wins1 / num_simulations * 100,
            wins2 / num_simulations * 100,
            ties / num_simulations * 100
        )

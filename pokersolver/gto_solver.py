"""
Basic GTO (Game Theory Optimal) solver for poker.
"""

from typing import Dict, List, Optional
import numpy as np
from pokersolver.card import Card
from pokersolver.equity import EquityCalculator
from pokersolver.range import Range


class GTOSolver:
    """Basic GTO solver for poker situations."""
    
    def __init__(self):
        """Initialize the GTO solver."""
        self.convergence_threshold = 0.01
        self.max_iterations = 1000
    
    def solve_push_fold(
        self,
        stack_size: float,
        pot_size: float,
        position: str = "BTN"
    ) -> Dict[str, float]:
        """
        Solve push/fold scenario (simple GTO scenario).
        
        Args:
            stack_size: Effective stack size in big blinds
            pot_size: Current pot size in big blinds
            position: Position (BTN, SB, BB, etc.)
            
        Returns:
            Dictionary with push/fold frequencies for hand ranges
        """
        # Simplified push/fold equilibrium
        # In reality, this would use Nash equilibrium calculations
        
        if stack_size <= 10:
            # Very short stack - push wider
            push_range = Range.from_percentage(30)
            return {
                "strategy": "push",
                "range": str(push_range),
                "equity_threshold": 35.0
            }
        elif stack_size <= 20:
            # Short stack - moderate range
            push_range = Range.from_percentage(20)
            return {
                "strategy": "push",
                "range": str(push_range),
                "equity_threshold": 40.0
            }
        else:
            # Deeper stack - tight range
            push_range = Range.from_percentage(10)
            return {
                "strategy": "push",
                "range": str(push_range),
                "equity_threshold": 45.0
            }
    
    def calculate_pot_odds(self, bet_size: float, pot_size: float) -> float:
        """
        Calculate pot odds for a bet.
        
        Args:
            bet_size: Size of the bet
            pot_size: Current pot size
            
        Returns:
            Pot odds as a percentage
        """
        return bet_size / (pot_size + bet_size) * 100
    
    def calculate_min_defense_frequency(self, bet_size: float, pot_size: float) -> float:
        """
        Calculate minimum defense frequency against a bet (MDF).
        
        Args:
            bet_size: Size of the bet
            pot_size: Current pot size
            
        Returns:
            Minimum defense frequency as a percentage
        """
        return pot_size / (pot_size + bet_size) * 100
    
    def optimal_bet_size(
        self,
        pot_size: float,
        value_ratio: float = 0.67,
        bluff_ratio: Optional[float] = None
    ) -> float:
        """
        Calculate optimal bet size for value/bluff ratio.
        
        Args:
            pot_size: Current pot size
            value_ratio: Ratio of value hands to bluffs (default 2:1)
            bluff_ratio: Optional specific bluff ratio
            
        Returns:
            Optimal bet size
        """
        # Simplified geometric sizing
        # In practice, would use node locking and CFR
        if bluff_ratio is None:
            bluff_ratio = 1 - value_ratio
        
        # Common GTO bet sizes are 1/3, 1/2, 2/3, or pot
        # Return 2/3 pot as a balanced default
        return pot_size * 0.67
    
    def ev_calculation(
        self,
        action: str,
        equity: float,
        pot_size: float,
        bet_size: float = 0,
        fold_equity: float = 0
    ) -> float:
        """
        Calculate expected value of an action.
        
        Args:
            action: "fold", "call", "raise"
            equity: Hand equity percentage
            pot_size: Current pot size
            bet_size: Size of bet to call or raise
            fold_equity: Percentage of time opponent folds
            
        Returns:
            Expected value
        """
        if action == "fold":
            return 0.0
        elif action == "call":
            return (equity / 100) * (pot_size + bet_size) - bet_size
        elif action == "raise":
            fold_ev = fold_equity / 100 * pot_size
            call_ev = (1 - fold_equity / 100) * (
                (equity / 100) * (pot_size + 2 * bet_size) - bet_size
            )
            return fold_ev + call_ev
        else:
            raise ValueError(f"Unknown action: {action}")
    
    def exploit_strategy(
        self,
        opponent_fold_frequency: float,
        equity: float,
        pot_size: float,
        bet_size: float
    ) -> str:
        """
        Determine exploitative strategy based on opponent tendencies.
        
        Args:
            opponent_fold_frequency: How often opponent folds (0-100)
            equity: Our equity percentage
            pot_size: Current pot
            bet_size: Our bet size
            
        Returns:
            Recommended action
        """
        # Calculate EV of bluffing
        bluff_ev = self.ev_calculation(
            "raise",
            equity,
            pot_size,
            bet_size,
            opponent_fold_frequency
        )
        
        # Calculate EV of giving up
        check_ev = 0
        
        if bluff_ev > check_ev:
            return "bet/raise"
        else:
            return "check/fold"

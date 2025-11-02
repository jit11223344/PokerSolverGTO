"""
Hand range representation and manipulation.
"""

from typing import List, Set
from pokersolver.card import Card


class Range:
    """Represents a range of poker hands."""
    
    def __init__(self, range_string: str = ""):
        """
        Initialize a range.
        
        Args:
            range_string: String representation of range (e.g., "AA,KK,AKs,AKo")
        """
        self.combos: Set[str] = set()
        if range_string:
            self.parse_range(range_string)
    
    def parse_range(self, range_string: str):
        """
        Parse a range string.
        
        Args:
            range_string: Comma-separated hand notations
                         e.g., "AA,KK,QQ" or "AKs,AKo,JTs+"
        """
        parts = [p.strip() for p in range_string.split(',')]
        
        for part in parts:
            if not part:
                continue
            
            if part.endswith('+'):
                # Range notation (e.g., "88+" means 88, 99, TT, JJ, QQ, KK, AA)
                self._add_plus_range(part[:-1])
            else:
                self._add_hand(part)
    
    def _add_hand(self, hand: str):
        """Add a specific hand to the range."""
        if len(hand) == 2:
            # Pocket pair (e.g., "AA", "KK")
            if hand[0] == hand[1]:
                self.combos.add(hand)
        elif len(hand) == 3:
            # Suited or offsuit (e.g., "AKs", "AKo")
            rank1, rank2, suit_type = hand[0], hand[1], hand[2]
            if suit_type.lower() == 's':
                self.combos.add(f"{rank1}{rank2}s")
            elif suit_type.lower() == 'o':
                self.combos.add(f"{rank1}{rank2}o")
    
    def _add_plus_range(self, base_hand: str):
        """Add a plus range (e.g., "88+" adds 88, 99, TT, JJ, QQ, KK, AA)."""
        ranks = "23456789TJQKA"
        
        if len(base_hand) == 2 and base_hand[0] == base_hand[1]:
            # Pocket pair range
            start_rank = base_hand[0]
            start_idx = ranks.index(start_rank)
            for i in range(start_idx, len(ranks)):
                rank = ranks[i]
                self.combos.add(f"{rank}{rank}")
        elif len(base_hand) == 3:
            # Suited/offsuit range
            rank1, rank2, suit_type = base_hand[0], base_hand[1], base_hand[2]
            # This would add all suited/offsuit combos with the same pattern
            self.combos.add(base_hand)
    
    def add(self, hand: str):
        """Add a hand to the range."""
        self._add_hand(hand)
    
    def remove(self, hand: str):
        """Remove a hand from the range."""
        self.combos.discard(hand)
    
    def contains(self, hand: str) -> bool:
        """Check if a hand is in the range."""
        return hand in self.combos
    
    def get_all_combos(self) -> List[str]:
        """Get all hand combinations in the range."""
        return sorted(list(self.combos))
    
    def size(self) -> int:
        """Get the number of hand combinations in the range."""
        # Each suited hand has 4 combos, each offsuit has 12 combos, each pair has 6 combos
        total = 0
        for combo in self.combos:
            if len(combo) == 2:
                # Pocket pair: 6 combinations
                total += 6
            elif combo.endswith('s'):
                # Suited: 4 combinations
                total += 4
            elif combo.endswith('o'):
                # Offsuit: 12 combinations
                total += 12
        return total
    
    def __str__(self) -> str:
        """String representation of the range."""
        return ','.join(sorted(self.combos))
    
    def __repr__(self) -> str:
        """Representation of the range."""
        return f"Range('{self}')"
    
    @staticmethod
    def from_percentage(percentage: float) -> 'Range':
        """
        Create a range representing the top percentage of hands.
        
        Args:
            percentage: Percentage of hands to include (0-100)
            
        Returns:
            Range object
        """
        # Simplified implementation - would need full hand ranking
        # This is a placeholder for GTO ranges
        if percentage >= 100:
            return Range("AA,KK,QQ,JJ,TT,99,88,77,66,55,44,33,22,AKs,AKo")
        elif percentage >= 20:
            return Range("AA,KK,QQ,JJ,TT,99,AKs,AKo,AQs,AQo")
        elif percentage >= 10:
            return Range("AA,KK,QQ,JJ,AKs,AKo")
        else:
            return Range("AA,KK,QQ")

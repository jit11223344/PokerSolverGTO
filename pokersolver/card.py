"""
Card and Deck classes for poker.
"""

from enum import IntEnum
from typing import List, Optional
import random


class Suit(IntEnum):
    """Card suits."""
    CLUBS = 0
    DIAMONDS = 1
    HEARTS = 2
    SPADES = 3


class Rank(IntEnum):
    """Card ranks."""
    TWO = 2
    THREE = 3
    FOUR = 4
    FIVE = 5
    SIX = 6
    SEVEN = 7
    EIGHT = 8
    NINE = 9
    TEN = 10
    JACK = 11
    QUEEN = 12
    KING = 13
    ACE = 14


class Card:
    """Represents a playing card."""
    
    RANK_CHARS = "23456789TJQKA"
    SUIT_CHARS = "cdhs"
    
    def __init__(self, rank: int, suit: int):
        """
        Initialize a card.
        
        Args:
            rank: Card rank (2-14, where 14 is Ace)
            suit: Card suit (0-3)
        """
        if not (2 <= rank <= 14):
            raise ValueError(f"Invalid rank: {rank}")
        if not (0 <= suit <= 3):
            raise ValueError(f"Invalid suit: {suit}")
        self.rank = rank
        self.suit = suit
    
    @classmethod
    def from_string(cls, card_str: str) -> 'Card':
        """
        Create a card from string representation (e.g., 'Ah', 'Tc', '2d').
        
        Args:
            card_str: String representation of card
            
        Returns:
            Card object
        """
        if len(card_str) != 2:
            raise ValueError(f"Invalid card string: {card_str}")
        
        rank_char = card_str[0].upper()
        suit_char = card_str[1].lower()
        
        if rank_char not in cls.RANK_CHARS:
            raise ValueError(f"Invalid rank: {rank_char}")
        if suit_char not in cls.SUIT_CHARS:
            raise ValueError(f"Invalid suit: {suit_char}")
        
        rank = cls.RANK_CHARS.index(rank_char) + 2
        suit = cls.SUIT_CHARS.index(suit_char)
        
        return cls(rank, suit)
    
    def __str__(self) -> str:
        """String representation of the card."""
        return f"{self.RANK_CHARS[self.rank - 2]}{self.SUIT_CHARS[self.suit]}"
    
    def __repr__(self) -> str:
        """Representation of the card."""
        return f"Card({self})"
    
    def __eq__(self, other) -> bool:
        """Check if two cards are equal."""
        if not isinstance(other, Card):
            return False
        return self.rank == other.rank and self.suit == other.suit
    
    def __hash__(self) -> int:
        """Hash for card."""
        return hash((self.rank, self.suit))
    
    def __lt__(self, other) -> bool:
        """Compare cards by rank, then suit."""
        if not isinstance(other, Card):
            return NotImplemented
        if self.rank != other.rank:
            return self.rank < other.rank
        return self.suit < other.suit


class Deck:
    """Represents a deck of cards."""
    
    def __init__(self, exclude_cards: Optional[List[Card]] = None):
        """
        Initialize a deck.
        
        Args:
            exclude_cards: Cards to exclude from the deck
        """
        self.cards = []
        exclude_set = set(exclude_cards) if exclude_cards else set()
        
        for rank in range(2, 15):
            for suit in range(4):
                card = Card(rank, suit)
                if card not in exclude_set:
                    self.cards.append(card)
    
    def shuffle(self):
        """Shuffle the deck."""
        random.shuffle(self.cards)
    
    def deal(self, n: int = 1) -> List[Card]:
        """
        Deal n cards from the deck.
        
        Args:
            n: Number of cards to deal
            
        Returns:
            List of dealt cards
        """
        if n > len(self.cards):
            raise ValueError(f"Cannot deal {n} cards, only {len(self.cards)} remaining")
        
        dealt = self.cards[:n]
        self.cards = self.cards[n:]
        return dealt
    
    def __len__(self) -> int:
        """Return number of cards in deck."""
        return len(self.cards)

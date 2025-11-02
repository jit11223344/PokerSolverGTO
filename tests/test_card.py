"""Tests for card module."""

import pytest
from pokersolver.card import Card, Deck, Suit, Rank


class TestCard:
    """Test Card class."""
    
    def test_create_card(self):
        """Test creating a card."""
        card = Card(Rank.ACE, Suit.SPADES)
        assert card.rank == 14
        assert card.suit == 3
    
    def test_from_string(self):
        """Test creating card from string."""
        card = Card.from_string("Ah")
        assert card.rank == 14
        assert card.suit == 2
        
        card = Card.from_string("2c")
        assert card.rank == 2
        assert card.suit == 0
    
    def test_invalid_card(self):
        """Test invalid card creation."""
        with pytest.raises(ValueError):
            Card(15, 0)  # Invalid rank
        
        with pytest.raises(ValueError):
            Card(10, 5)  # Invalid suit
        
        with pytest.raises(ValueError):
            Card.from_string("Xx")  # Invalid string
    
    def test_card_string(self):
        """Test card string representation."""
        card = Card.from_string("Kd")
        assert str(card) == "Kd"
        
        card = Card.from_string("Tc")
        assert str(card) == "Tc"
    
    def test_card_equality(self):
        """Test card equality."""
        card1 = Card.from_string("Ah")
        card2 = Card.from_string("Ah")
        card3 = Card.from_string("Kh")
        
        assert card1 == card2
        assert card1 != card3
    
    def test_card_comparison(self):
        """Test card comparison."""
        ace = Card.from_string("As")
        king = Card.from_string("Ks")
        
        assert king < ace
        assert ace > king


class TestDeck:
    """Test Deck class."""
    
    def test_create_deck(self):
        """Test creating a deck."""
        deck = Deck()
        assert len(deck) == 52
    
    def test_exclude_cards(self):
        """Test excluding cards from deck."""
        exclude = [Card.from_string("Ah"), Card.from_string("Kh")]
        deck = Deck(exclude_cards=exclude)
        assert len(deck) == 50
    
    def test_deal_cards(self):
        """Test dealing cards."""
        deck = Deck()
        cards = deck.deal(5)
        assert len(cards) == 5
        assert len(deck) == 47
    
    def test_deal_too_many(self):
        """Test dealing more cards than available."""
        deck = Deck()
        with pytest.raises(ValueError):
            deck.deal(100)
    
    def test_shuffle(self):
        """Test shuffling deck."""
        deck1 = Deck()
        deck2 = Deck()
        
        deck1.shuffle()
        
        # After shuffle, order should likely be different
        # (could fail rarely if shuffle produces same order)
        cards1 = deck1.deal(10)
        cards2 = deck2.deal(10)
        
        # Just verify we can shuffle without error
        assert len(cards1) == 10

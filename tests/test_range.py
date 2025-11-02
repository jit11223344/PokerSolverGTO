"""Tests for range module."""

import pytest
from pokersolver.range import Range


class TestRange:
    """Test Range class."""
    
    def test_create_empty_range(self):
        """Test creating empty range."""
        range_obj = Range()
        assert range_obj.size() == 0
    
    def test_parse_pocket_pairs(self):
        """Test parsing pocket pairs."""
        range_obj = Range("AA,KK,QQ")
        assert range_obj.contains("AA")
        assert range_obj.contains("KK")
        assert range_obj.contains("QQ")
        assert not range_obj.contains("JJ")
    
    def test_parse_suited_hands(self):
        """Test parsing suited hands."""
        range_obj = Range("AKs,AQs")
        assert range_obj.contains("AKs")
        assert range_obj.contains("AQs")
        assert not range_obj.contains("AKo")
    
    def test_parse_offsuit_hands(self):
        """Test parsing offsuit hands."""
        range_obj = Range("AKo,AQo")
        assert range_obj.contains("AKo")
        assert range_obj.contains("AQo")
        assert not range_obj.contains("AKs")
    
    def test_add_hand(self):
        """Test adding hands to range."""
        range_obj = Range()
        range_obj.add("AA")
        range_obj.add("KK")
        assert range_obj.contains("AA")
        assert range_obj.contains("KK")
    
    def test_remove_hand(self):
        """Test removing hands from range."""
        range_obj = Range("AA,KK,QQ")
        range_obj.remove("KK")
        assert range_obj.contains("AA")
        assert not range_obj.contains("KK")
        assert range_obj.contains("QQ")
    
    def test_range_size(self):
        """Test calculating range size."""
        range_obj = Range("AA")
        assert range_obj.size() == 6  # 6 combos of AA
        
        range_obj = Range("AKs")
        assert range_obj.size() == 4  # 4 suited combos
        
        range_obj = Range("AKo")
        assert range_obj.size() == 12  # 12 offsuit combos
    
    def test_get_all_combos(self):
        """Test getting all combinations."""
        range_obj = Range("AA,KK")
        combos = range_obj.get_all_combos()
        assert "AA" in combos
        assert "KK" in combos
        assert len(combos) == 2
    
    def test_string_representation(self):
        """Test string representation."""
        range_obj = Range("AA,KK,QQ")
        range_str = str(range_obj)
        assert "AA" in range_str
        assert "KK" in range_str
        assert "QQ" in range_str
    
    def test_from_percentage(self):
        """Test creating range from percentage."""
        range_obj = Range.from_percentage(10)
        assert range_obj.size() > 0
        
        # Higher percentage should have more combos
        range_obj2 = Range.from_percentage(20)
        assert range_obj2.size() >= range_obj.size()

"""
Example usage of PokerSolver GTO library.
"""

from pokersolver import Card, HandEvaluator, EquityCalculator, Range
from pokersolver.gto_solver import GTOSolver


def example_hand_evaluation():
    """Example: Evaluate poker hands."""
    print("=" * 60)
    print("Example 1: Hand Evaluation")
    print("=" * 60)
    
    # Royal flush
    cards = [Card.from_string(c) for c in ["Ah", "Kh", "Qh", "Jh", "Th"]]
    rank, kickers = HandEvaluator.evaluate(cards)
    print(f"Hand: {' '.join(str(c) for c in cards)}")
    print(f"Rank: {HandEvaluator.hand_rank_name(rank)}")
    print()
    
    # Full house
    cards = [Card.from_string(c) for c in ["Ks", "Kh", "Kd", "Qc", "Qh"]]
    rank, kickers = HandEvaluator.evaluate(cards)
    print(f"Hand: {' '.join(str(c) for c in cards)}")
    print(f"Rank: {HandEvaluator.hand_rank_name(rank)}")
    print()


def example_equity_calculation():
    """Example: Calculate equity between hands."""
    print("=" * 60)
    print("Example 2: Equity Calculation")
    print("=" * 60)
    
    # Pocket aces vs pocket kings
    hand1 = [Card.from_string("As"), Card.from_string("Ah")]
    hand2 = [Card.from_string("Ks"), Card.from_string("Kh")]
    
    print(f"Hand 1: {' '.join(str(c) for c in hand1)}")
    print(f"Hand 2: {' '.join(str(c) for c in hand2)}")
    print("Calculating equity (10000 simulations)...")
    
    eq1, eq2, tie = EquityCalculator.hand_vs_hand(hand1, hand2, num_simulations=10000)
    
    print(f"Hand 1 Equity: {eq1:.2f}%")
    print(f"Hand 2 Equity: {eq2:.2f}%")
    print(f"Tie Equity: {tie:.2f}%")
    print()


def example_equity_with_board():
    """Example: Calculate equity with a board."""
    print("=" * 60)
    print("Example 3: Equity with Board")
    print("=" * 60)
    
    hand1 = [Card.from_string("As"), Card.from_string("Kd")]
    hand2 = [Card.from_string("Qh"), Card.from_string("Qc")]
    board = [Card.from_string("Ah"), Card.from_string("7c"), Card.from_string("2d")]
    
    print(f"Hand 1: {' '.join(str(c) for c in hand1)}")
    print(f"Hand 2: {' '.join(str(c) for c in hand2)}")
    print(f"Board: {' '.join(str(c) for c in board)}")
    print("Calculating equity (10000 simulations)...")
    
    eq1, eq2, tie = EquityCalculator.hand_vs_hand(hand1, hand2, board, num_simulations=10000)
    
    print(f"Hand 1 Equity: {eq1:.2f}%")
    print(f"Hand 2 Equity: {eq2:.2f}%")
    print(f"Tie Equity: {tie:.2f}%")
    print()


def example_range_operations():
    """Example: Work with hand ranges."""
    print("=" * 60)
    print("Example 4: Range Operations")
    print("=" * 60)
    
    # Create a range
    range_obj = Range("AA,KK,QQ,JJ,AKs,AKo")
    
    print(f"Range: {range_obj}")
    print(f"All combos: {range_obj.get_all_combos()}")
    print(f"Total combinations: {range_obj.size()}")
    print()
    
    # Add and remove hands
    range_obj.add("TT")
    print(f"After adding TT: {range_obj}")
    
    range_obj.remove("JJ")
    print(f"After removing JJ: {range_obj}")
    print()


def example_gto_calculations():
    """Example: GTO solver calculations."""
    print("=" * 60)
    print("Example 5: GTO Calculations")
    print("=" * 60)
    
    solver = GTOSolver()
    
    # Push/fold scenario
    print("Push/Fold Analysis (10 BB stack):")
    result = solver.solve_push_fold(stack_size=10, pot_size=1.5)
    print(f"  Strategy: {result['strategy']}")
    print(f"  Range: {result['range']}")
    print(f"  Equity Threshold: {result['equity_threshold']:.1f}%")
    print()
    
    # Pot odds
    print("Pot Odds Analysis:")
    pot_odds = solver.calculate_pot_odds(bet_size=10, pot_size=20)
    mdf = solver.calculate_min_defense_frequency(bet_size=10, pot_size=20)
    print(f"  Bet: 10, Pot: 20")
    print(f"  Pot Odds: {pot_odds:.2f}%")
    print(f"  Minimum Defense Frequency: {mdf:.2f}%")
    print()
    
    # Optimal bet sizing
    print("Optimal Bet Sizing:")
    optimal_bet = solver.optimal_bet_size(pot_size=100)
    print(f"  Pot: 100")
    print(f"  Recommended Bet: {optimal_bet:.2f}")
    print()


def main():
    """Run all examples."""
    print("\n")
    print("*" * 60)
    print("PokerSolver GTO - Example Usage")
    print("*" * 60)
    print("\n")
    
    example_hand_evaluation()
    example_equity_calculation()
    example_equity_with_board()
    example_range_operations()
    example_gto_calculations()
    
    print("=" * 60)
    print("Examples Complete!")
    print("=" * 60)


if __name__ == "__main__":
    main()

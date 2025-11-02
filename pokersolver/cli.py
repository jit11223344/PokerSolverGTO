"""
Command-line interface for PokerSolver GTO.
"""

import argparse
import sys
from typing import List
from pokersolver.card import Card
from pokersolver.hand_evaluator import HandEvaluator, HandRank
from pokersolver.equity import EquityCalculator
from pokersolver.range import Range
from pokersolver.gto_solver import GTOSolver


def evaluate_hand_command(args):
    """Evaluate a poker hand."""
    try:
        cards = [Card.from_string(c) for c in args.cards]
        rank, kickers = HandEvaluator.evaluate(cards)
        rank_name = HandEvaluator.hand_rank_name(rank)
        
        print(f"\nHand: {' '.join(str(c) for c in cards)}")
        print(f"Rank: {rank_name}")
        print(f"Kickers: {kickers}")
    except Exception as e:
        print(f"Error: {e}", file=sys.stderr)
        sys.exit(1)


def calculate_equity_command(args):
    """Calculate equity between hands."""
    try:
        hand1 = [Card.from_string(c) for c in args.hand1]
        hand2 = [Card.from_string(c) for c in args.hand2]
        
        board = None
        if args.board:
            board = [Card.from_string(c) for c in args.board]
        
        simulations = args.simulations or 10000
        
        print(f"\nCalculating equity ({simulations} simulations)...")
        print(f"Hand 1: {' '.join(str(c) for c in hand1)}")
        print(f"Hand 2: {' '.join(str(c) for c in hand2)}")
        if board:
            print(f"Board: {' '.join(str(c) for c in board)}")
        
        eq1, eq2, tie_eq = EquityCalculator.hand_vs_hand(
            hand1, hand2, board, simulations
        )
        
        print(f"\nResults:")
        print(f"Hand 1 Equity: {eq1:.2f}%")
        print(f"Hand 2 Equity: {eq2:.2f}%")
        print(f"Tie Equity: {tie_eq:.2f}%")
    except Exception as e:
        print(f"Error: {e}", file=sys.stderr)
        sys.exit(1)


def range_command(args):
    """Work with hand ranges."""
    try:
        range_obj = Range(args.range_string)
        
        print(f"\nRange: {range_obj}")
        print(f"Combinations: {range_obj.get_all_combos()}")
        print(f"Total combos: {range_obj.size()}")
    except Exception as e:
        print(f"Error: {e}", file=sys.stderr)
        sys.exit(1)


def gto_solve_command(args):
    """Solve GTO scenarios."""
    try:
        solver = GTOSolver()
        
        if args.scenario == "push-fold":
            stack = args.stack or 10.0
            pot = args.pot or 1.5
            
            result = solver.solve_push_fold(stack, pot)
            
            print(f"\nPush/Fold Solution:")
            print(f"Stack Size: {stack} BB")
            print(f"Pot Size: {pot} BB")
            print(f"Strategy: {result['strategy']}")
            print(f"Recommended Range: {result['range']}")
            print(f"Equity Threshold: {result['equity_threshold']:.1f}%")
        
        elif args.scenario == "pot-odds":
            bet = args.bet or 10.0
            pot = args.pot or 20.0
            
            pot_odds = solver.calculate_pot_odds(bet, pot)
            mdf = solver.calculate_min_defense_frequency(bet, pot)
            
            print(f"\nPot Odds Analysis:")
            print(f"Bet Size: {bet}")
            print(f"Pot Size: {pot}")
            print(f"Pot Odds: {pot_odds:.2f}%")
            print(f"Minimum Defense Frequency: {mdf:.2f}%")
        
        elif args.scenario == "optimal-bet":
            pot = args.pot or 100.0
            
            bet_size = solver.optimal_bet_size(pot)
            
            print(f"\nOptimal Bet Sizing:")
            print(f"Pot Size: {pot}")
            print(f"Recommended Bet: {bet_size:.2f}")
            print(f"Bet/Pot Ratio: {bet_size/pot:.2f}x")
        
    except Exception as e:
        print(f"Error: {e}", file=sys.stderr)
        sys.exit(1)


def main():
    """Main entry point for CLI."""
    parser = argparse.ArgumentParser(
        description="PokerSolver GTO - Game Theory Optimal Poker Solver",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog="""
Examples:
  # Evaluate a hand
  pokersolver evaluate Ah Kh Qh Jh Th
  
  # Calculate equity
  pokersolver equity --hand1 As Kd --hand2 Qh Qc --board Ah 7c 2d
  
  # Work with ranges
  pokersolver range "AA,KK,QQ,AKs,AKo"
  
  # Solve GTO scenarios
  pokersolver gto push-fold --stack 10 --pot 1.5
  pokersolver gto pot-odds --bet 10 --pot 20
  pokersolver gto optimal-bet --pot 100
        """
    )
    
    subparsers = parser.add_subparsers(dest='command', help='Commands')
    
    # Evaluate hand command
    eval_parser = subparsers.add_parser('evaluate', help='Evaluate a poker hand')
    eval_parser.add_argument('cards', nargs='+', help='Cards (e.g., Ah Kd Qc)')
    eval_parser.set_defaults(func=evaluate_hand_command)
    
    # Equity calculator command
    equity_parser = subparsers.add_parser('equity', help='Calculate hand equity')
    equity_parser.add_argument('--hand1', nargs=2, required=True, help='First hand (2 cards)')
    equity_parser.add_argument('--hand2', nargs=2, required=True, help='Second hand (2 cards)')
    equity_parser.add_argument('--board', nargs='+', help='Board cards (0-5 cards)')
    equity_parser.add_argument('--simulations', type=int, help='Number of simulations')
    equity_parser.set_defaults(func=calculate_equity_command)
    
    # Range command
    range_parser = subparsers.add_parser('range', help='Work with hand ranges')
    range_parser.add_argument('range_string', help='Range string (e.g., "AA,KK,AKs")')
    range_parser.set_defaults(func=range_command)
    
    # GTO solver command
    gto_parser = subparsers.add_parser('gto', help='Solve GTO scenarios')
    gto_parser.add_argument('scenario', choices=['push-fold', 'pot-odds', 'optimal-bet'])
    gto_parser.add_argument('--stack', type=float, help='Stack size in BB')
    gto_parser.add_argument('--pot', type=float, help='Pot size')
    gto_parser.add_argument('--bet', type=float, help='Bet size')
    gto_parser.set_defaults(func=gto_solve_command)
    
    args = parser.parse_args()
    
    if not args.command:
        parser.print_help()
        sys.exit(1)
    
    args.func(args)


if __name__ == '__main__':
    main()

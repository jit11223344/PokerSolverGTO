# PokerSolverGTO

A comprehensive Game Theory Optimal (GTO) poker solver application written in Python. This tool helps poker players analyze hands, calculate equity, work with ranges, and solve GTO scenarios.

## Features

- **Hand Evaluation**: Evaluate poker hands from High Card to Straight Flush
- **Equity Calculator**: Monte Carlo simulation-based equity calculator for heads-up and multi-way pots
- **Range Management**: Parse and manipulate poker hand ranges
- **GTO Solver**: Solve basic GTO scenarios including push/fold, pot odds, and optimal bet sizing
- **Command-Line Interface**: Easy-to-use CLI for all features

## Installation

### From Source

```bash
git clone https://github.com/jit11223344/PokerSolverGTO.git
cd PokerSolverGTO
pip install -r requirements.txt
pip install -e .
```

### Requirements

- Python 3.7 or higher
- NumPy 1.19.0 or higher

## Usage

### Command-Line Interface

The application provides a command-line interface with several commands:

#### Evaluate a Poker Hand

```bash
pokersolver evaluate Ah Kh Qh Jh Th
```

Output:
```
Hand: Ah Kh Qh Jh Th
Rank: Straight Flush
Kickers: [14]
```

#### Calculate Equity

Calculate equity between two hands:

```bash
pokersolver equity --hand1 As Kd --hand2 Qh Qc
```

With a board:

```bash
pokersolver equity --hand1 As Kd --hand2 Qh Qc --board Ah 7c 2d
```

With custom simulation count:

```bash
pokersolver equity --hand1 As Kd --hand2 Qh Qc --simulations 50000
```

#### Work with Ranges

```bash
pokersolver range "AA,KK,QQ,AKs,AKo"
```

Output:
```
Range: AA,AKo,AKs,KK,QQ
Combinations: ['AA', 'AKo', 'AKs', 'KK', 'QQ']
Total combos: 34
```

#### Solve GTO Scenarios

**Push/Fold Analysis:**

```bash
pokersolver gto push-fold --stack 10 --pot 1.5
```

**Pot Odds Calculation:**

```bash
pokersolver gto pot-odds --bet 10 --pot 20
```

**Optimal Bet Sizing:**

```bash
pokersolver gto optimal-bet --pot 100
```

### Python API

You can also use PokerSolver as a Python library:

```python
from pokersolver import Card, HandEvaluator, EquityCalculator, Range, GTOSolver

# Evaluate a hand
cards = [Card.from_string(c) for c in ['Ah', 'Kh', 'Qh', 'Jh', 'Th']]
rank, kickers = HandEvaluator.evaluate(cards)
print(HandEvaluator.hand_rank_name(rank))  # "Straight Flush"

# Calculate equity
hand1 = [Card.from_string('As'), Card.from_string('Kd')]
hand2 = [Card.from_string('Qh'), Card.from_string('Qc')]
eq1, eq2, tie = EquityCalculator.hand_vs_hand(hand1, hand2)
print(f"Hand 1: {eq1:.2f}%, Hand 2: {eq2:.2f}%, Tie: {tie:.2f}%")

# Work with ranges
range_obj = Range("AA,KK,QQ,JJ,TT")
print(f"Range size: {range_obj.size()} combos")

# GTO calculations
solver = GTOSolver()
pot_odds = solver.calculate_pot_odds(bet_size=10, pot_size=20)
print(f"Pot odds: {pot_odds:.2f}%")
```

## Card Notation

Cards are represented using a two-character notation:
- **Ranks**: `2`, `3`, `4`, `5`, `6`, `7`, `8`, `9`, `T` (Ten), `J` (Jack), `Q` (Queen), `K` (King), `A` (Ace)
- **Suits**: `c` (clubs), `d` (diamonds), `h` (hearts), `s` (spades)

Examples: `Ah` (Ace of hearts), `Kd` (King of diamonds), `Tc` (Ten of clubs)

## Range Notation

Ranges use standard poker notation:
- **Pocket pairs**: `AA`, `KK`, `QQ`, etc.
- **Suited hands**: `AKs`, `JTs`, etc.
- **Offsuit hands**: `AKo`, `KQo`, etc.
- **Plus notation**: `88+` (all pairs 88 and better)

## Project Structure

```
PokerSolverGTO/
├── pokersolver/
│   ├── __init__.py          # Package initialization
│   ├── card.py              # Card and Deck classes
│   ├── hand_evaluator.py    # Hand evaluation logic
│   ├── equity.py            # Equity calculator
│   ├── range.py             # Range representation
│   ├── gto_solver.py        # GTO solver algorithms
│   └── cli.py               # Command-line interface
├── tests/                   # Unit tests
├── setup.py                 # Package setup
├── requirements.txt         # Dependencies
└── README.md               # This file
```

## Development

### Running Tests

```bash
python -m pytest tests/
```

### Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

MIT License - feel free to use this project for any purpose.

## Roadmap

Future enhancements planned:
- [ ] Advanced CFR (Counterfactual Regret Minimization) solver
- [ ] Full game tree analysis
- [ ] Support for different poker variants (PLO, Short Deck)
- [ ] GUI interface
- [ ] Database of solved scenarios
- [ ] Range vs. range equity calculations
- [ ] ICM (Independent Chip Model) calculations

## Contact

For questions or suggestions, please open an issue on GitHub.
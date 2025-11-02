# PokerSolver GTO - Quick Start Guide

## Installation

```bash
# Clone the repository
git clone https://github.com/jit11223344/PokerSolverGTO.git
cd PokerSolverGTO

# Install dependencies
pip install -r requirements.txt

# Install the package
pip install -e .
```

## Quick Start

### 1. Evaluate Poker Hands

```bash
# Royal Flush
pokersolver evaluate Ah Kh Qh Jh Th

# Four of a Kind
pokersolver evaluate As Ad Ah Ac Kh

# Full House
pokersolver evaluate Ks Kh Kd Qc Qh
```

### 2. Calculate Equity

```bash
# Aces vs Kings
pokersolver equity --hand1 As Ah --hand2 Ks Kh

# AK vs QQ with a board
pokersolver equity --hand1 As Kd --hand2 Qh Qc --board Ah 7c 2d

# More accurate with more simulations
pokersolver equity --hand1 As Kd --hand2 Qh Qc --simulations 50000
```

### 3. Work with Ranges

```bash
# Create a tight range
pokersolver range "AA,KK,QQ,JJ,TT"

# Include suited and offsuit hands
pokersolver range "AA,KK,QQ,AKs,AKo,AQs"
```

### 4. GTO Analysis

```bash
# Push/Fold in short stack situations
pokersolver gto push-fold --stack 10 --pot 1.5
pokersolver gto push-fold --stack 20 --pot 2.5

# Calculate pot odds
pokersolver gto pot-odds --bet 10 --pot 20
pokersolver gto pot-odds --bet 50 --pot 100

# Optimal bet sizing
pokersolver gto optimal-bet --pot 100
pokersolver gto optimal-bet --pot 250
```

## Python API Examples

### Hand Evaluation

```python
from pokersolver import Card, HandEvaluator

# Create cards
cards = [Card.from_string(c) for c in ["Ah", "Kh", "Qh", "Jh", "Th"]]

# Evaluate the hand
rank, kickers = HandEvaluator.evaluate(cards)
rank_name = HandEvaluator.hand_rank_name(rank)

print(f"Hand Rank: {rank_name}")  # Output: Straight Flush
```

### Equity Calculation

```python
from pokersolver import Card, EquityCalculator

# Create hands
hand1 = [Card.from_string("As"), Card.from_string("Ah")]
hand2 = [Card.from_string("Ks"), Card.from_string("Kh")]

# Calculate equity
eq1, eq2, tie = EquityCalculator.hand_vs_hand(hand1, hand2, num_simulations=10000)

print(f"AA: {eq1:.2f}%")  # Output: ~83%
print(f"KK: {eq2:.2f}%")  # Output: ~17%
```

### Range Management

```python
from pokersolver import Range

# Create a range
range_obj = Range("AA,KK,QQ,JJ,AKs,AKo")

# Get combinations
print(f"Total combos: {range_obj.size()}")  # Output: 34

# Check if hand is in range
print(range_obj.contains("AA"))  # Output: True
print(range_obj.contains("72o"))  # Output: False

# Add or remove hands
range_obj.add("TT")
range_obj.remove("JJ")
```

### GTO Calculations

```python
from pokersolver.gto_solver import GTOSolver

solver = GTOSolver()

# Push/fold analysis
result = solver.solve_push_fold(stack_size=10, pot_size=1.5)
print(f"Strategy: {result['strategy']}")
print(f"Range: {result['range']}")

# Pot odds
pot_odds = solver.calculate_pot_odds(bet_size=10, pot_size=20)
print(f"Pot Odds: {pot_odds:.2f}%")  # Output: 33.33%

# Minimum Defense Frequency
mdf = solver.calculate_min_defense_frequency(bet_size=10, pot_size=20)
print(f"MDF: {mdf:.2f}%")  # Output: 66.67%

# Optimal bet sizing
bet = solver.optimal_bet_size(pot_size=100)
print(f"Recommended Bet: {bet:.2f}")  # Output: 67.00
```

## Common Use Cases

### Pre-flop Analysis

```bash
# Compare premium hands
pokersolver equity --hand1 As Ad --hand2 Ks Kd
pokersolver equity --hand1 As Kh --hand2 Qd Qc
```

### Post-flop Scenarios

```bash
# Flop equity
pokersolver equity --hand1 As Ah --hand2 Kh Qh --board Jh Th 2c

# Turn equity
pokersolver equity --hand1 As Ah --hand2 Kh Qh --board Jh Th 2c 9h

# River analysis (complete board)
pokersolver equity --hand1 As Ah --hand2 Kh Qh --board Jh Th 2c 9h 3d
```

### Tournament Situations

```bash
# Short stack push/fold (10 BB)
pokersolver gto push-fold --stack 10 --pot 1.5

# Medium stack (20 BB)
pokersolver gto push-fold --stack 20 --pot 2.5

# Deep stack (50 BB+)
pokersolver gto push-fold --stack 50 --pot 3.0
```

## Tips and Best Practices

1. **Simulations**: Use higher simulation counts (50000+) for more accurate equity calculations
2. **Ranges**: Start with tight ranges and expand based on position and stack depth
3. **GTO vs Exploitative**: Use GTO as a baseline, but adjust based on opponent tendencies
4. **Card Notation**: Remember to use proper notation (Ah, Kd, Qc, etc.)
5. **Testing**: Run the test suite to verify everything is working: `pytest tests/`

## Running Tests

```bash
# Run all tests
python -m pytest tests/ -v

# Run specific test file
python -m pytest tests/test_card.py -v

# Run with coverage
python -m pytest tests/ --cov=pokersolver
```

## Examples Script

Run the included examples script to see all features:

```bash
python examples.py
```

## Troubleshooting

### Import Errors
Make sure you've installed the package:
```bash
pip install -e .
```

### NumPy Not Found
Install dependencies:
```bash
pip install -r requirements.txt
```

### Command Not Found
Reinstall the package:
```bash
pip uninstall pokersolver-gto
pip install -e .
```

## Next Steps

- Explore the Python API for custom applications
- Modify the GTO solver for specific scenarios
- Add custom hand ranges for your playing style
- Integrate with your poker tracking software

For more information, see the main README.md file.

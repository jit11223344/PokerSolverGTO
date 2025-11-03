# Quick Start Guide - PokerSolverGTO

## 🚀 Getting Started in 5 Minutes

### Step 1: Open the Project
1. Open **Android Studio**
2. Select `File → Open`
3. Navigate to `C:\Users\Administrator\AndroidStudioProjects\PokerSolverGTO`
4. Click **OK**

### Step 2: Sync Dependencies
- Android Studio will automatically sync Gradle
- Wait for "Gradle sync finished" message
- If prompted, accept SDK licenses

### Step 3: Build the App
```cmd
# In Terminal (Windows)
gradlew.bat assembleDebug
```

Or click **Build → Build Bundle(s) / APK(s) → Build APK(s)** in Android Studio

### Step 4: Run on Device/Emulator

#### Option A: Physical Device
1. Enable **Developer Options** on your Android device
2. Enable **USB Debugging**
3. Connect via USB
4. Click **Run ▶** in Android Studio
5. Select your device

#### Option B: Emulator
1. Click **AVD Manager** (phone icon)
2. Create Virtual Device (if none exists)
   - Choose **Pixel 6** or similar
   - Select **Android 13** (API 33) or higher
3. Click **Run ▶**
4. Select emulator

---

## 📱 App Navigation Flow

### Main Screen (Trainer Hub)
```
┌──────────────────────────────────────┐
│   GTO Poker Training Hub             │
├──────────────────────────────────────┤
│  🏆 6 Core Training Exercises        │
│                                      │
│  🎯 Preflop GTO Trainer              │
│     Master opening ranges...    →   │
│                                      │
│  🎲 Postflop Strategy                │
│     Practice C-bets...          →   │
│                                      │
│  🃏 Hand Ranking Quiz                │
│     Test hand comparison...     →   │
│                                      │
│  🔍 Best 5-Card Hand                 │
│     Identify best combos...     →   │
│                                      │
│  🧮 Pot Odds & EV Calculator         │
│     Master pot odds...          →   │
│                                      │
│  💎 GTO Database Solver              │
│     Query pre-solved spots...   →   │
│                                      │
│  ✨ Poker Table Visualizer           │
│     See SVG table demo          →   │
└──────────────────────────────────────┘
```

### Example: Preflop Trainer Flow
1. Tap **Preflop GTO Trainer**
2. See random hand + position (e.g., "BTN with K♠ Q♥")
3. Choose action: Fold / Call / Raise 2.5x / Raise 3x / All-In
4. Get instant feedback (✓ Correct / ✗ Incorrect)
5. See GTO recommendation
6. Tap **Next Hand** to continue

### Example: Poker Table Demo
1. Tap **Poker Table Visualizer**
2. See SVG poker table with 6 seats
3. Tap **Flop** → 3 cards dealt with animation
4. Tap **Turn** → 1 card dealt
5. Tap **River** → 1 card dealt
6. Tap **Next** → Deal new hand

### Example: GTO Database Solver
1. Tap **GTO Database Solver**
2. See pre-loaded scenario (e.g., "BTN vs BB, K♥9♠4♣")
3. View GTO strategy:
   - bet_66%: 45%
   - bet_33%: 30%
   - check: 25%
4. Choose an action to submit answer
5. Tap **Load Random Scenario** for next spot

---

## 🎮 Interactive Features

### Preflop Trainer
- **Positions**: UTG, MP, CO, BTN, SB, BB
- **Actions**: Fold, Call, Raise (2.5x, 3x), All-In
- **Feedback**: Real-time GTO accuracy
- **Stats**: Hands played, accuracy %

### Postflop Trainer
- **Streets**: Flop, Turn, River
- **Equity**: Monte Carlo simulation (5000 iterations)
- **Actions**: Check, Bet (33%, 50%, 75%, Pot)
- **Display**: Pot size, stack, equity %

### Hand Ranking Quiz
- Compare 2 random poker hands
- Choose stronger hand (A or B)
- Instant correct/incorrect feedback
- Streak tracking

### Best Hand Identifier
- Given 7 cards (2 hole + 5 board)
- Identify best 5-card combination
- Visual highlight of winning hand
- All 10 hand ranks covered

### Pot Odds Calculator
- Given pot size and bet amount
- Calculate pot odds %
- Determine call/fold EV
- Interactive number inputs

### GTO Database Solver
- Browse pre-solved scenarios
- Filter by position, stack depth
- View action frequencies
- Submit answers for training

---

## 🎨 UI Highlights

### SVG Poker Table
- **Layers**: Black border → Gray rim → Green felt → Shadow
- **Seats**: 6 positions with chip counts
- **Cards**: Animated dealing (alpha fade-in)
- **Buttons**: Flop (green), Turn (blue), River (purple), Next (orange)

### Color Theme
- **Background**: Dark blue (#1A1A2E)
- **Cards**: White with red/black suits
- **Chips**: Gold (#FFD700)
- **Felt**: Poker green (#2E7D5E)
- **Accents**: Material3 palette

### Animations
- Card dealing: 350ms alpha fade-in
- Button ripples: Material3 default
- Navigation: Compose transitions

---

## 🐛 Troubleshooting

### Build Errors
```cmd
# Clean build
gradlew.bat clean
gradlew.bat assembleDebug
```

### App Crashes on Launch
1. Check minimum SDK 24 (Android 7.0+)
2. Verify emulator/device has sufficient RAM
3. Check Logcat for stack trace

### Cards Not Showing
- Ensure `gto_scenarios_sample.json` is in `assets/`
- Verify drawable `poker_table.xml` exists
- Rebuild project

### Slow Equity Calculation
- Reduce Monte Carlo simulations (5000 → 1000)
- Edit `MonteCarloSimulator.kt`, line ~12
- Trade accuracy for speed

---

## 📊 Sample Data

### Pre-loaded Scenarios
- **BTN vs BB**: K♥9♠4♣ board (C-bet spot)
- **CO vs BB**: Q♦7♦2♣ board (Flush draw)

### To Add More Scenarios
1. Edit `app/src/main/assets/gto_scenarios_sample.json`
2. Add JSON object with:
   - `scenario_id`
   - `position`, `stack_depth`
   - `board` (array of card strings)
   - `gto_strategy` (action: frequency map)
3. Rebuild app

---

## 🔧 Customization

### Change Table Color
Edit `res/drawable/poker_table.xml`:
```xml
<path android:fillColor="#1E6A3E" ... />
```

### Adjust Simulation Count
Edit `MonteCarloSimulator.kt`:
```kotlin
suspend fun calculateEquity(..., simulations: Int = 10000) // Change here
```

### Modify Position Names
Edit `GTOStrategy.kt`:
```kotlin
enum class Position { UTG, MP, CO, BTN, SB, BB, LJ, HJ } // Add more
```

---

## 📈 Progress Tracking

### Stored in Room Database
- Total questions answered
- Correct answers count
- Accuracy percentage
- Last practice timestamp

### View Stats
- Each trainer screen shows live stats
- Main hub displays overall progress (future enhancement)

---

## ✅ Checklist for First Run

- [x] Android Studio installed
- [x] JDK 11+ configured
- [x] Project synced without errors
- [x] Emulator/device connected
- [x] App builds successfully
- [ ] Launch app
- [ ] Try each of 6 exercises
- [ ] View poker table demo
- [ ] Check GTO database solver

---

**Ready to master GTO poker! 🎉♠️♥️♣️♦️**

Need help? Check the main [README.md](README.md) or [Implementation Summary](IMPLEMENTATION_SUMMARY.md).


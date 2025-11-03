# Poker Table SVG Integration Guide

## 🎨 SVG Poker Table Structure

### File Location
```
app/src/main/res/drawable/poker_table.xml
```

### Visual Breakdown

```
┌────────────────────────────────────────┐
│  Outer Black Border (400x240)          │
│  ┌──────────────────────────────────┐  │
│  │  Gray Rim                         │  │
│  │  ┌────────────────────────────┐   │  │
│  │  │  Green Felt Surface        │   │  │
│  │  │  ┌──────────────────────┐  │   │  │
│  │  │  │  Inner Shadow        │  │   │  │
│  │  │  │  (darker green)      │  │   │  │
│  │  │  └──────────────────────┘  │   │  │
│  │  └────────────────────────────┘   │  │
│  └──────────────────────────────────┘  │
└────────────────────────────────────────┘
```

### SVG Layers (4 total)

1. **Outer Border** - `#000000` (Black)
2. **Gray Rim** - `#E0E0E0` (Light Gray)
3. **Green Felt** - `#2E7D5E` (Professional Poker Green)
4. **Shadow Gradient** - `#1A5A42` @ 30% opacity

---

## 🏗️ Compose Integration

### Option A: Image with painterResource (Used)

```kotlin
Image(
    painter = painterResource(id = R.drawable.poker_table),
    contentDescription = "Poker Table",
    modifier = Modifier
        .fillMaxWidth(0.9f)
        .aspectRatio(400f / 240f), // 1.67:1 ratio
    contentScale = ContentScale.FillBounds
)
```

**Pros:**
- ✅ Clean and declarative
- ✅ Easy to maintain
- ✅ Supports tinting/effects
- ✅ Vector scaling (no pixelation)

### Option B: Custom Canvas Drawing (Alternative)

```kotlin
Canvas(modifier = Modifier.size(400.dp, 240.dp)) {
    // Draw oval table manually
    drawOval(color = Color(0xFF2E7D5E), ...)
}
```

**Pros:**
- Dynamic colors
- Runtime modifications

**Cons:**
- More verbose
- Manual coordinate calculations

---

## 🎯 Player Seat Positioning

### 6-Max Table Layout

```
        [UTG]
          🎯
    
[MP]            [BB]
  🎯              🎯

[CO]     POT     [SB]
  🎯     $100     🎯

        [BTN]
    ──────────
    │Hero Cards│
    │  A♠ K♥  │
    └──────────┘
```

### Alignment Code

```kotlin
Seat("UTG", 950, Modifier.align(TopCenter).offset(y = 16.dp))
Seat("MP", 1200, Modifier.align(TopStart).offset(x = 24.dp, y = 48.dp))
Seat("CO", 1400, Modifier.align(CenterStart).offset(x = 8.dp))
Seat("BTN", 1700, Modifier.align(BottomCenter).offset(y = -16.dp))
Seat("SB", 800, Modifier.align(CenterEnd).offset(x = -8.dp))
Seat("BB", 1100, Modifier.align(TopEnd).offset(x = -24.dp, y = 48.dp))
```

---

## 🃏 Community Cards Overlay

### Center Positioning

```kotlin
Column(
    modifier = Modifier.align(Alignment.Center),
    horizontalAlignment = CenterHorizontally
) {
    Text("Pot: $120", color = Gold)
    
    Row(spacing = 8.dp) {
        // Flop
        PlayingCard("K♥")
        PlayingCard("9♠")
        PlayingCard("4♣")
        
        // Turn
        PlayingCard("10♦")
        
        // River
        PlaceholderCard() // Not dealt yet
    }
}
```

---

## 🎬 Animation System

### Card Deal Animation

```kotlin
val cardAlphaAnims = remember { List(7) { Animatable(0f) } }

LaunchedEffect(board, hero) {
    // Animate hero cards first
    cardAlphaAnims[0].animateTo(1f, tween(350))
    delay(120)
    cardAlphaAnims[1].animateTo(1f, tween(350))
    
    // Then flop/turn/river
    cardAlphaAnims[2..6].forEach {
        delay(120)
        it.animateTo(1f, tween(350))
    }
}
```

### Street Dealing Flow

1. **Preflop**: Deal 2 hero cards (alpha 0→1)
2. **Flop**: Deal 3 community cards
3. **Turn**: Deal 1 card
4. **River**: Deal 1 card
5. **Next Hand**: Reset all alphas to 0

---

## 🎨 Color Palette

### Table Colors
| Element | Color | Hex |
|---------|-------|-----|
| Felt | Poker Green | `#2E7D5E` |
| Rim | Light Gray | `#E0E0E0` |
| Border | Black | `#000000` |
| Shadow | Dark Green | `#1A5A42` |

### Chip Colors
| Denomination | Color | Hex |
|--------------|-------|-----|
| Gold (Display) | Gold | `#FFD700` |
| $1 | White | `#FFFFFF` |
| $5 | Red | `#E74C3C` |
| $25 | Green | `#27AE60` |
| $100 | Black | `#2C3E50` |

### UI Accents
| Action | Color | Hex |
|--------|-------|-----|
| Flop Button | Green | `#27AE60` |
| Turn Button | Blue | `#3498DB` |
| River Button | Purple | `#9B59B6` |
| Next Button | Orange | `#E67E22` |

---

## 📐 Dimensions & Ratios

### Table
- **Width**: 400dp (100%)
- **Height**: 240dp (aspect ratio 1.67:1)
- **Corner Radius**: 50% (oval shape)

### Cards
- **Playing Card**: 56dp × 80dp (ratio 7:10)
- **Corner Radius**: 6dp

### Seats
- **Avatar Circle**: 48dp diameter
- **Card Container**: 110dp × 64dp

### Spacing
- **Card Gap**: 8dp
- **Seat Offset**: 8-48dp from edges
- **Content Padding**: 12-16dp

---

## 🔧 Customization Options

### Change Table Color
Edit `poker_table.xml`:
```xml
<path
    android:fillColor="#1E6A3E"  <!-- Change this -->
    android:pathData="M 200,35 Q 355,35 ..." />
```

### Adjust Table Size
In Compose:
```kotlin
Image(
    painter = painterResource(R.drawable.poker_table),
    modifier = Modifier
        .fillMaxWidth(0.85f)  // 85% width
        .aspectRatio(400f / 240f)
)
```

### Add Table Logo
Overlay text/image at center:
```kotlin
Box(modifier = Modifier.fillMaxSize()) {
    Image(...) // Table
    Text(
        "POKER SOLVER GTO",
        modifier = Modifier.align(TopCenter),
        color = White.copy(alpha = 0.3f)
    )
}
```

---

## 🎯 Best Practices

### Performance
- ✅ Use `painterResource` for vectors (cached)
- ✅ Avoid re-creating Deck on recomposition
- ✅ Use `remember` for animation state
- ✅ Limit Monte Carlo simulations (<10k)

### Accessibility
- ✅ Add `contentDescription` to all cards
- ✅ High contrast for chip counts
- ✅ Minimum 48dp touch targets for buttons

### Responsiveness
- ✅ Use `.fillMaxWidth()` with max constraints
- ✅ Scale table based on screen size
- ✅ Test on tablets and phones

---

## 🐛 Troubleshooting

### Table not showing?
- Check `R.drawable.poker_table` exists
- Verify XML is in `res/drawable/` (not `res/raw/`)
- Clean & rebuild project

### Cards overlapping seats?
- Adjust `offset()` values
- Use `BoxWithConstraints` for responsive layout

### Animation jank?
- Reduce `tween` duration
- Use `LaunchedEffect` keys properly
- Avoid blocking coroutines

---

**Table is ready! 🎲 Deal the cards! ♠️♥️♣️♦️**


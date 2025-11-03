10. Confirmation dialog appears
        ↓
11. User taps "Cancel" (or "Clear")
        ↓
12. Dialog closes
        ↓
13. User taps back arrow ⬅️
        ↓
14. Returns to Main Menu
```

## Accessibility Features

Current:
- ✅ All buttons have contentDescription
- ✅ Icons are appropriately sized (24dp standard)
- ✅ Text has good contrast ratios
- ✅ Touch targets are sufficient (>48dp recommended)
- ✅ Scrollable content with clear hierarchy

TODO:
- Add more descriptive content descriptions
- Add screen reader support for settings impact
- Add haptic feedback on state changes
- Add large text mode support

## Performance Notes

```
LazyColumn Benefits:
├─ Only visible items rendered
├─ Smooth scrolling
├─ Memory efficient
└─ Can handle 50+ settings

State Management:
├─ Uses local mutableState (no persistence yet)
├─ Recomposes only affected items
├─ No complex logic
└─ Ready for database integration

Dialog Performance:
├─ AlertDialogs are lightweight
├─ No layout recomposition on show/hide
├─ Smooth animations
└─ Minimal memory overhead
```

## Next Development Steps (Visual)

```
Current State:
┌─────────────────┐
│ Settings Screen │
│ (UI Complete)   │
└────────┬────────┘
         ↓
1. Add Database Layer
   └─ Persist settings
   └─ Load on app start
   └─ Sync across sessions
         ↓
2. Implement Actions
   └─ Wire theme toggle
   └─ Wire audio/haptics
   └─ Implement data export
         ↓
3. Add Backend Features
   └─ Username editing
   └─ Avatar picker
   └─ Cloud sync
         ↓
4. Polish & Testing
   └─ Unit tests
   └─ UI tests
   └─ Performance testing
```

---

**Last Updated:** November 3, 2025
**Design Status:** ✅ COMPLETE
# Settings Screen - Visual Guide

## Navigation Flow

```
Main Menu (TrainerHomeScreen)
        ↓
   Drawer Menu Opens
        ↓
   Click "Settings" ⚙️
        ↓
   SettingsScreen Opens
   (with back arrow ⬅️)
        ↓
   Select Any Setting
        ↓
   Changes Stored in State
        ↓
   Back Button Returns to Menu
```

## Drawer Menu Items (After Update)

```
┌─────────────────────────────┐
│ 🏠 Home                     │
│ 🧮 Equity Calculator        │
│ 📊 Preflop Training         │
│ 👁️ Hand Evaluator          │
│ ⚙️ Settings                 │
└─────────────────────────────┘

Icons used:
- 🏠 Icons.Default.Home
- 🧮 Icons.Default.Calculate
- 📊 Icons.Default.Assessment
- 👁️ Icons.Default.Visibility
- ⚙️ Icons.Default.Settings
```

## Settings Screen Layout

```
┌──────────────────────────────────┐
│ ⬅️ Settings               [Header]│
└──────────────────────────────────┘
│                                  │
│ 👤 USER PROFILE                  │
├──────────────────────────────────┤
│ 👤 Edit Username          [>]    │
├──────────────────────────────────┤
│ 📷 Change Avatar          [>]    │
├──────────────────────────────────┤
│ 🔄 Reset Progress         [>]    │
│                                  │
│ ⚙️ APP PREFERENCES               │
├──────────────────────────────────┤
│ 🌙 Dark Theme          [Toggle]  │
├──────────────────────────────────┤
│ 🔊 Sound Effects       [Toggle]  │
├──────────────────────────────────┤
│ 📳 Vibration Feedback  [Toggle]  │
├──────────────────────────────────┤
│ ⚡ Animation Speed     [Dropdown]│
│                    [slow ▼]      │
│                                  │
│ 🎓 TRAINING SETTINGS             │
├──────────────────────────────────┤
│ 🎓 Difficulty Level    [Dropdown]│
│                    [intermediate]│
├──────────────────────────────────┤
│ ⏱️ Time per Question      [30]   │
├──────────────────────────────────┤
│ ▶️ Auto-advance        [Toggle]  │
│                                  │
│ 📦 DATA MANAGEMENT               │
├──────────────────────────────────┤
│ 🗑️ Clear History          [>]   │
│                           ⚠️ Orange
├──────────────────────────────────┤
│ 📥 Export Data            [>]    │
├──────────────────────────────────┤
│ 💣 Reset All Data         [>]    │
│                           ⚠️ Red
│                                  │
│ ℹ️ ABOUT                         │
├──────────────────────────────────┤
│ Version: 1.0.0                   │
│ Developer: PokerSolver Team      │
├──────────────────────────────────┤
│ 📋 Privacy Policy         [>]    │
├──────────────────────────────────┤
│ 📄 Terms of Service       [>]    │
│                                  │
└──────────────────────────────────┘
```

## Dropdown Menu Examples

### Animation Speed Dropdown
```
Normal (current)
├─ Slow     ← User selects
├─ Normal   ← Currently selected
└─ Fast
```

### Difficulty Level Dropdown
```
Intermediate (current)
├─ Beginner
├─ Intermediate ← Currently selected
└─ Advanced
```

## Dialog Examples

### Clear Training History Dialog
```
┌────────────────────────────────┐
│ Clear Training History?        │
├────────────────────────────────┤
│ This action cannot be undone.  │
│ All training history will be   │
│ permanently deleted.           │
├────────────────────────────────┤
│     [Clear]      [Cancel]      │
└────────────────────────────────┘
```

### Reset Progress Dialog
```
┌────────────────────────────────┐
│ Reset Progress?                │
├────────────────────────────────┤
│ Your player level and stats    │
│ will be reset to level 1.      │
│ This action cannot be undone.  │
├────────────────────────────────┤
│     [Reset]      [Cancel]      │
└────────────────────────────────┘
```

### Reset All Data Dialog
```
┌────────────────────────────────┐
│ Reset All App Data?            │
├────────────────────────────────┤
│ ALL app data including         │
│ profile, settings, and         │
│ training history will be       │
│ permanently deleted. This      │
│ cannot be undone.              │
├────────────────────────────────┤
│   [Reset All]    [Cancel]      │
└────────────────────────────────┘
```

## Color Scheme Reference

```
Main Background:     #0D1B2A (Dark Navy)
Card Background:     #1B263B (Darker Blue)
Icon Color:          #3498DB (Cyan Blue)
Section Headers:     #27AE60 (Green)
Warning Color:       #FF9800 (Orange)
Error Color:         #E74C3C (Red)
Primary Text:        #FFFFFF (White)
Secondary Text:      #AAAAAA (Gray)
```

## Icon Usage

```
Section Icons:
└─ No icon used (only text)

Option Icons (all cyan blue #3498DB):
├─ 👤 Icons.Default.Person
├─ 📷 Icons.Default.PhotoCamera
├─ 🔄 Icons.Default.Refresh
├─ 🌙 Icons.Default.DarkMode
├─ 🔊 Icons.Default.VolumeUp
├─ 📳 Icons.Default.Vibration
├─ ⚡ Icons.Default.Speed
├─ 🎓 Icons.Default.School
├─ ⏱️ Icons.Default.Timer
├─ ▶️ Icons.Default.PlayArrow
├─ 🗑️ Icons.Default.DeleteOutline
├─ 📥 Icons.Default.FileDownload
├─ 💣 Icons.Default.DeleteSweep
├─ 📋 Icons.Default.Info
└─ 📄 Icons.Default.Description

Action Icons:
├─ ⬅️ Icons.AutoMirrored.Filled.ArrowBack (header)
├─ ▶️ Icons.Default.ChevronRight (clickable items)
├─ ⋮ Icons.Default.MoreVert (dropdown trigger)
└─ ⊙ Material3 Switch (toggle)
```

## State Flow Diagram

```
User Opens Settings
        ↓
All States Initialize:
├─ isDarkTheme = true
├─ soundEnabled = true
├─ vibrationEnabled = true
├─ animationSpeed = "normal"
├─ difficultyLevel = "intermediate"
├─ timePerQuestion = "30"
├─ autoAdvance = false
└─ All dialog states = false
        ↓
User Interacts with Settings
├─ Toggles: isEnabled = !isEnabled
├─ Dropdowns: selectedValue = newOption
├─ Dialogs: showDialog = true
└─ Options: onClick function executes
        ↓
State Updates Trigger Recomposition
        ↓
UI Reflects Changes Immediately
        ↓
On Dialog Confirm:
├─ Execute action (TODO)
├─ Update state
└─ showDialog = false
```

## Screen Size Responsiveness

```
Small Phone (320dp):
├─ Full padding applied
├─ Text may wrap to 2 lines
├─ LazyColumn scrolls vertically
└─ All items remain clickable

Standard Phone (360dp):
├─ Optimal layout
├─ Comfortable spacing
├─ Good scroll experience
└─ All elements visible

Tablet (600dp+):
├─ Same layout (no grid change)
├─ More spacious feel
├─ Could be optimized later
└─ All elements remain centered
```

## User Journey Example

```
1. User opens app → Main Menu
        ↓
2. Swipe left or click menu icon → Drawer opens
        ↓
3. Tap "Settings" (with ⚙️ icon)
        ↓
4. Settings screen appears with back ⬅️ arrow
        ↓
5. Scroll through sections:
   • User Profile (click items)
   • App Preferences (toggle/select)
   • Training Settings (toggle/select)
   • Data Management (confirm dialogs)
   • About (info/links)
        ↓
6. User toggles "Dark Theme" toggle
        ↓
7. State updates: isDarkTheme = false
        ↓
8. (TODO) App theme changes
        ↓
9. User taps "Clear Training History"
        ↓


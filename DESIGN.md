# Pitiq Mobile — UI Design Specification

This document describes every screen, component, dialog, and visual token in the Pitiq photobooth kiosk Android app. Intended for UI/design recreation.

---

## System-Level UI Behavior

These apply globally across all screens:

- **Full immersive mode**: Status bar and navigation bar are permanently hidden. The app occupies 100% of the display.
- **System bars**: Hidden via `WindowInsetsController`. Briefly reappear on swipe (transient), then re-hide.
- **Screen always-on**: `FLAG_KEEP_SCREEN_ON` is set — display never dims or sleeps.
- **Back button**: Suppressed. No back navigation available to the user. Operator exit only via PIN.
- **Edge-to-edge**: Content renders under system bar areas (edge-to-edge layout enabled).

---

## Design System

### Color Palette

| Role | Hex | Description |
|------|-----|-------------|
| `primary` | `#E8C97E` | Warm gold — brand accent, buttons, highlights |
| `onPrimary` | `#1A1208` | Dark brown — text on gold buttons |
| `secondary` | `#D4A853` | Muted amber — secondary text, coin indicators |
| `background` | `#0D0D0D` | Near-black — all screen backgrounds |
| `surface` | `#1C1C1C` | Dark gray — cards, chips, overlays |
| `onBackground` | `#F5F0E8` | Warm cream/off-white — primary body text |
| `onSurface` | `#F5F0E8` | Same warm cream — text on cards |

All screens use a **dark theme**. No light mode.

### Typography

Material 3 default type scale. Key sizes used:
- App logo / hero display: **80sp, Bold, letterSpacing 8sp**
- Price display (currency amount): **96sp, Bold**
- Screen headings: **28sp, Bold**
- Sub-headings / layout names: **20–24sp, SemiBold**
- Body / instruction text: **16–22sp, Light or Regular**
- Small / supporting text: **11–14sp**

### Shape Language

- Buttons: `RoundedCornerShape(12dp)`
- Cards: `RoundedCornerShape(16dp)`
- Small chips/tags: `RoundedCornerShape(16dp)` (pill shape)
- Progress bars: `RoundedCornerShape(4dp)`
- QR code image: `RoundedCornerShape(16dp)`
- Coin step dots: `RoundedCornerShape(6dp)` (12dp circles)
- Control buttons (edit toolbar): `RoundedCornerShape(8dp)`

### Spacing & Layout

- Most screens: full bleed, fills entire screen, centered content
- Standard horizontal padding: 32–48dp
- Standard vertical spacing between major sections: 40–64dp
- All screens use center-alignment for primary content

---

## Screen Flow

```
OperatorSetupScreen (first-run only)
        |
  AttractScreen  <----------------------------------------------+
        | (tap anywhere)                                         |
  PaymentScreen                                                  |
        | (total reaches 40 pesos)                              |
  LayoutSelectionScreen                                          |
        | (confirm layout)                                       |
  PhotoCaptureScreen (repeats for each photo slot)               |
        | (all slots filled)                                     |
  EditScreen                                                     |
        | (print button)                                         |
  PrintScreen                                                    |
        | (success)                                              |
  UploadScreen                                                   |
        | (done)                                                 |
  QRShareScreen ------------------------------------------------->
                (countdown timer expires -> back to Attract)
```

---

## Screen 1 — Attract Screen (Idle / Splash)

**Purpose:** Kiosk idle state. Full-screen tap-to-start.

### Layout

Full-screen box with background `#0D0D0D`. Center-aligned vertical column.

### Content (normal state — printer connected)

1. **Animated ring** (behind logo):
   - Circular shape, color `#E8C97E` (gold) at 15% opacity
   - Diameter scales from 132dp (0.6 x 220dp) to 264dp (1.2 x 220dp) base size
   - Scale animation: 2000ms linear, restarts continuously (grows outward)
   - Opacity animates from 80% → 0% over same 2000ms, restart mode
   - Effect: expanding translucent gold halo pulsing outward behind the logo

2. **Logo text** `"pitiq"`:
   - Font: **80sp, Bold, letterSpacing 8sp**
   - Color: `#E8C97E` (gold)
   - All lowercase

3. **Price text** `"₱40 per session"`:
   - Font: **28sp, FontWeight.Light**
   - Color: `#F5F0E8` (warm cream)
   - 16dp below logo

4. **CTA text** `"Tap anywhere to begin"`:
   - Font: **18sp**
   - Color: `#D4A853` (muted amber)
   - 64dp below price text
   - **Pulsing opacity animation**: oscillates between 30% and 100% opacity, 1200ms linear, reverse repeat (breathing effect)

5. **Hidden operator trigger** (top-right corner):
   - 80dp × 80dp invisible tap zone with 8dp inset padding
   - Positioned at top-right corner of the screen
   - Long-press opens the Operator Exit Dialog
   - No visual indicator — completely invisible to users

### Content (error state — printer disconnected)

When printer is not detected, a full-screen overlay appears on top:

- Overlay background: `#E6000000` (approximately 90% black)
- Center-aligned column with 32dp padding:
  - `"OUT OF SERVICE"`: **32sp, Bold, color `#FF4444`** (bright red), centered text
  - `"PRINTER NOT DETECTED"`: **16sp, color `#FFAAAA`** (light pink-red), centered, 16dp below

- Screen tap is **disabled** in this state (clicking does nothing until printer reconnects)

---

## Screen 2 — Payment Screen

**Purpose:** User inserts coins. Shows live total and progress toward the session price (₱40).

### Layout

Full-screen box with background `#0D0D0D`. Centered vertical column with 48dp horizontal padding.

### Content (top to bottom, all vertically centered on screen)

1. **"No change" notice banner**:
   - Background: `#2A2000` (very dark amber/brown), rounded corners 8dp
   - Padding: 24dp horizontal, 12dp vertical
   - Text: `"No change will be given"`, 14sp, color `#D4A853` (amber)

2. **Coin total amount** (large hero number):
   - Displays current total in format `"₱{amount}"` — updates live as coins are inserted
   - Font: **96sp, Bold**
   - Color: `#E8C97E` (gold)
   - 56dp below the notice banner

3. **"of ₱40" sub-label**:
   - Text: `"of ₱40"`
   - Font: **22sp**
   - Color: `#F5F0E8` at 50% opacity (dim cream)
   - Immediately below the large amount

4. **Coin step milestone indicators**:
   - 4 milestones evenly spaced across full width: ₱10, ₱20, ₱30, ₱40
   - Each milestone is a small vertical column:
     - **Dot** at top: 12dp × 12dp circle
       - Gold `#E8C97E` if `coinTotal >= step value` (reached)
       - Dark gray `#1C1C1C` if not yet reached
     - **Label** below dot: `"₱{value}"`, 12sp, 4dp below dot
       - Gold `#E8C97E` if reached
       - Cream `#F5F0E8` at 30% opacity if not reached
   - 40dp below the sub-label

5. **Progress bar**:
   - Full width, 8dp height, rounded 4dp corners
   - Track (empty portion): dark gray `#1C1C1C`
   - Fill (progress): gold `#E8C97E`
   - Progress animates smoothly with 400ms ease when coins are inserted
   - Fills proportionally from 0 to full as total goes from ₱0 to ₱40
   - 16dp below step indicators

6. **Instruction text** `"Insert coins to continue"`:
   - Font: **18sp**
   - Color: `#F5F0E8` at 60% opacity
   - 40dp below progress bar

### Idle Timeout Behavior

- If **no coins are inserted within 90 seconds**, the session is automatically cancelled
- App navigates silently back to the Attract screen — no error message shown to the user
- Timeout timer is **paused** while Bluetooth is reconnecting (so it doesn't fire during hardware issues)
- If Bluetooth reconnect fails completely (`ReconnectFailed`), session is also cancelled immediately

### Debug-Only UI (shown only in debug/development builds)

Appears 48dp below instruction text. Not visible to end users:

- Row of 3 buttons spaced 12dp apart: labeled `"+₱10"`, `"+₱20"`, `"+₱40"`
  - Button background: `#1A1A00` (very dark yellow)
  - Text color: `#D4A853` (amber), 13sp
  - Each button simulates inserting that coin amount
- Below buttons: `"debug — simulated coins"`, 11sp, cream at 25% opacity

### Overlay — Bluetooth Reconnecting

When the Bluetooth coin acceptor hardware is disconnecting/reconnecting:
- Full-screen semi-transparent overlay: `#B3000000` (approximately 70% black)
- Center text: `"Reconnecting…"`, 22sp, color `#D4A853` (amber)

---

## Screen 3 — Layout Selection Screen

**Purpose:** User picks a photo strip layout (determines how many photos and their arrangement).

### Layout

Full-screen vertical column, background `#0D0D0D`, 32dp top and bottom vertical padding, centered horizontally.

### Content (top to bottom)

1. **Heading** `"Choose a layout"`:
   - Font: **28sp, Bold**
   - Color: `#F5F0E8` (cream)

2. **Sub-heading** `"Swipe to browse"`:
   - Font: **14sp**
   - Color: `#F5F0E8` at 50% opacity
   - 8dp below heading

3. **Horizontal scrolling layout card list** (auto-selects first card on load):
   - Appears 32dp below the sub-heading
   - Scrolls horizontally (swipe left/right)
   - 32dp padding on left and right ends
   - 20dp gap between cards
   - Each card: see Layout Card Component below

4. **Selected layout info + confirm section** (bottom area, shown once a card is selected):
   - Floats to the bottom of the screen (flexible spacer pushes content up)
   - 32dp horizontal padding
   - Layout name text: **20sp, SemiBold**, color `#E8C97E` (gold), centered
   - Photo count: `"{n} photos"`, 14sp, cream at 60% opacity, centered, below name
   - 24dp spacer
   - **"Select Layout" button**:
     - Full width, 56dp height
     - Background: `#E8C97E` (gold)
     - Text: `"Select Layout"`, 18sp, Bold, color `#1A1208` (dark brown)
     - Rounded corners 12dp

5. **32dp bottom spacer**

### Layout Card Component

Width: 160dp. Rounded corners: 16dp.

- Background: `#1C1C1C` (dark gray)
- Border: 2dp solid
  - Selected card: border color `#E8C97E` (gold)
  - Unselected card: border color `#1C1C1C` (same as background — invisible border)
- Internal padding: 16dp all sides
- Content from top to bottom:
  1. **Layout Preview visual** (see below) — fills most of the card height
  2. Layout name text: 14sp, Medium weight, color `#F5F0E8`, 12dp below preview
  3. Photo count: `"{n} photos"`, 12sp, cream at 50% opacity

### Layout Preview Component (inside cards)

A miniature representation of the photo slot arrangement:

- Portrait aspect ratio (tall rectangle, `aspectRatio 0.5`)
- Slots arranged in a grid with 4dp gaps
- 1 column for layouts with 4 or fewer photos; 2 columns for more
- Each slot rendered as a small rectangle:
  - Background: very dark `#0D0D0D` at 60% opacity
  - Border: 1dp, `#E8C97E` gold at 40% opacity, 4dp rounded corners
- Empty grid cells (when slot count is odd in 2-column layout): blank spacers

---

## Screen 4 — Photo Capture Screen

**Purpose:** Live camera viewfinder with automatic countdown and photo capture.

### Layout

Full-screen box. Camera preview fills 100% of the screen edge-to-edge.

### Elements

1. **Camera preview** (fills entire screen):
   - Uses the **front-facing camera** (selfie camera)
   - Live video fills the whole display
   - No borders or frame

2. **Slot indicator** (top center, overlaid on camera):
   - Normal mode: `"Photo {n} of {total}"` — e.g. `"Photo 2 of 4"`
   - Retake mode: `"Retake — Slot {n+1}"`
   - Font: **18sp, White**
   - Background: semi-transparent black `#88000000`
   - Padding: 20dp horizontal, 8dp vertical
   - Positioned top-center, 24dp from top edge

3. **Large countdown number** (center of screen):
   - Shows only during the final **3 seconds** (when countdown = 1, 2, or 3)
   - Font: **160sp, Bold, White** — extremely large, dominates screen
   - Fades in and fades out with `AnimatedVisibility`

4. **"Smile!" hint** (bottom center):
   - Visible during the **first 7 seconds** of countdown (when countdown = 4 through 10)
   - Text: `"Smile!"`, 28sp, White
   - Background: semi-transparent black `#66000000`
   - Padding: 32dp horizontal, 12dp vertical
   - Positioned at bottom-center, 48dp from bottom edge
   - Fades in/out smoothly

### Behavior Notes

- Countdown runs from 10 to 1 (10 seconds total per photo)
- Camera captures one frame per second automatically (burst capture)
- The final captured frame is used as the photo for that slot
- No manual shutter button — fully hands-free
- After all slots captured, advances to Edit screen

### Permission State (camera not granted)

If camera permission is denied:
- Centered message: `"Camera permission required"`, cream color
- Background: `#0D0D0D`
- App automatically requests the permission on load

---

## Screen 5 — Edit Screen

**Purpose:** User reviews captured photos, adjusts framing, optionally adds text, then prints.

### Layout

Full-screen box, background `#0D0D0D`. A full-screen canvas renders photos. UI controls float on top.

### Main Canvas (full screen)

- Renders the photo strip layout on screen
- Each photo slot is a dark rectangle `#1A1A1A`
- Photos fill their slot with center-crop scaling
- **Selected slot**: outlined with a 3dp gold border `#E8C97E`
- **Touch gestures on canvas**:
  - Tap on any slot → selects that slot (moves gold outline)
  - Pinch on selected slot → zooms photo in/out within the slot (**0.5× min to 3.0× max**)
  - Drag on selected slot → pans/repositions photo within the slot

### Top-Left — Countdown Timer

- Normal (>10s remaining): `"⏱ {seconds}s"`, e.g. `"⏱ 90s"` (starts at 90 seconds)
  - Font: 20sp, Bold
  - Color: `#D4A853` (amber)
- Warning (≤10s remaining): same format but color changes to `#FF6B6B` (red)
- Expired: `"Time's up!"`, same size/weight, color `#FF6B6B`
- Positioned top-left, 16dp from edges

### Top-Right — Editing Controls

Vertical stack of buttons, top-right corner, 16dp from edges, 8dp between buttons:

1. **Flip button** (always visible):
   - Square: 48dp × 48dp
   - Background: `#CC1C1C1C` (semi-transparent dark gray), 8dp rounded corners
   - Center content: `"↔"` symbol, White, 20sp
   - Tapping flips the selected slot's photo horizontally (mirror)

2. **"Retake" button** (visible only if retake not yet used for the selected slot):
   - Background: `#CC1C1C1C`, 8dp rounded corners
   - Padding: 12dp horizontal, 8dp vertical
   - Text: `"Retake"`, amber `#D4A853`, 14sp
   - Tapping pauses the edit timer and sends user back to camera for that slot
   - Each slot allows only **one retake** — button disappears after use

### Top-Center — Text Field Chips (conditional)

Only appears if the selected layout includes defined text overlay fields:

- Horizontal row of pill-shaped chips, centered, 8dp apart, 16dp from top
- Each chip for a text field:
  - **Empty state**: `"+ {fieldLabel}"`, e.g. `"+ Your Name"`, amber color
  - **Filled state**: shows the entered text, cream color
  - Background: `#CC1C1C1C` (semi-transparent dark), pill-shaped (16dp rounded corners)
  - Padding: 16dp horizontal, 6dp vertical
  - Tapping a chip opens a text input dialog

### Bottom — Print Button

- Anchored to bottom center of screen
- Full width minus 32dp horizontal padding, 24dp from bottom edge
- Height: 56dp
- Background: `#E8C97E` (gold)
- Normal text: `"Print Now"`, 20sp, Bold, dark brown `#1A1208`
- Loading text: `"Preparing…"`, same style
- Rounded corners 12dp
- Disabled and shows "Preparing…" while the final composite image is being rendered

### Text Input Dialog (triggered from chips)

A popup modal:
- Title: the field's label (e.g. "Your Name")
- Input: outlined text field, single line, enforces character limit
- Confirm button: `"Done"` (text button)
- Cancel button: `"Cancel"` (text button)

---

## Screen 6 — Print Screen

**Purpose:** Sends print job to the receipt/photo printer. Displays progress or error.

### Layout

Full-screen box, centered content, background `#0D0D0D`.

### State: Printing (normal)

1. **Animated printer icon circle**:
   - Circular container: 80dp
   - Background: gold `#E8C97E` at 15% opacity (subtle glow ring)
   - Center: printer emoji `"🖨"`, 40sp
   - **Pulsing scale animation**: oscillates between 80% and 110% size, 800ms linear, reverse repeat

2. **"Printing…" text**:
   - Font: **28sp, FontWeight.Light**
   - Color: `#F5F0E8` (cream)
   - 32dp below the circle

3. **Subtitle** `"Please wait for your photo strip"`:
   - Font: **16sp**
   - Color: cream at 50% opacity
   - 12dp below heading

### State: Print Failed

1. Warning emoji `"⚠"`, **64sp**, centered
2. `"Print Failed"` — **28sp, Bold**, color `#FF6B6B` (red), 24dp below emoji
3. Error message (specific error text) — 16sp, cream at 70% opacity, centered, 12dp below heading
4. **"Try Again" button** — 40dp below error message:
   - Full width minus 32dp padding, 56dp height
   - Background: gold `#E8C97E`
   - Text: `"Try Again"`, 18sp, Bold, dark brown
   - Rounded corners 12dp

---

## Screen 7 — Upload Screen

**Purpose:** Background processing: assembles GIF, uploads photos to cloud, generates share link. No user interaction.

### Layout

Full-screen box, centered content, background `#0D0D0D`.

### Content

1. **Status message** (changes dynamically):
   - `"Assembling your GIF…"` — while creating the animated GIF from burst frames
   - `"Uploading your photos…"` — while uploading to server
   - `"Queued for upload"` — if device is offline; will upload when connected
   - `"Preparing your link…"` — default initial state
   - Font: **22sp, FontWeight.Light**
   - Color: `#F5F0E8` (cream)

2. **Animated loading dots** `"• • •"`:
   - Font: **32sp**
   - Color: `#E8C97E` (gold)
   - Pulsing opacity: breathes between 20% and 100% opacity, 900ms linear, reverse repeat
   - 16dp below status message

3. **Queued sub-message** (visible only in queued/offline state):
   - Two-line text: `"Your link is being prepared"` / `"Please stay nearby"`
   - Font: **16sp**
   - Color: cream at 60% opacity
   - 16dp below dots

Screen advances automatically on completion. No buttons.

---

## Screen 8 — QR Share Screen

**Purpose:** Displays a QR code the user scans to download their photos. Countdown auto-returns to idle.

### Layout

Full-screen box, background `#0D0D0D`, centered.

### Main Content (centered column)

1. **Heading** `"Scan to download your photos"`:
   - Font: **24sp, Bold**
   - Color: `#F5F0E8` (cream)

2. **QR code** (32dp below heading):
   - 280dp × 280dp square
   - Rounded corners 16dp
   - **Loading state**: plain dark gray `#1C1C1C` placeholder square
   - **Loaded state**: rendered QR bitmap image (black and white QR pattern with rounded clip)

3. **Sub-label** `"Get your strip, GIF, and color photo"` (32dp below QR):
   - Font: **16sp**
   - Color: cream at 60% opacity

4. **Availability note** `"Available for 24 hours"` (8dp below sub-label):
   - Font: **14sp**
   - Color: amber `#D4A853` at 70% opacity

### Countdown Timer Badge (bottom center)

- **Starts at 60 seconds** — auto-returns to Attract when it reaches 0
- Large number showing seconds remaining
- Font: **48sp, Bold**
- Color: `#E8C97E` (gold)
- Positioned 40dp from the bottom edge
- **Fade animation**: when ≤10 seconds remain, the number gradually fades out
  - At 10s: fully visible (100% opacity)
  - At 1s: nearly invisible (~10% opacity)
  - Uses 500ms smooth transition per tick
  - Creates urgency as the QR screen is about to close

### QR Code Technical Spec

- Generated at **512×512 pixels** (internal resolution)
- Color: **black modules on white background** (standard QR colors)
- Minimum margin: 1 module (tight padding)
- Displayed on screen at **280dp × 280dp**, clipped to 16dp rounded corners

---

## Screen 9 — Operator Setup Screen (First-Run Only)

**Purpose:** One-time kiosk configuration screen shown only before the device is activated. Hidden from end users.

### Layout

Full-screen box centered, background `#0D0D0D`. Inner content column, max width 480dp, 32dp horizontal padding, 16dp spacing between all elements, centered horizontally.

### Content (top to bottom)

1. **Title** `"Operator Setup"`:
   - Material 3 headlineMedium typography
   - Color: `#E8C97E` (gold)

2. **Subtitle** `"One-time configuration. PIN required to exit kiosk."`:
   - Material 3 bodyMedium typography
   - Color: `#F5F0E8` at 70% opacity

3. **Spacer** 8dp

4. **Location ID field** (outlined text field):
   - Label: `"Location ID"`
   - Max 32 characters
   - Allowed characters: letters, digits, underscore, hyphen (3–32 total)
   - Error shown when field is non-empty but invalid: `"3–32 letters, digits, _ or -"`
   - Keyboard advances to next field on "Next"

5. **Operator PIN field** (outlined text field):
   - Label: `"Operator PIN (4–6 digits)"`
   - Numbers only, maximum 6 digits
   - Input masked (shows dots, not digits)
   - Error when non-empty but invalid: `"Enter 4–6 digits"`
   - Keyboard advances to next field on "Next"

6. **Confirm PIN field** (outlined text field):
   - Label: `"Confirm PIN"`
   - Same masking and digit-only constraints
   - Error when values differ: `"PINs don't match"`
   - Keyboard submits on "Done" if all fields valid

7. **Spacer** 8dp

8. **"Activate Kiosk" button**:
   - Full width, 56dp height
   - Uses Material 3 default button style (gold when enabled)
   - Text: `"Activate Kiosk"`, Material 3 labelLarge typography
   - **Disabled** (grayed out) until all three fields pass validation simultaneously

---

## Dialog 1 — Operator Exit Dialog

**Trigger:** Operator long-presses the invisible 80dp hit zone in the top-right corner of the Attract screen.

### Appearance

Standard Material 3 AlertDialog:

- **Title:** `"Operator Exit"`
- **Body section:**
  - Instruction text: `"Enter your PIN to exit kiosk mode."`
  - 16dp vertical spacer
  - Outlined text field:
    - Label: `"Operator PIN"`
    - Input masked (password dots)
    - Numbers only, max 6 digits
    - Error state: shows `"Incorrect PIN"` supporting text when wrong PIN is submitted
    - Submits on keyboard "Done" action
- **Confirm button:** text button labeled `"Exit Kiosk"`
  - Disabled until PIN has at least 4 digits
- **Dismiss button:** text button labeled `"Cancel"`

### Behavior

- Submitting an incorrect PIN: dialog stays open, shows error text
- Submitting the correct PIN: dialog closes and exits kiosk mode
- Tapping Cancel or outside dialog: dialog closes, returns to Attract screen

---

## Dialog 2 — Update Available Dialog

**Trigger:** App detects a new version available from the remote update server. Shown **only when the kiosk is idle** (on the Attract screen) — never interrupts an active customer session.

### Appearance

Standard Material 3 AlertDialog:

- **Title:** `"Update Available — v{versionName}"`
  - Version name pulled from remote update info
- **Body section:**
  - Changelog text (if available, non-empty), bodyMedium typography
  - 8dp spacer below changelog
  - Circular loading spinner shown only while download is in progress
- **Confirm button:** text button
  - Label: `"Download & Install"` (when update available, not yet downloading)
  - Label: `"Downloading…"` (while download is in progress)
  - Disabled during active download
- **Dismiss button:** text button labeled `"Later"`

---

## Summary: Visual Identity

| Attribute | Value |
|-----------|-------|
| Overall aesthetic | Dark luxury / minimalist kiosk UI |
| Background | Near-black `#0D0D0D` — used on every screen |
| Brand accent | Warm gold `#E8C97E` — buttons, logo, progress, highlights |
| Text color | Warm cream `#F5F0E8` — all primary body text |
| Secondary text | Muted amber `#D4A853` — hints, secondary labels |
| Error / danger | Red `#FF4444` and `#FF6B6B` — errors and warnings |
| Brand logo | Lowercase `"pitiq"`, 80sp Bold, gold color, 8sp wide letter-spacing |
| Primary buttons | Rounded 12dp, gold fill, dark brown text |
| Cards | Rounded 16dp, dark gray `#1C1C1C` surface, optional gold 2dp border |
| Overlays | Semi-transparent black (`#88–#E6000000` range) over content |
| Animation | Smooth infinite pulses (breathing scale/opacity), never abrupt |
| Typography feel | Very large hero numbers, sparse text, high contrast |
| Target device | Android tablet in portrait orientation, kiosk/fullscreen mode |

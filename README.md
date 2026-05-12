# Pitiq

An Android photobooth application for coin-operated kiosk machines deployed in cafes. Customers insert coins, take photos, and receive a printed strip plus a QR code to download their photos digitally.

---

## Overview

Pitiq runs on an Oppo Reno 5 5G in kiosk mode. It communicates with an ESP32 microcontroller via Bluetooth Serial to handle coin acceptance through a CH-926 coin acceptor, and prints to a 58mm monochrome thermal printer via USB OTG. All core session operations run fully offline.

**Price per session:** ₱40

---

## Tech Stack

| Layer | Technology |
|---|---|
| Android App | Kotlin, Jetpack Compose, CameraX, Hilt |
| Backend | Supabase (PostgreSQL, Storage, Auth, Edge Functions) |
| Share Page | Next.js on Vercel |
| Hardware | ESP32 (Bluetooth Serial), CH-926 coin acceptor, 58mm thermal printer |

---

## Session Flow

```
ATTRACT → PAYMENT → LAYOUT SELECTION → PHOTO CAPTURE → EDIT → PRINT → UPLOAD/QR → RESET → ATTRACT
```

1. **Attract** — Looping animation with price and "Tap to begin" prompt. Disabled if printer is not detected.
2. **Payment** — Real-time coin progress (₱0 → ₱40). 90-second idle timeout. Supports Bluetooth reconnection with buffered coin total.
3. **Layout Selection** — Scrollable gallery of photo strip layouts. Default layouts bundled in APK; remote layouts synced from Supabase.
4. **Photo Capture** — CameraX with layout overlay. Each slot: 10-second window with burst capture (5–8 frames), final 3-second countdown.
5. **Edit** — Drag, pinch-to-zoom, flip, and retake (1 per slot) with 90-second timer.
6. **Print** — Renders layout to 58mm monochrome bitmap and sends to thermal printer via USB.
7. **Upload / QR Share** — Uploads `thermal.png`, `color.png`, and `session.gif` to Supabase Storage. QR code shown only after upload completes. 60-second display window.
8. **Reset** — All session files deleted from device. No customer data persists after session ends.

---

## Architecture

```
app/
├── ui/          # Jetpack Compose screens and components
├── domain/      # Session model, state, and business logic
├── data/        # Room database, Supabase SDK, repositories
├── hardware/    # BluetoothManager (ESP32), PrinterManager (USB OTG)
├── kiosk/       # Device Owner, lock task, UI suppression
└── session/     # SessionViewModel, SessionCleaner, upload queue
```

Navigation graph: `Attract → Payment → LayoutSelection → PhotoCapture → Edit → Print → Upload → QRShare → Attract`

---

## Hardware

- **Device:** Oppo Reno 5 5G (minSdk API 23, Android 6.0+)
- **Coin acceptor:** CH-926 connected to ESP32 via pulse signal
- **Bluetooth:** ESP32 ↔ App via RFCOMM serial with per-session HMAC-SHA256 handshake (replay attack protection)
- **Printer:** 58mm thermal printer via USB OTG (ESC/POS protocol)

---

## Backend (Supabase)

**Tables:** `sessions`, `layouts`, `locations`

**Storage buckets:**
- `sessions/` — private, session-scoped uploads (`thermal.png`, `color.png`, `session.gif`)
- `layouts/` — public read, operator write

**Edge Functions:**
- `purge-expired-sessions` — runs hourly, deletes files and marks records purged 24 hours after `created_at`

Signed URLs with 30-minute expiry are re-issued on each share page load. Raw storage paths are never exposed.

---

## Share Page (Vercel)

URL format: `yourdomain.vercel.app/session/[session-id]`

Customers scan the QR code from their phone and can download:
- Thermal-style strip PNG (monochrome)
- Full-color original PNG
- GIF assembled from burst frames

Links expire 24 hours after session creation. No login required.

---

## Security

- Session IDs are UUID v4 generated on-device
- Bluetooth uses per-session HMAC-SHA256 challenge-response (static string unlock explicitly rejected)
- Kiosk mode via Android Device Owner (COSU) APIs — home button, notifications, and back navigation suppressed
- All sensitive data stored in `EncryptedSharedPreferences`
- No customer data persists on device or backend beyond 24 hours
- RLS enforced on all Supabase tables; `anon` key only in APK (never `service_role`)

---

## Operator Features

- Web dashboard authenticated via Supabase Auth
- Session counts and revenue tracking per location
- Print failure logs
- Layout management (upload, reorder, activate/deactivate)
- Device status by `location_id`
- In-app APK update delivery (no Google Play required)

---

## Development Phases

| Phase | Scope |
|---|---|
| 0 | Project foundation, architecture, local data layer |
| 1 | Kiosk mode, Device Owner, operator setup screen |
| 2 | ESP32 Bluetooth integration, thermal printer USB |
| 3 | All session flow screens |
| 4 | Supabase backend, schema, purge function, layout sync |
| 5 | Vercel share page |
| 6 | Operator dashboard |
| 7 | Security hardening |
| 8 | Unit and integration testing |
| 9 | Deployment (backend, web, device) |

See [ROADMAP.md](ROADMAP.md) for the full task breakdown.

---

## Requirements

- Android Studio (Kotlin + Jetpack Compose)
- Java 17+
- Physical Oppo Reno 5 5G (or compatible Android device with USB Host)
- ESP32 with CH-926 coin acceptor firmware
- 58mm USB thermal printer
- Supabase project
- Vercel account

---

## Notes

- Monetary values use integer ₱ throughout (e.g., `4000` = ₱40 in centavos or `40` in ₱ — must be consistent across Bluetooth protocol, DB record, and UI)
- Thermal printer VID/PID must be confirmed against the physical unit before Phase 2 implementation
- ESP32 firmware (HMAC shared secret, coin pulse mapping, buffer logic) is a separate deliverable and a hard dependency for Phase 2 integration tests
- Device Owner provisioning must be tested on every new device model before cafe deployment

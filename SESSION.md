# SESSION LOG

---

## Session 1 — 2026-05-12

**Topic:** Initial PROMPT.md review and design decisions

**Decisions made:**

| # | Issue | Decision |
|---|-------|----------|
| 1 | Countdown duration ambiguity | 10s total per shot. First 7s = pose. Last 3s = visible 3-2-1 countdown. Burst captured throughout all 10s. |
| 2 | Best-quality burst frame selection | Use last frame at countdown end. No selection algorithm. |
| 3 | Domain/hosting | Share page hosted on Vercel. Placeholder `yourdomain.vercel.app` until domain is set up. |
| 4 | BT disconnect during payment | Pause 90s timeout, show "Reconnecting…". ESP32 buffers coin total and sends on reconnect. If no reconnect in 30s, cancel session. Coin total not recoverable. |
| 5 | Edit timer during retake | Timer pauses when retake is initiated. Resets to 90s when customer returns to edit canvas. |
| 6 | No layouts on first boot | Default layouts bundled in APK. Remote sync supplements/overrides but is never required for core operation. |
| 7 | Offline >24h / upload never completed | Added `upload_status` (pending/uploaded/failed) and `upload_attempted_at` to sessions table. Expired-but-never-uploaded sessions marked `failed`, not `purged`. Purge job does idempotent deletes. |
| 8 | QR shown before upload ready | QR code shown only after upload is confirmed complete. "Preparing your link…" screen shown during upload with progress indicator. If offline, customer is informed to wait. |
| 9 | Printer not connected at session start | Pre-attract-screen printer check added. If printer not detected, attract screen goes "Out of Service" and operator alert shown. Session init blocked until printer reconnects. |
| 10 | location_id provisioning | One-time operator setup screen on first launch (before kiosk mode). Operator enters location ID. Stored in encrypted SharedPreferences. Not accessible to customers. |
| 11 | Android version target | Minimum API 30 (Android 11). Kiosk via Android Device Owner (COSU) APIs. |
| 12 | GIF size/compression | On-device assembly, 480px width, 256-color palette, target <5MB. Compressed before upload. |
| 13 | APK update strategy | In-app update: versioned endpoint checked on launch and after each session. Modal popup with changelog + download button. System package installer triggered. Dismissable by operator (PIN/gesture), reappears on next check if update still pending. No Play Store required. |

**Files modified:** `PROMPT.md`

---

## Session 2 — 2026-05-12

**Topic:** Roadmap creation + multi-device compatibility decisions

**Decisions made:**

| # | Issue | Decision |
|---|-------|----------|
| 14 | Roadmap created | Full development roadmap written to `ROADMAP.md`. 9 phases: Project Foundation, Kiosk Mode, Hardware Integration, Session Flow Screens, Backend (Supabase), Share Page (Vercel), Operator Dashboard, Security Hardening, Testing, Deployment. |
| 15 | Minimum Android API level | Lowered from API 30 to API 23 (Android 6.0). Hard floor due to `EncryptedSharedPreferences` requiring API 23. Covers ~99% of active Android devices. |
| 16 | Multi-device / tablet support | App designed to run on tablets and other Android devices beyond Oppo Reno 5 5G. `WindowSizeClass` used for adaptive layouts per screen (phone vs tablet form factor). |
| 17 | Bluetooth permissions across API levels | API 23–30: use legacy `BLUETOOTH` + `BLUETOOTH_ADMIN`. API 31+: use `BLUETOOTH_CONNECT` + `BLUETOOTH_SCAN`. Both sets declared in manifest; correct set requested at runtime via `Build.VERSION.SDK_INT` check. |
| 18 | USB Host hardware dependency | USB Host (for thermal printer) is a hardware capability, not guaranteed on all devices. Manifest declares feature as `required="false"`. App checks `PackageManager.FEATURE_USB_HOST` on startup; shows operator alert and disables print path if not supported. |
| 19 | Kiosk mode per-device variance | Device Owner COSU APIs work from API 21+. Some OEMs (Samsung, Huawei) override COSU behavior with proprietary kiosk SDKs. Standard APIs still function but must be tested on every new device model before cafe deployment. |
| 20 | Tablet UI scaling | Minimum touch target 48dp on all interactive elements. Adaptive rules per screen: layout selection uses 2-column grid on tablets, camera/edit canvas scale proportionally, QR code larger for scan distance. |

**Files modified:** `ROADMAP.md`, `SESSION.md`

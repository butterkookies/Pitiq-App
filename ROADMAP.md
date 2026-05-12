# PITIQ MOBILE — DEVELOPMENT ROADMAP

> Android photobooth app for coin-operated cafe kiosk machines.
> Stack: Kotlin + Jetpack Compose (Android), Supabase (PostgreSQL + Storage + Auth + Edge Functions), Vercel (Next.js share page).
> Target devices: Oppo Reno 5 5G (primary), expandable to tablets and other Android devices. minSdk API 23 (Android 6.0), kiosk mode via Device Owner COSU APIs.
> Last updated: 2026-05-12 (Session 4)

---

## PHASE 0 — PROJECT FOUNDATION

### 0.1 Android Project Setup
- [x] 0.1.1 Create new Android project in Android Studio (Kotlin, Jetpack Compose)
- [x] 0.1.2 Set `minSdk = 23`, `targetSdk = 35`, `compileSdk = 36` in `app/build.gradle.kts` (targetSdk bumped from 34; only SDK 36 platform is installed)
- [x] 0.1.3 Configure `applicationId = "com.pitiq.app"`, `versionCode = 1`, `versionName = "0.1.0"`
- [x] 0.1.4 Add dependencies: Compose BOM, Navigation Compose, CameraX, Coroutines, Kotlinx Serialization, Ktor, Supabase Kotlin SDK, AndroidX Security — all via version catalog (`gradle/libs.versions.toml`)
- [x] 0.1.5 Set up Hilt (hilt-android 2.52, KSP 2.1.0-1.0.29, `@HiltAndroidApp` on `PitiqApplication`)
- [x] 0.1.6 Configure ProGuard/R8 rules (`app/proguard-rules.pro`) for Serialization, Ktor, Supabase, ZXing, Hilt
- [x] 0.1.7 Set up Git repository (Session 3) and `.gitignore` (Session 4)

### 0.2 Project Architecture & Responsiveness
- [x] 0.2.1 Define package structure: `ui/`, `domain/`, `data/`, `hardware/`, `kiosk/`, `session/`, `di/`
- [x] 0.2.2 Define navigation graph with all screens: Attract → Payment → LayoutSelection → PhotoCapture → Edit → Print → Upload → QRShare → (Attract); also OperatorSetup. State-driven via `LaunchedEffect(sessionState)` in `AppNavigation.kt` — screens never self-navigate.
- [x] 0.2.3 Define `SessionState` sealed class covering all session phases (`Idle`, `Payment`, `LayoutSelection`, `PhotoCapture`, `Edit`, `Print`, `Upload`, `QRShare`)
- [x] 0.2.4 Define `SessionViewModel` as the central session state holder (`@HiltViewModel`, full state machine with all transitions)
- [x] 0.2.5 Define `Session`, `Layout`, `CapturedPhoto`, `TextField`, `UploadStatus` domain models in `domain/model/`
- [ ] 0.2.6 Integrate `WindowSizeClass` (Jetpack Compose adaptive) to detect phone vs tablet form factor
- [ ] 0.2.7 Define adaptive layout rules per screen:
  - Attract: scale animation and text to screen size
  - Layout Selection: 2-column grid on tablets, 1-column on phones
  - Photo Capture: camera preview fills screen, overlay scales proportionally
  - Edit Canvas: larger slot touch targets on tablets, same proportional layout
  - QR Share: QR code larger on tablets for easier scan distance
- [ ] 0.2.8 Ensure minimum touch target size 48dp across all interactive elements (readability at arm's length on any screen size)

### 0.3 Local Data Layer
- [x] 0.3.1 Set up Room database for offline session queue (`PitiqDatabase`, injected via `DatabaseModule`)
- [x] 0.3.2 Define `SessionEntity` Room entity (maps to `upload_queue` table); `LayoutEntity` deferred to Phase 3.3 when layout sync is implemented
- [x] 0.3.3 Define `UploadQueueDao` for queued upload operations (`enqueue`, `getPending`, `updateStatus`, `delete`)
- [x] 0.3.4 Set up `SecurePreferences` (`EncryptedSharedPreferences` wrapper) storing `location_id`, `operator_pin`, `bt_shared_secret`

### 0.4 Build & CI
- [x] 0.4.1 Configure debug and release build variants (`debug` with `.debug` appended to applicationId, `release` with R8/shrinking enabled)
- [x] 0.4.2 APK signing config reads from `keystore.properties` (gitignored) with env-var fallback (`KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`); `keystore.properties.template` committed as reference; signing silently skipped if keystore absent so debug builds always work
- [x] 0.4.3 `Makefile` (`make debug`, `make release`, `make install`, `make clean`) and `build.ps1` for Windows one-command builds

---

## PHASE 1 — KIOSK MODE

### 1.1 Device Owner Setup
- [x] 1.1.1 Create `KioskDeviceAdminReceiver` stub class (implementation deferred to Phase 1)
- [x] 1.1.2 Declare `BIND_DEVICE_ADMIN` and `device_admin` XML metadata in `AndroidManifest.xml` (`res/xml/device_admin.xml`)
- [x] 1.1.3 Implement `DevicePolicyManager.setLockTaskPackages()` to whitelist only this app
- [x] 1.1.4 Call `Activity.startLockTask()` on session start
- [ ] 1.1.5 Call `Activity.stopLockTask()` only from operator exit flow (PIN protected)
- [ ] 1.1.6 Note: Device Owner provisioning method differs per manufacturer — test on each target device model. Some OEMs (Samsung DeX, Huawei) override COSU with proprietary kiosk SDKs; standard COSU APIs still work but behavior may vary. Verify lock task behavior on every new device before deployment.

### 1.2 UI Suppression
- [x] 1.2.1 Set `WindowInsetsController` to hide system bars (status bar, navigation bar)
- [x] 1.2.2 Use `SYSTEM_UI_FLAG_IMMERSIVE_STICKY` flags for full-screen immersive mode
- [x] 1.2.3 Disable screen timeout via `FLAG_KEEP_SCREEN_ON` on the Activity window
- [x] 1.2.4 Suppress notifications using `DevicePolicyManager.setStatusBarDisabled()`
- [x] 1.2.5 Block physical back button (override `onBackPressed` to no-op during session)

### 1.3 First Launch — Operator Setup Screen
- [x] 1.3.1 On app start, check `EncryptedSharedPreferences` for `location_id`
- [x] 1.3.2 If not set, show operator setup screen (full-screen, before kiosk lock)
- [x] 1.3.3 Operator setup screen: text input for `location_id`, confirm button
- [x] 1.3.4 Validate `location_id` is non-empty and alphanumeric before saving
- [x] 1.3.5 Save `location_id` to `EncryptedSharedPreferences`
- [x] 1.3.6 After save, activate kiosk lock and proceed to attract screen
- [x] 1.3.7 Ensure operator setup screen is inaccessible after first setup (guarded by PIN)

---

## PHASE 2 — HARDWARE INTEGRATION

### 2.1 ESP32 Bluetooth Serial (Coin Acceptor)
- [x] 2.1.1 Declare Bluetooth permissions in manifest for both API ranges:
  - Legacy (API 23–30): `BLUETOOTH`, `BLUETOOTH_ADMIN`
  - Modern (API 31+): `BLUETOOTH_CONNECT`, `BLUETOOTH_SCAN`
  - At runtime: check `Build.VERSION.SDK_INT >= 31` and request the correct permission set accordingly
- [ ] 2.1.2 Implement `BluetoothManager` class using `BluetoothAdapter` + `BluetoothSocket` (RFCOMM, UUID: `00001101-0000-1000-8000-00805F9B34FB`)
- [ ] 2.1.3 Implement background coroutine loop to read from `BluetoothSocket.inputStream`
- [ ] 2.1.4 Define Bluetooth message protocol: message types (COIN_PULSE, HANDSHAKE_CHALLENGE, HANDSHAKE_RESPONSE, DISCONNECT_ACK)
- [ ] 2.1.5 Implement per-session rolling token handshake:
  - [ ] App generates random 128-bit challenge at session start
  - [ ] App sends challenge to ESP32 via Bluetooth
  - [ ] ESP32 responds with HMAC-SHA256(challenge, shared_secret)
  - [ ] App verifies HMAC before accepting any coin pulses for that session
  - [ ] Shared secret stored in `EncryptedSharedPreferences`; never transmitted
- [ ] 2.1.6 Implement `CoinAcceptorRepository` that parses coin pulse signals into `₱` amounts (₱1, ₱5, ₱10 pulse values from CH-926)
- [ ] 2.1.7 Implement coin total accumulation and `StateFlow<Int>` emitting current total
- [ ] 2.1.8 Handle Bluetooth disconnection events:
  - [ ] Emit `BluetoothState.DISCONNECTED` on `IOException` from read loop
  - [ ] Start 30-second reconnection timer on disconnect
  - [ ] Attempt reconnect every 3 seconds within that 30s window
  - [ ] On reconnect, re-run handshake, then request buffered coin total from ESP32
  - [ ] If 30s expires without reconnect, emit `BluetoothState.RECONNECT_FAILED`
- [ ] 2.1.9 Write unit tests for HMAC verification logic

### 2.2 Thermal Printer (USB OTG)
- [x] 2.2.1 Add `android.hardware.usb.host` feature (as `required="false"`) and `USB_PERMISSION` to manifest
- [ ] 2.2.1a On app start, check `packageManager.hasSystemFeature(PackageManager.FEATURE_USB_HOST)`:
  - If `false` → show persistent operator alert "This device does not support USB printing", disable print path entirely
  - If `true` → proceed with normal printer init flow
- [x] 2.2.2 Add USB device filter XML (`res/xml/usb_device_filter.xml`); VID/PID is placeholder — must be confirmed against physical printer before Phase 2.2 implementation
- [ ] 2.2.3 Implement `PrinterManager` class:
  - [ ] Detect USB device on `UsbManager.getDeviceList()`
  - [ ] Request `UsbManager.requestPermission()` and await result via `BroadcastReceiver`
  - [ ] Open `UsbDeviceConnection` and bulk-transfer endpoint
- [ ] 2.2.4 Implement printer connectivity check (used before attract screen):
  - [ ] Method `isPrinterConnected(): Boolean` — checks USB device list for known VID/PID
  - [ ] Returns true only if USB permission granted AND connection opens successfully
- [ ] 2.2.5 Implement ESC/POS bitmap printing:
  - [ ] Convert `Bitmap` to ESC/POS raster format (GS v 0 command)
  - [ ] Chunk print data into USB bulk-transfer packets (max 64 bytes per packet)
  - [ ] Append paper feed + cut command at end
- [ ] 2.2.6 Implement print result handling:
  - [ ] Success: `PrintResult.SUCCESS`
  - [ ] Failure types: `PAPER_OUT`, `PAPER_JAM`, `PRINTER_DISCONNECT`, `TIMEOUT`
  - [ ] Timeout: if no ACK within 15 seconds, emit `TIMEOUT`
- [ ] 2.2.7 Write printer error to session error log

---

## PHASE 3 — SESSION FLOW SCREENS

### 3.1 Attract / Idle Screen
- [ ] 3.1.1 Design and implement attract screen UI in Jetpack Compose
  - [ ] Full-screen looping animation (Lottie or custom Canvas animation)
  - [ ] Price display: "₱40 per session" — large, readable at arm's length
  - [ ] "Tap to begin" prompt with pulsing animation
- [ ] 3.1.2 Implement printer check on app start (before attract screen becomes interactive):
  - [ ] Call `isPrinterConnected()` on a background coroutine
  - [ ] If connected → attract screen is active and tappable
  - [ ] If not connected → show non-customer-facing operator alert banner (e.g., red overlay with "PRINTER NOT DETECTED — OUT OF SERVICE")
  - [ ] Re-check printer connection every 10 seconds while in "Out of Service" state
  - [ ] When printer detected again, dismiss alert and re-enable attract screen
- [ ] 3.1.3 Handle tap: call `SessionViewModel.initSession()` → generate UUID v4 session ID → navigate to Payment
- [ ] 3.1.4 Ensure attract screen auto-resets to this state after every session completion

### 3.2 Payment Screen
- [ ] 3.2.1 Design payment screen UI:
  - [ ] "No change given" notice — prominent
  - [ ] Coin progress indicator (e.g., ₱0 → ₱10 → ₱20 → ₱30 → ₱40)
  - [ ] Progress bar or circular indicator filling as coins inserted
  - [ ] "Insert ₱40" instruction
- [ ] 3.2.2 Subscribe to `CoinAcceptorRepository.coinTotal: StateFlow<Int>` and update progress UI in real-time
- [ ] 3.2.3 Implement 90-second idle timeout:
  - [ ] Start countdown when payment screen appears and Bluetooth is connected
  - [ ] If `coinTotal == 0` after 90 seconds → cancel session → navigate to Attract
  - [ ] If coins being inserted, timeout continues (do not reset on each coin)
- [ ] 3.2.4 Handle Bluetooth disconnect during payment:
  - [ ] Pause 90-second timer
  - [ ] Show "Reconnecting…" indicator overlay (non-blocking UI)
  - [ ] On reconnect: resume timer, request buffered coin total, update progress
  - [ ] On 30s reconnect failure: cancel session → navigate to Attract
- [ ] 3.2.5 When `coinTotal >= 4000` (₱40 in centavos, or use ₱ integer as defined):
  - [ ] Accept overpayment silently
  - [ ] Save `coins_inserted` to session state
  - [ ] Auto-navigate to Layout Selection
- [ ] 3.2.6 On session cancel from payment: clear session state, return to Attract

### 3.3 Layout Selection Screen
- [ ] 3.3.1 Define `Layout` data class: `id`, `name`, `previewImagePath`, `slotCount`, `frameAssetPath`, `textFields: List<TextField>`, `version`, `isDefault: Boolean`
- [ ] 3.3.2 Implement `LayoutRepository`:
  - [ ] Load bundled default layouts from APK assets (`assets/layouts/`)
  - [ ] Load synced remote layouts from local Room database (cached from last sync)
  - [ ] Merge: remote layouts override defaults by ID, appended otherwise
- [ ] 3.3.3 Design layout selection UI:
  - [ ] Scrollable/swipeable gallery (LazyRow or Pager)
  - [ ] Each card shows: preview image, layout name, slot count
  - [ ] Confirm button activates after selection
- [ ] 3.3.4 On confirm: lock layout to session, navigate to Photo Capture

### 3.4 Photo Capture Screen
- [ ] 3.4.1 Set up CameraX `Preview` + `ImageCapture` use cases
- [ ] 3.4.2 Show live camera preview full-screen
- [ ] 3.4.3 Overlay: render selected layout frame as semi-transparent overlay on top of camera feed (using `Canvas` or `AndroidView` overlay)
- [ ] 3.4.4 Show slot indicator: "Photo 1 of 3" (or however many slots layout has)
- [ ] 3.4.5 Implement 10-second slot capture window:
  - [ ] 0–7s: "Pose freely" — no visible countdown, friendly prompt text
  - [ ] 7–10s: large countdown "3… 2… 1" flashed on screen
  - [ ] Throughout 0–10s: capture burst of 5–8 frames using CameraX `ImageCapture` at ~1fps
  - [ ] At countdown = 0: use last captured frame as the final photo for this slot
- [ ] 3.4.6 Store all burst frames temporarily in `Context.cacheDir/session_[id]/slot_[n]/burst/`
- [ ] 3.4.7 Store final selected frame in `Context.cacheDir/session_[id]/slot_[n]/final.jpg`
- [ ] 3.4.8 After all slots captured: auto-navigate to Edit Phase

### 3.5 Edit Phase Screen
- [ ] 3.5.1 Render edit canvas:
  - [ ] Compose Canvas showing full layout with photos placed in slots
  - [ ] Layout frame and decorative elements rendered on top
  - [ ] Text fields rendered inline on canvas
- [ ] 3.5.2 Implement per-slot interactions:
  - [ ] Tap slot → select it (highlight border)
  - [ ] Horizontal flip button (flip `Bitmap` horizontally)
  - [ ] Drag to reposition within slot boundary (clamp pan to slot bounds)
  - [ ] Pinch-to-zoom within slot boundary (clamp scale to reasonable min/max)
- [ ] 3.5.3 Implement retake flow:
  - [ ] "Retake (1 remaining)" button per slot
  - [ ] On tap: pause 90s timer, navigate back to camera for that slot only
  - [ ] Camera opens for that slot with same 10s countdown + burst behavior
  - [ ] On return from retake: replace slot photo, reset timer to 90s, disable retake button ("Retake used"), mark slot retake as used
  - [ ] Retake used state persisted in session state (not re-enabled)
- [ ] 3.5.4 Implement text field editing:
  - [ ] If layout has text fields: show them inline on canvas
  - [ ] Tap text field → open soft keyboard input dialog
  - [ ] Update canvas render with entered text
- [ ] 3.5.5 Implement 90-second countdown timer:
  - [ ] Prominent countdown display on screen
  - [ ] Timer pauses when retake is initiated
  - [ ] Timer resets to 90s when returning from retake
  - [ ] At timer = 0: brief "Time's up — printing your strip" message for 3 seconds, then auto-proceed to Print
- [ ] 3.5.6 "Print Now" button always visible at bottom:
  - [ ] On tap: navigate to Print

### 3.6 Print Screen
- [ ] 3.6.1 Implement print render pipeline:
  - [ ] Take final canvas state (layout + photos + text)
  - [ ] Render to `Bitmap` at 58mm thermal width (384px at 203dpi or 576px at 300dpi — match printer spec)
  - [ ] Convert to monochrome (1-bit or 8-bit grayscale)
  - [ ] This rendered bitmap = `thermal.png` saved to `cacheDir/session_[id]/`
- [ ] 3.6.2 Show "Printing… please wait" screen with simple animation
- [ ] 3.6.3 Call `PrinterManager.print(bitmap)`:
  - [ ] On `PrintResult.SUCCESS`: save print success to session, navigate to Upload screen
  - [ ] On failure: show specific error message per failure type
  - [ ] Show "Retry" button for all failure types
  - [ ] Log failure event to session `error_log`
- [ ] 3.6.4 Implement retry: re-call `PrinterManager.print()` with same bitmap

### 3.7 Upload & QR Share Screen
- [ ] 3.7.1 Implement `MediaProcessor` for on-device GIF assembly:
  - [ ] Load all burst frames from `cacheDir/session_[id]/slot_*/burst/`
  - [ ] Resize each frame to 480px width (maintain aspect ratio)
  - [ ] Reduce to 256-color palette using `AndroidBitmap` color quantization
  - [ ] Assemble GIF using a Kotlin GIF encoder library (e.g., `gifencoder` or ported `AnimatedGifEncoder`)
  - [ ] Target output < 5MB; if over, drop frames or reduce quality
  - [ ] Save to `cacheDir/session_[id]/session.gif`
- [ ] 3.7.2 Generate `color.png`:
  - [ ] Composite all final slot photos into layout at full color, full resolution
  - [ ] Save to `cacheDir/session_[id]/color.png`
- [ ] 3.7.3 Show "Preparing your link…" screen with progress indicator
- [ ] 3.7.4 Implement `UploadManager`:
  - [ ] Upload `thermal.png`, `color.png`, `session.gif` to Supabase Storage under path `sessions/[session_id]/`
  - [ ] Use Supabase Kotlin SDK for upload
  - [ ] On success: create signed URLs (30-min expiry) for all 3 files
  - [ ] Write session record to `sessions` table in Supabase DB
  - [ ] Emit upload progress events
- [ ] 3.7.5 If offline at upload time:
  - [ ] Show "Your link is being prepared — please stay nearby" message
  - [ ] Queue upload in Room `UploadQueueDao`
  - [ ] Register `WorkManager` periodic job to retry when connectivity restored
  - [ ] On WorkManager success: trigger QR generation
- [ ] 3.7.6 QR code generation (only after upload confirmed complete):
  - [ ] Generate QR from URL `https://yourdomain.vercel.app/session/[session_id]`
  - [ ] Use `zxing-android-embedded` or `qrcode-kotlin` library
  - [ ] Display QR code full-screen with download instructions
- [ ] 3.7.7 60-second QR screen countdown:
  - [ ] Show countdown "QR available for Xs" on screen
  - [ ] At 0: navigate to Reset flow → Attract

### 3.8 Session Reset
- [ ] 3.8.1 Implement `SessionCleaner`:
  - [ ] Delete all files under `cacheDir/session_[id]/`
  - [ ] Clear `SessionViewModel` state
  - [ ] Clear Bluetooth session token/challenge state
- [ ] 3.8.2 Verify no customer images remain on device after cleanup
- [ ] 3.8.3 Navigate back to Attract screen

---

## PHASE 4 — BACKEND (SUPABASE)

### 4.1 Database Schema
- [ ] 4.1.1 Create `sessions` table with columns:
  - `session_id UUID PRIMARY KEY`
  - `created_at TIMESTAMPTZ DEFAULT NOW()`
  - `expires_at TIMESTAMPTZ` (= created_at + 24 hours)
  - `storage_urls JSONB` (keys: thermal, color, gif)
  - `location_id TEXT NOT NULL`
  - `coins_inserted INT NOT NULL`
  - `printed BOOLEAN DEFAULT FALSE`
  - `print_failed BOOLEAN DEFAULT FALSE`
  - `error_log JSONB DEFAULT '[]'`
  - `upload_status TEXT CHECK (upload_status IN ('pending','uploaded','failed')) DEFAULT 'pending'`
  - `upload_attempted_at TIMESTAMPTZ`
  - `purged BOOLEAN DEFAULT FALSE`
- [ ] 4.1.2 Create `layouts` table:
  - `id UUID PRIMARY KEY`
  - `name TEXT NOT NULL`
  - `slot_count INT NOT NULL`
  - `frame_asset_url TEXT`
  - `preview_url TEXT`
  - `text_fields JSONB`
  - `version INT NOT NULL`
  - `active BOOLEAN DEFAULT TRUE`
  - `sort_order INT`
  - `created_at TIMESTAMPTZ DEFAULT NOW()`
- [ ] 4.1.3 Create `locations` table:
  - `location_id TEXT PRIMARY KEY`
  - `name TEXT`
  - `created_at TIMESTAMPTZ DEFAULT NOW()`
- [ ] 4.1.4 Enable Row Level Security (RLS) on all tables
- [ ] 4.1.5 Write RLS policies:
  - Sessions: INSERT from anon (device writes its own session), SELECT only for authenticated operators
  - Layouts: SELECT for anon, INSERT/UPDATE/DELETE only for authenticated operators
  - Locations: SELECT for anon, full access for operators

### 4.2 Supabase Storage
- [ ] 4.2.1 Create `sessions` storage bucket (private, not public)
- [ ] 4.2.2 Create `layouts` storage bucket (public read, write for operators only)
- [ ] 4.2.3 Set storage RLS: device can write to `sessions/[session_id]/`, operators can write to `layouts/`
- [ ] 4.2.4 Verify signed URL generation works with 30-minute expiry

### 4.3 Purge Edge Function
- [ ] 4.3.1 Write Supabase Edge Function `purge-expired-sessions`:
  - [ ] Query sessions where `expires_at < NOW()` AND `purged = FALSE`
  - [ ] For each: delete files from storage at `sessions/[session_id]/` (idempotent — no error if missing)
  - [ ] Update record: `purged = TRUE`
- [ ] 4.3.2 Schedule edge function via Supabase cron (every hour)
- [ ] 4.3.3 Test purge function handles missing files gracefully (no exception on 404)

### 4.4 Layout Sync (Device ↔ Supabase)
- [ ] 4.4.1 Implement `LayoutSyncManager` in Android app:
  - [ ] On app launch (if online): fetch all `active = TRUE` layouts from Supabase
  - [ ] After each session (if online): check for layout updates
  - [ ] Compare version numbers; update local Room DB with newer entries
  - [ ] Download new frame assets and preview images to `filesDir/layouts/`
- [ ] 4.4.2 Ensure sync never interrupts an active session (run on background coroutine, guarded by session state check)

### 4.5 In-App Update Endpoint
- [ ] 4.5.1 Create `update.json` hosted on Supabase Storage or Vercel:
  ```json
  { "version": 2, "versionName": "1.1.0", "apkUrl": "https://...", "changelog": "..." }
  ```
- [ ] 4.5.2 Implement `UpdateChecker` in Android app:
  - [ ] Fetch `update.json` on app launch and after each session (if online)
  - [ ] Compare `version` to current `BuildConfig.VERSION_CODE`
  - [ ] If newer: show update modal (version, changelog, Download button)
  - [ ] Download APK to `getExternalFilesDir()`
  - [ ] Trigger system package installer via `FileProvider` + `Intent.ACTION_VIEW`
  - [ ] Dismiss modal via operator PIN or gesture
  - [ ] Re-show modal on next connectivity check if update still pending
  - [ ] Do not show update modal during active customer session

---

## PHASE 5 — SHARE WEB PAGE (VERCEL)

### 5.1 Next.js Project Setup
- [ ] 5.1.1 Create Next.js app (`npx create-next-app@latest pitiq-share`)
- [ ] 5.1.2 Configure for Vercel deployment (no SSR needed, can be static or minimal server)
- [ ] 5.1.3 Set environment variables: `SUPABASE_URL`, `SUPABASE_ANON_KEY`

### 5.2 Session Share Page (`/session/[session-id]`)
- [ ] 5.2.1 On page load: call Supabase to validate session exists and is not purged
- [ ] 5.2.2 If session `expires_at` passed or `purged = TRUE`: show "This link has expired" page
- [ ] 5.2.3 If valid: re-issue signed URLs (30-min expiry) for all 3 files on each page load
- [ ] 5.2.4 Show download options:
  - [ ] "Download Strip (Monochrome)" → `thermal.png`
  - [ ] "Download Original (Color)" → `color.png`
  - [ ] "Download GIF" → `session.gif`
- [ ] 5.2.5 Design: lightweight, mobile-first, matches kiosk brand aesthetic
- [ ] 5.2.6 No login required for customers — session ID in URL is the access token (signed URL handles security)

### 5.3 Deployment
- [ ] 5.3.1 Connect GitHub repo to Vercel
- [ ] 5.3.2 Set production domain
- [ ] 5.3.3 Update `update.json` and app config with final domain

---

## PHASE 6 — OPERATOR DASHBOARD

### 6.1 Dashboard Web App
- [ ] 6.1.1 Create separate web app (Next.js or plain React) for operator dashboard
- [ ] 6.1.2 Implement Supabase Auth login (email + password, operator accounts only)
- [ ] 6.1.3 Protect all dashboard routes with Supabase session middleware

### 6.2 Dashboard Features
- [ ] 6.2.1 **Session Overview:**
  - [ ] Total sessions today / this week / this month (per location)
  - [ ] Revenue tracking (sessions × ₱40, grouped by location and date)
  - [ ] Table of recent sessions with status (uploaded, failed, purged)
- [ ] 6.2.2 **Print Failure Log:**
  - [ ] Table of sessions where `print_failed = TRUE`
  - [ ] Show `error_log` content per session
- [ ] 6.2.3 **Layout Management:**
  - [ ] List all layouts with preview
  - [ ] Upload new layout (frame asset PNG + preview PNG + metadata)
  - [ ] Set slot count, text fields, sort order, active status
  - [ ] Reorder layouts (drag handles)
  - [ ] Deactivate/delete layout
- [ ] 6.2.4 **Device Status:**
  - [ ] Last session timestamp per location_id
  - [ ] Upload status of queued sessions (pending/uploaded/failed)

---

## PHASE 7 — SECURITY HARDENING

### 7.1 Android Security
- [ ] 7.1.1 Verify APK signing is configured and keystore is not committed to repo
- [ ] 7.1.2 Enable `android:allowBackup="false"` in manifest
- [ ] 7.1.3 Set `android:debuggable="false"` in release manifest
- [ ] 7.1.4 Disable `android:allowClearUserData` (or handle via kiosk policy)
- [ ] 7.1.5 Confirm `EncryptedSharedPreferences` used for all sensitive data (location_id, operator PIN, Bluetooth shared secret)
- [ ] 7.1.6 Verify Bluetooth HMAC verification: no raw static string unlock accepted
- [ ] 7.1.7 Verify session files are deleted from `cacheDir` after every session (no residual customer data)

### 7.2 Backend Security
- [ ] 7.2.1 Confirm RLS is enabled on all Supabase tables
- [ ] 7.2.2 Audit RLS policies: anon key cannot read other sessions, cannot write to layouts
- [ ] 7.2.3 Confirm signed URLs have short expiry (30 min) and raw storage paths are never exposed
- [ ] 7.2.4 Confirm purge job deletes files at expiry (24h)
- [ ] 7.2.5 Rotate Supabase `service_role` key if ever exposed; never embed in APK (use `anon` key only)

---

## PHASE 8 — TESTING

### 8.1 Unit Tests (Android)
- [ ] 8.1.1 HMAC verification logic (correct challenge → accepted, tampered → rejected)
- [ ] 8.1.2 Coin total accumulation (various pulse sequences, overpayment)
- [ ] 8.1.3 Session ID generation (UUID v4 format validation)
- [ ] 8.1.4 GIF compression: output < 5MB for typical burst sequence
- [ ] 8.1.5 Layout merge logic (remote overrides default by ID)
- [ ] 8.1.6 Upload queue: session queued when offline, dequeued when online

### 8.2 Integration Tests
- [ ] 8.2.1 Full session flow on-device (coin → layout → capture → edit → print → upload → QR)
- [ ] 8.2.2 Offline flow: complete session with printer, verify upload queued, simulate reconnect, verify upload completes and QR appears
- [ ] 8.2.3 Printer not connected on start → attract screen Out of Service → reconnect → attract resumes
- [ ] 8.2.4 Bluetooth disconnect during payment → reconnect within 30s → session continues with buffered coins
- [ ] 8.2.5 Bluetooth disconnect during payment → no reconnect → session cancelled → returns to Attract
- [ ] 8.2.6 Payment screen 90s idle timeout → session cancelled → returns to Attract
- [ ] 8.2.7 Edit timer reaches 0 → auto-print → "Time's up" message shown for 3s
- [ ] 8.2.8 Retake: timer pauses, resets to 90s on return, retake button disabled after use
- [ ] 8.2.9 QR screen 60s timeout → returns to Attract

### 8.3 Purge / Expiry Tests
- [ ] 8.3.1 Trigger purge edge function manually → verify files deleted → verify `purged = TRUE`
- [ ] 8.3.2 Purge on already-deleted files → verify no error (idempotent)
- [ ] 8.3.3 Share page with expired session ID → "link expired" shown
- [ ] 8.3.4 Share page with valid session → all 3 download links work

---

## PHASE 9 — DEPLOYMENT

### 9.1 Backend Deploy
- [ ] 9.1.1 Apply Supabase migrations (`supabase db push`)
- [ ] 9.1.2 Deploy edge function (`supabase functions deploy purge-expired-sessions`)
- [ ] 9.1.3 Confirm cron schedule is active
- [ ] 9.1.4 Create operator Supabase Auth account for dashboard

### 9.2 Web Deploy
- [ ] 9.2.1 Deploy share page to Vercel, set production domain
- [ ] 9.2.2 Deploy operator dashboard to Vercel (separate project), set auth env vars

### 9.3 Device Deploy
- [ ] 9.3.1 Build signed release APK
- [ ] 9.3.2 Sideload APK to Oppo Reno 5 5G
- [ ] 9.3.3 Provision device as Device Owner (`adb shell dpm set-device-owner ...`)
- [ ] 9.3.4 Run through operator setup screen: enter location_id
- [ ] 9.3.5 Verify kiosk mode locks correctly (no home button, no notifications)
- [ ] 9.3.6 Test full session flow end-to-end on deployed device with ESP32 and printer

### 9.4 First APK in Update Endpoint
- [ ] 9.4.1 Upload signed APK to Supabase Storage
- [ ] 9.4.2 Publish `update.json` with correct version and APK URL
- [ ] 9.4.3 Test in-app update flow on device

---

## NOTES

- All monetary values: use integer cents (₱1 = 100 units) or integer ₱ as appropriate; be consistent across Bluetooth protocol, session record, and UI.
- Thermal printer exact VID/PID must be confirmed against physical printer before Phase 2.2. USB Host support must be verified on each new device before deployment.
- Final Vercel domain replaces `yourdomain.vercel.app` everywhere before Phase 5 deployment.
- ESP32 firmware (HMAC shared secret, coin pulse mapping, buffer logic) is out of scope for this roadmap but is a hard dependency for Phase 2.1 integration tests.
- Multi-device compatibility: `minSdk = 23` (Android 6.0). Bluetooth permissions must be requested from the correct set based on `Build.VERSION.SDK_INT`. Device Owner provisioning must be tested on every new device model before deployment. USB Host is a hardware capability — not guaranteed on all phones; tablets typically support it.
- When deploying to tablets: re-test all session screens with `WindowSizeClass` adaptive layouts. Verify kiosk lock task behavior on that tablet's OEM firmware before installing in a cafe.

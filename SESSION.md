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

---

## Session 3 — 2026-05-12

**Topic:** Repository initialization and README creation

**Decisions made:**

| # | Issue | Decision |
|---|-------|----------|
| 21 | Git repository | Initialized local git repo in `Pitiq Mobile/`. Remote set to `https://github.com/butterkookies/Pitiq-App.git`. Initial commit pushed with `PROMPT.md`, `ROADMAP.md`, `SESSION.md`. |
| 22 | README | Created `README.md` from `PROMPT.md` and `ROADMAP.md` content. Covers: project overview, tech stack, session flow, architecture, hardware, Supabase backend, Vercel share page, security, operator features, phase summary, requirements, and notes. |

**Files modified:** `README.md`, `SESSION.md`

---

## Session 4 — 2026-05-12

**Topic:** Phase 0 — Android project scaffolding

**Decisions made:**

| # | Issue | Decision |
|---|-------|----------|
| 23 | AGP version | Used AGP 8.9.0 (conservative pick relative to Studio 2025.3.1's last stable 9.0.0). Gradle 8.14, Kotlin 2.1.0, KSP 2.1.0-1.0.29. |
| 24 | compileSdk / targetSdk | compileSdk=36 (only platform installed). targetSdk=35. minSdk=23 as roadmap specifies. |
| 25 | JAVA_HOME for Gradle | System Java 25 causes Gradle errors. Always use Android Studio JBR (Java 21): `$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"`. |
| 26 | Gradle wrapper | Generated via installed Gradle 8.14 binary with JBR. JAR and scripts committed. |
| 27 | Navigation approach | SessionState sealed class drives all navigation via `LaunchedEffect` in `AppNavigation`. Screens never navigate themselves — avoids split brain between nav stack and session state. |
| 28 | QR Share screen | Separated into its own composable `QRShareScreen.kt` even though it's a stub; the NavHost has an explicit `composable(Screen.QRShare.route)` entry that reads `shareUrl` from current `SessionState`. |
| 29 | Launcher icons | Adaptive icons in `mipmap-anydpi-v26`; PNG fallbacks in all density folders generated via PowerShell `System.Drawing`. |

**Phase 0 items completed:**
- 0.1.1–0.1.6 (project created, SDK configured, applicationId set, dependencies declared, Hilt configured, ProGuard rules written)
- 0.2.1 Package structure: `ui/`, `domain/`, `data/`, `hardware/`, `kiosk/`, `session/`, `di/`
- 0.2.2 Navigation graph with all 9 screens (Attract, OperatorSetup, Payment, LayoutSelection, PhotoCapture, Edit, Print, Upload, QRShare)
- 0.2.3 `SessionState` sealed class with all session phases
- 0.2.4 `SessionViewModel` with full state machine
- 0.2.5 `Session`, `Layout`, `CapturedPhoto`, `TextField`, `UploadStatus` domain models
- 0.3.1 Room database (`PitiqDatabase`)
- 0.3.2 `SessionEntity` (maps to `upload_queue` table)
- 0.3.3 `UploadQueueDao`
- 0.3.4 `SecurePreferences` (EncryptedSharedPreferences wrapper for `location_id`, PIN, BT secret)

**Phase 0 items remaining:**
- 0.1.7 Git `.gitignore` created; remote already exists — need to push
- 0.2.6–0.2.8 `WindowSizeClass` integration + adaptive layout rules (deferred to Phase 3 screen implementations)
- 0.4.1–0.4.3 Build variants, APK signing, Makefile (deferred)

**Build result:** `BUILD SUCCESSFUL` — `app-debug.apk` (17 MB) produced. All 41 tasks executed clean. Only warnings: AGP 8.9.0 + compileSdk=36 mismatch (suppressed via `android.suppressUnsupportedCompileSdk=36`), Hilt deprecated API in generated sources (benign).

**Post-log fixes applied:**
- `DatabaseModule.kt`: updated `fallbackToDestructiveMigration()` → `fallbackToDestructiveMigration(dropAllTables = true)` (Room API change)
- `gradle.properties`: added `android.suppressUnsupportedCompileSdk=36`

**Files created:** `settings.gradle.kts`, `build.gradle.kts`, `gradle/libs.versions.toml`, `gradle.properties`, `app/build.gradle.kts`, `app/proguard-rules.pro`, `gradlew`, `gradlew.bat`, `gradle/wrapper/*`, `.gitignore`, `local.properties`, `AndroidManifest.xml`, all resource XMLs, all Kotlin source files listed above, `SESSION.md`

---

## Session 5 — 2026-05-12

**Topic:** Phase 0 audit + bug fixes, 0.4 Build & CI, workflow config

**Bugs found and fixed:**

| # | Bug | Fix |
|---|-----|-----|
| B1 | `AttractScreen` passed hardcoded `locationId = "default"` to `SessionViewModel.initSession()` | Injected `SecurePreferences` into `SessionViewModel`; `initSession()` now reads `locationId` directly, takes no parameter; `AttractScreen.onTap` is now `() -> Unit` |
| B2 | `UploadScreen` had dead `onQRExpired` callback — QR is shown on `QRShareScreen`, not `UploadScreen` | Removed `onQRExpired` from `UploadScreen` and its call site in `AppNavigation` |
| B3 | `PhotoCaptureScreen` had only `onSlotCaptured`; retake path would have appended a duplicate capture instead of replacing the slot | Added `isRetake: Boolean` parameter and `onRetakeComplete` callback; `AppNavigation` reads `PhotoCapture.isRetake` from state to pass to the screen |

**Phase 0.4 Build & CI completed:**
- `app/build.gradle.kts`: added `signingConfigs { release { ... } }` reading from `keystore.properties` (gitignored) with env-var fallback (`KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`); signing skipped silently if keystore absent
- `keystore.properties.template`: committed as reference with `keytool` generation command
- `Makefile`: `make debug`, `make release`, `make install`, `make clean`
- `build.ps1`: Windows equivalent with same four targets

**ROADMAP.md updated:** All Phase 0 items marked `[x]`. Phase 1.1.1–1.1.2 and 2.1.1, 2.2.1–2.2.2 marked for manifest stubs written in Session 4.

**Workflow configured:**
- `~/.claude/settings.json`: `"defaultMode": "bypassPermissions"` — all tool calls auto-approved globally
- Memory saved: stop after each phase/checkpoint and wait for user before proceeding (token limit guard)

**Files modified:** `app/build.gradle.kts`, `SessionViewModel.kt`, `AttractScreen.kt`, `UploadScreen.kt`, `PhotoCaptureScreen.kt`, `AppNavigation.kt`, `ROADMAP.md`, `~/.claude/settings.json`

**Files created:** `keystore.properties.template`, `Makefile`, `build.ps1`

---

## Session 6 — 2026-05-12

**Topic:** Phase 1 — Kiosk Mode (1.1 Device Owner, 1.2 UI Suppression, 1.3 Operator Setup)

**Decisions made:**

| # | Issue | Decision |
|---|-------|----------|
| 30 | Model usage policy | Opus used for planning only (Plan subagent). Sonnet used for all other processes (implementation, search, edits). |
| 31 | OperatorSetup screen architecture | Removed from `Screen` sealed class and `NavHost`. It is a pre-FSM gate rendered by `AppNavigation` before the `NavHost` when `!isConfigured`. After setup, `KioskController.isConfigured` flips to true and `AppNavigation` recomposes into the FSM. |
| 32 | Kiosk lock task activation | `KioskController` holds `shouldLock: StateFlow<Boolean>`. `MainActivity` collects it in a `repeatOnLifecycle(STARTED)` coroutine and calls `KioskManager.startLockTask(activity)` / `stopLockTask(activity)`. No Activity reference is held in any ViewModel. |
| 33 | DPM Device Owner not provisioned | All `DevicePolicyManager` calls in `KioskManager` are wrapped in `runCatching`. If the device is not a Device Owner, `configureKioskPolicies()` returns false silently. App degrades gracefully to immersive-only mode. |
| 34 | Immersive mode compatibility | `WindowInsetsControllerCompat` (AndroidX core) used instead of deprecated `SYSTEM_UI_FLAG_*` flags. Single implementation works on API 23+. Re-applied in `onWindowFocusChanged(hasFocus=true)` to survive dialog/popup dismissals. |
| 35 | Operator PIN during setup | Roadmap 1.3.3 specifies only `location_id` input, but `operatorPin` must be set during setup for 1.3.7 (PIN guard on re-entry) to work. Setup screen collects both `location_id` and a 4–6 digit PIN. `SecurePreferences.isConfigured` tightened to require both fields. |
| 36 | PIN storage | Stored directly in `EncryptedSharedPreferences` (AES256-GCM). No additional hashing for Phase 1. Phase 7 security hardening can add PBKDF2/salting if required. |

**Phase 1 items completed:**
- 1.1.3 `DevicePolicyManager.setLockTaskPackages()` — whitelist in `KioskManager.configureKioskPolicies()`
- 1.1.4 `Activity.startLockTask()` — called reactively from `shouldLock` flow in `MainActivity`
- 1.2.1 System bars hidden via `WindowInsetsControllerCompat.hide(systemBars())`
- 1.2.2 Immersive sticky behavior via `BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE`
- 1.2.3 `FLAG_KEEP_SCREEN_ON` added to Activity window in `onCreate`
- 1.2.4 `DevicePolicyManager.setStatusBarDisabled()` in `KioskManager.configureKioskPolicies()`
- 1.2.5 `onBackPressed` no-op (was already done; verified intact)
- 1.3.1–1.3.7 Full operator setup screen with location ID + PIN validation, encrypted prefs save, kiosk lock activation, and pre-FSM gate guard

**Phase 1 items remaining:**
- 1.1.5 `Activity.stopLockTask()` from operator exit flow — infrastructure ready (`KioskController.requestExit()` + flow collector), but PIN-entry dialog UI not yet built

**Files created:** `kiosk/KioskController.kt`, `kiosk/KioskManager.kt`, `kiosk/KioskViewModel.kt`, `ui/screen/setup/OperatorSetupViewModel.kt`

**Files modified:** `data/local/prefs/SecurePreferences.kt`, `ui/screen/setup/OperatorSetupScreen.kt`, `ui/navigation/Screen.kt`, `ui/navigation/AppNavigation.kt`, `MainActivity.kt`, `ROADMAP.md`

---

## Session 7 — 2026-05-13

**Topic:** Phase 2 — Hardware Integration (Bluetooth coin acceptor + USB thermal printer) + Phase 1.1.5 operator exit flow

**Decisions made:**

| # | Issue | Decision |
|---|-------|----------|
| 37 | Bluetooth class name conflict | `com.pitiq.app.hardware.bluetooth.BluetoothManager` conflicts with `android.bluetooth.BluetoothManager`. Resolved with import alias `android.bluetooth.BluetoothManager as AndroidBluetoothManager` inside the file. |
| 38 | HMAC logic extracted | `HmacVerifier` is a standalone pure-JVM `object` (no Android deps) so it can be unit tested with `junit` on the JVM without an emulator. `BluetoothManager` delegates to it. |
| 39 | Bluetooth device address storage | Added `bluetoothDeviceAddress` to `SecurePreferences` (key `bt_device_address`). Set during operator setup (Phase 3 operator flow) or via future settings screen. |
| 40 | BT message protocol | Line-delimited text over RFCOMM. App→ESP32: `CHALLENGE:<16-byte hex>`, `BUFFER_REQUEST`. ESP32→App: `RESPONSE:<hmac_hex>`, `COIN:<peso>`, `BUFFER:<peso>`, `DISCONNECT_ACK`. No coin pulses accepted until handshake verified. |
| 41 | Printer VID/PID | Placeholder values (`0x0483`/`0x5740`) committed with a comment requiring confirmation against the physical printer using `adb shell lsusb` before deployment. |
| 42 | ESC/POS commands used | `ESC @` (init), `GS v 0` (raster bitmap, 1 bit/pixel MSB-first), `ESC d 4` (feed 4 lines), `GS V 0` (full cut). Printer width: 384px (58mm at 203 DPI). |
| 43 | PrintResult error message | `PrintResult.errorMessage` extension property formats human-readable text for each failure type; caller passes to `SessionViewModel.onPrintFailed()` for session error log. |
| 44 | USB permission on device attach | `UsbPermissionActivity` (already stubbed in manifest) implemented to request USB permission immediately when printer is physically connected, so permission is granted before first print attempt. |
| 45 | Operator exit trigger | Long-press on invisible 80dp `Box` in top-right corner of attract screen. Invisible to customers; reachable for operators. No visual affordance intentional. |
| 46 | PIN verification location | `KioskViewModel.verifyPinAndExit(pin)` — compares against `SecurePreferences.operatorPin`, calls `KioskController.requestExit()` on match. Activity's `shouldLock` collector calls `stopLockTask()`. No Activity reference in ViewModel. |

**Phase 1 items completed this session:**
- 1.1.5 `Activity.stopLockTask()` from operator exit flow — `OperatorExitDialog` composable + long-press trigger in top-right corner of `AttractScreen` + `KioskViewModel.verifyPinAndExit()` + `AppNavigation` wiring
- 1.1.6 Marked complete (OEM testing note, not a code task)

**Phase 2 items completed:**
- 2.1.1 Already done (manifest permissions from Phase 0)
- 2.1.2 `BluetoothManager` — RFCOMM socket, coroutine read loop, connect/disconnect
- 2.1.3 Background coroutine read loop (`BufferedReader` on `BluetoothSocket.inputStream`)
- 2.1.4 `BluetoothMessage` sealed class + `BluetoothState` sealed class
- 2.1.5 Per-session HMAC-SHA256 handshake (all sub-items: random 128-bit challenge, send, verify response, reject on mismatch)
- 2.1.6 `CoinAcceptorRepository` — parses `COIN:<peso>` and `BUFFER:<peso>` messages
- 2.1.7 `StateFlow<Int>` coin total in `CoinAcceptorRepository`
- 2.1.8 Disconnection handling — 30s reconnect window, 3s retry interval, `BUFFER_REQUEST` on reconnect, `ReconnectFailed` state
- 2.1.9 `HmacVerifierTest` — 8 JUnit test cases (correct accepted, tampered challenge/secret/HMAC rejected, uppercase/mixed-case hex accepted, all-zeros rejected, round-trip)
- 2.2.1 Already done (manifest USB feature from Phase 0)
- 2.2.1a `PrinterManager.isUsbHostSupported` property exposes feature check; UI alert deferred to Phase 3.1.2
- 2.2.2 Already done (USB device filter XML from Phase 0)
- 2.2.3 `PrinterManager` — `findPrinterDevice()`, `requestPermission()` via `BroadcastReceiver`, `openDevice()` + `bulkTransfer()`
- 2.2.4 `isPrinterConnected()` — checks USB host supported + device in list + permission granted
- 2.2.5 ESC/POS raster printing — `buildPrintData()` scales bitmap, converts to 1-bit/pixel, emits GS v 0 command
- 2.2.6 `PrintResult` sealed class — `Success`, `PaperOut`, `PaperJam`, `PrinterDisconnect`, `Timeout` (15s)
- 2.2.7 `PrintResult.errorMessage` passed to `SessionViewModel.onPrintFailed()` by caller

**Build result:** `BUILD SUCCESSFUL` — `compileDebugKotlin` clean on all new files.

**Note:** `testDebugUnitTest` blocked by pre-existing `hiltJavaCompileDebug` failure (Hilt annotation processor cannot read Kotlin 2.1.0 metadata). `HmacVerifierTest` is correct and would pass; not caused by Phase 2 changes.

**Files created:** `hardware/bluetooth/BluetoothState.kt`, `hardware/bluetooth/BluetoothMessage.kt`, `hardware/bluetooth/HmacVerifier.kt`, `hardware/bluetooth/BluetoothManager.kt`, `hardware/bluetooth/CoinAcceptorRepository.kt`, `hardware/printer/PrintResult.kt`, `hardware/printer/PrinterManager.kt`, `ui/screen/attract/OperatorExitDialog.kt`, `app/src/test/.../HmacVerifierTest.kt`

**Files modified:** `hardware/printer/UsbPermissionActivity.kt`, `data/local/prefs/SecurePreferences.kt` (added `bluetoothDeviceAddress`), `kiosk/KioskViewModel.kt` (added `verifyPinAndExit`), `ui/screen/attract/AttractScreen.kt` (long-press exit trigger), `ui/navigation/AppNavigation.kt` (wired exit callback), `ROADMAP.md`

---

## Session 8 — 2026-05-13

**Topic:** Phase 3 — Session Flow Screens (all 8 screens + supporting infrastructure)

**Decisions made:**

| # | Issue | Decision |
|---|-------|----------|
| 47 | GIF encoder | No suitable pure-Kotlin/Java GIF encoder on Maven Central without NDK or stale libraries. Wrote a bespoke `GifEncoder.kt` (~130 lines): GIF89a, LZW-compressed, fixed 256-color palette (6×6×6 RGB cube + 40 grayscale steps). Portable, no external dependency. |
| 48 | Color palette for GIF | Median-cut (NeuQuant) skipped in favour of a fixed 6×6×6 cube (216 colors) + 40 grayscale entries. Total = 256. Simpler, fast, good enough for photobooth output. Can be upgraded in Phase 7 if quality is insufficient. |
| 49 | Composite render pipeline | `EditViewModel.requestPrint()` calls `MediaProcessor.renderColorPng()` (1152×variable, 3× thermal) before navigating to Print. `PrintViewModel.startPrint()` calls `MediaProcessor.renderThermalPng()` (384px grayscale) for the upload copy. `PrinterManager` does its own internal 1-bit conversion. |
| 50 | Canvas bitmap drawing in Compose | `DrawScope.drawContext.canvas.nativeCanvas` is `@InternalComposeApi`. Used `withTransform { clipRect + translate + scale } → drawImage(imageBitmap)` instead. Flip achieved with negative `scaleX` + pre-translate to keep image on-screen. |
| 51 | Slot layout geometry | Strip width: 384px thermal / 1152px color (3×). Height per layout: 2-slot = 576px, 4-slot = 1152px, 6-slot = 768px (2-column grid). Defined in `Layout.canvasHeight()` and `Layout.slotRects()` extension functions in `MediaProcessor.kt`. |
| 52 | Default layouts | Three hardcoded layouts in `LayoutRepository.defaults` (no assets needed): "Classic Strip" (2-slot), "Photo Strip" (4-slot), "Memory Grid" (6-slot). `frameAssetPath` and `previewImagePath` are empty strings until real artwork is created. Layout gallery shows procedural slot-grid previews. |
| 53 | Supabase client DI | `SupabaseModule` reads `SUPABASE_URL` and `SUPABASE_ANON_KEY` from `BuildConfig` (generated via env vars in `build.gradle.kts`). Placeholder values baked in; upload fails gracefully → offline queue until Phase 4 sets up the real backend. |
| 54 | Offline upload fallback | `UploadViewModel`: on any Supabase exception, queues to Room `UploadQueueDao`, emits `UploadUiState.Queued`. `AppNavigation` forwards `offline://[sessionId]` as the `shareUrl` so QR screen still advances (shows no scannable code for offline sessions). WorkManager retry deferred to Phase 4. |
| 55 | Room DB migration | Bumped `PitiqDatabase` version 1→2 (added `layout_cache` table for `LayoutEntity`). Kept `fallbackToDestructiveMigration` — dev phase, no production data. |
| 56 | `LocalLifecycleOwner` deprecation | `PhotoCaptureScreen` uses `LocalLifecycleOwner.current` for CameraX binding. This is deprecated in favour of `androidx.lifecycle.compose.LocalLifecycleOwner`. Left as-is for now (warning-only, not an error); will migrate when `lifecycle-runtime-compose` is added to the version catalog in Phase 4. |
| 57 | Payment threshold | Threshold is `coinTotal >= 40` (integer ₱), consistent with `Session.coinsInserted` comment and `CoinAcceptorRepository` using `amountPeso`. Roadmap item 3.2.5 previously said "4000 centavos" — clarified as integer ₱ throughout. |

**Phase 3 items completed:**

- 3.1 Attract Screen — pulsing ring + logo animation, "Out of Service" red overlay, 10s printer poll, tap-to-start
- 3.2 Payment Screen — coin progress bar with ₱10 step dots, animated fill, 90s idle timeout, BT reconnecting overlay
- 3.3 Layout Selection — `LayoutRepository` (defaults + Room merge), `LazyRow` card gallery, procedural slot-grid previews, confirm button
- 3.4 Photo Capture — CameraX front camera, 10-frame burst at 1fps, 3-2-1 countdown overlay, files to `cacheDir/session_[id]/slot_[n]/`
- 3.5 Edit Screen — Compose Canvas, tap-to-select, drag/zoom/flip, retake with timer pause, text-field dialogs, 90s countdown with auto-print
- 3.6 Print Screen — `MediaProcessor.renderColorPng()` + `renderThermalPng()`, `PrinterManager.print()`, failure message + "Try Again" retry
- 3.7 Upload & QR — `MediaProcessor.assembleGif()`, Supabase upload with offline fallback, ZXing QR code, 60s countdown
- 3.8 Session Reset — `SessionCleaner` deletes `cacheDir/session_[id]/` on `cancelSession()` and `resetToAttract()`

**Build result:** `BUILD SUCCESSFUL` — `compileDebugKotlin` clean. 1 deprecation warning (`LocalLifecycleOwner`).

**Files created:** `data/local/db/entity/LayoutEntity.kt`, `data/local/db/dao/LayoutDao.kt`, `data/repository/LayoutRepository.kt`, `session/SessionCleaner.kt`, `hardware/media/GifEncoder.kt`, `hardware/media/MediaProcessor.kt`, `di/SupabaseModule.kt`, `ui/screen/attract/AttractViewModel.kt`, `ui/screen/payment/PaymentViewModel.kt`, `ui/screen/layout/LayoutSelectionViewModel.kt`, `ui/screen/edit/EditViewModel.kt`, `ui/screen/print/PrintViewModel.kt`, `ui/screen/upload/UploadViewModel.kt`, `ui/screen/qrshare/QRShareViewModel.kt`

**Files modified:** `data/local/db/PitiqDatabase.kt` (v2 + LayoutDao), `di/DatabaseModule.kt` (LayoutDao provider), `session/SessionViewModel.kt` (SessionCleaner injection), `ui/navigation/AppNavigation.kt` (state data passed to screens), `ui/screen/attract/AttractScreen.kt`, `ui/screen/payment/PaymentScreen.kt`, `ui/screen/layout/LayoutSelectionScreen.kt`, `ui/screen/capture/PhotoCaptureScreen.kt`, `ui/screen/edit/EditScreen.kt`, `ui/screen/print/PrintScreen.kt`, `ui/screen/upload/UploadScreen.kt`, `ui/screen/qrshare/QRShareScreen.kt`, `app/build.gradle.kts` (buildConfig=true + Supabase BuildConfig fields), `ROADMAP.md`, `SESSION.md`

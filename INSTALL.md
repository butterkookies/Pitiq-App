# Pitiq — Sideload Guide (Android / Oppo)

This guide installs the Pitiq debug APK directly on an Android phone without Google Play.
Tested on **Oppo Reno 5 5G (ColorOS 11)**. Works on any Android 6.0+ device.

---

## What to expect without hardware

The app launches fully, but shows an **"OUT OF SERVICE — PRINTER NOT DETECTED"** banner
on the attract screen. This is normal — the thermal printer and coin acceptor are not
connected yet. The banner disappears automatically once the printer is plugged in.

---

## Step 1 — Download the APK

Go to the [latest release](../../releases/latest) and download **`pitiq-debug.apk`**.

Or open this link directly on your phone's browser.

---

## Step 2 — Allow installs from unknown sources (Oppo / ColorOS)

ColorOS blocks installs from outside the App Market by default.

1. Open **Settings**
2. Go to **Additional Settings → Safety & Privacy** (or **Privacy & Security**)
3. Tap **Install Unknown Apps** (or **Special App Access → Install Unknown Apps**)
4. Find your browser (e.g. Chrome) in the list
5. Toggle **Allow from this source** on

> On older ColorOS (11 and below): Settings → Additional Settings →
> Safety and Privacy → Unknown Sources → turn on.

---

## Step 3 — Install the APK

1. Open **Files** (or your file manager) and find the downloaded `pitiq-debug.apk`
2. Tap the file
3. Tap **Install** on the prompt that appears
4. If you see "App not installed" — repeat Step 2 and make sure the correct browser
   or file manager is allowed
5. Tap **Done** or **Open**

---

## Step 4 — First launch

1. Open **Pitiq** from the app drawer
2. The **Operator Setup screen** appears on first run — enter any location ID
   (e.g. `cafe-01`) and tap Confirm
3. The app switches to kiosk mode and shows the attract screen
4. You will see the red **OUT OF SERVICE** banner — this is expected without hardware

---

## Exiting kiosk mode (for testing)

The app locks the screen during a session. To exit:

1. **Long-press the top-right corner** of the attract screen for ~2 seconds
2. Enter the operator PIN (default set during first run — if you did not set one,
   it is stored in the app's encrypted storage; reinstall to reset)

> If you get stuck in kiosk mode during testing, you can force-stop the app
> from another device via ADB: `adb shell am force-stop com.pitiq.app.debug`

---

## Reinstall / reset

To wipe all settings and start fresh, uninstall the app and reinstall.
All data (location ID, PIN, Bluetooth secret) is stored in encrypted storage
and is wiped on uninstall.

---

## Build info

| Field | Value |
|-------|-------|
| Build type | Debug |
| Application ID | `com.pitiq.app.debug` |
| Version | 0.1.0 |
| Min Android | 6.0 (API 23) |
| Target Android | 14 (API 35) |

---

## Troubleshooting

| Problem | Fix |
|---------|-----|
| "App not installed" | Allow unknown sources for your file manager / browser (Step 2) |
| App crashes on launch | Make sure Android version is 6.0 or newer |
| Stuck in kiosk mode | ADB force-stop: `adb shell am force-stop com.pitiq.app.debug` |
| Operator PIN forgotten | Uninstall and reinstall — resets all settings |

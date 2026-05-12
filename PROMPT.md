Here is the complete and updated prompt:

---

You are building an Android photobooth application for a coin-operated machine deployed in cafes. The app runs on an Oppo Reno 5 5G in kiosk mode. It communicates with an ESP32 microcontroller via Bluetooth Serial, which handles coin acceptance through a CH-926 coin acceptor. A 58mm monochrome thermal printer is connected via USB OTG. The app must work fully offline for all core session functions, and connect online only for monitoring, analytics, remote content updates, and temporary media delivery. The backend is Supabase (PostgreSQL + Supabase Storage + Supabase Auth). The share web page is hosted on Vercel.

---

**SESSION FLOW**

**IDLE / ATTRACT STATE**

The machine starts and always returns to an attract screen after every completed or abandoned session. The attract screen displays a looping animation or visual that draws attention in a cafe environment. It shows the price (₱40 per session) and a prompt to tap the screen to begin. No session state exists at this point. The screen must never show a blank or static UI while waiting for a customer.

Before the attract screen becomes interactive, the app performs a background USB printer connectivity check. If the printer is not detected, session initialization is disabled and a non-customer-facing operator alert is displayed on screen (e.g., a persistent banner or overlay visible only to an operator who can reach the device). The attract screen enters a passive "Out of Service" state until the printer is reconnected and detected. This prevents customers from paying and then failing at the print step.

**SESSION INITIALIZATION**

When the customer taps the attract screen, a new session is initialized entirely on-device. A cryptographically random session ID (UUID v4) is generated locally. No internet connection is required for this step. The session ID will be used to track all assets and records for this session throughout its lifecycle. The app immediately transitions to the payment screen.

**PAYMENT**

The payment screen appears immediately after session initialization. A clear notice is displayed stating that the machine does not give change. A coin progress indicator updates in real time as the ESP32 sends coin pulse signals via Bluetooth (e.g., ₱10 → ₱20 → ₱30 → ₱40). The ESP32 and app use a per-session rolling token or challenge-response handshake to prevent Bluetooth signal replay attacks. A raw static string unlock is explicitly not acceptable. When the accumulated total reaches ₱40, the session is unlocked and the app advances to layout selection. Overpayment is accepted silently.

If the Bluetooth connection to the ESP32 is lost during the payment phase, the app pauses the 90-second timeout and displays a "Reconnecting…" status indicator. The ESP32 buffers the accumulated coin total and transmits it upon successful reconnection. If reconnection is not restored within 30 seconds, the session is cancelled and the machine returns to the attract screen. The coin total at that point is not recoverable in software.

If no coins are inserted within 90 seconds of the payment screen appearing (and Bluetooth remains connected), the session is cancelled and the machine returns to the attract screen.

**LAYOUT SELECTION**

After payment is confirmed, the customer is presented with a scrollable or swipeable gallery of available photo strip layouts. Each layout shows a preview of how the final printed strip will look, including the number of photo slots, borders, and decorative elements. A set of default layouts is bundled with the APK and available from first boot, so the machine is operational without any prior sync. Remote layout updates from the operator supplement and override these defaults but are never required for the machine to function. Layouts are remotely updatable by the operator via Supabase-backed sync, so new layouts can be pushed without releasing a new APK. The customer selects and confirms one layout. Once confirmed, the layout is locked to the session and the app advances to photo capture.

**PHOTO CAPTURE**

The camera view opens. The chosen layout is shown as a semi-transparent overlay on top of the live camera feed so the customer can frame themselves correctly before each shot. The number of captures is determined by the layout (e.g., a 3-slot strip requires 3 photos).

Each capture slot has a 10-second window total. The first 7 seconds allow the customer to pose freely with no pressure. The final 3 seconds show a visible countdown (3, 2, 1) flashed prominently on screen. Throughout the full 10-second window, the app captures a burst of 5 to 8 frames from the camera. The final captured photo for each slot is the last frame taken when the countdown reaches 0. All burst frames are stored temporarily on-device for later GIF assembly. After all required photos are taken, the session advances automatically to the edit phase.

**EDIT PHASE**

The customer sees the complete layout with all their photos placed inside it, exactly as it will appear when printed. This is the live editing canvas. The layout frame, decorative elements, and any text areas are rendered together with the photos so the customer always sees the true final output.

Per photo slot, the customer can:
- Tap a slot to select it.
- Flip the photo horizontally.
- Drag to reposition the photo within the slot.
- Pinch to zoom in or out within the slot boundary.
- Use one retake for that slot. The retake option is labeled clearly as "Retake (1 remaining)" and after it is used it shows "Retake used" and is disabled. The retake re-opens the camera for that slot only, with the same 10-second countdown and burst capture behavior as the original capture.

If the customer initiates a retake, the 90-second edit timer pauses immediately and resets to 90 seconds when the customer returns from the retake camera view to the edit canvas.

If the layout includes designated text fields (such as a name field or date stamp), those fields are shown inline on the canvas and are editable by tapping them, which opens a soft keyboard input.

A countdown timer of 90 seconds is shown prominently throughout the edit phase. If the timer reaches zero, the app automatically confirms the current state of all edits and proceeds to print without requiring any additional button press. A brief 3-second on-screen message ("Time's up — printing your strip") is shown before printing begins so the customer is not startled. If the customer finishes before the timer expires, a "Print Now" button is always visible at the bottom of the edit screen.

**PRINT**

The final layout is rendered to a 58mm-width monochrome bitmap optimized for thermal printing. This render is the definitive print file. The app sends it to the thermal printer via USB. The screen shows a "Printing… please wait" state with a simple animation. On success, the app begins uploading session media and advances to the upload waiting screen. On failure (paper jam, paper out, printer disconnect, or timeout), the screen shows a specific error message and a retry option. All print failures are logged to the backend session record as an event.

**UPLOAD AND QR SHARE SCREEN**

After a successful print, the app displays a "Preparing your link…" screen with a progress indicator while it uploads session media to Supabase Storage and generates signed URLs. The QR code is shown to the customer only after the upload is confirmed complete and the session share page is ready to serve. This ensures the customer never scans a QR code that leads to an unavailable page.

If the device is offline at the time of upload, the "Preparing your link…" screen remains visible and the app queues the upload, retrying automatically when connectivity is restored. A note informs the customer that their link is being prepared and to stay nearby. Once upload succeeds, the QR code appears.

The QR code encodes a unique, signed, expiring session URL in the format: yourdomain.vercel.app/session/[session-id]. The customer scans this with their personal phone. The session URL opens a lightweight web page (not an app) where the customer can download:
- A thermal-style PNG (monochrome, strip format, mimicking the printed output).
- A full-color original PNG (from the raw captures, full quality).
- A GIF assembled from the burst frames captured during the photo capture phase, showing the session as a fast-paced replay.

The QR screen is displayed for 60 seconds after appearing. After 60 seconds, the machine automatically resets to the attract screen regardless of whether the customer has scanned the code. The session URL remains valid for 24 hours from the time of creation. After 24 hours, all associated files are permanently and automatically deleted. The session metadata record in the database is retained but marked as purged.

**RESET**

After the QR screen timeout, the app clears all local session state and temporary files from device storage. No customer images, videos, or personal data persist on the device after a session ends. The machine returns to the attract screen ready for the next customer.

---

**MEDIA AND STORAGE ARCHITECTURE**

During a session, all raw captures, burst frames, and rendered assets are stored only in temporary local device storage. On session completion (after successful print), the app uploads the following to Supabase Storage under a path keyed to the session ID:
- thermal.png — the monochrome strip render.
- color.png — the full-color composite.
- session.gif — assembled on-device from burst frames, resized to 480px width, with a 256-color palette, targeting under 5MB. Compression is applied before upload.

The Supabase database records a sessions table with the following fields: session_id, created_at, expires_at (created_at plus 24 hours), storage_urls, location_id, coins_inserted, printed (boolean), print_failed (boolean), error_log, upload_status (enum: pending / uploaded / failed), upload_attempted_at, and purged (boolean).

If the device remains offline past the session's expires_at timestamp and the upload was never completed, the upload_status is marked as failed rather than purged. The purge job handles missing files gracefully (idempotent delete) and does not treat a missing file as an error.

Storage URLs delivered to customers via the QR link must be signed URLs with a short expiry window (recommended: 30 minutes per access, re-issued on page load) so that raw storage paths are never exposed and cannot be scraped or shared beyond the intended recipient. A scheduled Supabase Edge Function or cron job runs periodically to delete files from storage where expires_at has passed and purged is still false, then marks those records as purged. Images and videos are never stored directly in the database. The database holds only metadata and references.

---

**ONLINE AND OFFLINE BEHAVIOR**

The entire session flow — layout selection, coin acceptance, photo capture, editing, and printing — must function without any internet connection. The app is offline-first for all core operations. Internet connectivity is used only for: uploading session media after print, delivering the QR share page, pushing layout updates from the operator, and sending session and revenue data to the monitoring dashboard. If the device is offline at the time of upload, the app queues the upload and retries automatically when connectivity is restored. The QR code is displayed only after upload completes. A visible status keeps the customer informed if upload is still in progress.

---

**OPERATOR MONITORING AND REMOTE MANAGEMENT**

The operator accesses a separate web-based dashboard authenticated via Supabase Auth (operator account only, not accessible to customers). The dashboard provides: real-time and historical session counts, revenue tracking per location, print failure logs, device connectivity status, and layout management (add, remove, reorder layouts remotely). Layout assets pushed from the dashboard are synced to the device on next connectivity. The app checks for layout updates on launch and after each session when online. The monitoring system must never interrupt or interfere with an active customer session.

---

**IN-APP UPDATES**

The app checks a versioned endpoint (hosted on Supabase or Vercel) on each launch and after each session when online. If a newer APK version is available, a modal dialog is displayed with a version number, changelog summary, and a prominent download button. Tapping the download button fetches the new APK to local storage and triggers the system package installer. The dialog can be dismissed by the operator (by entering an operator PIN or gesture) but reappears on the next connectivity check if the update is still pending. This mechanism does not require Google Play and is compatible with the sideloaded kiosk deployment.

---

**SECURITY REQUIREMENTS**

Session IDs are UUID v4, generated on-device, and used as session-scoped identifiers. Storage URLs are signed and time-limited, never permanent or publicly guessable. The ESP32-to-app Bluetooth communication uses a per-session rolling token to prevent replay attacks — a raw static string is explicitly not acceptable. The operator dashboard is protected by Supabase Auth and is entirely separate from the customer-facing session system. The Android app runs in kiosk mode: system notifications, the navigation bar, and access to other apps are fully suppressed during customer sessions. The APK is signed and sideloading of other apps on the device is disabled. No permanent customer data is stored anywhere — not on device, not in the database, not in storage — beyond the 24-hour expiry window.

---

**NON-FUNCTIONAL REQUIREMENTS**

The UI must feel premium and deliberate, not utilitarian. Transitions between session states should be smooth and intentional. All on-screen instructions must be short, readable at arm's length, and require no prior knowledge of how a photobooth works. The app must handle Bluetooth disconnection and reconnection with the ESP32 gracefully without crashing or locking the session. The architecture must support scaling to multiple machines across multiple cafe locations, each identified by a unique location_id.

On first launch before kiosk mode is activated, the app displays a one-time operator setup screen prompting the operator to enter the location ID. This ID is stored in encrypted SharedPreferences and used for all subsequent sessions. The setup screen is not accessible to customers during normal operation.

The minimum supported Android API level is 30 (Android 11). Kiosk mode is implemented using Android Device Owner (COSU) APIs, which are stable and well-supported at this API level.

All components — storage, database, authentication, and edge functions — are on Supabase to minimize vendor fragmentation and operational overhead at this stage. The customer-facing share page is hosted on Vercel.

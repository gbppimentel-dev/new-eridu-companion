# architecture

## interface

Java hosts a local WebView. `app.js`, `game-ui.js` and `terminal.js` normalize source fields, gate live-session presentation, and render the compact ZZZ interface. Game fonts, transparent cutouts and original HUD portraits provide visual identity. Controller observation is passive.

## account data

`Api` requests available official HoYoLAB records and Enka showcase data. `SyncPolicy` applies intervals and failure backoff. `AuthRecovery` separates scoped badge expiry from core authentication. Official login pages have no native JavaScript bridge. Validated account cookies are stored through AndroidKeyStore-backed encryption in `Vault`; stale requests cannot replace a newer login.

## visible game observations

The activity prepares a fixed-purpose observer through the existing AYN firmware service. `GameCapture` captures the game display only when ZZZ is visible. `HudClassifier`, local OCR and public portrait features produce small observations. Frames are processed locally and are not uploaded or stored as a production capture history.

`PortraitMatcher` uses shared active/reserve crop banks rather than team-specific labels. `SquadTracker` retains confirmed membership through obscured HUD frames and requires repeated agreement for the active slot. The UI centers the confidently detected on-field member. This is screen inference, not exact engine state; no exact hostile count is claimed.

`FrameRate` uses presented-frame timestamps from the actual game surface. Device playtime uses available Android usage history, with a Cocoon history fallback. Unknown/stale FPS stays unknown.

## sessions

New Eridu uses `moveTaskToBack(true)` on Home and retains its Recents task. Resume re-arms visibility detection. Optional Paired Home explicitly targets both Cocoon displays, records the current configured companion and game task, and repairs an incomplete resume. It reads companion assignments from Cocoon's SQLite database without modifying its library. A captured game task/process ending triggers a delayed companion-close safeguard; switching to another task in the same emulator releases the old association.

The helper's privileged task operations use `PServerBinder`, native existing-task APIs and fixed-purpose commands. It reads task metadata across physical/virtual displays, not game memory, saves or screenshots. A boot identifier and intent fingerprint constrain the association.

## boundaries

The local 2.5 development build adds an opt-in screen-verified daily routine through real Thor controller events. Its status and unverified paths are documented in DAILY-ROUTINE.md. No memory hooks, game patching or save writes are used. Authentication and service behavior can change independently of this app. Keep compatibility and validation claims tied to actual evidence.

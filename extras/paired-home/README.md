# Paired Home 1.2

Optional two-screen session helper for AYN Thor, Cocoon 3.11 and Wayfinder 1.4.1. Install the signed helper APK, then assign Wayfinder Home tap → Open app → Paired Home → bottom screen. Keep Cocoon as the default launcher.

Home explicitly brings Cocoon to displays 0 and 4 and returns controller focus to the top. The helper window is transparent, transient and excluded from Recents. Existing game/companion tasks are preserved.

A firmware-side metadata monitor remembers the captured game and configured companion. It uses the task ID, boot ID and a normalized intent fingerprint, retains readable game-ID extras, and scans all displays including virtual ones. It gives Cocoon time to restore the pair, repairs a missing companion through existing-task APIs, and can reopen a replaced companion task. It leaves a different lower-screen app alone. Once the captured game task and process have both ended for 2.5 seconds, it closes the captured companion. Another game task in the same host releases the old association.

It uses the existing privileged AYN PServerBinder service and reads companion assignments from Cocoon’s SQLite database. It has no network permission, screen-capture pipeline or game-file access. The database is opened read-only. A small root-side metadata process can continue while the tracked session exists; there is no permanent Android UI task.

Use Cocoon’s running-game preview → Resume to return, or X / Close on that card to quit. Before uninstalling this helper, remove its Home binding in Wayfinder. Other devices, Android users, display IDs, launcher activity names and database schemas are unverified.

Build from the repository root with `python3 build.py --paired-home`. Signing uses the same external environment options described in [building](../../docs/BUILDING.md). Development device tests covered paired Home and task-resume repair; live Quit was not exercised against an active user’s game.

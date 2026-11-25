# privacy

## account and network

Account-backed sections need internet. The app contacts official account/record services and Enka for showcase data; artwork is limited to allowed game-asset hosts. It has no companion analytics, advertising or automatic diagnostic-upload service.

Login is performed on official web pages. The native bridge is not exposed there. Validated cookies are encrypted using AndroidKeyStore-backed storage. Private cookies, account snapshots and signing credentials are not included in this repository or release bundle. Disconnect clears stored authentication; Android and remote identity providers have their own cookie/session behavior.

## local observation

The app processes visible ZZZ display frames locally for HUD/UID text and portrait matching. Production observation does not save a frame history or send frames to a recognition server. Passive controller events, frame timestamps and available playtime statistics support the lower-screen interface.

The optional Paired Home helper uses privileged task metadata and reads Cocoon's configured companion assignments. It stores package/task identifiers, a boot marker and a hashed intent identity in its private directory. It does not capture screen content or read game save files. Its session-end safeguard can stop the captured companion after the captured game task and process have ended.

The local development daily routine uses these transient observations to verify controller actions after an explicit Start. It stores the selected plan, account-scoped trigger/completion times, task summaries and measured durations locally. It writes real gamepad events only while the linked game remains visible and the opt-in run is active. A Stop request or manual controller override ends the run. No routine frames or raw OCR transcript are uploaded or retained.

## stored records and reports

Saved account records and UI preferences remain in private Android app storage. The Android manifest disables application backup. Release signing keys stay outside Git/CI.

Documentation screenshots use synthetic fixtures. Original private device captures, full account responses, UIDs, nicknames, cookies, source databases and development signing files are excluded from the source export. Before posting an issue, remove private identifiers and login data. There is no automatic report upload.

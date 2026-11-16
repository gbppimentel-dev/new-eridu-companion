# setup & maintenance

[README](../README.md) · [compatibility](COMPATIBILITY.md) · [privacy](PRIVACY.md)

## account connection

Install the signed APK and open Account → Connect. Complete the official web sign-in and select your game role and region. A login in the separate HoYoLAB Android app is not automatically shared. No account UID is preconfigured in the public build.

Enable the relevant Battle Records visibility in HoYoLAB. Verification challenges must be completed on the official page. Reconnect an affected section when requested; a scoped token error should not erase the entire account session. Avoid repeated retries when the service rate-limits requests.

## Cocoon pairing

Open the ZZZ app entry in Cocoon. Assign New Eridu Companion as its companion, with ZZZ on top and the companion below. Launch through that configured entry. Before matching the visible game UID, the companion shows its waiting state. Saved account records alone do not establish a current game session.

## paired sessions

Optional Paired Home is in [extras](../extras/paired-home/README.md). On the supported Thor setup:

1. Install `PairedHome-v1.2.apk`.
2. In Wayfinder's controller/combos settings, assign **Home tap → Open app → Paired Home → bottom screen**. Preserve your other shortcuts.
3. Keep Cocoon as Android's default Home app.
4. Home returns both screens to Cocoon, preserving tasks. Select the running game's preview and use **Resume** to return. Use **X / Close** on the running-game card to quit the pair.

The small suspended-game card inside Cocoon is its preview. It is distinct from Android PiP. The development setup disabled automatic PiP for paired game apps; video-app PiP permissions were not changed. This permission preference is optional and is configured through Android App Info → Picture-in-picture.

The helper uses elevated AYN firmware access and Cocoon's current component/schema layout. It is optional, device-specific, and not a universal Android launcher replacement. It monitors only the captured pair's task metadata. Do not replace a firmware service or change SELinux settings to make an unsupported device work.

## controls & background behavior

Controller feedback is passive and can be hidden in Companion Options. Game controls remain with the main screen; Wayfinder's existing screen-focus shortcuts remain separate. Account sections update at service-safe intervals. Screen observations and telemetry pause with the companion activity; Paired Home's small session monitor can continue watching the captured task pair.

## updates & removal

Install an official APK signed with the same certificate in place to retain local account selection/preferences. The public export changes defaults, not existing saved account selections. To disconnect, use Account → Disconnect. Uninstalling the app removes its private app data and key material.

Before uninstalling Paired Home, remove its Home binding in Wayfinder so the physical Home action has a valid destination. It does not modify game files or Cocoon's library.

## troubleshooting

- **waiting for a proxy:** enter ZZZ with the selected role. The visible UID must match the account.
- **missing section:** check visibility settings, connection and verification state; source publishing may be delayed.
- **unknown combat agent:** recognition can be uncertain. Keep the agent unidentified rather than substituting an owned character.
- **missing FPS:** the observer needs a fresh measurable game surface. Panel refresh rate is not a substitute.
- **Home affects one screen:** check the Wayfinder binding and the optional helper's Thor compatibility.

Never attach cookies, passwords, raw preferences, private captures or signing files to a public bug report.

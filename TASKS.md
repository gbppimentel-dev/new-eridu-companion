# tasks & validation

## source backup and publication

- [x] Export current ZZZ code/assets without account snapshots, private captures or keys.
- [x] Match the Underworld repository's README/gallery/docs/credits structure.
- [x] Add issue/PR forms, compatibility labels and read-only CI.
- [x] Make public builds portable and unsigned by default.
- [x] Add synthetic public UI fixtures and reproducible host checks.
- [x] Include optional Paired Home source, setup and privacy notes.
- [x] Package source-linked APK/ZIP/compatibility/manifest/checksum release assets.

## implemented development behavior

- [x] Title-case labels, original game typography/art and compact lower-screen layouts.
- [x] Passive controller feedback and centered ABXY vector outlines.
- [x] Three-agent display with the repeatedly confirmed on-field member centered.
- [x] Preserve the companion task on Home and support paired resume repair.
- [x] Keep stale/missing account sections explicit and respect service backoff.
- [x] Remove the unverified exact enemy counter.

## local daily routine development

- [x] Select errands, farming target and Backup Charge policy before starting.
- [x] Show current task, elapsed time, estimated time remaining and local trigger history.
- [x] Model the 4 AM Asia reset independently of Battery farming; test the 3 AM / 5 AM case.
- [x] Send real controller events and verify manual override on Thor.
- [x] Verify 80-Battery coffee and read charge overflow above 240 in game.
- [x] Verify automated Dennies reward/cost guards and controller deployment into combat.
- [x] Restore Thor USB access.
- [x] Verify successful controller battle completion and local Battery accounting.
- [x] Verify native battle repeat without a HoYoLAB/results Battery refresh.
- [x] Verify final exit through menu-to-city fade in an end-to-end Expert run.
- [ ] Complete an unattended full route and measure its actual duration.
- [x] Validate exact controller-based Backup Charge extraction and deployment into the funded battle.
- [x] Verify funded battle result handling and local Battery accounting.
- [x] Validate the selected-row scan across 15 Disc and 16 Expert stages.
- [ ] Validate every supported farming category.
- [ ] Validate alternate daily draw locations and loading/reconnection variations.
- [x] Publish v2.6.1 source and development-preview artifacts after confirmation.

## local 2.6 improvements

- [x] Remove Clear Alerts, optional Mail claims and their native command route in 2.6.1.

- [x] Swap Bangboo and Weekly Tasks on Today.
- [x] Add detailed main-screen progress while keeping Today visible; remove fullscreen progress.
- [x] Keep combat agent names visible with a dedicated two-line label area during automation.
- [x] Replace scrolling daily dialogs with a two-step planner and paged target/stage lists.
- [x] Show Disc set pairs with local artwork; explain Update Stage List before starting.
- [x] Use Done / Closed / Idle and explicit collection/attention states.
- [x] Surface Drive Disc stages and the exact one-time final-run top-up rule.
- [x] Require reward evidence for full daily completion and check claims after farming.
- [x] Verify a Disc top-up, low-Battery results exit and post-farm reward check on Thor.

## local 2.6.2 Temple fix

- [x] Reproduce the fresh-reset unclaimed Temple panel and missed controller shortcut.
- [x] Add fixed claim/receipt recognition and exact-account retry guards.
- [x] Verify live receipt recovery and current-reset completion on Thor.
- [x] Install and verify the signed local 2.6.2 patch.

## local 2.6.3 Video Store fix

- [x] Preserve and diagnose the untouched opening-congratulations screen.
- [x] Add message/ending/button confirmation checks and guarded retry recovery.
- [x] Require the Store errand 1/1 before persisting completion.
- [x] Verify live recovery, final reward checks and the installed signed patch.

## local 2.6.4 Temple tab fix

- [x] Reproduce Business Log Total Obtained being misclassified as a receipt.
- [x] Verify RB changes to Auto-Manage and X starts management on Thor.
- [x] Classify five captured Temple states, including idle management at the full currency cap.
- [x] Require actual running management instead of period-cap-only completion.
- [x] Verify automatic RB navigation from Business Log and actual running-state checkpoint.
- [ ] Verify the final signed build and updated Handbook chord on Thor; testing stopped at user request.
- [x] Publish the latest signed v2.6.4 preview and source after explicit user instruction.

## remaining validation and known limits

- [ ] Validate a clean installation, first account connection and UID selection on a second Thor/account.
- [ ] Verify portrait matching across the available roster, costumes, animations and arbitrary team changes in live play.
- [ ] Validate actual paired Quit on-device without risking an active user's session; automated lifecycle/task-policy checks cover the guard logic only.
- [ ] Recheck Signals authentication/history against a fresh account and verification challenge.
- [ ] Extend the portrait bank when reliable public assets are available; unavailable identities remain unknown.
- [ ] Verify other firmware versions, display arrangements and long suspend/resume sessions.

These unchecked items are not implied complete by a passing build or a synthetic screenshot. Runtime development tasks are tracked in [GitHub Issues](https://github.com/gbppimentel-dev/new-eridu-companion/issues).

- [Roster recognition and portrait coverage — #1](https://github.com/gbppimentel-dev/new-eridu-companion/issues/1)
- [Clean onboarding and Signals recovery — #2](https://github.com/gbppimentel-dev/new-eridu-companion/issues/2)
- [Paired Quit and suspend — #3](https://github.com/gbppimentel-dev/new-eridu-companion/issues/3)
- [Firmware and display compatibility — #4](https://github.com/gbppimentel-dev/new-eridu-companion/issues/4)

## Local v2.6.5 team rotation

- [x] Wait for the existing daily run to finish before inspecting Thor.
- [x] Read the saved Koleda → Rina → Claret preset and Claret’s relevant skills; restore the city without squad edits.
- [x] Replace generic combat inputs with observed agent-specific actions, ready-skill checks and Claret chain preference.
- [x] Add bounded long holds with interruption/release coverage.
- [ ] Validate live readiness, chain overlays and rotation timing when the user is ready for combat testing.
- [ ] Install/publish the new candidate only when requested; Thor is in use for other projects.

## Local v2.6.6 routine regressions

- [x] Reproduce Temple destination handoff waiting despite a recognised Auto-Manage tab.
- [x] Remove the separate sparse-text requirement for arrival and revenue detection.
- [x] Reproduce the split Cloud Crest receipt and verify separate Cloud / Crest label readings.
- [x] Complete the live Temple receipt → restart → final rewards retry.
- [x] Recognise the captured Store opening confirmation without relying on sparse full-page OCR.
- [x] Implement bounded known-Store confirmation retries and require disappearance before verification.
- [x] Reproduce combat idling after a long load despite correct fresh portrait matches; exclude loading from its inactivity watchdog.
- [x] Clear three consecutive disc battles with the final candidate, finish its reward check, and install signed v2.6.6 on Thor.
- [ ] Observe a fresh-day Video Store opening from start to finish with the revised reader.


## Weekly Routine — v2.7.0 Local

- [x] Add compact Do Weeklies plan, claims-only mode and boss selection.
- [x] Reuse controller feedback, Stop, elapsed time, ETA and reserved progress layout.
- [x] Add account-scoped weekly history and Monday 4 AM Asia reset.
- [x] Read free Notorious Hunt quota; reject paid/resource-cost deployment and confirm quota decrement before repeats.
- [x] Implement Ridu Weekly task/milestone and Hunt Weekly Challenge claims before and after bosses.
- [x] Verify saved screenshot readers and host/UI regressions; build locally.
- [x] Validate actual weekly claim receipts, nine-tile navigation and active milestone Claim controls.
- [x] Validate 11-boss discovery, required-squad deployment, completed results and quota read-back on Butcher.
- [x] Clear all three free Butcher rewards across testing, including two consecutive unattended clears after the staging fix.
- [x] Verify automatic exhausted-details recovery and Hunt Claim All receipt dismissal.
- [ ] Observe one uninterrupted three-clear route and final claims with the final signed candidate after the next weekly reset.
- [ ] Validate encounter-specific prompts, including Resonia where applicable. Unknown screens require takeover.
- [x] Install for explicitly authorized testing; first free Butcher clear verified.
- [x] Verify the zero-quota run skips deployment, claims Hunt rewards and completes both closing claim checks in the city.
- [x] Install the final signed candidate after the live route checks.
- [ ] Publish only after user confirmation.

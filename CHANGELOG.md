# changelog

## 2.7.0 — 2026-11-28 development preview

- Add Do Weeklies beside Do Dailies and on the Weekly Tasks card. Choose Claims Only or Claims + Bosses, with a paged list read from the game.
- Add Ridu Weekly completed-task and point-milestone claims, plus Notorious Hunt Weekly Challenge claims, before and after the boss route. Claims remain repeatable as the week progresses.
- Use the shared controller, session checks, Stop control and reserved progress deck. Track weekly history per account and reset the displayed quota Monday at 4 AM Asia.
- Verify the free Notorious Hunt quota and selected boss before deployment, reject resource-cost screens, and require the quota to decrease before another attempt. No Battery or Backup extraction is part of this route.
- Enforce Koleda → Rina → Claret for every boss; verify applied preset members and recheck the free quota after squad changes.
- Enter staging through a matched waypoint ring and bounded controller-stick pulses, then hand over only after the boss health bar appears.
- Handle wrapped bingo navigation, pulsing Claim colors, wrapped boss-list focus and the native boss-selection return menu.
- Include menu travel, squad verification, staging and quota read-back in weekly ETA, refining it from completed boss cycles.
- Smooth the staging-marker glyph before matching to tolerate subpixel antialiasing; require completed 1/1 result evidence and a readable Complete/Exit footer.
- Recognize exhausted boss details despite the No Reward Mode footer; support safe Back-only recovery before claims. Accept the active yellow Hunt Claim All caption.
- Thor testing earned all three free Butcher rewards, including two consecutive unattended clears with the staging fix. Quota read-back, result exit, zero-quota recovery, Hunt claim receipts and both closing reward checks passed. Final signed APK installed locally.

## 2.6.6 — local development, not released

- **Video Store:** recognise the opening confirmation from its fixed caption independently of sparse page OCR, accept supported split-text readings, and verify dismissal with bounded controller retries before returning to the Handbook.
- **Suibian Temple:** use the verified tab/receipt state to finish travel and enter management; recognise Claim Revenue independently of generic whole-page text. Read two-line Cloud Crest receipt labels separately, then confirm actual running management.
- **Combat:** start the no-input watchdog after arrival in combat, and reset it during loading. Long transitions no longer exhaust HUD-confirmation time before the first action.
- **Swaps:** retry a swallowed R1 at most twice, only while fresh repeated portrait readings still show the original agent. Pause after a bounded failure.
- **Defense:** add recovery dodges after held basic finishers and reactive dodges when the visible health indicator falls. Pause a failed combat check or persistently incomplete squad rather than leaving idle agents taking damage or cycling supports.

## 2.6.5 — local development, not released

- Replace the farming button loop with an observed-HUD rotation for Koleda, Rina and Claret. Supports use ready EX skills and swap; Claret gets basic combos, third-hit holds and alternating tap/held enhancements. Ready Ultimates take priority.
- Recognize Claret’s red Sharpness-ready icon separately from the supports’ purple EX icon. Inputs require the expected squad and a repeatedly observed active portrait.
- Prefer Claret in labelled controller chain choices; skip unrecognized options. Verify swaps through portraits rather than advancing an assumed agent counter.
- Allow bounded EX/basic holds with Stop, physical-input, session and foreground checks throughout; always release the held button on interruption.
- Built locally only. No live combat tests, installation or publication for this change.

## 2.6.4 — 2026-11-27

- **Temple navigation:** detect the selected Business Log / Auto-Manage tab through verified header labels and active-tab color; use RB and verify Auto-Manage before acting.
- **False receipt:** Total Obtained on Business Log no longer triggers repeated A presses. Only the verified Temple reward popup can be acknowledged.
- **Management:** read the actual Start / Stop control and confirm management is running before saving completion. A full Omnicoin period cap alone does not complete the task; Auto-Sell can still run.
- **Handbook input:** focus the game once per controller chord, allow the modifier to settle before A, and recover a leftover shortcut wheel before opening the Handbook.
- **Receipts:** accept verified Cloud Crest rewards as well as Omnicoin while retaining the required reward title and Confirm control.

## 2.6.3 — local development, not released

- **Video Store:** handle the prosperous-store-opening congratulations popup through a verified Confirm shortcut. Read the exact opening message, ending and button label before acting.
- **Verification:** save Store completion only after its focused in-game errand reads 1/1; initiating Open or returning to the city is insufficient.
- **Recovery:** allow a selected, unfinished Video Store errand to resume its known opening confirmation on the exact linked account. Continue through the normal reward checks.

## 2.6.2 — local development, not released

- **Temple:** recognize the completed-management panel using fixed labels and claim revenue through its verified controller X shortcut. Detect and acknowledge the custom Obtain Rewards receipt.
- **Recovery:** resume an unfinished Temple claim or receipt only when that errand is selected and the exact linked account is confirmed. Pending revenue is processed before running/period-limit completion.
- **Transitions:** wait briefly for exact identity re-verification before sending further inputs, and use agreeing complete-field readings with a paired numeral-only fallback for bright backgrounds, avoiding the former thresholded UID misread.

## 2.6.1 — 2026-11-25

- Remove Clear Alerts, its Mail claim option, and the supporting native automation route. Daily errands, farming and daily reward checks remain available.

## 2.6.0 — local development, not released

- **Today:** Bangboo moves beside Video Store; Weekly Tasks moves to the lower right. Verified Temple work is green and reads Done; active business reads Running. Inactive services read Closed / Idle; actionable states read Collect / Attention.
- **Progress:** a detailed run deck shows the task, status, progress, elapsed/remaining time, actual controller input and Stop. Fullscreen progress is removed. Today compresses to keep every card visible.
- **Daily Plan:** a two-step Errands/Farming flow with compact lists, game assets and paged reward choices. Disc rows show both set artworks and names. Update Stage List explains its controller-driven handbook scan before starting. No scrolling is required.
- **Combat Names:** reserve portrait/name/tag rows during automation and allow names to wrap without clipping.
- **Drive Discs:** prominent 60-Battery farming and an exact one-time final-batch Backup Charge top-up. Missing stage selection is resolved before Start. Static return-screen labels remain readable when low Battery disables Next.
- **Claims:** available daily rewards are always checked before and after farming, including Farm Only. Full daily completion also requires the reward checkpoint.

## 2.5.1 — local development, not released

- **Consistency:** on-device Battery accounting drives battle repeats; HoYoLAB notes never gate execution or completion. Menu presses are slower and transitions are checked twice.
- **Checkpoints:** task progress is read from the selected game card. The engagement cap is separate from 0/1 errand completion, and verified steps survive retries within the same reset cycle.
- **Reward Claims:** bank available engagement rewards before errands and before farming; persist verified errand completion before combat.
- **Current services:** Beckford Video Store dialogue through Ninne, controller Recommended Tapes/Open shortcuts, surprise-box Continue/Confirm animations, and explicit running Temple management detection.
- **Farming Screens:** separate Disc/Expert returns, selected-row stage scanning, and a verified exit from Expert Burnout when it conflicts with the40-Battery plan.
- **Reconnect:** verify the observed server-timeout dialog and its Reconnect button, then re-check the linked session before continuing.
- **Compact UI:** the live timer shares the existing footer without moving main content; farming targets use a scrollable modal picker that fits Thor.
- **Live testing:** a private, durable Stored Battery allowance is checked before any reserve confirmation. Premium refreshes and consumable tickets remain excluded.

## 2.5.0 — local development, not released

- **Daily routine:** opt-in route and farming plan, controller-event input, visible task progress, elapsed time, approximate ETA and manual stop/override.
- **Daily availability:** account-scoped trigger history and Asia's 4 AM reset; Battery farming follows the actual in-game balance independently of daily eligibility.
- **Battery plan:** material, Drive Disc and Expert targets, optional one-time final-batch Backup Charge top-off; Polychrome refreshes, Ether Batteries and Auto Combat tickets excluded.
- **Validation:** controller navigation, 80 Battery coffee and manual override verified on Thor. Full-route, reserve extraction and farming verification remain in development; see [routine status](docs/DAILY-ROUTINE.md).

## 2.4.4 — unreleased

- **game detection:** isolate the visible UID numeral strip from adjacent network icons, reject conflicting readings, and recognize the Touch city-menu HUD alongside controller layouts.

- **account recovery:** restore a single previously connected account from its scoped local cache when upgrading from development builds that never persisted their default selection. Requires the existing encrypted session; explicit selections and fresh installations remain unchanged. Multiple or inconsistent account caches require a manual selection.

## [2.4.3] — 2026-11-18

- **credits:** simple developer attribution for gbppimentel-dev in Companion Options and a compact Credits page, with third-party acknowledgements expandable below.

## [2.4.2] — 2026-11-16

First public source backup and development preview.

- **publication:** Hades-style repository documentation, synthetic gallery, issue forms, task checklist, CI, compatibility record and release packaging.
- **privacy:** removed developer UID defaults and private fixture/signing dependencies from the public export.
- **build:** portable SDK/JDK discovery, unsigned CI builds and external environment-based signing.
- **snapshot:** retains the 2.4.1 UI/runtime line and includes optional Paired Home 1.2 source separately.

The public build is a new source-built candidate. It is not presented as the exact device-tested APK, and the Thor installation is unchanged by this backup.

## 2.4.1 — 2026-11-16

- **Home:** preserve the companion task for Cocoon resume; re-arm visibility on return.

## 2.4 — 2026-11-16

- **recognition:** shared public portrait registration, retained three-agent membership and centered on-field presentation.
- **visuals:** restored ZZZ typography, centered face-button vector glyphs and separated Calendar channel artwork.

## 2.3 — 2026-11-16

- **session:** hidden UID/capture interruptions no longer immediately erase a verified session; encounter timers survive short HUD interruptions.
- **daily UI:** clear task colors, full-charge warning, spacing, rank art and adjusted cutouts.
- **accuracy:** removed misleading health-bar-derived enemy totals.

[2.4.2]: https://github.com/gbppimentel-dev/new-eridu-companion/releases/tag/v2.4.2

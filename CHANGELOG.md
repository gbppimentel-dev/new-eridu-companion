# changelog

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

# Daily Routine — Local Development

This is a development preview. The complete unattended route is not yet validated.

The plan selects errands, farming target and whether to use Backup Battery Charge once to complete the final batch. Daily errands use the Asia reset at 04:00 UTC+08; failed or stopped runs remain retryable. Available daily engagement rewards are banked before errands, again before farming, and after farming. Farm Only checks rewards before and after combat too. Errand-cycle completion is saved as soon as all four errands and the reward checkpoint are verified, so a later farming interruption cannot undo it. Task checkpoints are saved immediately from in-game progress or a verified service result. Max Engagement alone never marks a task complete. Old day-complete records without task evidence cannot lock out unfinished errands.

Farming is available independently of that reset and reads the current game balance, including overflow above 240. Starting at 03:00 and again at 05:00 does not assume the Battery has refilled.

HoYoLAB notes never gate the routine. Starting Battery is read in game, confirmed battle costs are subtracted locally for repeats, and the game balance is re-read after exiting results. Menu inputs settle for one second and transitions must match twice. Returns wait through unrecognized fade frames without sending speculative inputs, and handbook closing verifies its sidebar level. The observed server-timeout dialog is handled only after its Reconnect and Log In Again button labels are verified; recovery is bounded to three Reconnect attempts.

The app shows the current task, verified task/battle progress, elapsed time, an estimated remaining duration, last trigger and daily availability. ETA is an estimate; loading and reconnecting can extend it. The detailed run deck reserves space above Today; the cards compress to remain visible. Errands and Farming use non-scrolling steps. Targets and stages use five-row paged lists. Disc rows show the paired set artwork and names. There is no fullscreen automation popup. No complete-route duration has been measured yet. The former 10–20 minute estimate was provisional; actual run ETA remains visible in the main panel. Verified material battles have taken about 70–102 seconds including loading and result verification; route length and final charge adjustments add time.

Inputs use the actual discovered Thor controller node and focus the existing ZZZ task on display 0. No touch macros are sent by the runner. Stop is available in the companion; an unmatched physical controller button/axis event also stops the run. All held routine buttons are released on exit. No routine starts automatically on boot.

Resources: current Battery, the 80-Battery coffee and an optional final top-off from Backup Battery Charge. No Polychrome refresh, Ether Battery conversion or Auto Combat ticket action is allowed. A zero balance never starts a full reserve-funded extra batch. Expert targets request 40 Battery; if the saved Burnout setting doubles it to 80, the runner verifies the exit-Burnout confirmation and re-checks 40 before deployment.

## Evidence

- Linked-account checks, controller routing, handbook entry, coffee warp and 80 Battery reward verified on Thor.
- Manual controller override verified to stop an active run.
- Thor-sized browser checks cover the non-scrolling plan, target lists, 31-stage pagination, unclipped combat names, run deck and all six visible Today cards. Physical-device verification of 2.6 is recorded separately.
- Current errands are read by focused card: Coffee 1/1 and Draw 0/1 are distinct from the 400 engagement cap. Completed steps persist across retries.
- Fresh-reset coffee (+80 Battery), Mr. Roar surprise-box draw and Ninne Video Store opening completed automatically. The store selects a Happy promoter, Recommended Tapes and the Open shortcut, then acknowledges the final confirmation.
- Automated Dennies route verified the 125000 reward and 100 Battery cost, deployed, completed a battle through real controller input and counted the cost locally. The first measured completed battle took about 102 seconds; complete-route duration still needs measurement.
- Coffee, Daily Draw, Video Store and Temple checkpoints all survived retries and were skipped without a HoYoLAB refresh. Temple travel handles remembered map positions and reads the active tile border separately from artwork.
- Daily reset boundaries, 03:00/05:00 eligibility, reserve arithmetic, account isolation and UI progress regressions checked locally.
- Exact Backup Charge extraction verified on Thor using two quantity-reading modes and repeated agreement before confirmation. The in-game balance is re-read after conversion; natural regeneration during slow steps is allowed within its elapsed-time bound.
- Two consecutive Dennies battles completed through native X repeat without waiting for a HoYoLAB or results-screen Battery refresh.
- A 60-Battery Drive Disc battle completed and was counted correctly. Its distinct post-battle setup return is now recognized separately from Combat Simulation. Static Buffed Attributes, Reward Details and View Rewards labels identify that return even when low Battery disables Next; this check does not wait for Battery regeneration.
- A 40-Battery Expert run completed end to end, including native Burnout-off verification, battle completion, cost accounting and the category-to-city exit. It took 6m13s with the tested saved squad and target.
- The selected-row stage scan completed with 15 Drive Disc and 16 Expert entries, without duplicated artwork suffixes. Names are recognized from game text and can retain OCR spelling errors.
- A 100-Battery Agent XP run completed end to end, including recovery from the observed server-timeout dialog, selected-preset popup, Investigator Log reward verification, battle accounting and city return. It took 3m02s with the tested saved squad.
- The 2.6 Disc repeat completed end to end in 5m23s: exact 57 Backup Charge filler from 3 to 60, one 60-Battery battle, low-Battery results exit and final daily reward check. One naturally regenerated Battery remained. This is a measured Farm Only run, not a complete errand-route estimate.
- Full daily route, alternative draw variants and every farming target remain under live validation. Unrecognized screens stop the routine rather than guessing.

The v2.6.4 source and development-preview artifacts are published with user authorization. Targeted test results do not certify the remaining full-route and compatibility tasks.

## Video Store Confirmation — Local 2.6.3

The opening-congratulations popup requires its complete message, final line and Confirm button label to agree. The runner confirms through A, returns to the city and reads the focused Store errand card. It records completion only for 1/1, including when an already-open service message was observed. Requesting Open or reaching the city alone does not complete the checkpoint.

A retry can settle this specific popup when Store is selected, Farm Only is off, the daily cycle is incomplete and the exact linked account is verified. Other unknown menus remain blocked.

## Temple Navigation — Local 2.6.4

Business Log contains a Total Obtained statistic; that text is never treated as a reward receipt. Both tab labels and the selected orange tab are verified before navigation. Business Log uses RB to select Auto-Manage; the runner waits for the selected tab before reading its Start/Stop controls. Start uses X. Completion requires a stable Stop Auto-Manage state, and therefore does not skip Auto-Sell when the Omnicoin period cap is full. The active job is never stopped by this route.

The verified reward popup accepts Temple currency labels (Omnicoin or Cloud Crest), plus its Obtain Rewards title and Confirm button. Unknown screens remain bounded waits and do not receive speculative A presses.

## Temple Claim Recovery — Local 2.6.2

The Temple route reads the fixed completed-management and Claim Revenue labels before sending its X shortcut. The custom Obtain Rewards receipt requires its title, Confirm button and Temple currency label to agree. Pending revenue and receipts are handled before verified running-state completion. The period cap alone is not completion evidence.

A retry can resume a positively identified Temple claim/receipt on the exact linked account when Temple is enabled, Farm Only is off and the daily cycle remains incomplete. It settles that service first, returns to the city and performs the normal reward checkpoints. Unknown menus and other accounts are never treated as resumable Temple screens. Identity interruptions pause inputs for bounded re-verification; Stop and manual override remain active.

## Stage List And Labels

Update Stage List opens the Handbook through controller input and reads Area Patrol and Expert Challenge names. It does not deploy or spend Battery. Use it if a game update adds a stage; the explanation page appears before starting. Unknown stage rewards retain a generic disc icon rather than an invented pair.

Completion reads **Done**. Video Store uses **Running**, **Collect**, **Closed** and **Unknown**. Temple uses **Done**, **Managing**, **Collect**, **Attention**, **Idle** and **Unknown**. Unknown means no verified state; Closed and Idle mean the service needs starting.

Disc pair names follow the existing local equipment catalog. Stage associations were cross-checked against the game’s Swiftspine reward screen and the [Area Patrol stage list](https://gamemarket.gg/news/zenless-zone-zero/which-zzz-area-patrol-stage-should-you-farm-for-double-rewards), with the newest pair confirmed in [HoYoverse’s Steam announcements](https://store.steampowered.com/news/posts/?enddate=1785245407&feed=steam_community_announcements). Artwork uses the already bundled game assets.

Further Thor testing was stopped at the user’s request before the final v2.6.4 Handbook chord change was device-verified. The signed preview is saved for user testing; see [release validation](validation-v2.6.4.json).

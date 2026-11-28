# Weekly Routine — Local v2.7.0

Do Weeklies appears beside Do Dailies and opens from the Weekly Tasks card. It has two modes:

- **Claims Only:** check Ridu Weekly completed bingo tasks, available point milestones and Notorious Hunt Weekly Challenge rewards. A final claim pass is always included.
- **Claims + Bosses:** perform the same opening claims, clear the selected Notorious Hunt boss up to the remaining free weekly reward count, then check both reward pages again. One selected boss is repeated, using the saved challenge level and the required Koleda → Rina → Claret squad. Every boss deployment reads all three agent labels and verifies that order. If the team differs, the runner checks existing presets and verifies their applied members before deployment. Preset names alone never authorize a squad. It uses the existing Koleda / Rina / Claret rotation.

Read Available Bosses opens the Handbook and reads the boss list using controller input. It returns to the city without deploying. The list is stored for the linked account; refreshing it does not spend a weekly reward.

After deployment, a fresh matching combat HUD is required before staging movement. The runner locates the neutral waypoint ring after light smoothing to tolerate subpixel antialiasing, and sends bounded, half-second forward-stick pulses only while it stays centered. It releases the stick after every pulse and stops walking when the boss health bar appears. Numeric distance OCR does not control movement. Unknown layouts stop after a bounded wait or movement limit.

The runner verifies the named boss and a readable free reward quota before Next, checks the subsequent squad screen for resource costs, and checks that the quota decreased by exactly one after a successful battle. Unknown costs, unreadable quota, unexpected menus, failures or loss of session confirmation stop the route. It never selects paid repeats, replenishment, Backup Battery, Ether Batteries, Polychromes or Auto Combat tickets. Free rewards may include Polychromes received as rewards.

Weekly history is independent of daily history. It records last trigger, duration, state, last claim check and the quota observed in game. The displayed quota expires Monday at 04:00 UTC+08 and is read again on the next route; it is never replenished from a stale local counter. Claims can be run repeatedly as objectives finish over the week. A completed claim pass does not mean every weekly objective is finished. Other weekly content, including Hollow Zero runs, is outside this routine.

The main progress deck shows current task, stage count, elapsed time, ETA, controller input and Stop. The weekly ETA includes menu travel, squad checks, staging and quota read-back as well as combat; completed boss cycles refine the next estimate. The existing Today cards remain visible. Manual controller input, Home, foreground loss and account mismatch use the daily runner's interruption rules. No weekly routine starts automatically.

## Validation And Remaining Work

Host checks cover Monday reset boundaries, quota consumption, free-deployment decisions, expired setup evidence, focus outlines, account isolation, UI selection and unknown quota. Thor-sized browser checks cover the non-scrolling plan, list pagination, zero-quota state and progress layout. Read-only Android OCR probes use saved Thor captures of the weekly board, completed and claimable task focus, Hunt list, disabled reward panel and boss setup. These probes do not send game input.

Thor testing cleared all three free level-65 Newborn Dead End Butcher rewards with Koleda → Rina → Claret: one clear before the pause and two consecutive unattended clears after the staging fix. The first and final observed results were S rank in 2m 29s and 2m 17s. The runner recognized completed 1/1 results, exited them automatically, and verified the 2→1 free-quota decrement before repeating. Both entries in the two-fight run passed staging without manual input.

At zero free rewards, Next changes to No Reward Mode. Boss details are now recognized independently of that action; deployment still requires Next and a positive free quota. Same-account controller recovery from the known exhausted details page was verified live: it sends only Back to return to the city before checking claims. Hunt Claim All detection now accepts its active yellow caption as well as white; disabled gray remains rejected. The active Hunt Claim All receipt was verified live (60,000 Dennies and 90 Investigation Merits), followed by disappearance of the available claim. A final zero-quota run skipped deployment, rechecked both reward pages and reported Weekly Routine Complete in the city (8m 58s). The final signed APK was then installed on Thor. No Battery, Backup Battery, Polychromes, Ether Batteries or Auto Combat tickets were spent.

Testing exposed and fixed the result text crop, staging antialiasing, zero-quota return and active claim color. The complete three-clear sequence with final claims has not yet been observed as one uninterrupted run of the final candidate; free rewards are already exhausted for this week.

The preset reader has positive Koleda/Rina/Claret and negative alternate-team frame checks. Full automatic correction from a different preset has not yet been observed live. Other bosses' staging layouts, Resonia and special encounter prompts remain unverified; unknown screens stop for takeover. This local build does not establish unattended support for every listed boss.

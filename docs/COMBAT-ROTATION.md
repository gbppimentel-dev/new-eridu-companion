# Farming And Weekly Combat Rotation

v2.7.0 replaces the former modulo-based farming button loop. During the original v2.6.5 inspection, the requested squad was inspected through Preset Config on Thor after the existing routine completed: **Koleda → Rina → Claret Flint**. No preset was changed. The game was returned to the city. No combat tests were performed, no test battle was deployed, and no Battery was restored or spent during this inspection.

| Agent | Controller behavior |
| --- | --- |
| Koleda | Use a visibly ready Ultimate or EX Special, then R1 to Rina. Avoid a basic-attack phase. |
| Rina | Use a ready Ultimate; hold a ready EX Special, then prioritize R1 to Claret. If EX is unavailable, swap onward. |
| Claret | Use a ready Ultimate. Use Sharpness-ready enhancement inputs, alternating a tap with a held EX/follow-up. Use two basic taps and a held third input with a recovery dodge. React to visible health loss with a defensive dodge, then resume. Revisit supports after roughly 16 seconds on field, at a combo boundary. |

Claret’s in-game descriptions were read during the inspection. Her EX uses Sharpness and enters Crimson Inscription. Continuing to hold Special chains into follow-ups. Her enhanced basic string has three attacks, and holding its third attack extends it. The implementation uses conservative initial timings; it does not claim to recognize the exact current combo frame, Gash/Maim state or Crimson Inscription duration.

Each battle must observe all three expected portraits together twice. The active portrait then needs repeated fresh agreement before the routine chooses an agent-specific action. Readiness comes from the controller icon interiors: purple for the supports’ EX, red for Claret’s Sharpness-ready EX, and gold for Ultimate. These are visual hints, not exact resource telemetry. Icon hints also need repeated observations; an unchanged ready Ultimate cannot cause repeated casts until its unavailable state has been observed.

A persistently missing squad portrait while normal combat controls remain visible causes the routine to pause the battle. This is a missing-agent guard, not exact death telemetry. A health-bar colour heuristic queues defensive dodges; it is not an exact HP reading.

R1 swaps are acknowledged by a newly observed active portrait. If an input is swallowed by an EX/impact animation, the routine retries at most twice, spaced apart and only while fresh portraits repeatedly confirm the same original agent. A bounded failure pauses the battle instead of advancing an assumed order. Chain choices require the chain prompt plus a recognized agent name and its corresponding LB/RB legend. Claret is preferred on either side. A recognized support is accepted when Claret is not offered; unrecognized choices receive no input and may expire in game.

Holds are bounded to two seconds. The routine checks Stop, unexpected physical input, game visibility, observer heartbeat and linked-session freshness throughout the hold, and attempts release even if a press, wait or guard fails. Menu navigation and resource rules remain the existing controller-only routine. A different/unrecognized squad does not receive the former generic attack loop.

## Validation scope

Host checks cover the rotation state transitions, missing readiness, fresh/unknown portraits, swap acknowledgment, chain preference/debounce, icon signal classification, and interruption/release behavior. A previously captured combat image confirms the existing controller-label crop, purple EX crop and unavailable Ultimate crop. This does **not** validate Claret’s live red readiness icon, every chain overlay or the new rotation’s effectiveness.

Live testing was deferred for v2.6.5 and subsequently authorised for v2.6.6. That test reproduced the loading-watchdog regression: the no-input timer expired while loading before the first two HUD observations could agree. v2.6.6 starts this timer on arrival in combat and resets it during loading. See the v2.6.6 validation record for measured device coverage; the timings remain an initial rotation rather than exact animation-state telemetry.


## v2.6.6 targeted device result

The final candidate cleared three consecutive 60-Battery Swiftspine and Splitclaw disc battles, exited farming, re-read the remaining balance and completed the final daily reward check. The run took 7m37s including navigation/loading and spent 180 current Battery, with no Backup or consumables. It left 5 newly remaining/regenerated Battery because reserve filling was disabled for this test. An earlier successful guarded test used a further 60 current Battery. The final signed APK was installed on Thor.

This validates the tested squad and stage. It does not establish optimal rotation timing, universal enemy coverage or every chain overlay. The Store confirmation detector passed the captured blocked popup; a fresh-day Store opening still needs a future end-to-end check.

## v2.7.0 Weekly Result

The same rotation earned all three free level-65 Newborn Dead End Butcher rewards across testing with Koleda / Rina / Claret. Two clears ran consecutively without manual combat or staging input after the marker fix. The first and last observed results were S rank in 2m 29s and 2m 17s. No Battery or Polychromes were spent during weekly testing. Result/zero-quota return and final reward handling are covered in the [weekly validation](WEEKLY-ROUTINE.md). Other bosses and exact animation-state optimization remain unverified.

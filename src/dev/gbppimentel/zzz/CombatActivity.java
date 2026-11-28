package dev.gbppimentel.zzz;
/** Count unreadable combat time only after arrival, never while the game loads. */
final class CombatActivity {
 private long since=-1;
 void loading(){since=-1;}
 boolean stalled(long now,boolean inputSent){if(since<0||inputSent)since=now;return now-since>18000;}
}

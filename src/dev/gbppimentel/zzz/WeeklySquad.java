package dev.gbppimentel.zzz;
import java.io.IOException;import java.util.*;
/** Select an existing preset, then verify the actual three deployment labels. */
final class WeeklySquad {
 static DailyScreen ensure(DailyAutomation a)throws Exception{
  a.publish("running","Verifying Koleda → Rina → Claret");
  DailyScreen f=a.waitFor("Weekly Squad Labels",s->s.weekly!=null&&s.weekly.squad);
  if(f.weekly.squadReady)return f;
  a.press("X");f=presets(a);if(f.weekly.presetColumn==1){a.press("LEFT");f=presets(a);}
  Set<String> seen=new HashSet<>();String preferred=a.history.optString("preferredSquadPreset");
  for(int i=0;i<40;i++){
   String name=f.weekly.presetName;if(!seen.add(name+"|"+f.weekly.presetColumn))break;
   if(f.weekly.presetFull){
    a.press("A");f=a.waitFor("Applied Weekly Squad",s->s.weekly!=null&&s.weekly.squad);
    if(f.weekly.squadReady){a.history.put("preferredSquadPreset",name);WeeklyHistory.save(a.dir,a.uid,a.history);a.completed.put("Koleda → Rina → Claret Verified");return f;}
    // The cached name may have been edited. Verify members, never trust its name alone.
    if(preferred.equals(name)){preferred="";seen.clear();}
    a.press("X");f=presets(a);
    // Reopening configuration resets focus to the first preset. Restore the tested
    // cell before advancing, otherwise the search repeats the second preset.
    for(int row=0;row<i/2;row++){a.press("DOWN");f=presets(a);}
    if(i%2==1){a.press("RIGHT");f=presets(a);}
   }
   int col=f.weekly.presetColumn;if(col==0)a.press("RIGHT");else{a.press("LEFT");presets(a);a.press("DOWN");}f=presets(a);
  }
  throw new IOException("No Saved Koleda → Rina → Claret Preset Was Verified. No Deployment Sent.");
 }
 static DailyScreen presets(DailyAutomation a)throws Exception{return a.waitFor("Focused Squad Preset",s->s.weekly!=null&&s.weekly.presets&&!s.weekly.presetName.isEmpty()&&s.weekly.presetColumn>=0);}
}

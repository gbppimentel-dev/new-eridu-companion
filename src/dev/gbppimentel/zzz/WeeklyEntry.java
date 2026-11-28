package dev.gbppimentel.zzz;
import java.io.IOException;
/** Approach the visible staging waypoint in guarded, released-stick pulses. */
final class WeeklyEntry {
 static void enter(DailyAutomation a)throws Exception{
  a.task("Weekly Boss · Entering The Arena");a.enteringWeekly=true;a.combatHud=new CombatHudReader(a.dir);
  long until=System.currentTimeMillis()+180000;int moves=0;boolean observed=false;
  try{while(System.currentTimeMillis()<until){
   DailyScreen f=a.screen(true);boolean team=f.combat!=null&&f.combat.team.equals(CombatRotation.TEAM);
   if(f.bossBar&&team){a.completed.put("Boss Arena Entered · Squad Verified");return;}
   if(team&&f.weekly!=null&&f.weekly.waypointX>=0){
    if(!WeeklyPolicy.stagingMarker(f.weekly.waypointX,moves,true,f.bossBar))throw new IOException("Could Not Verify Arena Entry. Movement Stopped.");
    observed=true;a.input="FORWARD";a.inputAt=System.currentTimeMillis();a.publish("running","Approaching The Arena");
    a.controller.hold("FORWARD",500,()->a.checkCombatHold());moves++;
   }else{a.publish("waiting",observed?"Waiting For The Boss To Appear":"Confirming Squad And Arena Waypoint");Thread.sleep(300);}
  }throw new IOException("Boss Staging Entry Was Not Verified. Take Over In Game.");
  }finally{a.enteringWeekly=false;a.combatHud=null;}
 }
}

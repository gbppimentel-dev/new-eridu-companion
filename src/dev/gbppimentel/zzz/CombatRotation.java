package dev.gbppimentel.zzz;

import java.util.*;

/** Small, observed-HUD rotation for the user's farming squad. No speculative swaps. */
final class CombatRotation {
 static final String CLARET="1611",KOLEDA="1101",RINA="1211";
 static final Set<String> TEAM=new HashSet<>(Arrays.asList(CLARET,KOLEDA,RINA));
 static final class Frame {
  long at;String active="";Set<String> team=new HashSet<>();
  boolean skillReady,ultimateReady,chain,squadLost,controls,lowHealth;
  String leftChain="",rightChain="";double health=-1;
 }
 static final class Action {
  final String key,label;final int holdMs,settleMs;final long at;
  Action(String key,int hold,int settle,String label,long at){this.key=key;holdMs=hold;settleMs=settle;this.label=label;this.at=at;}
 }
 private String candidate="",active="",pendingSwapFrom="",supportExit="",lastChainChoices="";
 private int seen,skillSeen,ultimateSeen,ultimateOff,basicStep,claretSkills,swapAttempts;
 private final Set<String> spentUltimates=new HashSet<>();
 private long activeAt,lastFrameAt,notBefore,swapAt,lastSwapTry,lastSupportVisit,lastEnhance,lastChain;
 private boolean established,dodgePending,hurtDodge;
 private final Map<String,Double> health=new HashMap<>();
 private long lastDodge;
 private Action action(String key,int hold,int settle,String label,long now){
  notBefore=now+hold+settle;return new Action(key,hold,settle,label,now);
 }
 static String name(String id){return CLARET.equals(id)?"Claret":KOLEDA.equals(id)?"Koleda":RINA.equals(id)?"Rina":"Agent";}
 Action next(Frame f,long now){
  if(f==null||f.squadLost||f.at<=lastFrameAt||now<f.at||now-f.at>900){candidate="";seen=skillSeen=ultimateSeen=0;return null;}
  if(lastFrameAt>0&&f.at-lastFrameAt>1200){candidate="";seen=skillSeen=ultimateSeen=0;}
  lastFrameAt=f.at;
  // Chain decisions take priority, including over a skill's animation wait.
  if(f.chain){
   if(!f.team.equals(TEAM))return null;
   candidate="";seen=skillSeen=ultimateSeen=0;
   String choices=f.leftChain+":"+f.rightChain;
   if(now-lastChain<900||choices.equals(lastChainChoices))return null;
   String key="",target="";
   if(CLARET.equals(f.leftChain)){key="L1";target=CLARET;}
   else if(CLARET.equals(f.rightChain)){key="R1";target=CLARET;}
   // If Claret triggered the chain, she cannot be one of its choices.
   else if(RINA.equals(f.leftChain)||KOLEDA.equals(f.leftChain)){key="L1";target=f.leftChain;}
   else if(RINA.equals(f.rightChain)||KOLEDA.equals(f.rightChain)){key="R1";target=f.rightChain;}
   if(key.isEmpty())return null; // Unknown choices are allowed to time out in game.
   lastChain=now;lastChainChoices=choices;pendingSwapFrom="";supportExit="";
   return action(key,140,550,"Chain Attack · "+name(target),now);
  }
  lastChainChoices="";
  if(!f.team.equals(TEAM)||!TEAM.contains(f.active)){
   candidate="";seen=skillSeen=ultimateSeen=0;return null;
  }
  if(!f.active.equals(candidate)){candidate=f.active;seen=1;skillSeen=ultimateSeen=0;}
  else seen++;
  if(seen<2)return null;
  if(!f.active.equals(active)){
   active=f.active;activeAt=now;dodgePending=hurtDodge=false;skillSeen=ultimateSeen=ultimateOff=basicStep=0;
   if(CLARET.equals(active))lastSupportVisit=now;
   // A swap is acknowledged only by a new portrait, never by the sent input.
   if(!active.equals(pendingSwapFrom))pendingSwapFrom="";
   if(!active.equals(supportExit))supportExit="";
  }
  if(f.health>=0){Double before=health.put(active,f.health);if(CLARET.equals(active)&&before!=null&&before-f.health>.05)hurtDodge=true;}
  skillSeen=f.skillReady?skillSeen+1:0;
  ultimateSeen=f.ultimateReady?ultimateSeen+1:0;
  ultimateOff=f.ultimateReady?0:ultimateOff+1;
  if(ultimateOff>=2)spentUltimates.remove(active);
  if(!established){lastSupportVisit=now;established=true;}
  if(now<notBefore)return null;
  if(!pendingSwapFrom.isEmpty()){
   // A swap can be swallowed during an EX/impact animation. Retry only while
   // fresh, repeated portraits still prove the original agent is on field.
   if(active.equals(pendingSwapFrom)&&seen>=3&&swapAttempts<3&&now-lastSwapTry>=1800){swapAttempts++;lastSwapTry=now;return action("R1",140,650,"Retry Confirmed Agent Swap",now);}
   return null;
  }
  if(active.equals(supportExit))return swap(now,"Quick Swap After "+name(active)+" EX");
  if(ultimateSeen>=2&&!spentUltimates.contains(active)){
   spentUltimates.add(active);
   ultimateSeen=0;
   return action("R2",140,2400,name(active)+" · Ultimate",now);
  }
  if(RINA.equals(active)){
   if(now-activeAt<500||seen<4)return null;
   if(skillSeen>=2){
    supportExit=active;skillSeen=0;
    return action("Y",1150,350,"Rina · Held EX",now);
   }
   return swap(now,"Rina · Return To Damage Rotation");
  }
  if(KOLEDA.equals(active)){
   if(now-activeAt<500||seen<4)return null;
   if(skillSeen>=2){
    supportExit=active;skillSeen=0;
    return action("Y",160,750,"Koleda · EX",now);
   }
   return swap(now,"Koleda · Return To Damage Rotation");
  }
  if(seen<3)return null;
  if(dodgePending||hurtDodge&&now-lastDodge>=850||f.lowHealth&&now-lastDodge>=1700){dodgePending=hurtDodge=false;lastDodge=now;return action("A",120,180,"Claret · Defensive Dodge",now);}
  if(skillSeen>=2&&now-lastEnhance>=6500&&basicStep==0){
   lastEnhance=now;skillSeen=0;
   if(claretSkills++%2==1){return action("Y",1550,350,"Claret · Held EX Follow-Up",now);}
   return action("Y",160,350,"Claret · Crimson Inscription",now);
  }
  if(now-lastSupportVisit>=16000&&basicStep==0){lastSupportVisit=now;return swap(now,"Refresh Support Skills");}
  // Her enhanced basic has three stages; hold the third instead of scattering holds.
  int hit=basicStep++%3;
  if(hit==2){basicStep=0;dodgePending=true;return action("X",1450,350,"Claret · Held Basic Finisher",now);}
  return action("X",110,hit==0?330:420,"Claret · Basic Combo "+(hit+1)+" / 3",now);
 }
 private Action swap(long now,String label){pendingSwapFrom=active;swapAt=lastSwapTry=now;swapAttempts=1;supportExit="";return action("R1",140,650,label,now);}
 boolean swapUnconfirmed(long now){return !pendingSwapFrom.isEmpty()&&now-swapAt>9000;}
}

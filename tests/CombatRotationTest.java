package dev.gbppimentel.zzz;
import java.util.*;
public final class CombatRotationTest {
 static int checks;static void check(boolean v,String why){checks++;if(!v)throw new AssertionError(why);}
 static final class Driver {
  final CombatRotation rotation=new CombatRotation();long now=100000;String agent;boolean skill,ult;
  Driver(String id,boolean ready){agent=id;skill=ready;}
  CombatRotation.Frame frame(){CombatRotation.Frame f=new CombatRotation.Frame();f.at=now;f.active=agent;f.team.addAll(CombatRotation.TEAM);f.skillReady=skill;f.ultimateReady=ult;return f;}
  CombatRotation.Action tick(long advance){now+=advance;return rotation.next(frame(),now);}
  CombatRotation.Action untilAction(){for(int i=0;i<30;i++){CombatRotation.Action a=tick(200);if(a!=null)return a;}throw new AssertionError("No action for "+agent);}
  CombatRotation.Action after(CombatRotation.Action a){return tick(a.holdMs+a.settleMs+30);}
 }
 static void key(CombatRotation.Action a,String key,String why){check(a!=null&&a.key.equals(key),why);}
 public static void main(String[] args){
  Driver d=new Driver(CombatRotation.KOLEDA,true);CombatRotation.Action a=d.untilAction();key(a,"Y","Koleda uses ready EX");check(a.holdMs<500,"Koleda tap");
  a=d.after(a);key(a,"R1","Koleda swaps after EX");check(d.tick(1200)==null,"No repeated unconfirmed swaps");check(d.rotation.swapUnconfirmed(d.now+9100),"Unacknowledged swap times out");
  d.agent=CombatRotation.RINA;a=d.untilAction();key(a,"Y","Rina uses ready EX");check(a.holdMs>1000,"Rina holds EX");
  a=d.after(a);if(a==null)a=d.untilAction();key(a,"R1","Rina swaps after held EX and fresh portrait confirmation");
  d.agent=CombatRotation.CLARET;a=d.untilAction();key(a,"Y","Claret enhances before basic combo");check(a.holdMs<500,"First enhancement tap");
  a=d.after(a);key(a,"X","Claret basic one");check(a.holdMs<500,"First basic is a tap");
  a=d.after(a);key(a,"X","Claret basic two");check(a.holdMs<500,"Second basic is a tap");
  a=d.after(a);key(a,"X","Claret finisher");check(a.holdMs>1000,"Third basic is held");
  a=d.after(a);if(a==null)a=d.untilAction();key(a,"A","Dodge after a held finisher");
  boolean heldEx=false;for(int i=0;i<25;i++){a=d.after(a);if(a==null)a=d.untilAction();if(a.key.equals("Y")&&a.holdMs>1000){heldEx=true;break;}}
  check(heldEx,"Claret periodically continues into held EX follow-ups");
  Driver swallowed=new Driver(CombatRotation.KOLEDA,false);CombatRotation.Action firstSwap=swallowed.untilAction();key(firstSwap,"R1","Initial support swap");
  CombatRotation.Action retry=swallowed.tick(1900);if(retry==null)retry=swallowed.untilAction();key(retry,"R1","Retry only when fresh portraits still show the same agent");
  retry=swallowed.tick(1900);if(retry==null)retry=swallowed.untilAction();key(retry,"R1","One final bounded same-agent retry");check(swallowed.tick(1900)==null,"No endless swap retries");
  swallowed.agent=CombatRotation.RINA;CombatRotation.Action resumed=swallowed.untilAction();key(resumed,"R1","A new confirmed agent resumes its own rotation");
  Driver noEnergy=new Driver(CombatRotation.RINA,false);a=noEnergy.untilAction();key(a,"R1","No unpowered Rina basics or EX spam");
  noEnergy=new Driver(CombatRotation.KOLEDA,false);a=noEnergy.untilAction();key(a,"R1","No unpowered Koleda basics or EX spam");
  Driver ult=new Driver(CombatRotation.CLARET,false);ult.ult=true;a=ult.untilAction();key(a,"R2","Ready Ultimate takes priority");
  a=ult.after(a);if(a==null)a=ult.untilAction();check(!a.key.equals("R2"),"Unchanged ready icon cannot spam Ultimate");
  Driver independent=new Driver(CombatRotation.KOLEDA,false);independent.ult=true;a=independent.untilAction();key(a,"R2","Koleda ready Ultimate");
  CombatRotation.Frame bridge=independent.frame();bridge.at=++independent.now;bridge.chain=true;bridge.rightChain=CombatRotation.CLARET;
  a=independent.rotation.next(bridge,independent.now);key(a,"R1","Chain into Claret");
  independent.agent=CombatRotation.CLARET;independent.now+=a.holdMs+a.settleMs;
  a=independent.untilAction();key(a,"R2","Other agent Ultimate has an independent readiness latch");
  Driver hurt=new Driver(CombatRotation.CLARET,false);CombatRotation.Action swing=hurt.untilAction();
  CombatRotation.Frame hp=hurt.frame();hp.at=hurt.now+=swing.holdMs+swing.settleMs+30;hp.health=.8;hurt.rotation.next(hp,hurt.now);
  hp=hurt.frame();hp.at=hurt.now+=700;hp.health=.6;CombatRotation.Action dodge=hurt.rotation.next(hp,hurt.now);key(dodge,"A","Observed HP loss prompts a defensive dodge");
  Driver lost=new Driver(CombatRotation.CLARET,true);CombatRotation.Frame missing=lost.frame();missing.squadLost=true;check(lost.rotation.next(missing,lost.now)==null,"Do not fight with an incomplete squad");
  Driver stale=new Driver(CombatRotation.CLARET,true);CombatRotation.Frame f=stale.frame();f.at-=901;check(stale.rotation.next(f,stale.now)==null,"Stale HUD ignored");
  f=stale.frame();f.team.remove(CombatRotation.RINA);check(stale.rotation.next(f,stale.now)==null,"Incomplete team ignored");
  stale.now+=200;f=stale.frame();f.active="1191";check(stale.rotation.next(f,stale.now)==null,"Other teams do not receive this rotation");
  Driver chain=new Driver(CombatRotation.KOLEDA,true);a=chain.untilAction();f=chain.frame();f.at=++chain.now;f.chain=true;f.leftChain=CombatRotation.RINA;f.rightChain=CombatRotation.CLARET;
  a=chain.rotation.next(f,chain.now);key(a,"R1","Choose right-hand Claret even during EX settle");
  f.at=chain.now+=1200;check(chain.rotation.next(f,chain.now)==null,"Do not resubmit identical chain choice");
  f.at=++chain.now;f.leftChain=CombatRotation.CLARET;f.rightChain=CombatRotation.KOLEDA;a=chain.rotation.next(f,chain.now);key(a,"L1","Choose left-hand Claret");
  f.at=chain.now+=1200;f.leftChain="";f.rightChain="";check(chain.rotation.next(f,chain.now)==null,"Unknown chain choices receive no input");
  f.at=++chain.now;f.leftChain=CombatRotation.RINA;f.rightChain=CombatRotation.KOLEDA;a=chain.rotation.next(f,chain.now);key(a,"L1","Accept support chain when Claret is not a choice");
  System.out.println(checks+" combat rotation checks passed");
 }
}

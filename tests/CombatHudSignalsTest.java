package dev.gbppimentel.zzz;
import java.util.*;
public final class CombatHudSignalsTest {
 static int checks;static void check(boolean v,String why){checks++;if(!v)throw new AssertionError(why);}
 static void circle(int[] p,int w,int h,int cx,int cy,int radius,int color){for(int y=Math.max(0,cy-radius);y<Math.min(h,cy+radius);y++)for(int x=Math.max(0,cx-radius);x<Math.min(w,cx+radius);x++)if((x-cx)*(x-cx)+(y-cy)*(y-cy)<radius*radius)p[y*w+x]=color;}
 public static void main(String[] args){
  int w=960,h=540;int[] p=new int[w*h];Arrays.fill(p,0xff555555);
  check(!CombatHudSignals.skillReady(p,w,h,CombatRotation.RINA),"Grey EX unavailable");check(!CombatHudSignals.ultimateReady(p,w,h),"Grey Ultimate unavailable");check(!CombatHudSignals.controllerVisible(p,w,h),"No controller glyph");
  circle(p,w,h,835,484,16,0xffb441ec);check(CombatHudSignals.skillReady(p,w,h,CombatRotation.RINA),"Purple Rina EX ready");check(CombatHudSignals.skillReady(p,w,h,CombatRotation.KOLEDA),"Purple Koleda EX ready");check(!CombatHudSignals.skillReady(p,w,h,CombatRotation.CLARET),"Claret requires red Sharpness icon");
  circle(p,w,h,835,484,16,0xffed263d);check(CombatHudSignals.skillReady(p,w,h,CombatRotation.CLARET),"Claret Sharpness ready");check(!CombatHudSignals.skillReady(p,w,h,CombatRotation.RINA),"Other red effects not Rina energy");
  circle(p,w,h,890,415,16,0xffffcf20);check(CombatHudSignals.ultimateReady(p,w,h),"Gold Ultimate ready");
  Arrays.fill(p,0xff555555);circle(p,w,h,835,516,10,0xffffcf20);check(!CombatHudSignals.skillReady(p,w,h,CombatRotation.RINA),"Yellow Y legend excluded");
  circle(p,w,h,727,517,6,0xff1fbcf0);check(CombatHudSignals.controllerVisible(p,w,h),"Blue X controller label");
  check(CombatHudSignals.chainPrompt("CHAIN ATTACK"),"Chain prompt normalization");check(!CombatHudSignals.chainPrompt("Basic Attack"),"Basic text is not chain prompt");
  check(CombatHudSignals.chainAgent("LB Claret Flint","L1").equals(CombatRotation.CLARET),"Left Claret option");check(CombatHudSignals.chainAgent("Claret Flint RB","R1").equals(CombatRotation.CLARET),"Right Claret option");
  check(CombatHudSignals.chainAgent("Claret Flint","R1").isEmpty(),"Names without button proof ignored");check(CombatHudSignals.chainAgent("LB Claret Flint","R1").isEmpty(),"Wrong side legend ignored");
  check(CombatHudSignals.chainAgent("LB Koleda","L1").equals(CombatRotation.KOLEDA),"Known support option");check(CombatHudSignals.chainAgent("LB Unknown","L1").isEmpty(),"Unknown option ignored");
  System.out.println(checks+" combat HUD checks passed");
 }
}

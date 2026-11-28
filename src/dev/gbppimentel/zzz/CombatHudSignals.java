package dev.gbppimentel.zzz;

import java.util.Locale;
import java.util.regex.Pattern;

/** Controller-HUD hints from small, fixed icon interiors, excluding button legends. */
final class CombatHudSignals {
 enum Ink { PURPLE, RED, GOLD, CYAN }
 static double ink(int[] pixels,int width,int height,double cx,double cy,double radius,Ink ink){
  int n=0,hit=0;double sx=width/1920.0,sy=height/1080.0;
  int left=Math.max(0,(int)((cx-radius)*sx)),right=Math.min(width,(int)((cx+radius)*sx));
  int top=Math.max(0,(int)((cy-radius)*sy)),bottom=Math.min(height,(int)((cy+radius)*sy));
  for(int y=top;y<bottom;y++)for(int x=left;x<right;x++){
   double dx=(x/sx-cx)/radius,dy=(y/sy-cy)/radius;if(dx*dx+dy*dy>1)continue;
   int c=pixels[y*width+x],r=c>>16&255,g=c>>8&255,b=c&255;n++;
   boolean match=ink==Ink.PURPLE?b>110&&r>80&&b>g*1.3&&r>g*1.2:
    ink==Ink.RED?r>160&&g<105&&b<135:
    ink==Ink.GOLD?r>160&&g>115&&b<g*.75:b>110&&g>90&&r<g*.7;
   if(match)hit++;
  }
  return n==0?0:(double)hit/n;
 }
 static boolean controllerVisible(int[] p,int w,int h){return ink(p,w,h,1453,1035,13,Ink.CYAN)>.045;}
 static double healthRatio(int[] p,int w,int h){
  int green=0,total=0;
  for(int y=55*h/1080;y<63*h/1080;y++)for(int x=270*w/1920;x<565*w/1920;x++){
   int c=p[y*w+x],r=c>>16&255,g=c>>8&255,b=c&255;total++;if(g>100&&g>r*1.25&&g>b*1.25)green++;
  }
  return total==0?-1:(double)green/total;
 }
 static boolean lowHealth(int[] p,int w,int h){double health=healthRatio(p,w,h);return health>=0&&health<.3;}
 static boolean skillReady(int[] p,int w,int h,String agent){
  return CombatRotation.CLARET.equals(agent)?ink(p,w,h,1670,968,32,Ink.RED)>.16:ink(p,w,h,1670,968,32,Ink.PURPLE)>.16;
 }
 static boolean ultimateReady(int[] p,int w,int h){return ink(p,w,h,1780,830,32,Ink.GOLD)>.16;}
 static String clean(String text){return text.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+"," ").trim();}
 static boolean chainPrompt(String text){return clean(text).contains("chain attack");}
 static String chainAgent(String text,String button){
  String clean=clean(text),legend=button.equals("L1")?"(?:lb|l1)":"(?:rb|r1)";
  if(!Pattern.compile("(?:^| )"+legend+"(?: |$)").matcher(clean).find())return "";
  if(clean.contains("claret"))return CombatRotation.CLARET;
  if(Pattern.compile("(?:^| )rina(?: |$)").matcher(clean).find())return CombatRotation.RINA;
  if(clean.contains("koleda"))return CombatRotation.KOLEDA;
  return "";
 }
}

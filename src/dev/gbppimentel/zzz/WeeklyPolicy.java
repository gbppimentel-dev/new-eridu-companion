package dev.gbppimentel.zzz;
import java.util.regex.*;
/** Free weekly rewards only. No paid repeats or Battery top-ups. */
final class WeeklyPolicy {
 static final long WEEK_MS=604800000L,MONDAY_RESET=331200000L;
 static long cycle(long at){return Math.floorDiv(at-MONDAY_RESET,WEEK_MS);}
 static long nextReset(long at){return MONDAY_RESET+(cycle(at)+1)*WEEK_MS;}
 static boolean current(long now,long at){return at>0&&at<=now&&cycle(now)==cycle(at);}
 static int remaining(String text){Matcher m=Pattern.compile("(?<![0-9])([0-3])\\s*/\\s*3(?![0-9])").matcher(text==null?"":text);return m.find()?Integer.parseInt(m.group(1)):-1;}
 static int remainingDigits(String value,String total){String n=value==null?"":value.trim(),d=total==null?"":total.trim();return n.matches("[0-3]")&&d.equals("3")?Integer.parseInt(n):-1;}
 static boolean clearedResult(String objective,String progress,String footer){String action=DailyPolicy.clean(footer);return DailyPolicy.clean(objective).contains("complete notorious")&&progress!=null&&progress.replace(" ","").equals("1/1")&&(action.equals("complete")||action.equals("exit"));}
 static boolean requiredSquad(String first,String second,String third){return DailyPolicy.clean(first).equals("koleda")&&DailyPolicy.clean(second).equals("rina")&&DailyPolicy.clean(third).equals("claret");}
 static boolean stagingMarker(int x,int moves,boolean team,boolean boss){return team&&!boss&&x>=880&&x<=1040&&moves<12;}
 static boolean bossBar(int[] p){if(p==null||p.length!=480*270)return false;int green=0,total=0;for(int y=13;y<16;y++)for(int x=288;x<423;x++){int c=p[y*480+x],r=c>>16&255,g=c>>8&255,b=c&255;total++;if(g>125&&g>r*1.12&&g>b*1.6)green++;}return green>total*.45;}
 static int focusedTile(int[] p){
  if(p==null||p.length!=480*270)return -1;int best=-1;double high=.55;
  for(int i=0;i<9;i++){int l=new int[]{120,436,750}[i%3]/4,t=new int[]{240,470,700}[i/3]/4,r=l+78,b=t+57;double score=0;
   for(int offset=-1;offset<=1;offset++){int hit=0,total=0;
    for(int x=l+6;x<r-6;x++){for(int y:new int[]{t+offset,b-1+offset}){total++;if(border(p[y*480+x]))hit++;}}
    for(int y=t+6;y<b-6;y++){for(int x:new int[]{l+offset,r-1+offset}){total++;if(border(p[y*480+x]))hit++;}}
    score=Math.max(score,(double)hit/total);
   }if(score>high){high=score;best=i;}
  }return best;
 }
 static boolean huntClaim(String caption,double white,double colored){return DailyPolicy.clean(caption).equals("claim all")&&(white>.12||colored>.12);}
 static boolean activeClaim(int c){int r=c>>16&255,g=c>>8&255,b=c&255;return r>65&&g>120&&b<100&&g>r*.7&&r>g*.45;}
 static boolean border(int c){int r=c>>16&255,g=c>>8&255,b=c&255;return r>65&&g>100&&g>r*.7&&r>g*.45&&b<90&&g>b*1.5;}
 static boolean exhaustedReturn(boolean weekly,boolean controller,boolean details,int remaining){return weekly&&controller&&details&&remaining==0;}
 static boolean freeDeploy(int remaining,String expected,String visible,boolean next,boolean paid){return remaining>0&&remaining<=3&&!paid&&next&&!DailyPolicy.clean(expected).isEmpty()&&DailyPolicy.clean(expected).equals(DailyPolicy.clean(visible));}
 static boolean freeSquad(int remaining,long setupAge,boolean deploy,boolean resourceCost,boolean paid){return remaining>0&&remaining<=3&&setupAge>=0&&setupAge<20000&&deploy&&!resourceCost&&!paid;}
 static boolean quotaConsumed(int before,int after){return before>=1&&before<=3&&after==before-1;}
 static boolean bossName(String name){return name!=null&&name.length()>=3&&name.length()<=100&&name.matches("[A-Za-z][A-Za-z0-9 '\\-:,.!?&]+")&&!DailyPolicy.forbidden(name);}
 static boolean paid(String text){String s=DailyPolicy.clean(text);return DailyPolicy.forbidden(s)||s.contains("battery charge")||s.contains("restore battery")||s.contains("replenish")||s.contains("auto combat")||s.contains("ether batter")||s.contains("polychrome");}
}

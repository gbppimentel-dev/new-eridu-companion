package dev.gbppimentel.zzz;
import java.io.*;import java.util.*;import org.json.*;
/** Weekly menus use the same guarded controller/session runtime as daily errands. */
final class WeeklyAutomation {
 final DailyAutomation a;JSONObject h;int quota=-1;
 WeeklyAutomation(DailyAutomation automation){a=automation;}
 WeeklyScreen w(DailyScreen f)throws IOException{if(f.weekly==null)throw new IOException("Weekly Screen Could Not Be Read");return f.weekly;}
 DailyScreen hunt()throws Exception{
  DailyScreen f=a.training("notorious hunt");f=a.waitFor("Notorious Hunt Free Rewards",s->s.weekly!=null&&s.weekly.huntList&&s.weekly.remaining>=0);
  quota=w(f).remaining;h.put("remaining",quota).put("quotaAt",System.currentTimeMillis());WeeklyHistory.save(a.dir,a.uid,h);a.weeklyRemaining=quota;a.publish("running",a.step);return f;
 }
 void claimReceipt()throws Exception{
  DailyScreen f=a.waitFor("Weekly Claim Result",s->s.weekly!=null&&(s.weekly.receipt||s.weekly.board&&!s.weekly.claim||s.weekly.huntChallenge&&!s.weekly.challengeClaim));
  if(w(f).receipt){a.press("A");a.waitFor("Weekly Reward Page",s->s.weekly!=null&&(s.weekly.board||s.weekly.huntChallenge));}
 }
 void claims()throws Exception{
  a.task("Ridu Weekly · Claims");DailyScreen f=a.journal();
  f=a.focusedErrand();for(int i=0;i<24&&!f.weeklySelected;i++){a.press("LEFT");f=a.focusedErrand();}
  if(!f.weeklySelected)throw new IOException("Ridu Weekly Card Could Not Be Confirmed");a.press("A");
  f=a.waitFor("Ridu Weekly",s->s.weekly!=null&&s.weekly.board&&s.weekly.tile>=0);
  // Grid edges wrap. Move only while a fresh, verified tile is away from the origin.
  for(int i=0;i<8;i++){f=a.waitFor("Weekly Grid Focus",s->s.weekly!=null&&s.weekly.board&&s.weekly.tile>=0);int current=w(f).tile;if(current==0)break;a.press(current/3>0?"UP":"LEFT");}
  for(int index=0;index<9;index++){
   int row=index/3,col=index%3,expected=row*3+(row%2==0?col:2-col);final int tile=expected;
   f=a.waitFor("Weekly Task "+(expected+1),s->s.weekly!=null&&s.weekly.board&&s.weekly.tile==tile);
   if(w(f).claim){a.publish("running","Claiming Weekly Task "+(expected+1)+" / 9");a.press("A");claimReceipt();f=a.waitFor("Claimed Weekly Task",s->s.weekly!=null&&s.weekly.board&&s.weekly.tile==tile&&!s.weekly.claim);a.weeklyClaims++;}
   if(index<8)a.press(col==2?"DOWN":row%2==0?"RIGHT":"LEFT");
  }
  for(int pass=0;pass<3;pass++){f=a.waitFor("Weekly Milestones",s->s.weekly!=null&&s.weekly.board);if(!w(f).milestone)break;a.press("X");claimReceipt();a.weeklyClaims++;if(pass==2&&w(a.screen(false)).milestone)throw new IOException("Weekly Milestone Claim Needs Review");}
  h.put("claimsAt",System.currentTimeMillis());WeeklyHistory.save(a.dir,a.uid,h);a.press("B");f=a.waitFor("The Handbook",DailyScreen::journal);a.closeJournal(f);a.completed.put("Ridu Weekly Claims Checked");
 }
 void huntClaims()throws Exception{
  a.task("Notorious Hunt · Weekly Rewards");DailyScreen f=hunt();
  // The Weekly Challenge control is above the boss list. Verify focus before opening it.
  a.focusTarget(f,1470,370,true);f=a.waitFor("Weekly Challenge Rewards",s->s.weekly!=null&&s.weekly.huntChallenge);
  if(w(f).challengeClaim){a.press("X");claimReceipt();f=a.waitFor("Weekly Challenge Claimed",s->s.weekly!=null&&s.weekly.huntChallenge&&!s.weekly.challengeClaim);a.weeklyClaims++;}
  a.press("B");f=a.waitFor("Notorious Hunt",s->s.weekly!=null&&s.weekly.huntList);a.closeJournal(f);a.completed.put("Notorious Hunt Rewards Checked");
 }
 DailyScreen firstBoss()throws Exception{DailyScreen f=hunt();a.focusTarget(f,1470,370,false);a.press("DOWN");return a.waitFor("Focused Weekly Boss",s->s.weekly!=null&&s.weekly.huntList&&!s.stageTitles.isEmpty());}
 void scan()throws Exception{
  a.task("Reading Weekly Bosses");JSONArray bosses=new JSONArray();Set<String> seen=new HashSet<>(),positions=new HashSet<>();DailyScreen f=firstBoss();boolean end=false;
  for(int i=0;i<40;i++){
   String sig=f.stageTitles.toString();if(!positions.add(sig)){end=true;break;}
   for(String name:f.stageTitles.values()){if(!WeeklyPolicy.bossName(name))throw new IOException("Boss Name Was Not Readable");if(seen.add(name))bosses.put(new JSONObject().put("name",name));}
   a.press("DOWN");f=a.waitFor("Weekly Boss List",s->s.weekly!=null&&s.weekly.huntList&&(!s.stageTitles.isEmpty()||s.weekly.huntTop));if(w(f).huntTop){end=true;break;}
  }
  if(!end||bosses.length()==0)throw new IOException("Weekly Boss List Did Not Reach A Verified End");
  a.write("weekly-bosses.json",new JSONObject().put("uid",a.uid).put("at",System.currentTimeMillis()).put("bosses",bosses));a.closeJournal(f);a.completed.put("Weekly Boss List Updated");
 }
 DailyScreen setup(String name)throws Exception{
  DailyScreen f=firstBoss();Set<String> seen=new HashSet<>();boolean selected=false;
  for(int i=0;i<40;i++){
   if(!seen.add(f.stageTitles.toString()))break;
   for(Map.Entry<Integer,String> entry:f.stageTitles.entrySet())if(DailyPolicy.clean(entry.getValue()).equals(DailyPolicy.clean(name))){a.focusTarget(f,1470,entry.getKey(),true);selected=true;break;}
   if(selected)break;a.press("DOWN");f=a.waitFor("Selected Boss",s->s.weekly!=null&&s.weekly.huntList&&(!s.stageTitles.isEmpty()||s.weekly.huntTop));if(w(f).huntTop)break;
  }
  if(!selected)throw new IOException("Selected Weekly Boss Was Not Found. Update The Boss List.");
  long until=System.currentTimeMillis()+DailyPolicy.SCREEN_LIMIT_MS;
  while(System.currentTimeMillis()<until){f=a.screen(false);if(w(f).huntSetup)return f;if(f.has("teleport now")||f.has("teleport to")){a.tap(f,1115,628,"A");}else if(f.world())throw new IOException("Boss Service Needs Attention. Open Notorious Hunt And Retry.");Thread.sleep(500);}
  throw new IOException("Weekly Boss Setup Did Not Open");
 }
 void bosses()throws Exception{
  String name=a.plan.optString("boss");
  DailyScreen f=hunt();int planned=quota;if(planned>0&&!WeeklyPolicy.bossName(name))throw new IOException("Choose A Weekly Boss First");a.estimatedBattles=planned;a.stageTotal=4+planned;a.estimatedDuration=System.currentTimeMillis()-a.started+planned*h.optLong("bossCycleEstimateMs",420000)+180000;a.closeJournal(f);
  for(int i=0;i<planned;i++){
   if(WeeklyPolicy.cycle(a.started)!=WeeklyPolicy.cycle(System.currentTimeMillis()))throw new IOException("Weekly Reset Reached. Start Again To Recheck Free Rewards");
   long cycleStarted=System.currentTimeMillis();a.task("Notorious Hunt · "+dailyTitle(name)+" · "+(i+1)+" / "+planned);f=setup(name);WeeklyScreen proof=w(f);int before=proof.remaining;
   if(before!=quota||!WeeklyPolicy.freeDeploy(before,name,proof.boss,proof.next,proof.paid))throw new IOException("Free Weekly Deployment Could Not Be Verified");
   a.press("START");f=a.waitFor("Weekly Boss Squad",s->s.weekly!=null&&s.weekly.squad);f=WeeklySquad.ensure(a);
   // Team selection can take time: return to the boss details and reread its free quota.
   a.press("B");f=a.waitFor("Free Boss Reward Recheck",s->s.weekly!=null&&s.weekly.huntSetup);proof=w(f);before=proof.remaining;
   if(before!=quota||!WeeklyPolicy.freeDeploy(before,name,proof.boss,proof.next,proof.paid))throw new IOException("Free Weekly Quota Changed During Squad Selection");long proofAt=f.at;
   a.press("START");f=a.waitFor("Verified Weekly Squad",s->s.weekly!=null&&s.weekly.squad&&s.weekly.squadReady);
   // Reject any readable Battery, consumable or refresh cost. Never fall back to paid mode.
   if(!WeeklyPolicy.freeSquad(before,System.currentTimeMillis()-proofAt,true,f.battery()!=null,WeeklyPolicy.paid(f.region(1250,950,1900,1075))))throw new IOException("Weekly Boss Shows A Resource Cost. No Deployment Sent.");
   a.press("START");WeeklyEntry.enter(a);a.battle(0,true);
   f=a.waitFor("Weekly Boss Results",s->s.results()&&s.weekly!=null&&s.weekly.resultClear);a.press("B");a.waitFor("Post-Boss Menu",s->s.world()||s.weekly!=null&&s.weekly.huntSetup||s.journal());a.closeToCity();f=hunt();int after=quota;a.closeJournal(f);
   if(!WeeklyPolicy.quotaConsumed(before,after))throw new IOException("Weekly Reward Consumption Was Not Confirmed. No Repeat Sent.");
   long cycleDuration=System.currentTimeMillis()-cycleStarted;h.put("bossCycleEstimateMs",(h.optLong("bossCycleEstimateMs",cycleDuration)*2+cycleDuration)/3);WeeklyHistory.save(a.dir,a.uid,h);a.estimatedDuration=System.currentTimeMillis()-a.started+(planned-i-1)*h.optLong("bossCycleEstimateMs")+180000;
   a.stageIndex++;a.completed.put(dailyTitle(name)+" · Free Reward Verified");a.publish("running","Weekly Boss Complete · "+after+" Free Rewards Left");
  }
  if(planned==0)a.completed.put("Weekly Bosses · No Free Rewards Remaining");
 }
 static String dailyTitle(String s){StringBuilder out=new StringBuilder();boolean upper=true;for(char c:s.toCharArray()){out.append(upper?Character.toUpperCase(c):c);upper=Character.isWhitespace(c);}return out.toString();}
 void run(boolean scan)throws Exception{
  h=WeeklyHistory.read(a.dir,a.uid);a.history=h;
  h.put("lastTriggeredAt",a.started).put("lastState","running");WeeklyHistory.save(a.dir,a.uid,h);
  if(scan){scan();return;}
  a.stageTotal=4;int expected=a.plan.optBoolean("bosses",false)?(WeeklyPolicy.current(System.currentTimeMillis(),h.optLong("quotaAt"))?h.optInt("remaining",3):3):0;a.estimatedDuration=360000+Math.max(0,expected)*h.optLong("bossCycleEstimateMs",420000);
  claims();a.stageIndex++;huntClaims();a.stageIndex++;
  if(a.plan.optBoolean("bosses",false))bosses();
  huntClaims();a.stageIndex++;claims();a.stageIndex++;
 }
}

package dev.gbppimentel.zzz;

import android.graphics.Bitmap;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Predicate;
import org.json.*;

/** Opt-in, game-only daily runner. Screen/identity guards precede every input. */
final class DailyAutomation implements Runnable {
 static volatile boolean active,manualStop,weeklyReading;
 final GameContextReader context;
 final File dir=GameObserver.DIR;
 CombatHudReader combatHud;
 RoutineController controller;GameCapture capture;GameCapture.Foreground foreground;OcrEngine ocr;HudClassifier classifier;
 String action="",uid="",runId="",step="",message="",lastConsumed="",input="",errandEvidence="";boolean running,battery,reserve;long started,inputAt;
 int weeklyRemaining=-1,weeklyClaims;
 boolean enteringWeekly;
 JSONObject weeklyEvidence=new JSONObject();
 JSONObject plan,history;boolean errandsRun;long taskStarted,batteryAt;int availableBattery=-1;int stageIndex,stageTotal,estimatedBattles;long estimatedDuration;JSONArray completed=new JSONArray();int spent,battles,reserveUsed,reconnects;
 DailyAutomation(GameContextReader context){this.context=context;}
 static void manualOverride(){if(active)manualStop=true;}
 JSONObject read(File file)throws Exception{if(!file.exists()||file.length()>16384)return new JSONObject();return new JSONObject(new String(Files.readAllBytes(file.toPath()),"UTF-8"));}
 void write(String file,JSONObject json)throws Exception{File next=new File(dir,file+".next"),out=new File(dir,file);try(FileOutputStream f=new FileOutputStream(next)){f.write(json.toString().getBytes("UTF-8"));}next.setReadable(true,false);if(!next.renameTo(out))throw new IOException("Could Not Publish Routine Status");}
 void publish(String state,String text)throws Exception{message=text;write("daily-status.json",new JSONObject().put("id",runId).put("action",action).put("state",state).put("running",running).put("step",step).put("message",text).put("startedAt",started).put("at",System.currentTimeMillis()).put("completed",completed).put("batterySpent",spent).put("reserveUsed",reserveUsed).put("battles",battles).put("input",input).put("inputAt",inputAt).put("inputMode","Controller").put("stageIndex",stageIndex).put("stageTotal",stageTotal).put("estimatedBattles",estimatedBattles).put("estimatedDurationMs",estimatedDuration).put("elapsedMs",System.currentTimeMillis()-started).put("taskStartedAt",taskStarted).put("batteryAvailable",availableBattery).put("batteryReadAt",batteryAt).put("weeklyRemaining",weeklyRemaining).put("weeklyClaims",weeklyClaims).put("weeklyScreen",weeklyEvidence));}
 void task(String title)throws Exception{if(!title.equals(step)){taskStarted=System.currentTimeMillis();}step=title;publish("running",title);}
 void check()throws Exception{
  if(manualStop)throw new InterruptedException("Stopped By Controller Input");
  if(!GameObserver.watching())throw new InterruptedException("Companion Left The Foreground");
  JSONObject command=read(new File(dir,"daily-command.json"));if(command.optString("action").equals("stop")&&!command.optString("id").equals(runId))throw new InterruptedException("Stopped By You");
  if(System.currentTimeMillis()-started>DailyPolicy.RUN_LIMIT_MS)throw new IOException("Routine Time Limit Reached");
  if(!foreground.gameVisible())throw new InterruptedException("Game Left The Foreground");
  JSONObject identity=context.latest;if(!DailyPolicy.canContinue(uid,identity.optString("uid"),identity.optBoolean("uidConfirmed"),System.currentTimeMillis()-identity.optLong("at")))awaitIdentity();
 }
 void awaitIdentity()throws Exception{
  long until=System.currentTimeMillis()+20000;
  while(System.currentTimeMillis()<until){
   if(manualStop||!GameObserver.watching()||!foreground.gameVisible())throw new InterruptedException("Routine Stopped Before Session Confirmation");
   JSONObject command=read(new File(dir,"daily-command.json"));if(command.optString("action").equals("stop")&&!command.optString("id").equals(runId))throw new InterruptedException("Stopped By You");
   JSONObject identity=context.latest;if(DailyPolicy.canContinue(uid,identity.optString("uid"),identity.optBoolean("uidConfirmed"),System.currentTimeMillis()-identity.optLong("at")))return;
   publish("waiting","Confirming Your Linked Account");Thread.sleep(300);
  }throw new IOException("Game Identity Could Not Be Confirmed");
 }
 DailyScreen screen(boolean combat)throws Exception{
  check();foreground.focusGame();Bitmap b=capture.frame();DailyScreen frame;try{frame=new DailyScreen(b,ocr,classifier,combat&&!enteringWeekly);if(combat&&combatHud!=null&&!frame.results()&&!frame.loading())frame.combat=combatHud.read(b,ocr,frame);}finally{b.recycle();}
  if(weeklyReading){WeeklyScreen w=frame.weekly;weeklyEvidence=new JSONObject().put("tab",frame.handbookTab).put("journal",frame.journal()).put("phase",frame.phase).put("hasReader",w!=null);if(w!=null)weeklyEvidence.put("list",w.huntList).put("remaining",w.remaining).put("board",w.board).put("tile",w.tile).put("claim",w.claim).put("setup",w.huntSetup).put("challenge",w.huntChallenge).put("squad",w.squad).put("requiredSquad",w.squadReady).put("presets",w.presets).put("waypointVisible",w.waypointX>=0).put("bossBar",frame.bossBar);}
  if(DailyPolicy.loginOrError(frame.text)){
   DailyScreen.Line reconnect=frame.find("reconnect",950,570,1320,690);
   if(reconnect!=null&&frame.has("log in again")&&reconnects++<3){publish("waiting","Reconnecting To The Game · "+reconnects+" / 3");focusTarget(frame,reconnect.x(),reconnect.y(),true);Thread.sleep(2000);return screen(combat);}
   throw new IOException("Game Login Or Connection Needs Attention");
  }
  if(DailyPolicy.forbidden(frame.text))throw new IOException("Unexpected Resource Or Purchase Screen");
  publish(frame.loading()?"waiting":"running",frame.loading()?"Waiting For The Game To Load":step);return frame;
 }
 DailyScreen waitFor(String label,Predicate<DailyScreen> expected,long timeout)throws Exception{
  long until=System.currentTimeMillis()+timeout;while(System.currentTimeMillis()<until){DailyScreen frame=screen(false);if(expected.test(frame)){Thread.sleep(600);DailyScreen settled=screen(false);if(expected.test(settled))return settled;}publish("waiting","Waiting For "+label);Thread.sleep(350);}throw new IOException("Could Not Verify "+label+". No Further Inputs Sent.");
 }
 DailyScreen waitFor(String label,Predicate<DailyScreen> expected)throws Exception{return waitFor(label,expected,DailyPolicy.SCREEN_LIMIT_MS);}
 void press(String key)throws Exception{check();input=key;inputAt=System.currentTimeMillis();publish("running",step);controller.press(key,140);Thread.sleep(step.startsWith("Farming · Battle")?180:1000);}
 void tap(DailyScreen frame,int x,int y,String semantic)throws Exception{
  check();if(System.currentTimeMillis()-frame.at>8000)throw new IOException("Screen Observation Expired");
  if(semantic.equals("B")){press("B");return;}
  if(y>950&&(frame.has("waves of enemies")||frame.has("combat options"))){if(x>1450){press("START");return;}if(x<1000&&frame.has("challenge reward")){press("Y");return;}}
  if(frame.phase.equals("combat")){press(semantic);return;}
  if(frame.world()){
   if(x==1468&&y==94){controller.chord("L2","A");Thread.sleep(1800);return;}
   if(x==1540&&y==280){press("SELECT");return;}
   if(x==159&&y==89){press("START");return;}
   if(x==1515&&y==814){press("A");return;}
   throw new IOException("Unrecognized City Controller Action");
  }
  if(x==1610&&y==976&&(frame.find("tin master",500,760,1300,880)!=null||frame.find("18",500,760,1300,880)!=null)){press("A");return;}
  if(frame.journal()&&y==156){if(x==984){if(!frame.has("engagement today")){press("L1");}}else if(x==1180){press("R1");}return;}
  if(frame.journal()&&x<430&&y>280){for(int[] box:frame.focusBoxes)if(box[0]>450&&box[1]>290&&box[3]-box[1]>90){press("B");frame=screen(false);break;}}
  focusTarget(frame,x,y,true);
 }
 void focusTarget(DailyScreen frame,int x,int y,boolean confirm)throws Exception{
  // Only confirm a target after its gamepad focus outline contains it.
  for(int attempt=0;attempt<18;attempt++){
   DailyScreen current=attempt==0?frame:screen(false);int[] box=current.focusNear(x,y);
   if(box==null){publish("waiting","Waiting For Controller Focus");Thread.sleep(350);continue;}
   if(x>=box[0]-12&&x<=box[2]+12&&y>=box[1]-16&&y<=box[3]+20){if(confirm)press("A");return;}
   int cx=(box[0]+box[2])/2,cy=(box[1]+box[3])/2;String direction=Math.abs(x-cx)>Math.abs(y-cy)?(x<cx?"LEFT":"RIGHT"):(y<cy?"UP":"DOWN");press(direction);
  }throw new IOException("Controller Could Not Focus The Requested Action");
 }
 void tap(DailyScreen frame,DailyScreen.Line line,String semantic)throws Exception{if(line==null)throw new IOException("Expected Game Control Was Not Found");tap(frame,line.x(),line.y(),semantic);}
 void swipe(DailyScreen frame,int x,int y,int x2,int y2,int ms,String semantic)throws Exception{press(Math.abs(x2-x)>Math.abs(y2-y)?(x2<x?"RIGHT":"LEFT"):(y2<y?"DOWN":"UP"));}
 DailyScreen world()throws Exception{return waitFor("The City",f->f.world());}
 DailyScreen journal()throws Exception{
  DailyScreen f=waitFor("The City Or Handbook",s->s.world()||s.journal());
  if(!f.journal()&&f.phase.equals("shortcuts")){controller.up("L2");Thread.sleep(400);f=screen(false);if(f.phase.equals("shortcuts")){press("B");Thread.sleep(1000);}f=waitFor("The City Or Handbook",s->s.journal()||s.phase.equals("exploring"));}
  if(!f.journal()){tap(f,1468,94,"START");f=waitFor("The Handbook",DailyScreen::journal);}
  for(int i=0;i<6;i++){if(f.errands())return f;press("L1");f=waitFor("The Handbook",DailyScreen::journal);}throw new IOException("Daily Errands Tab Was Not Confirmed");
 }
 void closeJournal(DailyScreen f)throws Exception{if(!f.journal())throw new IOException("Handbook Not Visible");for(int i=0;i<3;i++){press("B");Thread.sleep(1800);f=waitFor("The City Or Handbook",s->s.world()||s.journal());if(f.world()){world();return;}}throw new IOException("Handbook Did Not Close");}
 DailyScreen focusedErrand()throws Exception{return waitFor("Focused Errand Card",s->s.errands()&&(s.weeklySelected||s.focusedCard!=null&&!s.focusedTitle.isEmpty()));}
 DailyScreen card(DailyScreen f,String phrase)throws Exception{
  f=focusedErrand();for(int left=0;left<20;left++){if(f.focusedMatches(phrase))return f;if(f.weeklySelected)break;press("LEFT");f=focusedErrand();}
  if(!f.weeklySelected)throw new IOException("Could Not Confirm The Start Of The Errand List");Set<String> seen=new HashSet<>();
  for(int page=0;page<24;page++){if(f.focusedMatches(phrase))return f;String signature=f.weeklySelected?"weekly":f.focusedTitle+"|"+f.focusedProgress;if(!seen.add(signature))return null;press("RIGHT");f=focusedErrand();}
  throw new IOException("Could Not Finish Reading The Errand List");
 }
 boolean warpErrand(String phrase)throws Exception{
  DailyScreen f=card(journal(),phrase);if(f==null)throw new IOException("Requested Errand Was Not Found. Completion Was Not Assumed.");
  if(f.focusedDone()){errandEvidence="Progress 1/1 In Game";closeJournal(f);return false;}
  if(!f.focusedProgress.replace(" ","").contains("0/1"))throw new IOException("Errand Progress Was Not Readable");
  tap(f,f.focusedCard[2]-45,f.focusedCard[3]-45,"A");Thread.sleep(3000);long until=System.currentTimeMillis()+DailyPolicy.SCREEN_LIMIT_MS;
  while(System.currentTimeMillis()<until){f=screen(false);if(f.world()){world();return true;}if(f.has("teleport now")||f.has("teleport to")){tap(f,1115,628,"A");Thread.sleep(3000);}Thread.sleep(1000);}throw new IOException("Warp Did Not Finish");
 }
 boolean checkpoint(String key,String label)throws Exception{JSONObject tasks=history.optJSONObject("tasks");JSONObject record=tasks==null?null:tasks.optJSONObject(key);if(record==null||!DailyPolicy.taskCurrent(System.currentTimeMillis(),record.optLong("at")))return false;completed.put(label+" · Already Complete");publish("running",label);return true;}
 void checkpointDone(String key,String label,String evidence)throws Exception{JSONObject tasks=history.optJSONObject("tasks");if(tasks==null){tasks=new JSONObject();history.put("tasks",tasks);}tasks.put(key,new JSONObject().put("at",System.currentTimeMillis()).put("evidence",evidence));DailyHistory.save(dir,uid,history);completed.put(label);publish("running",label);}
 boolean allErrandsCurrent(long at){JSONObject tasks=history.optJSONObject("tasks");if(tasks==null)return false;for(String key:new String[]{"coffee","draw","store","temple"}){JSONObject record=tasks.optJSONObject(key);if(record==null||!DailyPolicy.taskCurrent(at,record.optLong("at")))return false;}return true;}
 void coffee()throws Exception{
  task("80-Battery Coffee");if(checkpoint("coffee",step))return;if(!warpErrand("coffee")){checkpointDone("coffee",step,errandEvidence);return;}
  DailyScreen f=world();press("A");
  f=waitFor("Coffee Dialogue",s->!s.world()&&(s.has("tin master")||s.has("coffee")));
  for(int i=0;i<8;i++){
   DailyScreen.Line special=f.find("tin master special",1250,300,1910,750);
   if(special!=null){tap(f,special,"A");break;}
   DailyScreen.Line option=f.find("coffee",1250,300,1910,750);if(option!=null){tap(f,option,"A");break;}
   if(!f.has("tin master"))throw new IOException("Unexpected Coffee Dialogue");press("A");f=screen(false);
  }
  long until=System.currentTimeMillis()+DailyPolicy.SCREEN_LIMIT_MS;boolean ordered=false,coffeeReward=false;
  while(System.currentTimeMillis()<until){f=screen(false);if(f.world()&&coffeeReward){checkpointDone("coffee",step,"80 Battery Reward In Game");return;}
   if(f.obtainedBattery==80||f.has("obtained")&&f.has("battery charge")&&f.has("80")){coffeeReward=true;tap(f,960,730,"A");ordered=true;}else if(f.has("tin master special")&&f.has("order")){
    DailyScreen.Line drink=f.find("tin master special");if(!ordered&&drink!=null){tap(f,drink,"A");f=screen(false);}
    String lower=f.region(1000,700,1900,1075);if(!lower.contains("2000")&&!lower.contains("2 000")&&!lower.contains("2500")&&!lower.contains("free"))throw new IOException("Coffee Price Could Not Be Verified");
    DailyScreen.Line order=f.find("order",900,700,1910,1080);if(order==null)order=f.find("purchase",900,700,1910,1080);tap(f,order,"A");ordered=true;
   }else if(f.has("tin master")&&f.find("no thanks")==null){press("A");}else Thread.sleep(400);
  }throw new IOException("Coffee Completion Was Not Verified");
 }
 void dailyDraw()throws Exception{
  task("Daily Draw");if(checkpoint("draw",step))return;if(!warpErrand("scratch")){checkpointDone("draw",step,errandEvidence);return;}DailyScreen f=screen(false);
  f=world();press("A");
  f=waitFor("Daily Draw",s->!s.world()&&(s.has("surprise box")||s.has("scratch")||s.has("howl")||s.has("divination")));
  boolean obtained=false,selected=false;long until=System.currentTimeMillis()+120000;
  while(System.currentTimeMillis()<until){
   f=screen(false);
   if(f.world()&&obtained){checkpointDone("draw",step,"Daily Draw Reward In Game");return;}
   if(f.has("already claimed")&&f.has("today")){obtained=true;press("B");continue;}
   if(f.has("obtained")||f.has("obtain rewards")||selected&&f.has("confirm")&&f.has("share")){obtained=true;press("A");Thread.sleep(2500);}
   else if(f.has("continue")&&f.has("surprise box")){press("A");Thread.sleep(3000);}
   else if(!selected&&f.has("surprise box")&&f.has("daily")){tap(f,1180,490,"A");selected=true;Thread.sleep(3000);}
   else if(!selected&&(f.has("scratch")||f.has("howl"))){for(int i=0;i<4;i++){f=screen(false);swipe(f,800,450+i*65,1300,450+i*65,350,"A");}selected=true;Thread.sleep(3000);}
   else if(obtained&&!f.world()&&(f.has("surprise box")||f.has("card exchange"))){press("B");}
   else {publish("waiting","Waiting For Daily Draw Animation");Thread.sleep(750);}
  }throw new IOException("Daily Draw Completion Was Not Verified");
 }
 void store()throws Exception{
  task("Video Store");if(checkpoint("store",step))return;if(!warpErrand("store for")){checkpointDone("store",step,errandEvidence);return;}
  DailyScreen f=world();press("A");
  settleStore();
 }
 void verifyStoreOpening()throws Exception{
  publish("running","Verifying Video Store Opening");closeToCity();DailyScreen book=journal(),card=card(book,"store for");boolean verified=card!=null&&card.focusedDone();closeJournal(card==null?screen(false):card);
  if(!verified)throw new IOException("Video Store Opening Was Not Confirmed By Its Errand Card");
  checkpointDone("store","Video Store","Opening Confirmed And Store Errand 1/1 In Game");
 }
 void settleStore()throws Exception{
  DailyScreen f;boolean promoter=false,tapes=false,opened=false;int choices=0,confirmations=0;long until=System.currentTimeMillis()+DailyPolicy.SCREEN_LIMIT_MS;
  while(System.currentTimeMillis()<until){
   f=screen(false);
   if(f.storeOpeningConfirm){if(++confirmations>4)throw new IOException("Video Store Confirmation Did Not Close");publish("running","Confirming Video Store Opening");press("A");opened=true;Thread.sleep(2000);continue;}
   if(f.world()&&opened){verifyStoreOpening();return;}
   if(f.has("business is booming")||f.has("currently open")||f.has("business in progress")){verifyStoreOpening();return;}
   if(f.has("operating with integrity")&&f.has("every denny counts")){press("A");opened=true;Thread.sleep(2500);}
   else if(f.has("ready to open shop")){press("A");opened=true;Thread.sleep(2500);}
   else if(f.has("yesterday s account")){press("B");}
   else if(f.has("promotion style")&&f.has("select promoter")){if(f.has("happy")){press("A");promoter=true;}else if(choices++<30)press("RIGHT");else throw new IOException("No Available Happy Promoter Was Found");}
   else if(f.has("recommended tapes")){press("X");tapes=true;Thread.sleep(1800);}
   else if(f.has("business performance")&&f.has("open")){
    if(!promoter||f.has("select promoter")){tap(f,960,700,"A");}
    else if(!tapes){tap(f,1220,700,"A");}
    else {press("X");opened=true;Thread.sleep(2500);}
   }
   else if(f.has("open for business")){tap(f,f.find("open for business"),"A");opened=true;}
   else if(f.has("obtained")||f.has("obtain rewards")){press("A");Thread.sleep(2000);}
   else if(f.has("18")||f.has("ninne")||f.has("video store")){if(choices++>20)throw new IOException("Video Store Dialogue Did Not Settle");DailyScreen.Line manage=f.find("manage",1200,300,1910,800);if(manage==null)manage=f.find("view business",1200,300,1910,800);if(manage!=null)tap(f,manage,"A");else press("A");}
   else {publish("waiting","Waiting For Video Store");Thread.sleep(750);}
  }throw new IOException("Video Store Completion Was Not Verified");
 }

 void temple()throws Exception{
  task("Suibian Temple");if(checkpoint("temple",step))return;warpQuick("failume heights","suibian temple");
  waitFor("Temple Management",x->x.templeTab>=0||x.templeRewardReceipt);settleTemple();
 }
 void settleTemple()throws Exception{
  long until=System.currentTimeMillis()+DailyPolicy.SCREEN_LIMIT_MS;int claims=0;boolean claimReceipt=false;
  while(System.currentTimeMillis()<until){
   DailyScreen f=screen(false);DailyPolicy.TempleAction action=DailyPolicy.templeAction(f.templeTab,f.templeRewardReceipt,f.templeClaimReady,f.templeStartReady,f.templeRunning);
   switch(action){
    case OPEN_AUTO:
     publish("running","Opening Auto-Manage");press("R1");Thread.sleep(1500);waitFor("Selected Auto-Manage Tab",x->x.templeTab==1,30000);break;
    case CLAIM:
     if(++claims>2)throw new IOException("Temple Revenue Claim Did Not Settle");publish("running","Claiming Temple Revenue");press("X");Thread.sleep(2200);
     waitFor("Temple Revenue Receipt",x->x.templeRewardReceipt||x.templeTab==1&&(x.templeStartReady||x.templeRunning),45000);break;
    case RECEIPT:
     // Only the verified Temple receipt can acknowledge rewards. Business Log's
     // Total Obtained metric is not a reward dialog.
     claimReceipt=true;publish("running","Temple Revenue Received");press("A");Thread.sleep(2000);break;
    case START:
     publish("running","Starting Auto-Manage");press("X");Thread.sleep(2000);waitFor("Auto-Manage Running",x->x.templeTab==1&&x.templeRunning,45000);break;
    case RUNNING:
     waitFor("Verified Auto-Manage Running",x->x.templeTab==1&&x.templeRunning,15000);checkpointDone("temple",step,claimReceipt?"Revenue Claimed And Auto-Manage Running In Game":"Auto-Manage Running In Game");closeToCity();return;
    default:publish("waiting","Waiting For A Verified Temple Screen");Thread.sleep(750);
   }
  }throw new IOException("Temple Management Did Not Settle");
 }
 void rewards()throws Exception{
  task("Daily Rewards");DailyScreen f=journal();
  for(int attempt=0;attempt<5;attempt++){
   if(!f.pendingDailyRewards){if(f.dailyActivityComplete)checkpointDone("rewards",step,"All In-Game Milestones Claimed");else completed.put("Available Daily Rewards Claimed");closeJournal(f);return;}
   if(f.dailyActivityComplete&&!f.pendingDailyRewards){checkpointDone("rewards",step,"All In-Game Milestones Claimed");closeJournal(f);return;}
   press("X");f=waitFor("Daily Reward Result",s->s.has("obtained")||s.has("obtain rewards")||s.errands());
   if(f.has("obtained")||f.has("obtain rewards")){press("A");}Thread.sleep(2000);f=waitFor("Daily Errands",DailyScreen::errands);

  }throw new IOException("Daily Reward Claim Has Not Settled In Game");
 }
 void saveErrandCompletion()throws Exception{long now=System.currentTimeMillis();if(allErrandsCurrent(now)&&DailyHistory.rewardCurrent(history,now))history.put("errandsCompletedAt",now);DailyHistory.save(dir,uid,history);}
 void quickNavTarget(DailyScreen frame,String label,boolean shortcut)throws Exception{
  for(int i=0;i<24;i++){
   DailyScreen f=i==0?frame:screen(false);DailyScreen.Line target=f.find(label,0,shortcut?935:100,1920,shortcut?1075:930);
   int[] focus=shortcut?f.travelShortcutFocus:f.travelAreaFocus;if(target==null){if(shortcut&&focus!=null){press("LEFT");continue;}throw new IOException("Travel Target Moved Out Of View");}
   if(focus==null){Thread.sleep(500);continue;}
   if(target.x()>=focus[0]&&target.x()<=focus[2]&&target.y()>=focus[1]&&target.y()<=focus[3]){press("A");return;}
   int dx=target.x()-(focus[0]+focus[2])/2,dy=target.y()-(focus[1]+focus[3])/2;press(Math.abs(dx)>Math.abs(dy)?dx<0?"LEFT":"RIGHT":dy<0?"UP":"DOWN");
  }throw new IOException("Travel Target Focus Could Not Be Confirmed");
 }
 void warpQuick(String area,String service)throws Exception{
  DailyScreen f=world();tap(f,1540,280,"SELECT");f=waitFor("Travel Map",s->s.has("quick nav"));press("START");
  f=waitFor("Quick Nav",s->s.find(area)!=null);quickNavTarget(f,area,false);f=waitFor("Travel Shortcut",s->s.travelShortcutFocus!=null);quickNavTarget(f,service,true);
  long until=System.currentTimeMillis()+DailyPolicy.SCREEN_LIMIT_MS;while(System.currentTimeMillis()<until){f=screen(false);if(f.world()||service.equals("suibian temple")&&DailyPolicy.templeArrived(f.templeTab,f.templeRewardReceipt,f.templeClaimReady))return;if(f.has("teleport to")||f.has("teleport now")){tap(f,1115,628,"A");}Thread.sleep(350);}throw new IOException("Travel Did Not Finish");
 }
 boolean farmingSetup(DailyScreen f){return f.discSetup||f.has("waves of enemies")||f.has("combat options")||f.find("next",1450,950,1850,1075)!=null&&f.battery()!=null;}
 void closeToCity()throws Exception{
  long until=System.currentTimeMillis()+DailyPolicy.SCREEN_LIMIT_MS;int backs=0,storeConfirms=0;
  while(System.currentTimeMillis()<until){DailyScreen f=screen(false);if(f.world()){world();return;}
   if(f.storeOpeningConfirm){if(++storeConfirms>4)throw new IOException("Video Store Confirmation Did Not Close");press("A");Thread.sleep(1800);continue;}
   if(f.journal()){closeJournal(f);return;}
   boolean menu=farmingSetup(f)||f.has("restore battery charge")||f.has("options","storage","agents")||f.has("waves of enemies")||f.has("combat options")||f.has("more features")||f.farmingCategories||f.has("basic material","agent promotion","custom")||f.has("business")||f.has("temple")||f.has("assigned bangboo")||f.has("auto managed")||f.has("quick nav")||f.has("change areas")||f.weekly!=null&&(f.weekly.huntSetup||f.weekly.huntMenu||f.weekly.board||f.weekly.huntChallenge);
   if(menu&&backs<6){press("B");backs++;Thread.sleep(2500);}
   else{publish("waiting","Waiting For The City To Settle");Thread.sleep(750);}
  }throw new IOException("Return To The City To Continue");
 }
 int journalBattery(DailyScreen frame)throws Exception{
  if(frame.headerBattery<0)frame=waitFor("Current Battery Charge",s->s.journal()&&s.headerBattery>=0);availableBattery=frame.headerBattery;batteryAt=System.currentTimeMillis();publish("running",step);return availableBattery;
 }
 DailyScreen training(String type)throws Exception{
  type=DailyPolicy.clean(type);DailyScreen f=journal();tap(f,1180,156,"R1");f=waitFor("Training",s->s.journal()&&s.handbookTab==2);int y=type.equals("area patrol")?580:type.equals("expert challenge")?690:type.equals("notorious hunt")?800:480;tap(f,300,y,"A");return waitFor(type,s->s.journal()&&s.find("go",1350,350,1700,950)!=null);
 }
 DailyScreen.Line stageRow(DailyScreen f,String name)throws Exception{
  for(int i=0;i<40;i++){DailyScreen.Line title=f.find(name,450,300,1320,920);if(title!=null)return title;swipe(f,1100,865,1100,440,550,"UP");f=waitFor("Stage List",s->s.journal());}throw new IOException("Selected Farming Stage Was Not Found");
 }
 DailyScreen prepareFarm()throws Exception{
  String target=plan.optString("target","dennies"),type=target.equals("discs")?"area patrol":target.equals("expert")?"expert challenge":"combat simulation";
  DailyScreen f=training(type);String name=target.equals("discs")||target.equals("expert")?plan.optString("stage"):target.equals("promotion")?"agent promotion":target.equals("engine-promotion")?"w engine modification":target.equals("skills")?"agent skill":"basic material";
  if(name.isEmpty())throw new IOException("Choose A Farming Stage First");DailyScreen.Line row=stageRow(f,name);f=screen(false);row=f.find(name,450,300,1320,920);if(row==null)throw new IOException("Stage List Changed");DailyScreen.Line go=f.find("go",1350,row.y()+30,1700,Math.min(960,row.y()+160));tap(f,go!=null?go:row,"A");
  long until=System.currentTimeMillis()+DailyPolicy.SCREEN_LIMIT_MS;
  while(System.currentTimeMillis()<until){f=screen(false);if(f.has("teleport")){tap(f,1115,628,"A");}else if(f.has("waves of enemies")||f.find("next",1450,950,1850,1075)!=null||f.has("combat options")&&f.find("deploy")!=null)break;else if(f.world()&&f.has("bertha")){tap(f,1515,814,"A");}Thread.sleep(400);}
  if(System.currentTimeMillis()>=until)throw new IOException("Farming Setup Did Not Open");
  int[] selectedCost=f.battery();if(target.equals("expert")&&selectedCost!=null&&selectedCost[1]==80&&f.has("burnout mode")){
   press("R3");f=waitFor("Exit Burnout Mode",s->s.has("exit","burnout mode","normal levels","confirm","cancel"));tap(f,f.find("confirm"),"A");f=waitFor("Normal Expert Cost",s->{int[] charge=s.battery();return charge!=null&&charge[1]==40&&s.find("next",1450,950,1850,1075)!=null;});
  }
  String preset=target.equals("dennies")?"bounty":target.equals("agent-xp")?"investigation":target.equals("engine-xp")?"w engine specialization":target.equals("plating")?"plating agent":"";
  if(!preset.isEmpty()){DailyScreen.Line choice=f.find(preset,100,700,1830,900);tap(f,choice,"A");f=waitFor("Preset Actions",s->s.has("modify")&&(s.has("start combat")||s.has("rename")));DailyScreen.Line startCombat=f.find("start combat");tap(f,startCombat!=null?startCombat.x():1255,startCombat!=null?startCombat.y():745,"A");f=waitFor("Your Squad",s->s.has("combat options")&&s.find("deploy")!=null);press("B");f=waitFor("Farming Setup",s->s.has("waves of enemies"));}
  if(target.equals("dennies")||target.equals("agent-xp")||target.equals("engine-xp")||target.equals("plating")){
   press("Y");DailyScreen preview=waitFor("Challenge Rewards",s->s.has("challenge rewards"));String expected=target.equals("dennies")?"denny":target.equals("agent-xp")?"log":target.equals("engine-xp")?"power supply":"plating";
   if(!preview.rewardText.contains(expected))throw new IOException("Farming Rewards Do Not Match Your Plan");if(target.equals("dennies")&&DailyPolicy.dennyReward(preview.rewardText)==0)throw new IOException("Dennies Reward Was Not Verified");tap(preview,1558,135,"B");f=waitFor("Farming Setup",s->s.has("waves of enemies"));
  }
  return f;
 }
 void reserveTopOff(int needed)throws Exception{
  if(needed<=0||needed>=100||reserveUsed!=0||availableBattery<=0)throw new IOException("Invalid Reserve Top-Off Request");int before=availableBattery;long beforeAt=System.currentTimeMillis();task("Final Batch · Backup Battery Charge");DailyScreen f=world();tap(f,159,89,"START");f=waitFor("Game Menu",s->s.has("options","storage","agents"));press("SELECT");f=waitFor("Battery Resource Bar",s->{for(int[] box:s.focusBoxes)if(box[0]>1100&&box[2]<1450&&box[1]<40&&box[3]<110)return true;return false;});press("A");
  f=waitFor("Backup Battery Charge",s->s.has("restore battery charge","extract backup battery charge"));
  // Select the Backup source, then verify its editable amount BEFORE any confirmation.
  tap(f,785,505,"A");f=waitFor("Selected Backup Battery Charge",s->s.backupAmount>0);
  boolean exact=false;for(int adjustments=0;adjustments<240;adjustments++){
   f=waitFor("Backup Charge Quantity",s->s.backupAmount>0,15000);int amount=f.quantity();if(amount<0)throw new IOException("Backup Extraction Amount Could Not Be Verified. Nothing Converted.");
   if(amount==needed){Thread.sleep(600);DailyScreen proof=screen(false);if(proof.backupAmount==needed&&proof.has("confirm")){f=proof;exact=true;break;}continue;}publish("running","Setting Backup Charge · "+amount+" → "+needed);press(amount>needed?"LEFT":"RIGHT");
  }
  if(!exact)throw new IOException("Could Not Set The Exact Backup Amount");if(!f.has("confirm"))throw new IOException("Backup Confirmation Could Not Be Read. Nothing Converted.");String budgetHold=DailyTestBudget.hold(dir,needed,"Final Batch · "+runId);press("A");Thread.sleep(2000);
  f=waitFor("Backup Extraction Result",s->s.has("obtained")||s.has("restore battery charge")||s.has("options","storage","agents")||s.world());if(f.has("obtained")){press("A");}closeToCity();f=journal();int after=journalBattery(f);closeJournal(f);if(!DailyPolicy.backupConversion(before,needed,after,System.currentTimeMillis()-beforeAt))throw new IOException("Backup Conversion Needs Review. No More Reserves Will Be Used.");DailyTestBudget.confirm(dir,budgetHold);reserveUsed=needed;completed.put("Backup Battery Charge · "+needed);publish("running",step);
 }

 void battle(int expectedCost)throws Exception{battle(expectedCost,false);}
 void battle(int expectedCost,boolean weeklyBoss)throws Exception{
  long battleStarted=System.currentTimeMillis();task((weeklyBoss?"Weekly Boss · Battle ":"Farming · Battle ")+(battles+1)+(estimatedBattles>0?" / ~"+estimatedBattles:""));long until=System.currentTimeMillis()+(weeklyBoss?10*60*1000L:DailyPolicy.BATTLE_LIMIT_MS);boolean fought=false;CombatRotation rotation=new CombatRotation();CombatActivity activity=new CombatActivity();combatHud=new CombatHudReader(dir);
  try{
  while(System.currentTimeMillis()<until){DailyScreen f=screen(true);
   if(f.phase.equals("combat")||f.combat!=null&&(f.combat.chain||weeklyBoss&&f.combat.controls&&f.combat.team.equals(CombatRotation.TEAM))){fought=true;
    long now=System.currentTimeMillis();if(f.combat!=null&&f.combat.squadLost){pauseFailedCombat();throw new IOException("Squad Incomplete Or Agent Down. Battle Paused.");}CombatRotation.Action next=rotation.next(f.combat,now);
    if(rotation.swapUnconfirmed(now)){pauseFailedCombat();throw new IOException("Agent Swap Could Not Be Confirmed. Battle Paused.");}
    if(next!=null){combatInput(next);activity.stalled(System.currentTimeMillis(),true);}
    else if(activity.stalled(now,false)){pauseFailedCombat();throw new IOException("Could Not Confirm The Combat HUD. Battle Paused.");}
    else {publish("waiting","Reading Your Squad And Combat Prompts");Thread.sleep(120);}
   }else if(f.results()){
    if(!fought){publish("waiting","Waiting For The Next Battle To Start");Thread.sleep(600);continue;}
    f=waitFor("Completed Battle",s->s.results()&&(weeklyBoss?s.weekly!=null&&s.weekly.resultClear:s.battleCleared&&s.find("complete",1400,950,1850,1070)!=null));
    if(!fought||f.has("challenge failed")||f.has("defeat"))throw new IOException("Battle Did Not Complete Successfully");if(!weeklyBoss){spent+=expectedCost;availableBattery=DailyPolicy.afterBattle(availableBattery,expectedCost);if(availableBattery<0)throw new IOException("Verified Battery Budget Was Lost");batteryAt=System.currentTimeMillis();}battles++;long duration=System.currentTimeMillis()-battleStarted;long old=history.optLong("battleEstimateMs",duration);history.put("battleEstimateMs",(old*2+duration)/3);publish("running","Battle Complete");return;
   }else if(f.loading()){activity.loading();Thread.sleep(350);}else Thread.sleep(300);
  }throw new IOException("Battle Time Limit Reached. Take Over In Game.");
  }finally{combatHud=null;}
 }
 void pauseFailedCombat()throws Exception{controller.hold("START",140,()->checkCombatHold());}
 void combatInput(CombatRotation.Action next)throws Exception{
  check();if(System.currentTimeMillis()-next.at>900)throw new IOException("Combat Observation Expired Before Input. Take Over In Game.");input=next.key;inputAt=System.currentTimeMillis();publish("running",next.label);
  // The guard runs throughout a hold, so Stop, manual input and Home still release it.
  controller.hold(next.key,next.holdMs,()->checkCombatHold());
  // Animation waits are observed in the next frames; do not sleep through chain prompts.
 }
 void checkCombatHold()throws Exception{
  if(manualStop)throw new InterruptedException("Stopped By Controller Input");
  if(!GameObserver.watching()||!foreground.gameVisible())throw new InterruptedException("Game Left The Foreground");
  JSONObject command=read(new File(dir,"daily-command.json"));
  if(command.optString("action").equals("stop")&&!command.optString("id").equals(runId))throw new InterruptedException("Stopped By You");
  JSONObject identity=context.latest;
  if(!DailyPolicy.canContinue(uid,identity.optString("uid"),identity.optBoolean("uidConfirmed"),System.currentTimeMillis()-identity.optLong("at")))throw new InterruptedException("Session Confirmation Lost During Input");
 }
 void farm()throws Exception{
  String target=plan.optString("target","dennies");if(!Arrays.asList("dennies","agent-xp","engine-xp","plating","promotion","engine-promotion","skills","discs","expert").contains(target))throw new IOException("Unknown Farm Target");
  int batch=target.equals("discs")?60:target.equals("expert")?40:100;
  while(battles<DailyPolicy.MAX_BATTLES){task("Battery Farming");DailyScreen f=journal();int available=journalBattery(f);estimatedBattles=battles+DailyPolicy.forecastBattles(available,batch,reserve&&reserveUsed==0,DailyPolicy.MAX_BATTERY_SPEND);estimatedDuration=System.currentTimeMillis()-started+DailyPolicy.estimateMs(0,estimatedBattles-battles,history.optLong("battleEstimateMs",144000))+(reserve&&reserveUsed==0&&available%batch>0?180000:0);closeJournal(f);if(available==0){completed.put("Battery Farming · 0 Remaining");return;}
   if(available<batch){if(reserveUsed>0){completed.put("Battery Farming · "+available+" Newly Regenerated");return;}if(!reserve){completed.put("Battery Farming · "+available+" Remaining");return;}reserveTopOff(batch-available);}
   f=prepareFarm();if(f.battery()==null)f=waitFor("Farming Battery Cost",s->s.battery()!=null&&(s.has("waves of enemies")||s.has("combat options")));int[] cost=f.battery();if(cost==null||!DailyPolicy.safeDeploy(cost[0],cost[1],batch,spent))throw new IOException("Battle Cost Does Not Match Your Selected Batch");
   DailyScreen.Line next=f.find("next",1450,950,1850,1075);if(next!=null||f.has("waves of enemies")){press("START");f=waitFor("Your Squad",s->s.has("combat options")&&s.find("deploy")!=null);}
   if(f.battery()==null)f=waitFor("Deployment Battery Cost",s->s.battery()!=null&&s.has("combat options"));cost=f.battery();if(cost==null||!DailyPolicy.safeDeploy(cost[0],cost[1],batch,spent))throw new IOException("Deployment Cost Could Not Be Verified");press("START");battle(batch);
   while(battles<DailyPolicy.MAX_BATTLES&&DailyPolicy.safeDeploy(availableBattery,batch,batch,spent)){
    f=waitFor("Battle Results",DailyScreen::results);int[] shown=f.battery();if(shown!=null&&shown[1]!=batch)throw new IOException("Repeat Cost Changed");
    Thread.sleep(2500);f=screen(false);DailyScreen.Line repeat=f.find("do it again",900,850,1500,1080);if(repeat==null)throw new IOException("Repeat Control Could Not Be Verified");press("X");battle(batch);
   }
   f=screen(false);tap(f,f.find("complete",1400,950,1850,1075),"B");waitFor("Post-Battle",s->s.world()||farmingSetup(s)||s.farmingCategories||s.journal());closeToCity();
  }throw new IOException("Maximum Battle Count Reached");
 }
 void scanStages()throws Exception{
  JSONArray result=new JSONArray();Set<String> seen=new HashSet<>();for(String category:Arrays.asList("Area Patrol","Expert Challenge")){
   task("Scanning "+category);DailyScreen f=training(category);Set<String> positions=new HashSet<>();boolean finished=false;
   for(int page=0;page<40;page++){
    if(f.stageTitles.isEmpty())throw new IOException("Stage Names Could Not Be Verified");
    String position=f.stageTitles.values().toString();for(int[] box:f.focusBoxes)if(box[0]>450&&box[2]>1600&&box[2]-box[0]>900&&box[1]>290&&box[3]<960)position+="|"+box[1]+":"+box[3];
    if(!positions.add(position)){finished=true;break;}
    for(String title:f.stageTitles.values())if(seen.add(category+title))result.put(new JSONObject().put("category",category).put("name",title));
    press("DOWN");f=waitFor("Stage List",s->s.journal()&&!s.stageTitles.isEmpty());
   }if(!finished)throw new IOException("Stage Scan Did Not Reach A Verified End");closeJournal(f);
  }write("daily-stages.json",new JSONObject().put("stages",result));completed.put("Stage List Updated");
 }
 @Override public void run(){
  try{File consumed=new File(dir,"daily-consumed.json");lastConsumed=read(consumed).optString("id");
   while(GameObserver.watching()){
    JSONObject command=read(new File(dir,"daily-command.json"));String id=command.optString("id");
    if(!id.isEmpty()&&!id.equals(lastConsumed)&&(command.optString("action").equals("start")||command.optString("action").equals("scan")||command.optString("action").equals("weekly")||command.optString("action").equals("weekly-scan"))&&System.currentTimeMillis()-command.optLong("issuedAt")<10000){
     lastConsumed=id;write("daily-consumed.json",new JSONObject().put("id",id));runId=id;action=command.optString("action");weeklyReading=action.startsWith("weekly");uid=command.optString("uid");plan=command.optJSONObject("plan");if(plan==null)plan=new JSONObject();battery=action.equals("start")&&command.optBoolean("battery");reserve=plan.optBoolean("reserve",true);started=System.currentTimeMillis();history=null;availableBattery=-1;batteryAt=0;stageIndex=stageTotal=estimatedBattles=0;estimatedDuration=0;step="";completed=new JSONArray();spent=battles=reserveUsed=reconnects=weeklyClaims=0;weeklyRemaining=-1;manualStop=false;running=active=true;
     try{if(command.optString("action").equals("start")&&uid.matches("[1-9][0-9]{7,11}")){history=DailyHistory.read(dir,uid);history.put("lastTriggeredAt",started).put("lastState","running");DailyHistory.save(dir,uid,history);}capture=new GameCapture(1920,1080);foreground=new GameCapture.Foreground();controller=new RoutineController(foreground);ocr=new OcrEngine();classifier=new HudClassifier(new String(Files.readAllBytes(new File(dir,"hud-templates.json").toPath()),"UTF-8"));task("Checking Your Session");awaitIdentity();final boolean incompleteErrands=action.equals("start")&&!plan.optBoolean("farmOnly")&&history!=null&&!DailyHistory.cycleComplete(history,started),recoverTemple=incompleteErrands&&plan.optBoolean("temple",true),recoverStore=incompleteErrands&&plan.optBoolean("store",true);DailyScreen f=waitFor("Your Linked City Session Or Pending Errand",s->{JSONObject identity=context.latest;long age=System.currentTimeMillis()-identity.optLong("at");return DailyPolicy.canStart(uid,identity.optString("uid"),identity.optBoolean("uidConfirmed"),s.phase,!s.touch,age)||DailyPolicy.canResumeErrand(uid,identity.optString("uid"),identity.optBoolean("uidConfirmed"),age,recoverTemple,s.templeTab>=0||s.templeRewardReceipt)||DailyPolicy.canResumeErrand(uid,identity.optString("uid"),identity.optBoolean("uidConfirmed"),age,recoverStore,s.storeOpeningConfirm)||WeeklyPolicy.exhaustedReturn(action.equals("weekly"),!s.touch,s.weekly!=null&&s.weekly.huntSetup,s.weekly==null?-1:s.weekly.remaining)&&DailyPolicy.canContinue(uid,identity.optString("uid"),identity.optBoolean("uidConfirmed"),age);},30000);
      if(WeeklyPolicy.exhaustedReturn(action.equals("weekly"),!f.touch,f.weekly!=null&&f.weekly.huntSetup,f.weekly==null?-1:f.weekly.remaining)){task("Returning From Completed Weekly Hunts");closeToCity();}
      if(action.startsWith("weekly")){new WeeklyAutomation(this).run(action.equals("weekly-scan"));}else if(action.equals("scan")){scanStages();}else {
       history=DailyHistory.read(dir,uid);errandsRun=!plan.optBoolean("farmOnly")&&!DailyHistory.cycleComplete(history,started);
       history.put("lastTriggeredAt",started).put("lastState","running");DailyHistory.save(dir,uid,history);
       stageIndex=0;stageTotal=2;for(String k:new String[]{"coffee","draw","store","temple"})if(errandsRun&&plan.optBoolean(k,true))stageTotal++;if(battery)stageTotal++;if(errandsRun)stageTotal++;
       estimatedBattles=0;estimatedDuration=DailyPolicy.estimateMs(stageTotal,0,history.optLong("battleEstimateMs",144000));
       if(recoverStore&&!f.world()&&f.storeOpeningConfirm){task("Video Store");publish("running","Resuming Video Store Opening");settleStore();}
       if(recoverTemple&&!f.world()&&(f.templeTab>=0||f.templeRewardReceipt)){task("Suibian Temple");publish("running","Resuming Temple Claim");settleTemple();}
       rewards();stageIndex++;
       if(errandsRun){if(plan.optBoolean("coffee",true)){coffee();stageIndex++;}if(plan.optBoolean("draw",true)){dailyDraw();stageIndex++;}if(plan.optBoolean("store",true)){store();stageIndex++;}if(plan.optBoolean("temple",true)){temple();stageIndex++;}rewards();stageIndex++;saveErrandCompletion();}
       if(battery){farm();stageIndex++;}
       rewards();stageIndex++;saveErrandCompletion();
      }running=active=false;publish("complete",action.equals("weekly-scan")?"Weekly Boss List Updated":action.equals("weekly")?"Weekly Routine Complete":action.equals("scan")?"Stage List Updated":"Routine Complete");
     }catch(InterruptedException stop){running=active=false;publish("stopped",stop.getMessage());}catch(Exception error){running=active=false;publish("attention",error.getMessage()==null?"Routine Needs Attention":error.getMessage());}
     finally{active=false;running=false;weeklyReading=false;if(controller!=null){controller.close();controller=null;}if(history!=null&&action.equals("start"))try{history.put("lastFinishedAt",System.currentTimeMillis()).put("lastDurationMs",System.currentTimeMillis()-started).put("lastState",read(new File(dir,"daily-status.json")).optString("state"));DailyHistory.save(dir,uid,history);}catch(Exception ignored){}if(history!=null&&action.startsWith("weekly"))try{history.put("lastFinishedAt",System.currentTimeMillis()).put("lastDurationMs",System.currentTimeMillis()-started).put("lastState",read(new File(dir,"daily-status.json")).optString("state"));WeeklyHistory.save(dir,uid,history);}catch(Exception ignored){}if(ocr!=null){ocr.api.recycle();ocr=null;}}
    }Thread.sleep(200);
   }
  }catch(Exception ignored){}finally{if(running)try{running=active=false;publish("interrupted","Routine Interrupted");}catch(Exception ignored){}}
 }
}

package dev.gbppimentel.zzz;
import android.graphics.Bitmap;import org.json.*;import java.io.*;import java.nio.channels.*;import java.nio.charset.StandardCharsets;import java.nio.file.Files;

/** Firmware-run, game-only screen observer. Never stores or transmits a frame. */
public final class GameObserver {
 static final File DIR=new File("/data/user/0/dev.gbppimentel.zzz/files"),WATCH=new File(DIR,"observer.watch"),OUTPUT=new File(DIR,"observation.json");
 static final String VERSION=BuildFlags.VERSION;static DeviceStatsReader telemetry;
 static void write(JSONObject value)throws Exception{File next=new File(DIR,"observation.next");try(FileOutputStream out=new FileOutputStream(next)){out.write(value.toString().getBytes(StandardCharsets.UTF_8));}next.setReadable(true,false);if(!next.renameTo(OUTPUT))throw new IOException("Cannot publish observation");}
 static JSONObject frame(String phase,double confidence,long sequence,int interval,long captureMs,long classifyMs)throws Exception{return new JSONObject().put("telemetry",telemetry==null?new JSONObject():telemetry.latest).put("version",VERSION).put("phase",phase).put("confidence",Math.round(confidence*100)/100.0).put("at",System.currentTimeMillis()).put("sequence",sequence).put("intervalMs",interval).put("captureMs",captureMs).put("classifyMs",classifyMs).put("source","visible_hud").put("gameVisible",!phase.equals("not_visible")&&!phase.equals("unavailable"));}
 static boolean watching(){return WATCH.exists()&&System.currentTimeMillis()-WATCH.lastModified()<3500;}
 public static void main(String[] args){
  try{if(args.length>0&&args[0].equals("--benchmark")){benchmark();return;}
   if(!watching())return;
   try(RandomAccessFile lockFile=new RandomAccessFile(new File(DIR,"observer.lock"),"rw");FileLock lock=lockFile.getChannel().tryLock()){
    if(lock==null)return;telemetry=new DeviceStatsReader();Thread statsThread=new Thread(telemetry,"GameStatistics");statsThread.setDaemon(true);statsThread.start();
    GameContextReader context=new GameContextReader();Thread contextThread=new Thread(context,"GameContext");contextThread.setDaemon(true);contextThread.start();Thread controllerThread=new Thread(()->ControllerReader.main(new String[0]),"ControllerInput");controllerThread.setDaemon(true);controllerThread.start();
    PortraitMatcher portraits=new PortraitMatcher(new String(Files.readAllBytes(new File(DIR,"portraits.json").toPath()),StandardCharsets.UTF_8));long portraitStamp=0;String lastSquad="";SquadTracker squadTracker=new SquadTracker();String squadKind="";
    GameCapture capture=new GameCapture(960,540);GameCapture.Foreground foreground=new GameCapture.Foreground();
    HudClassifier classifier=new HudClassifier(new String(Files.readAllBytes(new File(DIR,"hud-templates.json").toPath()),StandardCharsets.UTF_8));HudClassifier.Stable stable=new HudClassifier.Stable();
    long combatHudAt=0;long sequence=0,published=0,lastJar=new File(DIR,"observer.jar").lastModified();String lastPhase="";boolean lastMoving=false;int motionCount=0;int interval=100;double average=20;
    while(watching()&&new File(DIR,"observer.jar").lastModified()==lastJar){
     long begin=System.nanoTime();long now=System.currentTimeMillis();
     String foregroundPackage=foreground.topPackage();if(foregroundPackage==null){Thread.sleep(300);continue;}
     if(!"com.HoYoverse.Nap".equals(foregroundPackage)){
      if(!lastPhase.equals("not_visible")||now-published>=1000){write(frame("not_visible",1,++sequence,500,0,0).put("launcherVisible","rip.moth.cocoonshell".equals(foregroundPackage)||"com.android.launcher3".equals(foregroundPackage)));published=now;lastPhase="not_visible";}
      stable=new HudClassifier.Stable();classifier.previous=null;squadTracker=new SquadTracker();squadKind="";combatHudAt=0;Thread.sleep(300);continue;
     }
     Bitmap bitmap=null;
     try{long c0=System.nanoTime();bitmap=capture.frame();long c1=System.nanoTime();int[] portraitPixels=new int[960*540];bitmap.getPixels(portraitPixels,0,960,0,0,960,540);Bitmap reduced=Bitmap.createScaledBitmap(bitmap,480,270,true);int[] pixels=new int[480*270];reduced.getPixels(pixels,0,480,0,0,480,270);reduced.recycle();bitmap.recycle();bitmap=null;
      // A window change invalidates the sample before classification/publication.
      if(!watching()||!foreground.gameVisible())continue;
      HudClassifier.Result result=classifier.classify(pixels);long c2=System.nanoTime();stable.update(result.phase,result.confidence);if(result.phase.equals("combat"))combatHudAt=now;String observedPhase=stable.phase;if((observedPhase.equals("unknown")||observedPhase.equals("transition"))&&now-combatHudAt<5000)observedPhase="combat";context.phase=observedPhase;File dynamicPortraits=new File(DIR,"portraits.dynamic.json");if(dynamicPortraits.exists()&&dynamicPortraits.lastModified()!=portraitStamp){try{portraitStamp=dynamicPortraits.lastModified();portraits=new PortraitMatcher(new String(Files.readAllBytes(dynamicPortraits.toPath()),StandardCharsets.UTF_8));}catch(Exception ignored){}}
      String kind=observedPhase.equals("combat")?"combat":observedPhase.equals("menu")?"menu":"none";if(kind.equals("combat")&&(lastPhase.equals("results")||lastPhase.equals("exploring")||lastPhase.equals("shortcuts")||lastPhase.equals("not_visible")))squadTracker=new SquadTracker();squadKind=kind;JSONArray squad=squadTracker.update(portraits.find(portraitPixels,960,540,kind),now);StringBuilder squadKey=new StringBuilder();for(int i=0;i<squad.length();i++)squadKey.append(squad.getJSONObject(i).optString("id")).append(':').append(squad.getJSONObject(i).optInt("slot")).append(';');String squadSignature=squadKey.toString();
      motionCount=result.motion>.025?Math.min(4,motionCount+1):Math.max(-4,motionCount-1);boolean moving=motionCount>=2?true:motionCount<=-2?false:lastMoving;
      long captureMs=(c1-c0)/1000000,classifyMs=(c2-c1)/1000000;average=average*.85+((c2-c0)/1000000.0)*.15;
      interval=average>180?400:average>80?200:100;
      if(!observedPhase.equals(lastPhase)||moving!=lastMoving||!squadSignature.equals(lastSquad)||now-published>=1000){JSONObject details=System.currentTimeMillis()-context.latest.optLong("at")<6000?new JSONObject(context.latest.toString()):new JSONObject();details.put("agents",squad);details.remove("activeAgent");if(observedPhase.equals("combat"))for(int i=0;i<squad.length();i++)if(squad.getJSONObject(i).optInt("slot",-1)==0)details.put("activeAgent",squad.getJSONObject(i).optString("id"));JSONObject out=frame(details.optBoolean("login")?"login":observedPhase,stable.confidence,++sequence,interval,captureMs,classifyMs).put("context",details).put("motion",moving).put("scores",new JSONObject().put("city",round(result.city)).put("combat",round(result.combat)).put("menu",round(result.menu)).put("shortcuts",round(result.shortcuts)));write(out);published=now;lastPhase=observedPhase;lastMoving=moving;lastSquad=squadSignature;}
     }catch(Exception e){if(now-published>=1000){write(frame("unavailable",0,++sequence,1000,0,0).put("reason","Screen capture unavailable"));published=now;}interval=1000;}finally{if(bitmap!=null)bitmap.recycle();}
     long elapsed=(System.nanoTime()-begin)/1000000;if(elapsed<interval)Thread.sleep(interval-elapsed);
    }
   }
  }catch(Exception e){try{write(frame("unavailable",0,0,1000,0,0).put("reason","Observer could not start"));}catch(Exception ignored){}}
 }
 static double round(double v){return Math.round(v*1000)/1000.0;}
 static void benchmark()throws Exception{GameCapture c=new GameCapture();GameCapture.Foreground foreground=new GameCapture.Foreground();HudClassifier classifier=new HudClassifier(new String(Files.readAllBytes(new File(DIR,"hud-templates.json").toPath()),StandardCharsets.UTF_8));long total=0,max=0;int n=25;HudClassifier.Result last=null;for(int i=0;i<n;i++){long start=System.nanoTime();Bitmap b=c.frame();int[] p=new int[480*270];b.getPixels(p,0,480,0,0,480,270);b.recycle();last=classifier.classify(p);long ms=(System.nanoTime()-start)/1000000;if(i>0){total+=ms;max=Math.max(max,ms);}Thread.sleep(50);}System.out.println(new JSONObject().put("gameVisible",foreground.gameVisible()).put("meanMs",total/(double)(n-1)).put("maxMs",max).put("phase",last.phase).put("confidence",last.confidence).put("city",last.city).put("combat",last.combat).put("menu",last.menu));}
}

package dev.gbppimentel.zzz;
import android.graphics.Bitmap;import org.json.*;import java.nio.file.Files;import java.io.File;
/** Entirely local OCR and portrait features; no frame or raw OCR text is published. */
final class GameContextReader implements Runnable {
 volatile JSONObject latest=new JSONObject();volatile String phase="unknown";
 String read(String n)throws Exception{return new String(Files.readAllBytes(new File(GameObserver.DIR,n).toPath()),"UTF-8");}
 public void run(){try{OcrEngine ocr=new OcrEngine();GameCapture capture=new GameCapture(1920,1080);GameCapture.Foreground foreground=new GameCapture.Foreground();ContextParser parser=new ContextParser(read("combat-catalog.json"));ContextParser.Identity identity=new ContextParser.Identity();long hiddenSince=0;int loginFrames=0;
  while(GameObserver.watching()){long start=System.currentTimeMillis();Bitmap full=null,small=null;try{
   if(!foreground.gameVisible()){if(hiddenSince==0)hiddenSince=start;if(start-hiddenSince>5000){identity.clear();latest=new JSONObject();}Thread.sleep(500);continue;}hiddenSince=0;
   full=capture.frame();String uid=ContextParser.uid(ocr.read(full,1650,1056,145,24,true));identity.update(uid);small=Bitmap.createScaledBitmap(full,960,540,true);full.recycle();full=null;
   if(!foreground.gameVisible())continue;
   boolean fight=phase.equals("combat");String top=ocr.read(small,210,25,720,110,false);String active=parser.agent(top);boolean login=false;if(uid.isEmpty()){String middle=ContextParser.clean(ocr.read(small,240,280,480,220,false));login=middle.contains("start game")||middle.contains("tap to begin")||middle.contains("tap to start")||middle.contains("log in");}
   loginFrames=login?loginFrames+1:0;login=loginFrames>=2;if(login)identity.clear();JSONObject out=new JSONObject().put("at",start).put("uid",identity.confirmed).put("uidConfirmed",!identity.confirmed.isEmpty()).put("login",login).put("ocr",true);
   if(!active.isEmpty())out.put("selectedAgent",active);
   JSONObject enemy=parser.enemy(top);if(fight&&enemy==null){String name=ocr.enemyName(small,parser);if(!name.isEmpty())enemy=new JSONObject().put("name",name).put("source","screen_text");}if(enemy!=null&&fight)out.put("enemy",enemy);
   if(!fight){String area=parser.area(ocr.read(small,0,35,330,115,false));if(!area.isEmpty())out.put("area",area);}
   if(foreground.gameVisible()&&GameObserver.watching())latest=out;
  }catch(Exception ignored){}finally{if(full!=null)full.recycle();if(small!=null)small.recycle();}
  long used=System.currentTimeMillis()-start;Thread.sleep(Math.max(200,1500-used));}
 }catch(Throwable e){try{latest=new JSONObject().put("ocr",false);}catch(Exception ignored){}}}
}

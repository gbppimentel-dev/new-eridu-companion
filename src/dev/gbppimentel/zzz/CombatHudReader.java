package dev.gbppimentel.zzz;

import android.graphics.Bitmap;
import com.googlecode.tesseract.android.ResultIterator;
import org.json.*;
import java.io.*;
import java.nio.file.Files;
import java.util.*;

/** Short-lived battle reader: portraits and ready icons must come from the same frame. */
final class CombatHudReader {
 final PortraitMatcher portraits;final Set<String> members=new HashSet<>();
 int completeFrames;boolean teamConfirmed;long missingSince;
 CombatHudReader(File dir)throws Exception{
  File source=new File(dir,"portraits.json"),dynamic=new File(dir,"portraits.dynamic.json");
  PortraitMatcher model=new PortraitMatcher(new String(Files.readAllBytes(source.toPath()),"UTF-8"));
  if(dynamic.exists())try{model=new PortraitMatcher(new String(Files.readAllBytes(dynamic.toPath()),"UTF-8"));}catch(Exception ignored){}
  portraits=model;
 }
 CombatRotation.Frame read(Bitmap full,OcrEngine ocr,DailyScreen screen)throws Exception{
  CombatRotation.Frame out=new CombatRotation.Frame();out.at=screen.at;
  Bitmap small=Bitmap.createScaledBitmap(full,960,540,true);
  try{
  int[] pixels=new int[960*540];small.getPixels(pixels,0,960,0,0,960,540);
  JSONArray raw=portraits.find(pixels,960,540,"combat");Set<String> current=new HashSet<>();
  for(int i=0;i<raw.length();i++){
   JSONObject a=raw.getJSONObject(i);String id=a.optString("id");current.add(id);
   if(a.optInt("slot",-1)==0)out.active=id;
  }
  // Confirm all three together twice. Cached squad membership alone cannot unlock input.
  if(current.equals(CombatRotation.TEAM)){if(++completeFrames>=2){members.clear();members.addAll(current);teamConfirmed=true;}}
  else {completeFrames=0;for(String id:current)if(!CombatRotation.TEAM.contains(id)){members.clear();teamConfirmed=false;}}
  if(teamConfirmed)out.team.addAll(members);
  boolean controls=CombatHudSignals.controllerVisible(pixels,960,540);out.controls=controls;
  if(controls&&teamConfirmed&&current.size()<3){if(missingSince==0)missingSince=out.at;out.squadLost=out.at-missingSince>=4000;}else missingSince=0;
  out.health=controls?CombatHudSignals.healthRatio(pixels,960,540):-1;out.lowHealth=out.health>=0&&out.health<.3;
  if(controls){out.skillReady=CombatHudSignals.skillReady(pixels,960,540,out.active);out.ultimateReady=CombatHudSignals.ultimateReady(pixels,960,540);}
  else {
   // One small OCR pass handles both choices; do not perform three upscaled scans.
   String[] choices;
   if(CombatHudSignals.chainPrompt(screen.text)){
    StringBuilder left=new StringBuilder(),right=new StringBuilder();
    for(DailyScreen.Line line:screen.lines)if(line.y()>280)(line.x()<960?left:right).append(' ').append(line.text);
    choices=new String[]{screen.text,left.toString(),right.toString()};
   }else choices=chainChoices(small,ocr);
   if(CombatHudSignals.chainPrompt(choices[0])){
    out.chain=true;out.leftChain=CombatHudSignals.chainAgent(choices[1],"L1");out.rightChain=CombatHudSignals.chainAgent(choices[2],"R1");
   }
   // No normal inputs while an animation/menu occludes the controller HUD.
   if(!out.chain)out.active="";
  }
  return out;
  }finally{small.recycle();}
 }
 private String[] chainChoices(Bitmap small,OcrEngine ocr){
  Bitmap overlay=Bitmap.createBitmap(small,50,140,860,380);ResultIterator iterator=null;
  try{
   ocr.api.setPageSegMode(11);ocr.api.setVariable("tessedit_char_whitelist","");ocr.api.setImage(overlay);
   String text=ocr.api.getUTF8Text();StringBuilder left=new StringBuilder(),right=new StringBuilder();
   if(CombatHudSignals.chainPrompt(text==null?"":text)){
    iterator=ocr.api.getResultIterator();if(iterator!=null){iterator.begin();do{
     String line=iterator.getUTF8Text(2);if(line==null||iterator.confidence(2)<60)continue;
     int[] box=iterator.getBoundingBox(2);((box[0]+box[2])/2<430?left:right).append(' ').append(line);
    }while(iterator.next(2));}
   }
   return new String[]{text==null?"":text,left.toString(),right.toString()};
  }finally{if(iterator!=null)iterator.delete();ocr.api.clear();overlay.recycle();}
 }
}

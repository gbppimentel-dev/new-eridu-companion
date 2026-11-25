package dev.gbppimentel.zzz;
import java.io.*;import java.nio.file.Files;import org.json.*;
/** Local, account-scoped history. Failed runs never consume the daily reset. */
final class DailyHistory {
 static JSONObject read(File dir,String uid){try{File f=new File(dir,"daily-history.json");if(f.length()>65536)return new JSONObject();JSONObject all=new JSONObject(new String(Files.readAllBytes(f.toPath()),"UTF-8"));JSONObject j=all.optJSONObject(uid);return j==null?new JSONObject():j;}catch(Exception e){return new JSONObject();}}
 static void save(File dir,String uid,JSONObject value)throws Exception{
  File file=new File(dir,"daily-history.json");JSONObject all=new JSONObject();try{if(file.length()<65536)all=new JSONObject(new String(Files.readAllBytes(file.toPath()),"UTF-8"));}catch(Exception ignored){}
  all.put(uid,value);File next=new File(dir,"daily-history.next");try(FileOutputStream out=new FileOutputStream(next)){out.write(all.toString().getBytes("UTF-8"));}next.setReadable(true,false);if(!next.renameTo(file))throw new IOException("Could Not Save Run History");
 }
 static boolean allTasksCurrent(JSONObject history,long at){JSONObject tasks=history.optJSONObject("tasks");if(tasks==null)return false;for(String key:new String[]{"coffee","draw","store","temple"}){JSONObject task=tasks.optJSONObject(key);if(task==null||!DailyPolicy.taskCurrent(at,task.optLong("at")))return false;}return true;}
 static boolean cycleComplete(JSONObject history,long at){return !DailyPolicy.errandsAvailable(at,history.optLong("errandsCompletedAt"))&&allTasksCurrent(history,at)&&rewardCurrent(history,at);}
 static boolean rewardCurrent(JSONObject h,long at){JSONObject tasks=h.optJSONObject("tasks"),reward=tasks==null?null:tasks.optJSONObject("rewards");return reward!=null&&DailyPolicy.taskCurrent(at,reward.optLong("at"));}
 static JSONObject verifiedTasks(JSONObject h,long now)throws Exception{JSONObject out=new JSONObject(),tasks=h.optJSONObject("tasks");for(String key:new String[]{"coffee","draw","store","temple","rewards"}){JSONObject task=tasks==null?null:tasks.optJSONObject(key);out.put(key,task!=null&&DailyPolicy.taskCurrent(now,task.optLong("at")));}return out;}
 static JSONObject summary(File dir,String uid,long now)throws Exception{JSONObject h=read(dir,uid);return new JSONObject().put("verifiedTasks",verifiedTasks(h,now)).put("lastTriggeredAt",h.optLong("lastTriggeredAt")).put("lastFinishedAt",h.optLong("lastFinishedAt")).put("lastState",h.optString("lastState")).put("lastDurationMs",h.optLong("lastDurationMs")).put("errandsAvailable",!cycleComplete(h,now)).put("nextResetAt",DailyPolicy.nextReset(now)).put("battleEstimateMs",h.optLong("battleEstimateMs",144000));}
}

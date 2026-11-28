package dev.gbppimentel.zzz;
import android.content.Context;import org.json.*;import java.io.*;import java.nio.file.Files;
/** App-owned fixed start/stop requests; no arbitrary macro or shell input. */
final class DailyRunLink {
 static void request(Context context,String action,String uid,String options)throws Exception{
  if(!action.equals("start")&&!action.equals("stop")&&!action.equals("scan")&&!action.equals("weekly")&&!action.equals("weekly-scan"))return;
  if(options.length()>4096)throw new IOException("Plan Too Large");JSONObject plan=new JSONObject(options);if(!action.equals("stop")&&status(context.getFilesDir()).optBoolean("running"))throw new IOException("A Routine Is Already Running");
  JSONObject clean=new JSONObject();for(String key:new String[]{"coffee","draw","store","temple","reserve"})clean.put(key,plan.optBoolean(key,true));clean.put("target",plan.optString("target","dennies"));clean.put("farmOnly",plan.optBoolean("farmOnly",false));clean.put("category",plan.optString("category","Basic Material"));clean.put("stage",plan.optString("stage",""));
  if(action.startsWith("weekly")){clean=new JSONObject().put("bosses",plan.optBoolean("bosses",false));String boss=plan.optString("boss");if(!boss.isEmpty()&&!WeeklyPolicy.bossName(boss))throw new IOException("Invalid Weekly Boss");clean.put("boss",boss);}
  JSONObject command=new JSONObject().put("id",java.util.UUID.randomUUID().toString()).put("action",action).put("issuedAt",System.currentTimeMillis()).put("uid",uid).put("battery",action.equals("start")&&plan.optBoolean("battery",true)).put("plan",clean);
  File next=new File(context.getFilesDir(),"daily-command.next"),target=new File(context.getFilesDir(),"daily-command.json");
  try(FileOutputStream out=new FileOutputStream(next)){out.write(command.toString().getBytes("UTF-8"));}if(!next.renameTo(target))throw new IOException("Cannot queue daily request");
 }
 static JSONObject status(File directory){try{File f=new File(directory,"daily-status.json");if(f.length()>16384)return new JSONObject();JSONObject j=new JSONObject(new String(Files.readAllBytes(f.toPath()),"UTF-8"));if(j.optBoolean("running")&&System.currentTimeMillis()-j.optLong("at")>10000){j.put("running",false).put("state","interrupted").put("message","Routine Interrupted. Return To The City Before Starting Again.");}return j;}catch(Exception ignored){return new JSONObject();}}
}

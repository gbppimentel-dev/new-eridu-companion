package dev.gbppimentel.zzz;

import java.util.Arrays;
import java.util.Map;
import org.json.JSONObject;

/** Recover a pre-public-build selection without choosing among multiple accounts. */
final class AccountSelection {
 static final class Saved {
  final String uid,server;
  Saved(String uid,String server){this.uid=uid;this.server=server;}
 }
 static Saved legacy(Map<String,?> preferences,boolean hasSession){
  // An explicit choice (including intentionally empty) always wins.
  if(preferences.containsKey("uid")||!hasSession)return null;
  Saved selected=null;
  for(Map.Entry<String,?> entry:preferences.entrySet()){
   String key=entry.getKey();
   if(!key.startsWith("state:"))continue;
   String uid=key.substring(6);
   if(!uid.matches("[1-9][0-9]{7,11}"))continue;
   // Multiple saved accounts require the user to choose, even if one is damaged.
   if(selected!=null)return null;
   try{
    if(!(entry.getValue() instanceof String))return null;
    JSONObject state=new JSONObject((String)entry.getValue());
    String server=state.optString("server");
    if(!uid.equals(state.optString("uid"))||!"connected".equals(state.optString("session"))||
       !Arrays.asList("prod_gf_jp","prod_gf_us","prod_gf_eu","prod_gf_sg").contains(server))return null;
    selected=new Saved(uid,server);
   }catch(Exception invalid){return null;}
  }
  return selected;
 }
}

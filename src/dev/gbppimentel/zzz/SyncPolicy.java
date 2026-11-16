package dev.gbppimentel.zzz;
import java.util.*;
final class SyncPolicy {
 final Map<String,Long> due=new HashMap<>();long blockedUntil;boolean verification;int errors;
 static long retryAfter(String value,long now){if(value==null)return 0;try{return Math.max(0,Math.min(86400000,Long.parseLong(value.trim())*1000L));}catch(Exception ignored){}try{java.text.SimpleDateFormat f=new java.text.SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz",java.util.Locale.US);f.setLenient(false);return Math.max(0,Math.min(86400000,f.parse(value).getTime()-now));}catch(Exception ignored){return 0;}}
 synchronized boolean allowed(long now){return !verification&&now>=blockedUntil;}
 synchronized boolean ready(String key,long now){return allowed(now)&&now>=due.getOrDefault(key,0L);}
 synchronized void success(String key,long now,long interval){errors=0;due.put(key,now+interval);}
 synchronized void failure(long now,int code,long retry){errors++;if(code==-100||code==10001||code==10002||code==1034||code==10035||code==5003)verification=true;blockedUntil=now+Math.max(retry,code==429?60000:Math.min(300000,5000L<<(Math.min(errors-1,6))));}
 synchronized void reconnect(){verification=false;blockedUntil=0;errors=0;due.clear();}
}

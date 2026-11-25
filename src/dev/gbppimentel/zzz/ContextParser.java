package dev.gbppimentel.zzz;
import org.json.*;import java.util.*;import java.util.regex.*;
final class ContextParser {
 final JSONObject catalog;
 ContextParser(String json)throws Exception{catalog=new JSONObject(json);}
 static String uid(String text){Matcher m=Pattern.compile("(?<![0-9])([1-9][0-9]{8,9})(?![0-9])").matcher(text);return m.find()?m.group(1):"";}
 static String consistentUid(String... readings){String found="";for(String text:readings){String next=uid(text);if(next.isEmpty())continue;if(!found.isEmpty()&&!found.equals(next))return "";found=next;}return found;}
 static String clean(String s){return s.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9 ]"," ").replaceAll("\\s+"," ").trim();}
 JSONObject enemy(String text)throws Exception{String hay=" "+clean(text)+" ";JSONArray list=catalog.optJSONArray("enemies");for(int i=0;list!=null&&i<list.length();i++){JSONObject e=list.getJSONObject(i);String n=clean(e.optString("name"));if(n.length()>3&&hay.contains(" "+n+" "))return e;}return null;}
 String area(String text){String hay=" "+clean(text)+" ";JSONArray list=catalog.optJSONArray("areas");for(int i=0;list!=null&&i<list.length();i++){String name=list.optString(i),n=clean(name).replace("restricted area ","").replace("hollow ","");if(n.length()>5&&hay.contains(" "+n+" "))return name;}return "";}
 String agent(String text){String hay=" "+clean(text)+" ";JSONArray list=catalog.optJSONArray("agents");for(int i=0;list!=null&&i<list.length();i++){JSONObject a=list.optJSONObject(i);String n=clean(a.optString("name"));if(n.length()>3&&hay.contains(" "+n+" "))return a.optString("id");}return "";}
 static final class Identity{String candidate="",confirmed="";int count;boolean update(String next){if(next.isEmpty())return !confirmed.isEmpty();if(next.equals(candidate))count++;else{candidate=next;count=1;}if(count>=(confirmed.isEmpty()?2:3))confirmed=next;return !confirmed.isEmpty();}void clear(){candidate=confirmed="";count=0;}}
}

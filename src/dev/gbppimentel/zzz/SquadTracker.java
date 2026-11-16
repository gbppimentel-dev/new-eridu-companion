package dev.gbppimentel.zzz;
import org.json.*;import java.util.*;
/** Confirm changing HUD slots independently, but retain the complete squad through occlusion. */
final class SquadTracker {
 final String[] candidate=new String[3];final int[] count=new int[3];final List<String> members=new ArrayList<>();
 JSONArray update(JSONArray raw,long now)throws Exception{String[] current=new String[3],confirmed=new String[3];for(int i=0;i<raw.length();i++){JSONObject a=raw.getJSONObject(i);int slot=a.optInt("slot",-1);if(slot>=0&&slot<3)current[slot]=a.optString("id");}for(int slot=0;slot<3;slot++){String id=current[slot];count[slot]=id==null?0:id.equals(candidate[slot])?count[slot]+1:1;candidate[slot]=id;if(count[slot]>=3)confirmed[slot]=id;}
 Set<String> complete=new LinkedHashSet<>();for(String id:confirmed)if(id!=null)complete.add(id);if(complete.size()==3&&!new HashSet<String>(members).equals(complete)){members.clear();members.addAll(complete);}else if(members.size()<3)for(String id:complete)if(!members.contains(id)&&members.size()<3)members.add(id);
 JSONArray out=new JSONArray();for(String id:members){int slot=-1;for(int i=0;i<3;i++)if(id.equals(confirmed[i]))slot=i;out.put(new JSONObject().put("id",id).put("slot",slot));}return out;}
}

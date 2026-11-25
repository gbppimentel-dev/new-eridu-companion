package dev.gbppimentel.zzz;
import java.util.*;
import org.json.JSONObject;
public final class AccountSelectionTest {
 static Map<String,Object> saved(String uid,String server)throws Exception{
  Map<String,Object> p=new HashMap<>();p.put("state:"+uid,new JSONObject().put("uid",uid).put("server",server).put("session","connected").toString());return p;
 }
 static void check(boolean condition){if(!condition)throw new AssertionError("Account migration policy");}
 public static void main(String[] args)throws Exception{
  Map<String,Object> p=saved("1000000001","prod_gf_jp");
  AccountSelection.Saved s=AccountSelection.legacy(p,true);check(s!=null&&s.uid.equals("1000000001")&&s.server.equals("prod_gf_jp"));
  check(AccountSelection.legacy(new HashMap<>(),true)==null);
  check(AccountSelection.legacy(p,false)==null);
  p.put("state:","{\"uid\":\"\",\"session\":\"offline\"}");check(AccountSelection.legacy(p,true)!=null);
  p.put("uid","");check(AccountSelection.legacy(p,true)==null);
  p.put("uid","1000000000");check(AccountSelection.legacy(p,true)==null);
  p=saved("1000000001","prod_gf_jp");p.putAll(saved("1000000000","prod_gf_us"));check(AccountSelection.legacy(p,true)==null);
  p=saved("1000000001","invalid");check(AccountSelection.legacy(p,true)==null);
  p=saved("1000000001","prod_gf_jp");p.put("state:1000000001","{\"uid\":\"1000000000\"}");check(AccountSelection.legacy(p,true)==null);
  p.put("state:1000000001","broken json");check(AccountSelection.legacy(p,true)==null);
  p=saved("1000000001","prod_gf_jp");p.put("state:1000000001",new JSONObject((String)p.get("state:1000000001")).put("session","disconnected").toString());check(AccountSelection.legacy(p,true)==null);
  for(String region:Arrays.asList("prod_gf_jp","prod_gf_us","prod_gf_eu","prod_gf_sg"))check(AccountSelection.legacy(saved("1000000001",region),true).server.equals(region));
  System.out.println("15 legacy account selection checks passed");
 }
}

package dev.gbppimentel.zzz;
import java.util.*;
final class AuthRecovery {
 static boolean expired(int code){return code==-100||code==10001||code==10002;}
 static boolean verification(int code){return code==1034||code==10035||code==5003;}
 static String freshBadgeCookies(String cookies){Set<String> stale=new HashSet<>(Arrays.asList("e_nap_token","e_lrsag","SERVERID","SERVERCORSID"));StringBuilder out=new StringBuilder();for(String part:cookies.split(";")){int i=part.indexOf('=');if(i>0&&!stale.contains(part.substring(0,i).trim()))out.append(part.trim()).append("; ");}return out.toString();}
}

package dev.gbppimentel.zzz;
import java.net.URI;
import java.util.Locale;
final class LoginPolicy {
 static final String DESKTOP_UA="Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36";
 static final String TOOLS="https://act.hoyolab.com/app/zzz-game-record/index.html?lang=en-us#/zzz";
 static final String COMMUNITY="https://www.hoyolab.com/home";
 static boolean trustedWeb(String address){try{URI u=new URI(address);if(!"https".equalsIgnoreCase(u.getScheme())||u.getUserInfo()!=null)return false;String h=u.getHost();if(h==null)return false;h=h.toLowerCase(Locale.ROOT);return domain(h,"hoyolab.com")||domain(h,"hoyoverse.com")||domain(h,"mihoyo.com")||domain(h,"geetest.com")||h.equals("accounts.google.com")||h.equals("appleid.apple.com")||h.equals("www.facebook.com")||h.equals("twitter.com")||h.equals("api.twitter.com")||h.equals("x.com");}catch(Exception e){return false;}}
 private static boolean domain(String h,String d){return h.equals(d)||h.endsWith("."+d);}
 static String desktop(String address){if(!trustedWeb(address))return TOOLS;try{URI u=new URI(address);if("act.hoyolab.com".equalsIgnoreCase(u.getHost())&&u.getPath().startsWith("/app/zzz-game-record/")&&u.getPath().endsWith("/m.html"))return TOOLS;return address;}catch(Exception e){return TOOLS;}}
}

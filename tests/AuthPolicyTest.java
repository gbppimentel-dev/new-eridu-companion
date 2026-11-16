package dev.gbppimentel.zzz;

/** Runs with synthetic tokens only, on the desktop JVM. */
public final class AuthPolicyTest {
 static int checks;
 static void check(boolean condition,String message){if(!condition)throw new AssertionError(message);checks++;}
 static void rejected(String value)throws Exception{boolean fail=false;try{SessionCookies.parse(value);}catch(Exception e){fail=true;}check(fail,"Rejected malformed or unrelated session");}
 public static void main(String[] args)throws Exception{
  check(LoginPolicy.trustedWeb("https://account.hoyolab.com/login"),"Official account host");
  check(LoginPolicy.trustedWeb("https://account.hoyoverse.com/login-platform"),"Official account iframe");
  check(LoginPolicy.trustedWeb("https://accounts.google.com/o/oauth2/auth"),"Explicit provider navigation");
  check(!LoginPolicy.trustedWeb("https://hoyolab.com.evil.example/login"),"Domain suffix spoof rejected");
  check(!LoginPolicy.trustedWeb("https://hoyolab.com@evil.example/login"),"Userinfo spoof rejected");
  check(!LoginPolicy.trustedWeb("https://evil.example@hoyolab.com/login"),"URL credentials rejected");
  check(!LoginPolicy.trustedWeb("http://www.hoyolab.com/home"),"Cleartext rejected");
  check(!LoginPolicy.trustedWeb("hoyolab://openApp"),"App handoff cannot silently become a WebView page");
  check(!LoginPolicy.trustedWeb("intent://home#Intent;scheme=hoyolab;end"),"Intent links use visible recovery");
  check(!LoginPolicy.trustedWeb("javascript:alert(1)"),"Javascript URL rejected");
  check(LoginPolicy.desktop("https://act.hoyolab.com/app/zzz-game-record/m.html?lang=en-us").equals(LoginPolicy.TOOLS),"Mobile gateway rewritten to web flow");
  check(LoginPolicy.desktop(LoginPolicy.COMMUNITY).equals(LoginPolicy.COMMUNITY),"Desktop community fallback preserved");
  check(!LoginPolicy.DESKTOP_UA.contains("Android")&&!LoginPolicy.DESKTOP_UA.contains("wv"),"Web mode avoids mobile gateway detection");
  String raw=SessionCookies.parse("ltuid_v2=123; ltoken_v2=synthetic-value; tracking=discard");
  check(raw.contains("ltoken_v2=synthetic-value"),"Raw session header accepted");
  check(!raw.contains("tracking"),"Only account session cookies imported");
  check(SessionCookies.parse("Cookie: ltuid=123; ltoken=sample").contains("ltoken=sample"),"Cookie prefix handled");
  check(SessionCookies.parse("{\"ltuid_v2\":\"123\",\"ltoken_v2\":\"sample\"}").contains("ltoken_v2=sample"),"Mapping export accepted");
  check(SessionCookies.parse("[{\"name\":\"ltoken_v2\",\"value\":\"sample\",\"domain\":\".hoyolab.com\"}]").contains("ltoken_v2=sample"),"Browser array export accepted");
  check(SessionCookies.parse("{\"cookies\":[{\"name\":\"cookie_token_v2\",\"value\":\"sample\",\"domain\":\"hoyolab.com\"}]}").contains("cookie_token_v2=sample"),"Wrapped browser export accepted");
  rejected("[{\"name\":\"ltoken_v2\",\"value\":\"sample\",\"domain\":\"hoyolab.com.evil.example\"}]");
  rejected("{\"ltoken_v2\":\"sample\\r\\nInjected: header\"}");
  rejected("{\"ltoken_v2\":\"sample;injected=value\"}");
  rejected("{\"password\":\"not-a-session\"}");
  rejected("not a cookie");
  rejected(new String(new char[65537]).replace('\0','x'));
  System.out.println(checks+" authentication routing/import checks passed");
 }
}

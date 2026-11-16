package dev.gbppimentel.zzz;
import android.app.*;import android.os.*;import android.content.*;import android.view.*;import android.webkit.*;import android.widget.*;import android.text.InputType;import java.io.*;import java.util.*;import java.util.concurrent.*;import org.json.*;

/** Official web login; no native bridge is exposed to account pages. */
public class LoginActivity extends Activity {
 final Handler handler=new Handler();
 final ExecutorService worker=Executors.newSingleThreadExecutor();
 final ArrayList<WebView> pages=new ArrayList<>();
 FrameLayout browser;LinearLayout recovery;TextView status,explanation;ProgressBar progress;
 volatile boolean closed,validating;boolean tools,loginDialogOpened;String lastCandidate="";int attempts;
 final Runnable timeout=()->recover("The official sign-in page did not finish loading. Retry or use the desktop community page.");
 final Runnable poll=new Runnable(){public void run(){if(closed)return;if(!tools)capture(false);handler.postDelayed(this,1800);}};
 int dp(int x){return (int)(x*getResources().getDisplayMetrics().density+.5f);}
 Button action(String title,View.OnClickListener listener){Button b=new Button(this);b.setText(title);b.setAllCaps(false);b.setTextSize(11);b.setTextColor(0xffeeeeee);b.setMinWidth(dp(40));b.setOnClickListener(listener);return b;}
 WebView current(){return pages.get(pages.size()-1);}

 @Override public void onCreate(Bundle saved){
  super.onCreate(saved);Api.LIMITS.reconnect();WebView.setWebContentsDebuggingEnabled(false);
  getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|WindowManager.LayoutParams.FLAG_SECURE);
  getWindow().getDecorView().setSystemUiVisibility(5894);tools=getIntent().getBooleanExtra("tools",false);
  LinearLayout root=new LinearLayout(this);root.setOrientation(1);root.setBackgroundColor(0xff151515);
  LinearLayout bar=new LinearLayout(this);bar.setGravity(Gravity.CENTER_VERTICAL);bar.setPadding(dp(8),dp(5),dp(8),dp(5));
  bar.addView(action("‹",v->goBack()));status=new TextView(this);status.setTextColor(0xffeeeeee);status.setTextSize(12);status.setPadding(dp(8),0,dp(6),0);
  bar.addView(status,new LinearLayout.LayoutParams(0,-2,1));
  bar.addView(action("Connect",v->{if(!capture(true))recover("Sign in on the official web page, then tap Connect. Your separate HoYoLAB app login is not shared with this companion.");}));
  root.addView(bar);progress=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);
  progress.setProgressTintList(android.content.res.ColorStateList.valueOf(0xffd8fa00));root.addView(progress,new LinearLayout.LayoutParams(-1,dp(3)));
  LinearLayout help=new LinearLayout(this);help.setGravity(Gravity.CENTER_VERTICAL);
  help.addView(action("Web sign-in",v->load(LoginPolicy.TOOLS)));help.addView(action("Desktop site",v->load(LoginPolicy.COMMUNITY)));help.addView(action("Other connection",v->showSessionOptions()));root.addView(help);
  browser=new FrameLayout(this);root.addView(browser,new LinearLayout.LayoutParams(-1,0,1));setContentView(root);
  CookieManager.getInstance().setAcceptCookie(true);addPage();
  recovery=new LinearLayout(this);recovery.setOrientation(1);recovery.setGravity(Gravity.CENTER);recovery.setPadding(dp(22),dp(12),dp(22),dp(12));recovery.setBackgroundColor(0xff202020);
  TextView heading=new TextView(this);heading.setText("HoYoLAB Connection");heading.setTextSize(22);heading.setTypeface(android.graphics.Typeface.create("sans-serif-medium",0));heading.setTextColor(0xffd8fa00);recovery.addView(heading);
  explanation=new TextView(this);explanation.setTextSize(13);explanation.setTextColor(0xffeeeeee);explanation.setGravity(Gravity.CENTER);explanation.setPadding(0,dp(12),0,dp(16));recovery.addView(explanation);
  LinearLayout choices=new LinearLayout(this);choices.setGravity(Gravity.CENTER);choices.addView(action("Retry web login",v->load(LoginPolicy.TOOLS)));choices.addView(action("Desktop site",v->load(LoginPolicy.COMMUNITY)));recovery.addView(choices);
  recovery.addView(action("Use a browser session",v->showSessionOptions()));recovery.setVisibility(View.GONE);browser.addView(recovery,new FrameLayout.LayoutParams(-1,-1));
  load(LoginPolicy.TOOLS);handler.postDelayed(poll,1800);
 }
 WebView addPage(){
  if(!pages.isEmpty())current().setVisibility(View.GONE);
  WebView page=new WebView(this);page.setBackgroundColor(0xff202020);WebSettings s=page.getSettings();
  s.setJavaScriptEnabled(true);s.setDomStorageEnabled(true);s.setAllowFileAccess(false);s.setAllowContentAccess(false);s.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
  s.setUserAgentString(LoginPolicy.DESKTOP_UA);s.setUseWideViewPort(true);s.setLoadWithOverviewMode(true);s.setBuiltInZoomControls(true);s.setDisplayZoomControls(false);s.setSupportMultipleWindows(true);
  CookieManager.getInstance().setAcceptThirdPartyCookies(page,true);
  page.setWebViewClient(new WebViewClient(){
   @Override public boolean shouldOverrideUrlLoading(WebView view,WebResourceRequest request){
    String url=request.getUrl().toString();String scheme=request.getUrl().getScheme();
    if(scheme!=null&&!Arrays.asList("https","about","blob","data").contains(scheme.toLowerCase(Locale.ROOT))){recover("This link opens a separate app. Continue with the official web sign-in here instead.");return true;}
    if(!request.isForMainFrame())return false;
    if("about:blank".equals(url)&&pages.size()>1)return false;
    if(!LoginPolicy.trustedWeb(url)){recover("This link opens the separate HoYoLAB app and cannot pass its login back. Use web sign-in here instead.");return true;}
    String desktop=LoginPolicy.desktop(url);if(!desktop.equals(url)){view.loadUrl(desktop);return true;}return false;
   }
   @Override public void onPageStarted(WebView v,String url,android.graphics.Bitmap icon){status.setText("Loading official HoYoLAB…");progress.setVisibility(View.VISIBLE);handler.removeCallbacks(timeout);handler.postDelayed(timeout,30000);}
   @Override public void onPageFinished(WebView v,String url){
    handler.removeCallbacks(timeout);status.setText("Official web sign-in · pinch to zoom");progress.setVisibility(View.GONE);if(!tools)capture(false);
    handler.postDelayed(()->{if(!closed&&!validating&&v==current())v.evaluateJavascript("Boolean(document.body&&(document.body.innerText.trim().length>20||document.querySelector(\"iframe\")))",value->{if("false".equals(value))recover("The official page could not finish loading its sign-in components. Retry or use the desktop site.");});},10000);
    if(!tools&&url.startsWith("https://act.hoyolab.com/app/zzz-game-record/"))openWebLogin(v,0);
   }
   @Override public void onReceivedError(WebView v,WebResourceRequest r,WebResourceError e){if(r.isForMainFrame())recover("Could not load official sign-in. Check your connection and retry.");}
   @Override public void onReceivedHttpError(WebView v,WebResourceRequest r,WebResourceResponse e){if(r.isForMainFrame()&&e.getStatusCode()>=400)recover("The official page is unavailable. Retry or use the desktop site.");}
   @Override public void onReceivedSslError(WebView v,SslErrorHandler h,android.net.http.SslError e){h.cancel();recover("The official site's secure connection could not be verified. Retry when your network is available.");}
  });
  page.setWebChromeClient(new WebChromeClient(){
   @Override public void onProgressChanged(WebView v,int value){progress.setProgress(value);}
   @Override public boolean onCreateWindow(WebView v,boolean dialog,boolean userGesture,Message result){
    if(!userGesture||pages.size()>=3)return false;WebView popup=addPage();WebView.WebViewTransport transport=(WebView.WebViewTransport)result.obj;transport.setWebView(popup);result.sendToTarget();return true;
   }
   @Override public void onCloseWindow(WebView v){closePopup();}
  });
  pages.add(page);browser.addView(page,new FrameLayout.LayoutParams(-1,-1));if(recovery!=null)recovery.bringToFront();return page;
 }
 void openWebLogin(WebView page,int tries){if(closed||validating||loginDialogOpened||page!=current())return;page.evaluateJavascript("(()=>{const plugin=window.hoyoverseAccountSdk&&window.hoyoverseAccountSdk.initAccountSeaPlugin;if(plugin&&typeof plugin.checkLoginInfo==='function'){plugin.checkLoginInfo({colorMode:'dark',uxMode:{mobile:'popup',pc:'popup'}});return true;}const nodes=[...document.querySelectorAll('*')];const vm=nodes.map(x=>x.__vue__).find(x=>x&&typeof x.$showAccountFlow==='function');if(vm){vm.$showAccountFlow({colorMode:'dark',uxMode:{mobile:'popup',pc:'popup'}});return true;}return false;})()",value->{if("true".equals(value))loginDialogOpened=true;else if(tries<16)handler.postDelayed(()->openWebLogin(page,tries+1),750);});}
 void closePopup(){if(pages.size()<2)return;WebView popup=pages.remove(pages.size()-1);browser.removeView(popup);popup.destroy();current().setVisibility(View.VISIBLE);}
 void load(String url){loginDialogOpened=false;while(pages.size()>1)closePopup();recovery.setVisibility(View.GONE);current().setVisibility(View.VISIBLE);current().loadUrl(LoginPolicy.desktop(url));}
 void recover(String message){if(closed||recovery==null)return;handler.removeCallbacks(timeout);status.setText("Connection needs attention");progress.setVisibility(View.GONE);explanation.setText(message);recovery.setVisibility(View.VISIBLE);recovery.bringToFront();}
 boolean capture(boolean explicit){
  if(closed)return false;if(validating){status.setText("Validating your ZZZ account…");return true;}LinkedHashMap<String,String> jar=new LinkedHashMap<>();
  for(String url:new String[]{"https://www.hoyolab.com","https://act.hoyolab.com","https://sg-act-public-api.hoyolab.com","https://api-account-os.hoyolab.com"}){
   String cookies=CookieManager.getInstance().getCookie(url);
   if(cookies!=null)for(String part:cookies.split(";")){int k=part.indexOf('=');if(k>0)jar.put(part.substring(0,k).trim(),part.substring(k+1));}
  }
  if(!(jar.containsKey("ltoken_v2")||jar.containsKey("ltoken")||jar.containsKey("cookie_token_v2")||jar.containsKey("cookie_token")))return false;
  StringBuilder all=new StringBuilder();for(Map.Entry<String,String> e:jar.entrySet())all.append(e.getKey()).append('=').append(e.getValue()).append("; ");
  if(explicit||!all.toString().equals(lastCandidate))validate(all.toString());return true;
 }
 void validate(String candidate){
  if(validating||closed)return;validating=true;lastCandidate=candidate;status.setText("Validating your ZZZ account…");progress.setVisibility(View.VISIBLE);
  worker.execute(()->{try{
   android.content.SharedPreferences prefs=getSharedPreferences("eridu",0);
   Api api=new Api(this,prefs.getString("uid",""),prefs.getString("server","prod_gf_jp"));api.cookies=candidate;api.persistCookies=false;
   JSONObject roles=api.roles();JSONArray list=roles.optJSONArray("list");
   if(list==null||list.length()==0)throw new IOException("This HoYoLAB account has no linked ZZZ profile. Sign in with the account you use for ZZZ.");
   if(closed||Thread.currentThread().isInterrupted())return;
   Vault.put(this,api.cookies);CookieManager.getInstance().flush();prefs.edit().putBoolean("loginChanged",true).apply();
   runOnUiThread(()->{if(!closed){Toast.makeText(this,"HoYoLAB connected · validating selected profile",Toast.LENGTH_SHORT).show();finish();}});
  }catch(Exception e){
   String message=e instanceof Api.Failure?e.getMessage():e instanceof java.net.UnknownHostException||e instanceof java.net.SocketTimeoutException?"Could not reach HoYoLAB to validate this session. Check your connection and retry.":e instanceof IOException?e.getMessage():"Could not validate this session. Sign in again or use a fresh browser session.";
   runOnUiThread(()->recover(message));
  }finally{validating=false;}});
 }
 void showSessionOptions(){
  new AlertDialog.Builder(this).setTitle("Connect with a browser session")
   .setMessage("If your login provider requires a full browser, import a HoYoLAB session on this device. It is validated first and stored encrypted. Never send it in chat. The separate HoYoLAB app cannot share its private login automatically.")
   .setPositiveButton("Paste session",(d,w)->pasteSession())
   .setNeutralButton("Import file",(d,w)->startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT).setType("*/*").addCategory(Intent.CATEGORY_OPENABLE),41))
   .setNegativeButton("Back to web login",(d,w)->load(LoginPolicy.TOOLS)).show();
 }
 void pasteSession(){
  EditText input=new EditText(this);input.setHint("HoYoLAB session cookies or cookie JSON");
  input.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
  input.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);input.setMinLines(3);
  new AlertDialog.Builder(this).setTitle("Paste session on this Thor").setView(input)
   .setPositiveButton("Validate & connect",(d,w)->{String text=input.getText().toString();input.setText("");importSession(text);})
   .setNegativeButton("Cancel",(d,w)->input.setText("")).show();
 }
 void importSession(String text){try{validate(SessionCookies.parse(text));}catch(Exception e){recover("This file or pasted text does not contain a usable HoYoLAB session. Import cookies from a signed-in HoYoLAB browser session.");}}
 @Override protected void onActivityResult(int request,int result,Intent data){
  super.onActivityResult(request,result,data);if(request!=41||result!=RESULT_OK||data==null||data.getData()==null)return;
  try(InputStream in=getContentResolver().openInputStream(data.getData())){
   ByteArrayOutputStream out=new ByteArrayOutputStream();byte[] b=new byte[4096];int n;
   while((n=in.read(b))!=-1){if(out.size()+n>65536)throw new IOException();out.write(b,0,n);}
   importSession(new String(out.toByteArray(),"UTF-8"));
  }catch(Exception e){recover("Could not read that session file. Select a cookie JSON or text export smaller than 64 KB.");}
 }
 void goBack(){if(pages.size()>1){closePopup();return;}if(recovery.getVisibility()==View.VISIBLE){recovery.setVisibility(View.GONE);return;}finish();}
 @Override public void onBackPressed(){goBack();}
 @Override protected void onDestroy(){closed=true;handler.removeCallbacksAndMessages(null);worker.shutdownNow();for(WebView page:pages)page.destroy();super.onDestroy();}
}

package dev.gbppimentel.sessionhome;
import android.app.Activity;import android.os.*;import android.widget.Toast;import java.io.*;
/** A Home action, not an app/session manager. Cocoon retains ownership of pairs and Quit. */
public final class MainActivity extends Activity {
 @Override public void onCreate(Bundle state){super.onCreate(state);getWindow().getDecorView().setAlpha(0);new Thread(()->{Exception failure=null;try{String apk=getApplicationInfo().sourceDir;if(!apk.matches("[A-Za-z0-9/_.~+=-]+"))throw new IOException("Invalid installed path");String runner="CLASSPATH="+apk+" app_process /system/bin dev.gbppimentel.sessionhome.SessionReturn";File script=new File(getFilesDir(),"paired-home.sh");try(FileOutputStream out=new FileOutputStream(script)){out.write(("#!/system/bin/sh\n"+runner+" capture\n"+
 // Launch the external screen explicitly: CATEGORY_HOME on display 4 can move the main launcher there.
 "am start --display 4 -f 0x10060000 -n rip.moth.cocoonshell/.ExternalDisplayActivity\n"+
 "am start --display 0 -f 0x10060000 -a android.intent.action.MAIN -c android.intent.category.HOME -n rip.moth.cocoonshell/.MainActivity\n"+
 "settings put system screen_focus_lock 1\n"+runner+" watch >/dev/null 2>&1 &\n").getBytes("UTF-8"));}
 IBinder binder=(IBinder)Class.forName("android.os.ServiceManager").getMethod("getService",String.class).invoke(null,"PServerBinder");if(binder==null)throw new IOException("Thor device service unavailable");Parcel data=Parcel.obtain(),reply=Parcel.obtain();try{data.writeStringArray(new String[]{"sh /data/user/0/dev.gbppimentel.sessionhome/files/paired-home.sh","0"});if(!binder.transact(0,data,reply,0))throw new IOException("Home action unavailable");}finally{data.recycle();reply.recycle();}
 }catch(Exception e){failure=e;}final Exception error=failure;runOnUiThread(()->{if(error!=null)Toast.makeText(this,"Could Not Return Both Screens Home",Toast.LENGTH_SHORT).show();finishAndRemoveTask();overridePendingTransition(0,0);});},"PairedHome").start();}
}

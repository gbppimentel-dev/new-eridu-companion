package dev.gbppimentel.zzz;
import android.content.*;import org.json.*;import java.util.*;
public class BootReceiver extends BroadcastReceiver{public void onReceive(Context c,Intent i){if(!c.getSharedPreferences("eridu",0).getBoolean("reminders",false))return;for(Map.Entry<String,?> e:c.getSharedPreferences("reminders",0).getAll().entrySet())try{JSONObject j=new JSONObject(e.getValue().toString());ReminderReceiver.schedule(c,e.getKey(),j.getString("title"),j.getString("text"),j.getLong("at"));}catch(Exception ignored){}}}

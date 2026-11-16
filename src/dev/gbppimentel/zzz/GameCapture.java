package dev.gbppimentel.zzz;
import android.graphics.Bitmap;import android.os.IBinder;import java.lang.reflect.*;import java.util.*;

/** Small screenshot of physical display 0. Called only by the firmware helper. */
final class GameCapture {
 private final Object args;private final Method capture;
 GameCapture()throws Exception{this(480,270);}
 GameCapture(int width,int height)throws Exception{
  Class<?> surface=Class.forName("android.view.SurfaceControl");
  long[] ids=(long[])surface.getDeclaredMethod("getPhysicalDisplayIds").invoke(null);
  if(ids.length==0)throw new IllegalStateException("No display");
  long display=ids[0];
  try{Class<?> global=Class.forName("android.hardware.display.DisplayManagerGlobal");Object manager=global.getDeclaredMethod("getInstance").invoke(null);Object info=global.getDeclaredMethod("getDisplayInfo",int.class).invoke(manager,0);Object address=info.getClass().getField("address").get(info);display=((Number)address.getClass().getMethod("getPhysicalDisplayId").invoke(address)).longValue();}catch(Exception ignored){}
  IBinder token=(IBinder)surface.getDeclaredMethod("getPhysicalDisplayToken",long.class).invoke(null,display);
  Class<?> builder=Class.forName("android.view.SurfaceControl$DisplayCaptureArgs$Builder");Object b=builder.getConstructor(IBinder.class).newInstance(token);
  builder.getMethod("setSize",int.class,int.class).invoke(b,width,height);
  for(Class<?> c=builder;c!=null;c=c.getSuperclass())try{c.getDeclaredMethod("setCaptureSecureLayers",boolean.class).invoke(b,false);break;}catch(NoSuchMethodException ignored){}
  args=builder.getMethod("build").invoke(b);capture=surface.getDeclaredMethod("captureDisplay",args.getClass());
 }
 Bitmap frame()throws Exception{Object result=capture.invoke(null,args);if(result==null)throw new IllegalStateException("Capture unavailable");Bitmap hardware=(Bitmap)result.getClass().getMethod("asBitmap").invoke(result);if(hardware==null)throw new IllegalStateException("Protected frame");try{return hardware.copy(Bitmap.Config.ARGB_8888,false);}finally{hardware.recycle();try{Object buffer=result.getClass().getMethod("getHardwareBuffer").invoke(result);if(buffer instanceof AutoCloseable)((AutoCloseable)buffer).close();}catch(Exception ignored){}}}

 static final class Foreground {
  private final Object service;private final Method tasks;
  Foreground()throws Exception{service=Class.forName("android.app.ActivityTaskManager").getDeclaredMethod("getService").invoke(null);tasks=service.getClass().getMethod("getTasks",int.class,boolean.class,boolean.class,int.class);}
  String topPackage(){try{List<?> list=(List<?>)tasks.invoke(service,8,false,false,0);for(Object info:list){Object component=info.getClass().getField("topActivity").get(info);if(component==null)continue;boolean visible=true;try{visible=info.getClass().getField("isVisible").getBoolean(info);}catch(Exception ignored){}if(!visible)continue;String pkg=(String)component.getClass().getMethod("getPackageName").invoke(component);return pkg;}return "";}catch(Exception e){return null;}}
  boolean gameVisible(){return "com.HoYoverse.Nap".equals(topPackage());}
 }
}

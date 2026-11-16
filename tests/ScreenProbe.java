package dev.gbppimentel.zzz;
import android.graphics.*;import android.os.*;import java.lang.reflect.*;import java.io.*;import java.util.*;
public class ScreenProbe {
 public static void main(String[] a)throws Exception{
  Class<?> sc=Class.forName("android.view.SurfaceControl");for(Method m:sc.getDeclaredMethods())if(Arrays.asList("getPhysicalDisplayIds","getPhysicalDisplayToken","captureDisplay").contains(m.getName()))System.out.println(m);
  Class<?> at=Class.forName("android.app.ActivityTaskManager");Object service=at.getDeclaredMethod("getService").invoke(null);for(Method m:service.getClass().getMethods())if(m.getName().equals("getTasks"))System.out.println(m);
  long[] ids=(long[])sc.getDeclaredMethod("getPhysicalDisplayIds").invoke(null);System.out.println("Displays "+Arrays.toString(ids));
  android.os.IBinder token=(android.os.IBinder)sc.getDeclaredMethod("getPhysicalDisplayToken",long.class).invoke(null,ids[0]);
  Class<?> builder=Class.forName("android.view.SurfaceControl$DisplayCaptureArgs$Builder");Object b=builder.getConstructor(android.os.IBinder.class).newInstance(token);builder.getMethod("setSize",int.class,int.class).invoke(b,480,270);Object args=builder.getMethod("build").invoke(b);
  long start=System.nanoTime();Object result=sc.getDeclaredMethod("captureDisplay",args.getClass()).invoke(null,args);Bitmap hardware=(Bitmap)result.getClass().getMethod("asBitmap").invoke(result);Bitmap image=hardware.copy(Bitmap.Config.ARGB_8888,false);System.out.println("Capture "+image.getWidth()+"x"+image.getHeight()+" "+((System.nanoTime()-start)/1000000)+"ms");
  try(FileOutputStream out=new FileOutputStream("/data/local/tmp/eridu-probe.png")){image.compress(Bitmap.CompressFormat.PNG,100,out);}new File("/data/local/tmp/eridu-probe.png").setReadable(true,false);image.recycle();hardware.recycle();
 }
}

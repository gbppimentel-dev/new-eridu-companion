package dev.gbppimentel.zzz;
import java.io.*;import java.nio.*;import java.nio.charset.StandardCharsets;import java.util.*;import org.json.*;
/** Passive reader: discovers the controller by name; never grabs or writes input. */
public final class ControllerReader {
 static final File DIR=new File("/data/user/0/dev.gbppimentel.zzz/files"),WATCH=new File(DIR,"observer.watch"),PID=new File(DIR,"controller.pid"),ERROR=new File(DIR,"controller.error");
 static final Object LOCK=new Object();static final Map<String,Boolean> buttons=new LinkedHashMap<>();static final Map<String,Long> pulses=new LinkedHashMap<>();static final Map<String,Double> axes=new LinkedHashMap<>();static long published;static boolean dirty,urgent;
 static String key(int code){switch(code){case 304:return "A";case 305:return "B";case 307:return "X";case 308:return "Y";case 310:return "L1";case 311:return "R1";case 312:return "L2";case 313:return "R2";case 314:return "SELECT";case 315:return "START";case 317:return "L3";case 318:return "R3";case 544:return "UP";case 545:return "DOWN";case 546:return "LEFT";case 547:return "RIGHT";default:return null;}}
 static void button(String name,boolean down){if(!Boolean.valueOf(down).equals(buttons.get(name))){buttons.put(name,down);if(down)pulses.put(name,System.currentTimeMillis());dirty=urgent=true;}}
 static void event(int type,int code,int value)throws Exception{ synchronized(LOCK){
  if(type==1){String name=key(code);if(name!=null&&value!=2)button(name,value!=0);}
  else if(type==3){String axis=code==0?"lx":code==1?"ly":code==2?"rx":code==5?"ry":null;
   if(axis!=null){double v=Math.max(-1,Math.min(1,value/32767.0));if(Math.abs(v)<.025)v=0;Double old=axes.put(axis,v);if(old==null||Math.abs(old-v)>.01){dirty=true;if(v==0)urgent=true;}}
   else if(code==9)button("R2",value>2000);else if(code==10)button("L2",value>2000);
   else if(code==16){button("LEFT",value<0);button("RIGHT",value>0);}else if(code==17){button("UP",value<0);button("DOWN",value>0);}
  }else if(type==0&&code==3){buttons.clear();axes.clear();dirty=urgent=true;}
  if(type==0&&code==0&&dirty&&(urgent||System.currentTimeMillis()-published>=33))publish(true);
 }}
 static void write(File f,String value)throws Exception{try(FileOutputStream out=new FileOutputStream(f)){out.write(value.getBytes(StandardCharsets.UTF_8));}f.setReadable(true,false);}
 static void publish(boolean available)throws Exception{synchronized(LOCK){JSONObject state=new JSONObject().put("available",available).put("at",System.currentTimeMillis()).put("buttons",new JSONObject(buttons)).put("pulses",new JSONObject(pulses)).put("axes",new JSONObject(axes));File next=new File(DIR,"controller.next");write(next,state.toString());if(!next.renameTo(new File(DIR,"controller.json")))throw new IOException("Input frame unavailable");published=System.currentTimeMillis();dirty=urgent=false;}}
 static void cleanup(){try{String own=Integer.toString(android.os.Process.myPid());if(PID.exists()&&new String(java.nio.file.Files.readAllBytes(PID.toPath()),StandardCharsets.UTF_8).trim().equals(own))PID.delete();}catch(Exception ignored){}}
 static String controller()throws IOException{File[] entries=new File("/sys/class/input").listFiles();String best=null;int highest=0;if(entries!=null)for(File f:entries){if(!f.getName().matches("event[0-9]+"))continue;try{String n=new String(java.nio.file.Files.readAllBytes(new File(f,"device/name").toPath()),StandardCharsets.UTF_8).toLowerCase(Locale.ROOT);int score=n.contains("odin")?100:n.contains("ayn")?90:n.contains("gamepad")?70:n.contains("controller")?60:0;if(score>highest){highest=score;best="/dev/input/"+f.getName();}}catch(Exception ignored){}}if(best==null)throw new IOException("Controller not detected");return best;}
 public static void main(String[] args){try{
  if(!WATCH.exists()||System.currentTimeMillis()-WATCH.lastModified()>4000)return;
  try(DataInputStream input=new DataInputStream(new FileInputStream(controller()))){
   write(PID,Integer.toString(android.os.Process.myPid()));ERROR.delete();publish(true);
   Thread watch=new Thread(()->{while(true){try{Thread.sleep(1000);if(!WATCH.exists()||System.currentTimeMillis()-WATCH.lastModified()>4000){publish(false);cleanup();System.exit(0);}publish(true);}catch(Exception e){cleanup();System.exit(0);}}},"ThorControllerWatch");watch.setDaemon(true);watch.start();
   byte[] record=new byte[24];ByteBuffer b=ByteBuffer.wrap(record).order(ByteOrder.LITTLE_ENDIAN);
   while(true){input.readFully(record);event(b.getShort(16)&65535,b.getShort(18)&65535,b.getInt(20));}
  }
 }catch(Exception e){try{publish(false);write(ERROR,"unavailable");}catch(Exception ignored){}}finally{cleanup();}}
}

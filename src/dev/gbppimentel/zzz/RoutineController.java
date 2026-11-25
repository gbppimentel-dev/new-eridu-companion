package dev.gbppimentel.zzz;
import java.io.*;import java.nio.*;import java.util.*;
/** Real Thor input events, routed to the existing game task. No touch emulation. */
final class RoutineController implements AutoCloseable {
 static final Map<String,int[]> KEYS=new HashMap<>();
 static {String[] names={"A","B","X","Y","L1","R1","SELECT","START","L3","R3"};int[] codes={304,305,307,308,310,311,314,315,317,318};for(int i=0;i<codes.length;i++)KEYS.put(names[i],new int[]{1,codes[i],1});KEYS.put("UP",new int[]{3,17,-1});KEYS.put("DOWN",new int[]{3,17,1});KEYS.put("LEFT",new int[]{3,16,-1});KEYS.put("RIGHT",new int[]{3,16,1});KEYS.put("L2",new int[]{1,312,1});KEYS.put("R2",new int[]{1,313,1});}
 static final ControllerExpectations expected=new ControllerExpectations();
 final FileOutputStream out;final GameCapture.Foreground foreground;final Set<String> held=new HashSet<>();
 RoutineController(GameCapture.Foreground foreground)throws Exception{this.foreground=foreground;out=new FileOutputStream(ControllerReader.controller());}
 synchronized void event(int type,int code,int value)throws Exception{byte[] bytes=new byte[24];ByteBuffer b=ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);b.putShort(16,(short)type);b.putShort(18,(short)code);b.putInt(20,value);if(type!=0)expected.expect(type,code,value,System.currentTimeMillis());out.write(bytes);}
 void down(String name)throws Exception{int[] k=KEYS.get(name);if(k==null)throw new IOException("Unknown Controller Button");foreground.focusGame();held.add(name);event(k[0],k[1],k[2]);sync();}
 void up(String name)throws Exception{int[] k=KEYS.get(name);event(k[0],k[1],0);sync();held.remove(name);}
 void chord(String modifier,String key)throws Exception{down(modifier);try{Thread.sleep(100);press(key,100);}finally{up(modifier);}}
 void sync()throws Exception{event(0,0,0);out.flush();}
 void press(String name,int ms)throws Exception{int[] key=KEYS.get(name);if(key==null)throw new IOException("Unknown Controller Button");foreground.focusGame();held.add(name);try{event(key[0],key[1],key[2]);sync();long until=System.currentTimeMillis()+Math.min(500,Math.max(50,ms));while(System.currentTimeMillis()<until){if(DailyAutomation.manualStop)throw new InterruptedException("Stopped By Controller Input");Thread.sleep(10);}}finally{event(key[0],key[1],0);sync();held.remove(name);}}
 @Override public void close(){for(String name:new ArrayList<>(held))try{int[] key=KEYS.get(name);event(key[0],key[1],0);sync();}catch(Exception ignored){}held.clear();try{out.close();}catch(Exception ignored){}}
}

package dev.gbppimentel.zzz;
import java.util.*;
public final class ControllerHoldTest {
 static int checks;
 static void check(boolean ok,String why){checks++;if(!ok)throw new AssertionError(why);}
 static final class Clock implements ControllerHold.Timer {long now;boolean fail;public long now(){return now;}public void sleep(int ms)throws Exception{if(fail)throw new InterruptedException("Timer interrupted");now+=ms;}}
 public static void main(String[] args)throws Exception{
  List<Boolean> signals=new ArrayList<>();Clock c=new Clock();
  ControllerHold.run(1150,()->{},down->signals.add(down),c);
  check(c.now>=1150&&c.now<1200,"Held EX must exceed the old 500 ms cap");check(signals.equals(Arrays.asList(true,false)),"One down and one up");
  signals.clear();c.now=0;ControllerHold.run(9000,()->{},down->signals.add(down),c);check(c.now==2000,"Bound long holds");
  signals.clear();c.now=0;try{ControllerHold.run(1500,()->{if(c.now>=60)throw new InterruptedException("Stop");},down->signals.add(down),c);throw new AssertionError("Stop ignored");}catch(InterruptedException expected){}
  check(c.now==60,"Stop interrupts hold promptly");check(signals.equals(Arrays.asList(true,false)),"Stop releases held button");
  signals.clear();try{ControllerHold.run(1500,()->{throw new InterruptedException("Already stopped");},down->signals.add(down),c);throw new AssertionError();}catch(InterruptedException expected){}
  check(signals.isEmpty(),"Already-stopped routine sends no input");
  signals.clear();try{ControllerHold.run(1500,()->{},down->{signals.add(down);if(down)throw new Exception("Write failed");},c);throw new AssertionError();}catch(Exception expected){}
  check(signals.equals(Arrays.asList(true,false)),"Failed press still attempts release");
  signals.clear();c.fail=true;try{ControllerHold.run(1500,()->{},down->signals.add(down),c);throw new AssertionError();}catch(InterruptedException expected){}
  check(signals.equals(Arrays.asList(true,false)),"Interrupted sleep releases button");
  System.out.println(checks+" controller hold checks passed");
 }
}

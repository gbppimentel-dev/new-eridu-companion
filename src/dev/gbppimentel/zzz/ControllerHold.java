package dev.gbppimentel.zzz;

/** Bounded controller holds always release, including Stop and failed input writes. */
final class ControllerHold {
 interface Guard {void check()throws Exception;}
 interface Signal {void send(boolean down)throws Exception;}
 interface Timer {long now();void sleep(int ms)throws Exception;}
 static final Timer REAL=new Timer(){public long now(){return System.nanoTime()/1000000;}public void sleep(int ms)throws Exception{Thread.sleep(ms);}};
 static void run(int requestedMs,Guard guard,Signal signal,Timer timer)throws Exception{
  guard.check();
  try{
   signal.send(true);long until=timer.now()+Math.min(2000,Math.max(50,requestedMs));
   while(timer.now()<until){guard.check();timer.sleep(20);}
  }finally{signal.send(false);}
 }
}

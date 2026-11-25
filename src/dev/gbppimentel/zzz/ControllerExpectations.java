package dev.gbppimentel.zzz;
import java.util.*;
/** Exact one-shot acknowledgments prevent the runner from stopping on its own input. */
final class ControllerExpectations {
 static final class Event {final int type,code,value;final long until;Event(int t,int c,int v,long now){type=t;code=c;value=v;until=now+1000;}}
 final List<Event> pending=new ArrayList<>();
 synchronized void expect(int t,int c,int v,long now){expire(now);pending.add(new Event(t,c,v,now));}
 void expire(long now){pending.removeIf(e->e.until<now);}
 synchronized boolean consume(int t,int c,int v,long now){expire(now);for(int i=0;i<pending.size();i++){Event e=pending.get(i);if(e.type==t&&e.code==c&&e.value==v){pending.remove(i);return true;}}return false;}
}

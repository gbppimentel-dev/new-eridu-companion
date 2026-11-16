package dev.gbppimentel.zzz;
import java.util.*;
/** Presented frames of one game layer, never panel refresh or companion render rate. */
final class FrameRate {
 static final class Sample{final Double fps;final int frames;Sample(Double f,int n){fps=f;frames=n;}}
 static Sample parse(String dump,long now){TreeSet<Long> times=new TreeSet<>();for(String line:dump.split("\\r?\\n")){String[] parts=line.trim().split("\\s+");if(parts.length!=3)continue;try{long t=Long.parseLong(parts[1]);if(t>0&&t<Long.MAX_VALUE&&t<=now+100000000L&&t>=now-2200000000L)times.add(t);}catch(NumberFormatException ignored){}}if(times.size()<3)return new Sample(null,times.size());long first=times.first(),last=times.last();if(now-last>1500000000L)return new Sample(null,0);double seconds=(last-first)/1e9;if(seconds<.1)return new Sample(null,times.size());double fps=(times.size()-1)/seconds;if(fps>240||fps<0)return new Sample(null,0);return new Sample(Math.round(fps*10)/10.0,times.size());}
 static List<String> layers(String dump){List<String> result=new ArrayList<>();for(String line:dump.split("\\r?\\n")){line=line.trim();if(line.contains("com.HoYoverse.Nap/")&&line.startsWith("SurfaceView[")&&line.contains("(BLAST)"))result.add(line);}return result;}
}

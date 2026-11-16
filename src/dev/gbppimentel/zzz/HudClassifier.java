package dev.gbppimentel.zzz;
import org.json.*;import java.util.*;

/** Visible-HUD classifier. Its output is an observation, never engine telemetry. */
final class HudClassifier {
 static final int WIDTH=480,HEIGHT=270;
 static final class Template {String phase;int x,y,w,h;double[] gray;double mean,norm;}
 static final class Result {String phase;double confidence,motion,city,combat,menu,shortcuts,dark,results;Result(String p,double c){phase=p;confidence=c;}}
 final List<Template> templates=new ArrayList<>();int[] previous;
 HudClassifier(String json)throws Exception{JSONArray list=new JSONArray(json);for(int i=0;i<list.length();i++){JSONObject j=list.getJSONObject(i);Template t=new Template();t.phase=j.getString("phase");t.x=j.getInt("x");t.y=j.getInt("y");t.w=j.getInt("w");t.h=j.getInt("h");JSONArray pixels=j.getJSONArray("rgb");t.gray=new double[t.w*t.h];for(int k=0;k<t.gray.length;k++){double g=.299*pixels.getInt(k*3)+.587*pixels.getInt(k*3+1)+.114*pixels.getInt(k*3+2);t.gray[k]=g;t.mean+=g;}t.mean/=t.gray.length;for(double g:t.gray)t.norm+=(g-t.mean)*(g-t.mean);t.norm=Math.sqrt(t.norm);templates.add(t);}}
 double match(Template t,int[] pixels){double best=0;for(int dy=-3;dy<=3;dy+=1)for(int dx=-3;dx<=3;dx+=1){int x=t.x+dx,y=t.y+dy;if(x<0||y<0||x+t.w>=WIDTH||y+t.h>=HEIGHT)continue;double sum=0,sumSq=0,dot=0;int k=0;for(int yy=0;yy<t.h;yy++)for(int xx=0;xx<t.w;xx++){int rgb=pixels[(y+yy)*WIDTH+x+xx];double g=.299*((rgb>>16)&255)+.587*((rgb>>8)&255)+.114*(rgb&255);sum+=g;sumSq+=g*g;dot+=(t.gray[k++]-t.mean)*g;}double variance=Math.max(0,sumSq-sum*sum/t.gray.length);if(variance<25*t.gray.length||t.norm<1)continue;best=Math.max(best,dot/(t.norm*Math.sqrt(variance)));}return best;}
 Result classify(int[] pixels){Result r=new Result("unknown",0);for(Template t:templates){double v=match(t,pixels);if(t.phase.equals("results"))r.results=Math.max(r.results,v);else if(t.phase.equals("city"))r.city=Math.max(r.city,v);else if(t.phase.equals("combat"))r.combat=Math.max(r.combat,v);else if(t.phase.equals("menu"))r.menu=Math.max(r.menu,v);else if(t.phase.equals("shortcuts"))r.shortcuts=Math.max(r.shortcuts,v);}
  double brightness=0,variance=0,motion=0;int samples=0;for(int y=30;y<HEIGHT-15;y+=5)for(int x=15;x<WIDTH-15;x+=5){int p=pixels[y*WIDTH+x];double g=(((p>>16)&255)+((p>>8)&255)+(p&255))/3.0;brightness+=g;variance+=g*g;if(previous!=null){int q=previous[y*WIDTH+x];motion+=(Math.abs(((p>>16)&255)-((q>>16)&255))+Math.abs(((p>>8)&255)-((q>>8)&255))+Math.abs((p&255)-(q&255)))/765.0;}samples++;}brightness/=samples;variance=variance/samples-brightness*brightness;r.motion=previous==null?0:motion/samples;r.dark=brightness;previous=pixels.clone();
  if(r.results>.87){r.phase="results";r.confidence=r.results;}
  else if(r.menu>.82&&r.menu>=r.combat-.015&&r.menu>=r.city-.015){r.phase="menu";r.confidence=r.menu;}
  else if(r.combat>.80&&r.combat>r.city+.035){r.phase="combat";r.confidence=r.combat;}
  else if(r.city>.80){r.phase=r.shortcuts>.87?"shortcuts":"exploring";r.confidence=r.city;}
  else if(brightness<24&&variance<1100){r.phase="transition";r.confidence=.65;}
  return r;
 }
 static final class Stable {
  String phase="unknown",candidate="unknown";int count;double confidence;
  boolean update(String next,double value){if(next.equals(phase)){candidate=next;count=0;confidence=value;return false;}if(next.equals(candidate))count++;else{candidate=next;count=1;}int needed=next.equals("unknown")?6:next.equals("transition")?3:2;if(count>=needed){phase=next;confidence=value;count=0;return true;}return false;}
 }
}

package dev.gbppimentel.zzz;
import org.json.*;
/** Shared registration for every public agent portrait; no team-specific samples. */
final class PortraitFeatures {
 static final String VERSION="public-hud-v4";
 static final int[][] ACTIVE={{5,0,175,56},{0,0,180,56},{10,0,170,56},{5,0,175,52},{5,2,175,58}};
 static final int[][] RESERVE={{25,-10,165,54},{20,-10,165,54},{30,-10,165,54},{25,-8,165,52},{25,-12,165,56}};
 static void append(JSONArray out,String id,int[] pixels,int width,int height)throws Exception{for(int mode=0;mode<2;mode++)for(int[] box:mode==0?ACTIVE:RESERVE){JSONArray rgb=new JSONArray();for(int y=0;y<12;y++)for(int x=0;x<24;x++){int l=(int)((box[0]+x*(box[2]-box[0])/24.0)*width/180),r=(int)((box[0]+(x+1)*(box[2]-box[0])/24.0)*width/180),t=(int)((box[1]+y*(box[3]-box[1])/12.0)*height/64),b=(int)((box[1]+(y+1)*(box[3]-box[1])/12.0)*height/64);double red=0,green=0,blue=0;int count=0;for(int yy=t;yy<Math.max(t+1,b);yy++)for(int xx=l;xx<Math.max(l+1,r);xx++){count++;if(xx<0||xx>=width||yy<0||yy>=height)continue;int c=pixels[yy*width+xx];double alpha=((c>>>24)&255)/255.;red+=((c>>16)&255)*alpha;green+=((c>>8)&255)*alpha;blue+=(c&255)*alpha;}rgb.put(red/Math.max(1,count)).put(green/Math.max(1,count)).put(blue/Math.max(1,count));}out.put(new JSONObject().put("id",id).put("kind","combat").put("role",mode==0?"active":"reserve").put("rgb",rgb));}}
}

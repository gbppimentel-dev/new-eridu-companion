package dev.gbppimentel.zzz;
import android.graphics.*;import org.json.*;
public final class OcrProbe {public static void main(String[] args)throws Exception{try{OcrEngine engine=new OcrEngine();Bitmap b=BitmapFactory.decodeFile("/data/local/tmp/eridu-v2-game.png");System.out.println(new JSONObject().put("uid",engine.read(b,1650,1056,145,24,true)).put("header",engine.read(b,400,130,1000,150,false)).put("target",engine.read(b,520,0,960,180,false)));}catch(Throwable t){System.out.println(t.toString());}}}

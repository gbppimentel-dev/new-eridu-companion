package dev.gbppimentel.zzz;
import android.graphics.Bitmap;
/** Fixed-caption and controller-focus evidence for weekly menus; no saved screenshots. */
final class WeeklyScreen {
 boolean huntMenu,resultClear,resultExit,squad,squadReady,squadReadable,presets,presetFull;int presetColumn=-1;String presetName="";int waypointX=-1;
 boolean board,claim,milestone,huntTop,huntList,huntSetup,huntChallenge,challengeClaim,receipt,paid,next;int tile=-1,remaining=-1;String boss="";
 static double bright(int[] px,int x,int y,int w,int h,boolean green){int n=0,total=0;for(int yy=y/4;yy<(y+h)/4;yy++)for(int xx=x/4;xx<(x+w)/4;xx++){int c=px[yy*480+xx],r=c>>16&255,g=c>>8&255,b=c&255;total++;if(green?WeeklyPolicy.activeClaim(c):r>175&&g>175&&b>175)n++;}return total==0?0:(double)n/total;}
 WeeklyScreen(Bitmap full,OcrEngine o,DailyScreen f,int[] px){

  String title=DailyPolicy.clean(o.readLine(full,130,155,940,62));
  board=title.contains("three tile streak")||f.has("special points rewards","mission objective");
  if(board){
   tile=WeeklyPolicy.focusedTile(px);
   for(int[] b:f.focusBoxes){int w=b[2]-b[0],h=b[3]-b[1];if(b[0]>=100&&b[2]<=1075&&b[1]>=225&&b[3]<=940&&w>260&&w<350&&h>185&&h<245){int col=Math.round(((b[0]+b[2])/2-275)/315f),row=Math.round(((b[1]+b[3])/2-350)/230f);if(col>=0&&col<3&&row>=0&&row<3)tile=row*3+col;}}
   String button=DailyPolicy.clean(o.readLine(full,1190,870,195,45));claim=tile>=0&&button.equals("claim")&&bright(px,1210,878,165,35,true)>.04;
   milestone=DailyPolicy.clean(o.readLine(full,800,1007,278,43)).contains("claim special points rewards")&&bright(px,808,1015,255,27,false)>.15;
  }
  if(f.journal()&&f.handbookTab==2){
   String sidebar=DailyPolicy.clean(o.read(full,175,765,250,70,false));int free=WeeklyPolicy.remaining(o.readLine(full,1185,260,95,48));if(free<0)free=WeeklyPolicy.remainingDigits(o.readLine(full,1200,263,33,44),o.readLine(full,1237,263,36,44));
   huntList=(sidebar.contains("notorious hunt")||f.has("drop chances")||DailyPolicy.clean(o.readLine(full,540,258,520,30)).contains("drop chances"));if(huntList)remaining=free;
  }
  if(huntList)for(int[] b:f.focusBoxes)if(b[0]>1280&&b[2]<1660&&b[1]>305&&b[3]<435&&b[2]-b[0]>250&&b[2]-b[0]<360)huntTop=true;
  if(huntList&&f.stageTitles.isEmpty())for(int[] b:f.focusBoxes){
   if(b[0]<450||b[2]>1750||b[1]<450||b[3]>950||b[2]-b[0]<900||b[3]-b[1]<150||b[3]-b[1]>260)continue;
   int y=b[3]-56;String go=DailyPolicy.clean(o.readLine(full,1380,y-28,205,56)),name=DailyPolicy.clean(o.readLine(full,530,b[1]+30,780,65));
   if(go.equals("go")&&WeeklyPolicy.bossName(name))f.stageTitles.put(y,name);
  }
  huntChallenge=DailyPolicy.clean(o.readLine(full,480,246,620,50)).equals("weekly challenge")&&f.has("complete weekly challenges");
  if(huntChallenge)challengeClaim=WeeklyPolicy.huntClaim(o.readLine(full,1260,342,170,40),bright(px,1275,349,145,28,false),bright(px,1275,349,145,28,true));
  String quota=DailyPolicy.clean(o.readLine(full,1250,1005,330,48));
  if(quota.contains("remaining rewards")){
   remaining=WeeklyPolicy.remaining(quota);boss=DailyPolicy.clean(o.readLine(full,750,185,710,65));
   next=DailyPolicy.clean(o.readLine(full,1650,1005,170,50)).equals("next");
   huntSetup=remaining>=0&&WeeklyPolicy.bossName(boss)&&DailyPolicy.clean(o.readLine(full,760,680,410,50)).equals("challenge rewards")&&DailyPolicy.clean(o.readLine(full,760,365,330,45)).equals("current intel");
   paid=WeeklyPolicy.paid(quota+" "+f.resourceFooter+" "+f.region(1400,950,1900,1075));
  }
  if(!f.journal()){
   String deploy=DailyPolicy.clean(o.readLine(full,1680,1007,150,48)),options=DailyPolicy.clean(o.readLine(full,125,1008,205,45));
   squad=deploy.equals("deploy")&&options.equals("combat options");
   if(squad){String first=DailyPolicy.clean(o.readLine(full,235,895,180,39)),second=DailyPolicy.clean(o.readLine(full,710,895,180,39)),third=DailyPolicy.clean(o.readLine(full,1180,895,180,39));if(!second.equals("rina"))second=DailyPolicy.clean(o.brightLine(full,710,895,180,39));if(!third.equals("claret"))third=DailyPolicy.clean(o.brightLine(full,1180,895,180,39));squadReadable=!first.isEmpty()&&!second.isEmpty()&&!third.isEmpty();squadReady=WeeklyPolicy.requiredSquad(first,second,third);}
   presets=DailyPolicy.clean(o.read(full,1660,999,180,68,false)).contains("deploy preset");
   if(presets)for(int[] b:f.focusBoxes){int width=b[2]-b[0],height=b[3]-b[1];if(b[0]<130||b[2]>1800||b[1]<100||b[3]>975||width<700||width>900||height<210||height>310)continue;presetName=DailyPolicy.clean(o.readLine(full,b[0]+22,b[1]+17,520,45));presetColumn=b[0]<600?0:1;presetFull=WeeklyPolicy.remaining(o.readLine(full,b[2]-180,b[1]+17,95,45))==3;}
   if(!squad&&!presets&&!board&&!huntSetup&&!huntChallenge&&!f.bossBar){int[] marker=WeeklyWaypoint.locate(px);if(marker!=null){waypointX=marker[0];}}
  }
  if(f.results()){
   String objective=DailyPolicy.clean(o.read(full,125,667,350,65,false)),progress=o.readLine(full,535,678,78,43),exit=DailyPolicy.clean(o.readLine(full,1620,1005,225,55));
   resultExit=exit.equals("exit");resultClear=WeeklyPolicy.clearedResult(objective,progress,exit);
  }
  if(!f.journal()&&!squad&&!presets&&!huntSetup)huntMenu=f.has("remaining rewards","time left this cycle")||DailyPolicy.clean(o.readLine(full,195,995,350,37)).contains("remaining rewards")&&DailyPolicy.clean(o.readLine(full,195,1030,420,37)).contains("time left this cycle");
  receipt=f.rewardPopup||f.has("obtained")&&f.find("confirm",700,650,1250,850)!=null;
 }
}

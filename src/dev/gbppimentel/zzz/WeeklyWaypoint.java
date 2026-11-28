package dev.gbppimentel.zzz;
/** Match the objective ring after light smoothing to tolerate subpixel antialiasing. */
final class WeeklyWaypoint{ static final int[] REFERENCE={86,91,90,63,27,20,20,21,59,90,87,83,90,86,89,21,66,189,191,190,191,87,24,77,81,90,86,21,180,185,82,25,27,65,191,192,19,75,91,27,174,168,22,19,13,15,17,21,152,198,19,91,37,198,21,23,49,209,208,89,21,21,209,75,72,134,145,22,29,230,231,221,219,75,20,96,186,36,206,74,22,128,220,224,203,203,189,14,36,203,21,193,97,21,92,200,191,183,189,169,20,56,189,26,97,185,18,17,192,185,181,194,28,24,145,135,40,20,204,46,19,11,66,70,12,22,24,172,29,77,41,75,190,51,21,17,18,19,33,178,97,30,79,72,25,55,171,170,129,127,167,156,74,16,76,76,74,74,45,16,52,87,86,65,18,39,83,74,87};

 static double[] gray(int[] p){double[] out=new double[p.length];for(int i=0;i<p.length;i++){int c=p[i];out[i]=.299*(c>>16&255)+.587*(c>>8&255)+.114*(c&255);}return out;}
 static double[] blur(double[] p,int width,int height){double[] out=new double[p.length];for(int y=1;y<height-1;y++)for(int x=1;x<width-1;x++){int i=y*width+x;out[i]=(p[i-width-1]+2*p[i-width]+p[i-width+1]+2*p[i-1]+4*p[i]+2*p[i+1]+p[i+width-1]+2*p[i+width]+p[i+width+1])/16;}return out;}
 static int[] locate(int[] p){
  if(p==null||p.length!=480*270)return null;
  double[] raw=new double[169];for(int i=0;i<raw.length;i++)raw[i]=REFERENCE[i];double[] softened=blur(raw,13,13),reference=new double[121];int n=0;for(int y=1;y<12;y++)for(int x=1;x<12;x++)reference[n++]=softened[y*13+x];
  double mean=0,norm=0;for(double v:reference)mean+=v;mean/=reference.length;for(double v:reference)norm+=(v-mean)*(v-mean);norm=Math.sqrt(norm);
  double[] pixels=blur(gray(p),480,270);double high=.86;int[] best=null;
  for(int y=35;y<175;y++)for(int x=215;x<265;x++){
   double sum=0,sumSq=0,dot=0;int k=0;for(int yy=0;yy<11;yy++)for(int xx=0;xx<11;xx++){double g=pixels[(y+yy)*480+x+xx];sum+=g;sumSq+=g*g;dot+=(reference[k++]-mean)*g;}
   double variance=sumSq-sum*sum/reference.length;if(variance<reference.length*25)continue;double score=dot/(norm*Math.sqrt(variance));if(score>high){high=score;best=new int[]{(x+5)*4,(y+12)*4+10};}
  }return best;
 }
}
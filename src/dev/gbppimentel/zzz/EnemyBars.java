package dev.gbppimentel.zzz;
import java.util.*;
/** Counts only saturated, dark-outlined world-space health-bar segments. */
final class EnemyBars {
 static boolean green(int p){int r=(p>>16)&255,g=(p>>8)&255,b=p&255;return g>125&&g>r*1.12&&g>b*1.6;}
 static boolean dark(int p){return ((p>>16)&255)+((p>>8)&255)+(p&255)<230;}
 static int count(int[] p,int w,int h){List<int[]> centers=new ArrayList<>();for(int y=h/6;y<h*4/5;y++){int start=-1;for(int x=w/8;x<w*7/8;x++){boolean on=green(p[y*w+x]);if(on&&start<0)start=x;if((!on||x==w*7/8-1)&&start>=0){int len=x-start;if(len>=7&&len<=100){int outline=0;for(int xx=start;xx<x;xx++)if(dark(p[Math.min(h-1,y+3)*w+xx])||dark(p[Math.max(0,y-3)*w+xx]))outline++;if(outline>len*.65){boolean duplicate=false;for(int[] c:centers)if(Math.abs(c[0]-(start+x)/2)<50&&Math.abs(c[1]-y)<12)duplicate=true;if(!duplicate)centers.add(new int[]{(start+x)/2,y});}}start=-1;}}}return Math.min(10,centers.size());}
}

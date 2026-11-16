package dev.gbppimentel.zzz;
/** Home suspends the companion task for Cocoon; it never removes the resumable task. */
final class CompanionLifecycle {
 boolean paired;long homeSince=-1;
 void resume(){paired=false;homeSince=-1;}
 static boolean isHomeReason(String reason){return "homekey".equals(reason)||"home".equals(reason);}
 boolean observe(long now,boolean valid,boolean gameVisible,boolean launcherVisible){
  if(!valid){homeSince=-1;return false;}
  if(gameVisible){paired=true;homeSince=-1;return false;}
  if(!paired||!launcherVisible){homeSince=-1;return false;}
  if(homeSince<0)homeSince=now;
  return now-homeSince>=700;
 }
}

package dev.gbppimentel.sessionhome;
/** Task removal is not a quit until the game process is also gone for a grace period. */
final class SessionPolicy {
 static final int KEEP=0,RELEASE=1,CLOSE_COMPANION=2;
 static int missing(boolean sameTaskExists,boolean anotherGameTask,boolean gameProcess,long missingMs){
  if(sameTaskExists)return KEEP;
  if(anotherGameTask)return RELEASE;
  if(!gameProcess&&missingMs>=2500)return CLOSE_COMPANION;
  if(gameProcess&&missingMs>=5000)return RELEASE;
  return KEEP;
 }
}

package dev.gbppimentel.sessionhome;
public final class SessionPolicyTest {public static void main(String[] args){
 if(SessionPolicy.missing(true,false,true,90000)!=SessionPolicy.KEEP)throw new AssertionError("A game parked on a virtual display still exists");
 if(SessionPolicy.missing(true,false,false,90000)!=SessionPolicy.KEEP)throw new AssertionError("Task data wins over a transient process lookup");
 if(SessionPolicy.missing(false,false,false,0)!=SessionPolicy.KEEP)throw new AssertionError("Transient removal is not a quit");
 if(SessionPolicy.missing(false,false,false,2499)!=SessionPolicy.KEEP)throw new AssertionError("Full grace period required");
 if(SessionPolicy.missing(false,false,false,2500)!=SessionPolicy.CLOSE_COMPANION)throw new AssertionError("Confirmed quit closes the pair");
 if(SessionPolicy.missing(false,false,true,3000)!=SessionPolicy.KEEP)throw new AssertionError("A process restart gets time to restore its task");
 if(SessionPolicy.missing(false,false,true,5000)!=SessionPolicy.RELEASE)throw new AssertionError("A live host with a different activity must not be force-stopped");
 if(SessionPolicy.missing(false,true,true,90000)!=SessionPolicy.RELEASE)throw new AssertionError("Another title in the same emulator is a separate session");
 System.out.println("8 paired-session policy checks passed");}}

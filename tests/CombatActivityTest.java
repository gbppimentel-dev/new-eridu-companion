package dev.gbppimentel.zzz;
public final class CombatActivityTest {
 static int checks;static void check(boolean ok){checks++;if(!ok)throw new AssertionError("Combat loading/activity check "+checks);}
 public static void main(String[] args){CombatActivity a=new CombatActivity();a.loading();check(!a.stalled(75000,false));check(!a.stalled(75500,false));check(!a.stalled(93000,false));check(a.stalled(93001,false));check(!a.stalled(94000,true));check(!a.stalled(95000,false));a.loading();check(!a.stalled(180000,false));check(!a.stalled(180300,false));check(a.stalled(199000,false));System.out.println(checks+" combat loading/activity checks passed");}
}

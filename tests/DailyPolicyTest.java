package dev.gbppimentel.zzz;
public final class DailyPolicyTest {
 static int checks;static void check(boolean value){if(!value)throw new AssertionError("Daily policy check "+checks);checks++;}
 public static void main(String[]args){
  check(DailyPolicy.canStart("1000000001","1000000001",true,"exploring",true,100));
  check(!DailyPolicy.canStart("1000000001","1000000000",true,"exploring",true,100));
  check(!DailyPolicy.canStart("1000000001","1000000001",true,"combat",true,100));
  check(!DailyPolicy.canStart("1000000001","1000000001",true,"exploring",false,100));
  check(!DailyPolicy.canStart("1000000001","1000000001",true,"exploring",true,6000));
  check(!DailyPolicy.canContinue("1000000001","1000000001",false,100));
  check(!DailyPolicy.canContinue("1000000001","1000000001",true,8000));
  check(DailyPolicy.safeDeploy(320,100,100,0));check(DailyPolicy.safeDeploy(60,60,60,300));
  check(!DailyPolicy.safeDeploy(20,100,100,0));check(!DailyPolicy.safeDeploy(100,60,100,0));check(!DailyPolicy.safeDeploy(100,0,0,0));
  check(DailyPolicy.reserveTopOff(20,100,80)==80);check(DailyPolicy.reserveTopOff(20,60,40)==40);check(DailyPolicy.reserveTopOff(40,60,20)==20);
  check(DailyPolicy.reserveTopOff(0,100,1000)==0);check(DailyPolicy.reserveTopOff(100,100,1000)==0);check(DailyPolicy.reserveTopOff(20,100,79)==0);
  check(DailyPolicy.forbidden("Use Ether Battery"));check(DailyPolicy.forbidden("Use Polychromes"));check(DailyPolicy.forbidden("Insufficient Battery Charge"));
  check(!DailyPolicy.forbidden("Restore Battery Charge Extract Backup Battery Charge Ether Battery Polychrome"));
  check(DailyPolicy.loginOrError("Failed to obtain server list"));check(DailyPolicy.loginOrError("Press To Play"));
  check(DailyPolicy.batteryPair("240 / 100")[0]==240);check(DailyPolicy.batteryPair("40 / 60")[1]==60);check(DailyPolicy.batteryPair("240 / 240")==null);
  check(DailyPolicy.dennyReward("Challenge Rewards Denny 125000 EMPTY")==125000);
  check(DailyPolicy.dennyReward("Denny 125000 Investigator Log")==0);
  check(DailyPolicy.nextBattleCost(40,0)==40);check(DailyPolicy.nextBattleCost(19,0)==0);
  long three=java.time.Instant.parse("2026-11-24T19:00:00Z").toEpochMilli(),five=three+7200000;
  check(!DailyPolicy.errandsAvailable(three+1000,three));
  check(DailyPolicy.errandsAvailable(five,three));
  check(DailyPolicy.nextReset(three)==java.time.Instant.parse("2026-11-24T20:00:00Z").toEpochMilli());
  check(DailyPolicy.nextReset(five)==java.time.Instant.parse("2026-11-25T20:00:00Z").toEpochMilli());
  check(DailyPolicy.resetCycle(three)==DailyPolicy.resetCycle(three+3599999));
  check(DailyPolicy.resetCycle(three)!=DailyPolicy.resetCycle(three+3600000));
  check(DailyPolicy.forecastBattles(320,100,true,1000)==4);
  check(DailyPolicy.forecastBattles(20,100,true,80)==1);
  check(DailyPolicy.forecastBattles(20,100,true,79)==0);
  check(DailyPolicy.forecastBattles(0,100,true,1000)==0);
  check(DailyPolicy.forecastBattles(140,60,true,1000)==3);
  check(DailyPolicy.forecastBattles(140,60,false,1000)==2);
  check(DailyPolicy.batchFor("expert")==40);check(DailyPolicy.batchFor("discs")==60);
  check(DailyPolicy.taskCurrent(three+1000,three));check(!DailyPolicy.taskCurrent(five,three));check(DailyPolicy.taskCurrent(five+1000,five));check(!DailyPolicy.taskCurrent(five,0));check(!DailyPolicy.taskCurrent(three,three-86400000L));
  check(DailyPolicy.afterBattle(320,100)==220);check(DailyPolicy.afterBattle(60,60)==0);check(DailyPolicy.afterBattle(59,60)==-1);check(DailyPolicy.afterBattle(100,0)==-1);
  check(DailyPolicy.backupConversion(80,20,100,1000));check(DailyPolicy.backupConversion(80,20,101,1000));check(!DailyPolicy.backupConversion(80,20,102,1000));check(!DailyPolicy.backupConversion(80,20,99,1000));check(!DailyPolicy.backupConversion(0,100,100,1000));check(!DailyPolicy.backupConversion(80,20,100,-1));
  check(DailyPolicy.chargeQuantity("57")==57);check(DailyPolicy.chargeQuantity(" 1 ")==1);check(DailyPolicy.chargeQuantity("240")==240);check(DailyPolicy.chargeQuantity("241")==-1);check(DailyPolicy.chargeQuantity("0")==-1);check(DailyPolicy.chargeQuantity("57/240")==-1);
  check(DailyPolicy.chargeQuantity("57","")==57);check(DailyPolicy.chargeQuantity("","60")==60);check(DailyPolicy.chargeQuantity("17","17")==17);check(DailyPolicy.chargeQuantity("17","77")==-1);check(DailyPolicy.chargeQuantity("","")==-1);
  System.out.println(checks+" daily automation safety checks passed");
 }
}

package dev.gbppimentel.zzz;

import java.util.Locale;
import java.util.regex.*;

/** Resource and session limits shared by the on-device routine and host checks. */
final class DailyPolicy {
 static final long RUN_LIMIT_MS=45*60*1000L,SCREEN_LIMIT_MS=180000,BATTLE_LIMIT_MS=5*60*1000L;
 static final int COFFEE_DENNY_LIMIT=2500,MAX_BATTERY_SPEND=1500,MAX_BATTLES=20;
 static String clean(String value){return value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9 /]"," ").replaceAll("\\s+"," ").trim();}
 // Fixed Temple label crops must agree; a generic Revenue word never authorizes X.
 static boolean templeClaimReady(String status,String claim,String revenue){return clean(status).replace(" ","").matches("automanagementi?completed")&&clean(claim).matches("claim(?: [0-9]+)?")&&clean(revenue).equals("revenue");}
 static boolean templeRewardReceipt(String title,String confirm,String currency){String item=clean(currency);return clean(title).equals("obtain rewards")&&clean(confirm).equals("confirm")&&(item.equals("omnicoin")||item.equals("cloud crest"));}
 static boolean canResumeErrand(String selected,String visible,boolean confirmed,long age,boolean requested,boolean knownScreen){return requested&&knownScreen&&canContinue(selected,visible,confirmed,age);}
 static boolean storeOpeningConfirmation(String message,String ending,String button){return clean(message).equals("wishing you a prosperous store opening may the dennies")&&clean(ending).equals("flow")&&clean(button).equals("confirm");}
 enum TempleAction { WAIT,OPEN_AUTO,CLAIM,RECEIPT,START,RUNNING }
 static int templeTab(double businessOrange,double autoOrange){if(businessOrange>.35&&businessOrange-autoOrange>.15)return 0;if(autoOrange>.35&&autoOrange-businessOrange>.15)return 1;return -1;}
 static TempleAction templeAction(int tab,boolean receipt,boolean claim,boolean start,boolean running){if(receipt)return TempleAction.RECEIPT;if(tab==0)return TempleAction.OPEN_AUTO;if(tab!=1)return TempleAction.WAIT;if(claim)return TempleAction.CLAIM;if(start)return TempleAction.START;if(running)return TempleAction.RUNNING;return TempleAction.WAIT;}
 static boolean forbidden(String text){String s=clean(text);return s.contains("use ether batter")||s.contains("consume ether batter")||s.contains("replenish battery")||s.contains("battery replenishment")||s.contains("use polychrome")||s.contains("insufficient battery")||s.contains("insufficient denn")||s.contains("payment method")||s.contains("purchase monochrome");}
 static boolean loginOrError(String text){String s=clean(text);return s.contains("failed to obtain")||s.contains("connection timed out")||s.contains("connection lost")||s.contains("network error")||s.contains("tap to begin")||s.contains("press to play")||s.contains("tap to start")||s.contains("log in to your account");}
 static int batteryBudget(int available){return Math.max(0,Math.min(MAX_BATTERY_SPEND,available)/20*20);}
 static int nextBattleCost(int available,int alreadySpent){return Math.max(0,Math.min(100,Math.min(batteryBudget(available),MAX_BATTERY_SPEND-alreadySpent)/20*20));}
 static boolean safeDeploy(int available,int cost,int planned,int alreadySpent){return cost>=20&&cost<=100&&cost%20==0&&cost==planned&&available>=cost&&alreadySpent+cost<=MAX_BATTERY_SPEND;}
 static boolean canStart(String selected,String visible,boolean confirmed,String phase,boolean touch,long observationAge){return selected!=null&&selected.matches("[1-9][0-9]{7,11}")&&selected.equals(visible)&&confirmed&&touch&&observationAge>=0&&observationAge<6000&&(phase.equals("exploring")||phase.equals("shortcuts"));}
 static boolean canContinue(String selected,String visible,boolean confirmed,long observationAge){return selected!=null&&!selected.isEmpty()&&selected.equals(visible)&&confirmed&&observationAge>=0&&observationAge<8000;}
 static int reserveTopOff(int available,int batch,int reserves){if(available<=0||available>=batch||batch<=0)return 0;int need=batch-available;return reserves>=need?need:0;}
 // Asia reset is fixed at 04:00 UTC+08, independent of the Mac/device time zone.
 static final long DAY_MS=86400000L,RESET_ORIGIN_MS=-4*3600000L;
 static long resetCycle(long now){return Math.floorDiv(now-RESET_ORIGIN_MS,DAY_MS);}
 static long nextReset(long now){return (resetCycle(now)+1)*DAY_MS+RESET_ORIGIN_MS;}
 static boolean errandsAvailable(long now,long lastCompleted){return lastCompleted<=0||resetCycle(now)!=resetCycle(lastCompleted);}
 static boolean taskCurrent(long now,long completedAt){return completedAt>0&&resetCycle(now)==resetCycle(completedAt);}
 static int chargeQuantity(String value){String s=value==null?"":value.replaceAll("\\s+","");if(!s.matches("[0-9]{1,3}"))return -1;int n=Integer.parseInt(s);return n>0&&n<=240?n:-1;}
 static int chargeQuantity(String plain,String digits){int a=chargeQuantity(plain),b=chargeQuantity(digits);return a>0&&b>0?(a==b?a:-1):Math.max(a,b);}
 static boolean backupConversion(int before,int amount,int after,long elapsed){return before>0&&before<100&&amount>0&&amount<100&&elapsed>=0&&elapsed<=RUN_LIMIT_MS&&after>=before+amount&&after<=before+amount+(elapsed+359999)/360000;}
 static int afterBattle(int available,int cost){return cost>=20&&cost<=100&&cost%20==0&&available>=cost?available-cost:-1;}
 static int batchFor(String target){return "discs".equals(target)?60:"expert".equals(target)?40:100;}
 static int forecastBattles(int battery,int batch,boolean reserve,int reserves){if(battery<=0||batch<=0)return 0;return battery/batch+(reserve&&reserveTopOff(battery%batch,batch,reserves)>0?1:0);}
 static long estimateMs(int errands,int battles,long battleMs){return errands*45000L+battles*(Math.max(30000,Math.min(300000,battleMs))+45000L)+120000L;}
 static int[] batteryPair(String text){Matcher m=Pattern.compile("(?<![0-9])([0-9]{1,3})\\s*/\\s*(20|40|60|80|100)(?![0-9])").matcher(text.replace(",",""));if(!m.find())return null;return new int[]{Integer.parseInt(m.group(1)),Integer.parseInt(m.group(2))};}
 static int dennyReward(String text){String s=clean(text);if(!s.contains("denny")||s.contains("investigator log")||s.contains("plating agent")||s.contains("power supply"))return 0;Matcher m=Pattern.compile("(?<![0-9])(25000|50000|75000|100000|125000)(?![0-9])").matcher(s.replace(" ",""));return m.find()?Integer.parseInt(m.group(1)):0;}
}

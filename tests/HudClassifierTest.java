package dev.gbppimentel.zzz;
import java.awt.image.BufferedImage;import javax.imageio.ImageIO;import java.io.*;import java.nio.file.*;import java.util.*;
public final class HudClassifierTest {
 public static void main(String[] args)throws Exception{String model=Files.readString(Path.of("assets/hud-templates.json"));int checks=0;String[][] cases={{"results","results"},{"combat","combat"},{"menu","menu"},{"options","menu"},{"city","shortcuts"}};
  int[] blank=new int[480*270];Arrays.fill(blank,0xff000000);if(!new HudClassifier(model).classify(blank).phase.equals("transition"))throw new AssertionError("Blank transition");checks++;
  Arrays.fill(blank,0xffeeeeee);if(!new HudClassifier(model).classify(blank).phase.equals("unknown"))throw new AssertionError("Unrecognized screen cannot become combat");checks++;
  HudClassifier.Stable stable=new HudClassifier.Stable();stable.update("combat",.95);if(stable.phase.equals("combat"))throw new AssertionError("One frame must not switch state");checks++;stable.update("combat",.95);if(!stable.phase.equals("combat"))throw new AssertionError("Stable combat");checks++;for(int i=0;i<5;i++)stable.update("unknown",0);if(!stable.phase.equals("combat"))throw new AssertionError("Short occlusion must retain state");checks++;stable.update("unknown",0);if(!stable.phase.equals("unknown"))throw new AssertionError("Long unknown must not claim combat");checks++;System.out.println(checks+" HUD checks passed");
 }
}

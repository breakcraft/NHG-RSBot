package org.rsbot.script.randoms;

import org.rsbot.script.Random;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSComponent;
import org.rsbot.script.wrappers.RSObject;

/*
* Updated by Iscream (Feb 3, 10)
* Updated by Twistedmind (Feb 4, 10) Small camera turning issue...
* Updated by Iscream (Feb 5, 10)
* Updated by TwistedMind (Feb 7, 10) "What have you guys been smoking??? I cleaned the code and it worked again... Why atTile if there's atObject?"
* Updated by Arbiter (Sep 21, 10) Switched back to atTile. Obvious spread out model is obvious. Also fixed the fail returning. >.>
* Updated by Jacmob (Oct 10, 10) Cleaned up disgraceful activateCondition()
*/
@ScriptManifest(authors = {"Aelin", "LM3", "IceCandle", "Taha"}, name = "Pinball", version = 2.7)
public class Pinball extends Random {

    private static final int[] OBJ_PILLARS = {15000, 15002, 15004, 15006, 15008};

	private static final int[] OBJ_ACTIVATE = {15000, 15002, 15004, 15006, 15007, 15008};

	private int continueCounter = 0;

    public boolean activateCondition() {
        return game.isLoggedIn() && objects.getNearest(OBJ_ACTIVATE) != null;
    }

    private int getScore() {
        int IFACE_PINBALL = 263;
        RSComponent score = interfaces.get(IFACE_PINBALL).getComponent(1);
        try {
            return Integer.parseInt(score.getText().split(" ")[1]);
        } catch (java.lang.ArrayIndexOutOfBoundsException t) {
            return 10;
        }
    }

    public int loop() {
        if (!activateCondition()) {
            return -1;
        }
        if (interfaces.canContinue() && continueCounter < 10) {
            interfaces.clickContinue();
			continueCounter++;
            return random(1000, 1200);
        }
		continueCounter = 0;
        if (getMyPlayer().isMoving() || (getMyPlayer().getAnimation() != -1)) {
            return random(1000, 1600);
        }
        if (getScore() >= 10) {
            int OBJ_EXIT=15010;
            RSObject exit = objects.getNearest(OBJ_EXIT);
            if (exit != null) {
                if (calc.tileOnScreen(exit.getLocation()) && tiles.doAction(exit.getLocation(), "Exit")) {
                    sleep(random(2000, 2200));
                    exit.doAction("Exit");
                    return random(1000, 1200);
                } else {
                    camera.setCompass('s');
                    walking.walkTileOnScreen(exit.getLocation());
                    return random(1400, 1500);
                }
            }
        }
        if (objects.getNearest(OBJ_PILLARS) != null) {
            if (!calc.tileOnScreen(objects.getNearest(OBJ_PILLARS).getLocation())) {
                walking.walkTileOnScreen(objects.getNearest(OBJ_PILLARS).getLocation());
                return random(500, 600);
            }
            sleep(random(400, 500));
            if (!tiles.doAction(objects.getNearest(OBJ_PILLARS).getLocation(), "Tag"))
            	return 1;
            else
            	sleep(500,1000);
            int before = getScore();
            for (int i = 0; i < 100; i++) {
                if (getScore() > before)
                	return random(50,100);
                sleep(25,75);
            }
            return random(1000, 1300);
        }
        return random(200, 400);
    }

}
package org.rsbot.script.randoms;

import org.rsbot.script.Detector;
import org.rsbot.script.methods.Calculations;
import org.rsbot.script.methods.Game;
import org.rsbot.script.methods.Objects;
import org.rsbot.script.methods.Players;
import org.rsbot.script.methods.ui.Camera;
import org.rsbot.script.methods.ui.Interfaces;
import org.rsbot.script.wrappers.GameObject;
import org.rsbot.script.wrappers.InterfaceComponent;

/**
 * Solves the pinball random by tagging pillars.
 * <p/>
 * The object of the game is to tag each pillar that has glowing rings around it.
 * This scores the player one point.
 * After he or she scores ten points he or she is free to leave through the exit.
 * If a pillar that doesn't have the glowing rings around it is tagged,
 * the score is reset to zero and the player must try again.
 *
 * @author Timer
 */
public class Pinball extends Detector {
	private static final int[] INACTIVE_PILLARS = {15001, 15003, 15005, 15007, 15009};
	private static final int[] ACTIVE_PILLARS = {15000, 15002, 15004, 15006, 15008};
	private static final int INTERFACE_PINBALL_SCORE = 263;

	@Override
	public boolean isDetected() {
		return Game.isLoggedIn() && Objects.getNearest(INACTIVE_PILLARS) != null;
	}

	@Override
	protected int loop() {
		if (Players.getLocal().isMoving() || Players.getLocal().getAnimation() != -1) {
			return random(300, 500);
		}

		if (getScore() >= 10) {
			final GameObject exit = Objects.getNearest(15010);
			if (exit != null) {
				if (exit.getLocation().isOnScreen()) {
					return exit.interact("Exit") ? random(4000, 4200) : 0;
				} else {
					Camera.setCompass('s');

					exit.getLocation().walkOnScreen();
					return random(1400, 1500);
				}
			}
		}

		final GameObject pillar = Objects.getNearest(ACTIVE_PILLARS);
		if (pillar != null) {
			if (Calculations.distanceTo(pillar) > 2 && !pillar.isOnScreen()) {
				pillar.getLocation().walkOnScreen();
				return random(500, 600);
			}

			if (pillar.interact("Tag")) {
				final int before = getScore();
				for (int i = 0; i < 50; i++) {
					if (getScore() > before) {
						return random(50, 100);
					}
					sleep(random(70, 100));
				}
			}
		}
		return isDetected() ? random(50, 100) : -1;
	}

	private int getScore() {
		final InterfaceComponent score = Interfaces.get(INTERFACE_PINBALL_SCORE).getComponent(1);
		try {
			return Integer.parseInt(score.getText().split(" ")[1]);
		} catch (final ArrayIndexOutOfBoundsException t) {
			return -1;
		}
	}
}

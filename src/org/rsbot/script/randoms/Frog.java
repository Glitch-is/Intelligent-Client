package org.rsbot.script.randoms;

import org.rsbot.script.Detector;
import org.rsbot.script.methods.Calculations;
import org.rsbot.script.methods.NPCs;
import org.rsbot.script.methods.Objects;
import org.rsbot.script.methods.ui.Interfaces;
import org.rsbot.script.util.Filter;
import org.rsbot.script.wrappers.InterfaceComponent;
import org.rsbot.script.wrappers.NPC;

/**
 * Solves the frog event, mainly clicks continue.
 * <p/>
 * The random event begins with the player being teleported to the
 * Land of the Frogs, where they have to speak to Frog Herald,
 * who tells them how to solve the event.
 * The player then has to speak to the frog with the crown,
 * which is the Frog prince/princess (depending on their gender)
 * whom they must kiss, in order to break a spell.
 * Doing so will reward the player with a random event gift,
 * which contains the option of the prince /princess outfit.
 * If they talk to any other frog in that area,
 * the king/queen will be offended. If the player does it several times,
 * the king/queen will get angry and teleport the player to the
 * land of frogs, where the player is also transformed into a frog.
 * Speaking to the frog princess/prince will teleport the player
 * back to a random place in Gielinor, without a reward.
 *
 * @author Timer
 */
public class Frog extends Detector {
	private boolean spokeToHerald = false;

	@Override
	public boolean isDetected() {
		final NPC npc = getFrogHerald();
		return npc != null && Objects.getNearest(5917) != null && Calculations.canReach(npc.getLocation(), false);
	}

	@Override
	protected boolean onRun() {
		spokeToHerald = false;
		return true;
	}

	@Override
	protected int loop() {
		//TODO add support for leaving the frog land if random failed.
		if (Interfaces.canContinue()) {
			final InterfaceComponent heraldTalkComp = Interfaces.getComponent(242, 4);
			spokeToHerald = spokeToHerald || (heraldTalkComp.isValid() && (heraldTalkComp.containsText("crown") || heraldTalkComp.containsText("is still waiting")));

			Interfaces.clickContinue();
			return random(800, 1500);
		}

		if (!spokeToHerald) {
			final NPC npc = getFrogHerald();

			if (npc != null) {
				if (npc.isOnScreen()) {
					npc.interact("Talk-to", "Frog");
					return random(1500, 2000);
				}

				npc.getLocation().walkOnMap();
				return random(2000, 4000);
			}
			return 0;
		}

		final NPC npc = getSpecialFrog();
		if (npc != null) {
			if (npc.isOnScreen()) {
				npc.interact("Talk-to", "Frog");
				return random(1500, 2000);
			}

			npc.getLocation().walkOnMap();
			return random(2000, 4000);
		}
		return isDetected() ? 0 : -1;
	}

	private NPC getSpecialFrog() {
		return NPCs.getNearest(new Filter<NPC>() {
			public boolean accept(final NPC npc) {
				return !npc.isMoving() && npc.getHeight() == -278;
			}
		});
	}

	private NPC getFrogHerald() {
		return NPCs.getNearest("Frog Herald");
	}
}

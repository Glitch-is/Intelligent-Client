package org.rsbot.script.randoms;

import org.rsbot.script.Detector;
import org.rsbot.script.methods.NPCs;
import org.rsbot.script.methods.Players;
import org.rsbot.script.methods.ui.Interfaces;
import org.rsbot.script.wrappers.Interface;
import org.rsbot.script.wrappers.InterfaceComponent;
import org.rsbot.script.wrappers.NPC;
import org.rsbot.script.wrappers.Tile;

import java.util.HashMap;
import java.util.Map;

/**
 * Solves the Mime random by mimicking the mime.
 * <p/>
 * The player must copy the mime's actions to entertain the three Strange Watchers.
 *
 * @author Timer
 */
public class Mime extends Detector {
	private static final Tile performanceTile = new Tile(2008, 4762);
	private int animation = -1;
	private static final int INTERFACE = 188;
	private static final Map<Integer, String> emotes = new HashMap<Integer, String>();

	static {
		emotes.put(857, "Think");
		emotes.put(860, "Cry");
		emotes.put(861, "Laugh");
		emotes.put(866, "Dance");
		emotes.put(1128, "Glass Wall");
		emotes.put(1129, "Lean on air");
		emotes.put(1130, "Climb Rope");
		emotes.put(1131, "Glass Box");
	}

	@Override
	public boolean isDetected() {
		return NPCs.getNearest(1056) != null;
	}

	@Override
	protected int loop() {
		if (Interfaces.canContinue()) {
			Interfaces.clickContinue();
		}

		if (Players.getLocal().getLocation().equals(performanceTile)) {
			final NPC mime = NPCs.getNearest(1056);
			final int animation = mime.getAnimation();

			if (animation != -1 && animation != 858) {
				this.animation = animation;
			}

			Interface mimeInterface = Interfaces.get(INTERFACE);
			if (mimeInterface.isValid()) {
				final String find = emotes.get(this.animation);
				if (find != null) {
					for (final InterfaceComponent component : mimeInterface.getComponents()) {
						final String componentText = component.getText();

						if (componentText != null && componentText.equalsIgnoreCase(find)) {
							component.interact(find);
						}
					}
				}
			}
		}

		return isDetected() ? 0 : -1;
	}
}

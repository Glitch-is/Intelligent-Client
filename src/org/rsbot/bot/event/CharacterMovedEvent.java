package org.rsbot.bot.event;

import org.rsbot.bot.event.handler.EventMulticaster;
import org.rsbot.bot.event.listener.CharacterMovedListener;
import org.rsbot.script.wrappers.Character;
import org.rsbot.script.wrappers.NPC;
import org.rsbot.script.wrappers.Player;

import java.util.EventListener;

/**
 * A character moved event.
 */
public class CharacterMovedEvent extends RSEvent {
	private static final long serialVersionUID = 8883312847545757405L;

	private final org.rsbot.bot.accessors.RSCharacter character;
	private final int direction;
	private Character wrapped;

	public CharacterMovedEvent(final org.rsbot.bot.accessors.RSCharacter character, final int direction) {
		this.character = character;
		this.direction = direction;
	}

	@Override
	public void dispatch(final EventListener el) {
		((CharacterMovedListener) el).characterMoved(this);
	}

	public org.rsbot.script.wrappers.Character getCharacter() {
		if (wrapped == null) {
			if (character instanceof org.rsbot.bot.accessors.RSNPC) {
				final org.rsbot.bot.accessors.RSNPC npc = (org.rsbot.bot.accessors.RSNPC) character;
				wrapped = new NPC(npc);
			} else if (character instanceof org.rsbot.bot.accessors.RSPlayer) {
				final org.rsbot.bot.accessors.RSPlayer player = (org.rsbot.bot.accessors.RSPlayer) character;
				wrapped = new Player(player);
			}
		}
		return wrapped;
	}

	/**
	 * 0 = NW
	 * 1 = N
	 * 2 = NE
	 * 3 = W
	 * 4 = E
	 * 5 = SW
	 * 6 = S
	 * 7 = SE
	 */
	public int getDirection() {
		return direction;
	}

	@Override
	public long getMask() {
		return EventMulticaster.CHARACTER_MOVED_EVENT;
	}
}

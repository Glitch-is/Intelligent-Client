package org.rsbot.script.wrappers;

import org.rsbot.bot.accessors.RSNPCDef;
import org.rsbot.script.methods.Players;

import java.lang.ref.SoftReference;

/**
 * Represents a non-player character.
 */
public class NPC extends Character {
	private final SoftReference<org.rsbot.bot.accessors.RSNPC> npc;

	public NPC(final org.rsbot.bot.accessors.RSNPC npc) {
		this.npc = new SoftReference<org.rsbot.bot.accessors.RSNPC>(npc);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.rsbot.bot.accessors.RSCharacter getAccessor() {
		return npc.get();
	}

	/**
	 * Gets the actions that this NPC contains.
	 *
	 * @return The actions array.
	 */
	public String[] getActions() {
		final RSNPCDef def = getDefInternal();
		if (def != null) {
			return def.getActions();
		}
		return new String[0];
	}

	/**
	 * Gets the ID of this NPC.
	 *
	 * @return The id.
	 */
	public int getID() {
		final RSNPCDef def = getDefInternal();
		if (def != null) {
			return def.getType();
		}
		return -1;
	}

	/**
	 * Gets the prayer icon.
	 *
	 * @return The prayer icon.
	 */
	public int getPrayerIconIndex() {
		final RSNPCDef def = getDefInternal();
		return def == null ? -1 : def.getPrayerIcon();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		final RSNPCDef def = getDefInternal();
		if (def != null) {
			return def.getName();
		}
		return "";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getLevel() {
		final org.rsbot.bot.accessors.RSNPC c = npc.get();
		if (c == null) {
			return -1;
		} else {
			return c.getLevel();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isInteractingWithLocalPlayer() {
		return getInteracting() != null && getInteracting().equals(Players.getLocal());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		for (final String act : getActions()) {
			sb.append(act);
			sb.append(",");
		}
		if (sb.length() > 0) {
			sb.setLength(sb.length() - 1);
		}
		return "NPC[" + getName() + "],actions=[" + sb.toString() + "]"
				+ super.toString();
	}

	/**
	 * Gets the definition of this npc.
	 *
	 * @return The definition.
	 */
	RSNPCDef getDefInternal() {
		final org.rsbot.bot.accessors.RSNPC c = npc.get();
		if (c == null) {
			return null;
		} else {
			return c.getRSNPCDef();
		}
	}
}

package org.rsbot.script.wrappers;

import org.rsbot.bot.accessors.RSPlayerComposite;

import java.lang.ref.SoftReference;

/**
 * Represents a player.
 */
public class Player extends Character {
	private final SoftReference<org.rsbot.bot.accessors.RSPlayer> p;
	private static final int EQUIPMENT_CONSTANT = 1073741824;

	public Player(final org.rsbot.bot.accessors.RSPlayer p) {
		this.p = new SoftReference<org.rsbot.bot.accessors.RSPlayer>(p);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.rsbot.bot.accessors.RSCharacter getAccessor() {
		return p.get();
	}

	/**
	 * Gets the appearance of the character.
	 *
	 * @return The array of item ids.
	 */
	public int[] getAppearance() {
		final RSPlayerComposite comp = p.get().getComposite();
		if (comp != null) {
			final int[] equip = comp.getEquipment();
			for (int i = 0; i < equip.length; i++) {
				equip[i] -= EQUIPMENT_CONSTANT;
				if (equip[i] < 0 || equip[i] > 1000000000) {
					equip[i] = -1;
				}
			}
			return equip;
		}
		return null;
	}

	/**
	 * Gets the team that the player is on.
	 *
	 * @return The team.
	 */
	public int getTeam() {
		return p.get().getTeam();
	}

	/**
	 * Gets the prayer icon.
	 *
	 * @return The prayer icon.
	 */
	public int getPrayerIconIndex() {
		return p.get().getPrayerIcon();
	}

	/**
	 * Gets the skull icon.
	 *
	 * @return The skull icon.
	 */
	public int getSkullIconIndex() {
		return p.get().getSkullIcon();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return verify() ? p.get().getName() : null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getLevel() {
		return verify() ? p.get().getLevel() : -1;
	}

	/**
	 * The NPC id associated with this Player.
	 *
	 * @return The NPC id.
	 */
	public int getNPCID() {
		final RSPlayerComposite comp = p.get().getComposite();
		if (comp != null) {
			return comp.getNPCID();
		}
		return -1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "Player[" + getName() + "]" + super.toString();
	}
}
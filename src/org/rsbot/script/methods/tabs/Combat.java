package org.rsbot.script.methods.tabs;

import org.rsbot.bot.concurrent.Task;
import org.rsbot.script.methods.Game;
import org.rsbot.script.methods.Settings;
import org.rsbot.script.methods.ui.Interfaces;
import org.rsbot.script.util.Timer;
import org.rsbot.script.wrappers.InterfaceComponent;

/**
 * An utility class that handles the Combat tab.
 *
 * @author Timer
 */
public class Combat {
	public static final int INTERFACE = 884;
	public static final int INTERFACE_WEAPON = 0;
	public static final int INTERFACE_SPECIAL_ATTACK_BAR = 4;
	public static final int INTERFACE_AUTO_RETALIATE = 16;

	/**
	 * Opens the game tab relating to this utility.
	 *
	 * @return <tt>true</tt> if the tab was opened, otherwise <tt>false</tt>.
	 */
	public static boolean openTab() {
		final Game.Tabs tab = Game.Tabs.ATTACK;
		return Game.getCurrentTab() == tab || Game.openTab(tab, true);
	}

	/**
	 * Retrieves the current weapon's name.
	 *
	 * @return The <code>String</code> value of the weapon name, an empty string if one does not exist.
	 */
	public static String getWeaponName() {
		final InterfaceComponent weaponName = Interfaces.getComponent(INTERFACE, INTERFACE_WEAPON);
		return weaponName != null ? weaponName.getText() : "";
	}

	/**
	 * Index setup:
	 * |  0  | |  1  |
	 * |  2  | |  3  |
	 *
	 * @return The index related to the currently selected combat style.
	 */
	public static int getCombatStyle() {
		return Settings.get(Settings.COMBAT_STYLE);
	}

	/**
	 * Attempts to set the combat style to the desired index.
	 *
	 * @param styleIndex The style to attempt to set.
	 * @return <tt>true</tt> if it was set; otherwise <tt>false</tt>.
	 */
	public static boolean setCombatStyle(final int styleIndex) {
		if (styleIndex == getCombatStyle()) {
			return true;
		}
		if (openTab()) {
			final InterfaceComponent button = Interfaces.getComponent(INTERFACE, styleIndex + 11);
			if (button != null && button.click(true)) {
				final Timer timer = new Timer(800);
				while (timer.isRunning() && getCombatStyle() != styleIndex) {
					Task.sleep(50);
				}
				return getCombatStyle() == styleIndex;
			}
		}
		return false;
	}

	/**
	 * Determines if auto retaliation is active.
	 *
	 * @return <tt>true</tt> if it is active; otherwise <tt>false</tt>.
	 */
	public static boolean isAutoRetaliating() {
		return Settings.get(Settings.AUTO_RETALIATE) == 0;
	}

	/**
	 * Attempts to enable or disable automatic retaliation.
	 *
	 * @param enabled Enable auto retaliation.
	 * @return <tt>true</tt> if the provided param was successfully set; otherwise <tt>false</tt>.
	 */
	public static boolean setAutoRetaliation(final boolean enabled) {
		if (isAutoRetaliating() == enabled) {
			return true;
		}
		if (openTab()) {
			final InterfaceComponent button = Interfaces.getComponent(INTERFACE, INTERFACE_AUTO_RETALIATE);
			return button != null && button.interact("Auto Retaliate");
		}
		return false;
	}

	/**
	 * Determines the current amount of special energy we have.
	 *
	 * @return The percentage of special energy we currently have (100 max).
	 */
	public static int getSpecialEnergy() {
		final int val = Settings.get(Settings.SPECIAL_ATTACK);
		return val != -1 ? val / 10 : -1;
	}

	/**
	 * Evaluates if we have a special attack queued.
	 *
	 * @return <tt>true</tt> if the next attack in queue is a special attack; otherwise <tt>false</tt>/
	 */
	public static boolean isSpecialAttackQueued() {
		return Settings.get(Settings.SPECIAL_ATTACK_ENABLED) == 1;
	}

	/**
	 * Queues or removes the special attack from the attack queue.
	 *
	 * @param enabled Enable special attack.
	 * @return <tt>true</tt> if the provided param was successfully set.
	 */
	public static boolean setSpecialAttack(final boolean enabled) {
		if (isSpecialAttackQueued() == enabled) {
			return true;
		}
		final InterfaceComponent bar = Interfaces.getComponent(INTERFACE, INTERFACE_SPECIAL_ATTACK_BAR);
		return bar != null && bar.interact("Use", "Special Attack");
	}
}

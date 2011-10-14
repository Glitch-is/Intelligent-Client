package org.rsbot.script.methods;

import org.rsbot.bot.Context;
import org.rsbot.ui.AccountManager;

/**
 * Selected account information.
 *
 * @author Timer
 */
public class Account {
	/**
	 * Sets the current bot's account name.
	 *
	 * @param name The account name.
	 * @return <tt>true</tt> if the account existed.
	 */
	public static boolean set(final String name) {
		return Context.get().bot.setAccount(name);
	}

	/**
	 * The account's name.
	 *
	 * @return The currently selected account's name.
	 */
	public static String getName() {
		return Context.get().composite.account;
	}

	/**
	 * The account's password.
	 *
	 * @return The currently selected account's password.
	 */
	public static String getPassword() {
		return AccountManager.getPassword(getName());
	}

	/**
	 * The account's pin.
	 *
	 * @return The currently selected account's pin.
	 */
	public static String getPin() {
		return AccountManager.getPin(getName());
	}

	/**
	 * The account's selected reward.
	 *
	 * @return The currently selected account's reward.
	 */
	public static String getReward() {
		return AccountManager.getReward(getName());
	}
}

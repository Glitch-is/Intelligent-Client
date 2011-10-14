package org.rsbot.script.methods;

import org.rsbot.bot.Context;
import org.rsbot.bot.accessors.Client;
import org.rsbot.script.util.Filter;
import org.rsbot.script.wrappers.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * Player related operations.
 */
public class Players {
	/**
	 * A filter that accepts all matches.
	 */
	public static final Filter<Player> ALL_FILTER = new Filter<Player>() {
		public boolean accept(final Player player) {
			return true;
		}
	};

	/**
	 * Returns an <tt>Player</tt> object representing the current player.
	 *
	 * @return An <tt>Player</tt> object representing the player.
	 */
	public static Player getLocal() {
		return new Player(Context.get().client.getMyRSPlayer());
	}

	/**
	 * Returns an array of all valid <tt>Player</tt>s.
	 *
	 * @return All valid RSPlayers.
	 */
	public static Player[] getLoaded() {
		return getLoaded(Players.ALL_FILTER);
	}

	/**
	 * Returns an array of all valid <tt>Player</tt>s.
	 *
	 * @param filter Filters out unwanted matches.
	 * @return All valid RSPlayers.
	 */
	public static Player[] getLoaded(final Filter<Player> filter) {
		final Client client = Context.get().client;
		final int[] indices = client.getRSPlayerIndexArray();
		final org.rsbot.bot.accessors.RSPlayer[] array = client.getRSPlayerArray();
		final Set<Player> players = new HashSet<Player>();
		for (final int index : indices) {
			if (index != 0 && array[index] != null) {
				final Player player = new Player(array[index]);
				if (player.verify() && filter.accept(player)) {
					players.add(player);
				}
			}
		}
		return players.toArray(new Player[players.size()]);
	}

	/**
	 * Returns the <tt>Player</tt> that is nearest, out of all of the Players
	 * accepted by the provided filter.
	 *
	 * @param filter Filters unwanted matches.
	 * @return An <tt>Player</tt> object representing the nearest player that
	 *         was accepted by the provided Filter; or null if there are no
	 *         matching players in the current region.
	 */
	public static Player getNearest(final Filter<Player> filter) {
		int min = 20;
		Player closest = null;
		final Client client = Context.get().client;
		final org.rsbot.bot.accessors.RSPlayer[] players = client.getRSPlayerArray();
		final int[] indices = client.getRSPlayerIndexArray();
		for (final int index : indices) {
			if (players[index] == null) {
				continue;
			}
			final Player player = new Player(players[index]);
			if (player != null && filter.accept(player)) {
				final int distance = Calculations.distanceTo(player);
				if (distance < min) {
					min = distance;
					closest = player;
				}
			}
		}
		return closest;
	}

	/**
	 * Returns the <tt>Player</tt> that is nearest, out of all of the Players
	 * with the provided name.
	 *
	 * @param name The name of the <tt>Player</tt> that you are searching for.
	 * @return An <tt>Player</tt> object representing the nearest player with
	 *         the provided name; or null if there are no matching players in
	 *         the current region.
	 */
	public static Player getNearest(final String name) {
		return getNearest(new Filter<Player>() {
			public boolean accept(final Player player) {
				return player != null && player.getName() != null && player.getName().equalsIgnoreCase(name);
			}
		});
	}

	/**
	 * Returns the <tt>Player</tt> that is nearest, out of all of the Players
	 * with the provided combat level.
	 *
	 * @param level The combat level of the <tt>Player</tt> that you are
	 *              searching for.
	 * @return An <tt>Player</tt> object representing the nearest player with
	 *         the provided combat level; or null if there are no matching
	 *         players in the current region.
	 */
	public static Player getNearest(final int level) {
		return getNearest(new Filter<Player>() {
			public boolean accept(final Player player) {
				return player != null && player.getLevel() == level;
			}
		});
	}
}

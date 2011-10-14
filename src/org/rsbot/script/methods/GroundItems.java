package org.rsbot.script.methods;

import org.rsbot.bot.Context;
import org.rsbot.bot.accessors.Client;
import org.rsbot.bot.accessors.HashTable;
import org.rsbot.bot.accessors.RSItem;
import org.rsbot.script.util.Filter;
import org.rsbot.script.wrappers.GroundItem;
import org.rsbot.script.wrappers.Item;
import org.rsbot.script.wrappers.Tile;
import org.rsbot.script.wrappers.internal.Deque;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides access to ground items.
 */
public class GroundItems {
	public static final Filter<GroundItem> ALL_FILTER = new Filter<GroundItem>() {
		public boolean accept(final GroundItem item) {
			return true;
		}
	};

	/**
	 * Returns all ground items in the loaded area.
	 *
	 * @return All ground items in the loaded area.
	 */
	public static GroundItem[] getLoaded() {
		return getLoaded(52, ALL_FILTER);
	}

	/**
	 * Returns all matching ground items in the loaded area.
	 *
	 * @param filter Filters out unwanted matches.
	 * @return All ground items
	 */
	public static GroundItem[] getLoaded(final Filter<GroundItem> filter) {
		return getLoaded(52, filter);
	}

	/**
	 * Returns all ground items within the provided range.
	 *
	 * @param range The range (max distance in all directions) in which to check
	 *              items for.
	 * @return <tt>GroundItem</tt> array containing all of the items in range.
	 */
	public static GroundItem[] getLoaded(final int range) {
		return getLoaded(range, ALL_FILTER);
	}

	/**
	 * Returns all matching ground items within the provided range.
	 *
	 * @param range  The range (max distance in all directions) in which to check items for.
	 * @param filter Filters out unwanted matches.
	 * @return <tt>GroundItem</tt> array containing all of the items in range.
	 */
	public static GroundItem[] getLoaded(final int range, final Filter<GroundItem> filter) {
		final ArrayList<GroundItem> temp = new ArrayList<GroundItem>();
		final int pX = Players.getLocal().getLocation().getX();
		final int pY = Players.getLocal().getLocation().getY();
		final int minX = pX - range, minY = pY - range;
		final int maxX = pX + range, maxY = pY + range;
		for (int x = minX; x < maxX; x++) {
			for (int y = minY; y < maxY; y++) {
				final GroundItem[] items = getLoadedAt(x, y);
				for (final GroundItem item : items) {
					if (item != null && filter.accept(item)) {
						temp.add(item);
					}
				}
			}
		}
		return temp.toArray(new GroundItem[temp.size()]);
	}

	/**
	 * Returns the nearest ground item that is accepted by the provided Filter.
	 *
	 * @param filter Filters out unwanted matches.
	 * @return The nearest item that is accepted by the provided Filter; or null.
	 */
	public static GroundItem getNearest(final Filter<GroundItem> filter) {
		int dist = Integer.MAX_VALUE;
		final int pX = Players.getLocal().getLocation().getX();
		final int pY = Players.getLocal().getLocation().getY();
		final int minX = pX - 52, minY = pY - 52;
		final int maxX = pX + 52, maxY = pY + 52;
		GroundItem itm = null;
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				final GroundItem[] items = getLoadedAt(x, y);
				for (final GroundItem item : items) {
					if (item != null && filter.accept(item) && Calculations.distanceTo(item.getLocation()) < dist) {
						dist = Calculations.distanceTo(item.getLocation());
						itm = item;
					}
				}
			}
		}
		return itm;
	}

	/**
	 * Returns the nearest item on the ground with an ID that matches any of the
	 * IDs provided.
	 *
	 * @param ids The IDs to look for.
	 * @return The nearest GroundItem with an ID that matches any in the array of
	 *         IDs provided; or null if no matching ground items were found.
	 */
	public static GroundItem getNearest(final int... ids) {
		return getNearest(new Filter<GroundItem>() {
			public boolean accept(final GroundItem item) {
				if (item != null) {
					final int iid = item.getItem().getID();
					for (final int id : ids) {
						if (id == iid) {
							return true;
						}
					}
				}
				return false;
			}
		});
	}

	/**
	 * Returns the nearest item on the ground with one of the provided names.
	 *
	 * @param names The names to look for.
	 * @return The nearest GroundItem with a name that matches any in the array provided; or null if
	 *         no matching ground items were found.
	 */
	public static GroundItem getNearest(final String... names) {
		return getNearest(new Filter<GroundItem>() {
			public boolean accept(final GroundItem item) {
				final String name = item != null ? item.getItem().getName() : null;
				if (name != null) {
					for (final String n : names) {
						if (n.equalsIgnoreCase(name)) {
							return true;
						}
					}
				}
				return false;
			}
		});
	}

	/**
	 * Returns all the ground items at a tile on the current plane.
	 *
	 * @param x The x position of the tile in the world.
	 * @param y The y position of the tile in the world.
	 * @return An array of the ground items on the specified tile.
	 */
	public static GroundItem[] getLoadedAt(final int x, final int y) {
		if (!Game.isLoggedIn()) {
			return new GroundItem[0];
		}
		final List<GroundItem> list = new ArrayList<GroundItem>();

		final Client client = Context.get().client;
		final HashTable itemNC = client.getRSItemHashTable();
		final int id = x | y << 14 | client.getPlane() << 28;

		final org.rsbot.bot.accessors.NodeListCache itemNLC = (org.rsbot.bot.accessors.NodeListCache) Nodes.lookup(itemNC, id);

		if (itemNLC == null) {
			return new GroundItem[0];
		}

		final Deque<org.rsbot.bot.accessors.RSItem> itemNL = new Deque<RSItem>(itemNLC.getNodeList());
		for (org.rsbot.bot.accessors.RSItem item = itemNL.getHead(); item != null; item = itemNL.getNext()) {
			list.add(new GroundItem(new Tile(x, y), new Item(item)));
		}

		return list.toArray(new GroundItem[list.size()]);
	}

	/**
	 * Returns all the ground items at a tile on the current plane.
	 *
	 * @param t The tile.
	 * @return An array of the ground items on the specified tile.
	 */
	public static GroundItem[] getLoadedAt(final Tile t) {
		return getLoadedAt(t.getX(), t.getY());
	}
}

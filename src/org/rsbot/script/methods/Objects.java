package org.rsbot.script.methods;

import org.rsbot.bot.Context;
import org.rsbot.bot.accessors.Client;
import org.rsbot.bot.accessors.RSAnimableNode;
import org.rsbot.script.util.Filter;
import org.rsbot.script.wrappers.GameObject;
import org.rsbot.script.wrappers.Tile;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Provides access to in-game physical objects.
 */
public class Objects {
	public static final int TYPE_INTERACTABLE = 1;
	public static final int TYPE_FLOOR_DECORATION = 2;
	public static final int TYPE_BOUNDARY = 4;
	public static final int TYPE_WALL_DECORATION = 8;

	/**
	 * A filter that accepts all matches.
	 */
	public static final Filter<GameObject> ALL_FILTER = new Filter<GameObject>() {
		public boolean accept(final GameObject obj) {
			return true;
		}
	};

	/**
	 * Returns all the <tt>GameObject</tt>s in the local region.
	 *
	 * @return An <tt>GameObject[]</tt> of all objects in the loaded region.
	 */
	public static GameObject[] getLoaded() {
		return getLoaded(Objects.ALL_FILTER);
	}

	/**
	 * Returns all the <tt>GameObject</tt>s in the local region within specified distance.
	 *
	 * @param distance The range to search (box-like).
	 * @return An <tt>GameObject[]</tt> of all objects in the loaded region within specified range.
	 */
	public static GameObject[] getLoaded(final int distance) {
		final Set<GameObject> objects = new LinkedHashSet<GameObject>();
		final Tile currTile = Players.getLocal().getLocation(), baseTile = Game.getMapBase();
		final int sX = Math.max(0, currTile.getX() - baseTile.getX() - distance);
		final int sY = Math.max(0, currTile.getY() - baseTile.getY() - distance);
		final int eX = Math.min(104, currTile.getX() - baseTile.getX() + distance);
		final int eY = Math.min(104, currTile.getY() - baseTile.getY() + distance);
		for (int x = sX; x < eX; x++) {
			for (int y = sY; y < eY; y++) {
				for (final GameObject o : getAtLocal(x, y, -1)) {
					if (o != null) {
						objects.add(o);
					}
				}
			}
		}
		return objects.toArray(new GameObject[objects.size()]);
	}

	/**
	 * Returns all the <tt>GameObject</tt>s in the local region within specified distance.
	 *
	 * @param distance The range to search (box-like).
	 * @param filter   Filters out unwanted objects.
	 * @return An <tt>GameObject[]</tt> of all objects in the loaded region within specified range.
	 */
	public static GameObject[] getLoaded(final int distance, final Filter<GameObject> filter) {
		final Set<GameObject> objects = new LinkedHashSet<GameObject>();
		final Tile currTile = Players.getLocal().getLocation(), baseTile = Game.getMapBase();
		final int sX = Math.max(0, currTile.getX() - baseTile.getX() - distance);
		final int sY = Math.max(0, currTile.getY() - baseTile.getY() - distance);
		final int eX = Math.min(104, currTile.getX() - baseTile.getX() + distance);
		final int eY = Math.min(104, currTile.getY() - baseTile.getY() + distance);
		for (int x = sX; x < eX; x++) {
			for (int y = sY; y < eY; y++) {
				for (final GameObject o : getAtLocal(x, y, -1)) {
					if (o != null && filter.accept(o)) {
						objects.add(o);
					}
				}
			}
		}
		return objects.toArray(new GameObject[objects.size()]);
	}

	/**
	 * Returns all the <tt>GameObject</tt>s in the local region accepted by the
	 * provided Filter.
	 *
	 * @param filter Filters out unwanted objects.
	 * @return An <tt>GameObject[]</tt> of all the accepted objects in the loaded
	 *         region.
	 */
	public static GameObject[] getLoaded(final Filter<GameObject> filter) {
		final Set<GameObject> objects = new LinkedHashSet<GameObject>();
		for (int x = 0; x < 104; x++) {
			for (int y = 0; y < 104; y++) {
				for (final GameObject o : getAtLocal(x, y, -1)) {
					if (o != null && filter.accept(o)) {
						objects.add(o);
					}
				}
			}
		}
		return objects.toArray(new GameObject[objects.size()]);
	}

	/**
	 * Returns all the <tt>GameObject</tt>s in the local region with the provided
	 * ID(s).
	 *
	 * @param ids Allowed object IDs.
	 * @return An array of the region's RSObjects matching the provided ID(s).
	 */
	public static GameObject[] getLoaded(final int... ids) {
		return getLoaded(new Filter<GameObject>() {
			public boolean accept(final GameObject o) {
				for (final int id : ids) {
					if (o.getID() == id) {
						return true;
					}
				}
				return false;
			}
		});
	}

	/**
	 * Returns all the <tt>GameObject</tt>s in the local region with the provided
	 * name(s).
	 *
	 * @param names Allowed object names.
	 * @return An array of the region's RSObjects matching the provided name(s).
	 */
	public static GameObject[] getLoaded(final String... names) {
		return getLoaded(new Filter<GameObject>() {
			public boolean accept(final GameObject o) {
				final String name = o.getName();
				if (!name.isEmpty()) {
					for (final String n : names) {
						if (n != null && n.equalsIgnoreCase(name)) {
							return true;
						}
					}
				}
				return false;
			}
		});
	}

	/**
	 * Returns the <tt>GameObject</tt>s which are on the specified <tt>Tile</tt>.
	 *
	 * @param t The tile on which to search.
	 * @return An GameObject[] of the objects on the specified tile.
	 */
	public static GameObject[] getLoadedAt(final Tile t) {
		return getAt(t, -1);
	}

	/**
	 * Returns the <tt>GameObject</tt>s which are on the specified <tt>Tile</tt>
	 * matching types specified by the flags in the provided mask.
	 *
	 * @param t    The tile on which to search.
	 * @param mask The type flags.
	 * @return An GameObject[] of the objects on the specified tile.
	 */
	public static GameObject[] getAt(final Tile t, final int mask) {
		final Client client = Context.get().client;
		final Set<GameObject> objects = getAtLocal(t.getX() - client.getBaseX(), t.getY() - client.getBaseY(), mask);
		return objects.toArray(new GameObject[objects.size()]);
	}

	private static Set<GameObject> getAtLocal(int x, int y, final int mask) {
		final org.rsbot.bot.accessors.Client client = Context.get().client;
		final Set<GameObject> objects = new LinkedHashSet<GameObject>();
		if (client.getRSGroundArray() == null) {
			return objects;
		}

		try {
			final int plane = client.getPlane();
			final org.rsbot.bot.accessors.RSGround rsGround = client.getRSGroundArray()[plane][x][y];

			if (rsGround != null) {
				org.rsbot.bot.accessors.RSObject rsObj;
				org.rsbot.bot.accessors.RSInteractable obj;

				// Interactive (e.g. Trees)
				if ((mask & TYPE_INTERACTABLE) != 0) {
					for (RSAnimableNode node = rsGround.getRSAnimableList(); node != null; node = node.getNext()) {
						obj = node.getRSAnimable();
						if (obj != null
								&& obj instanceof org.rsbot.bot.accessors.RSObject) {
							rsObj = (org.rsbot.bot.accessors.RSObject) obj;
							if (rsObj.getID() != -1) {
								objects.add(new GameObject(rsObj, GameObject.Type.INTERACTABLE, plane));
							}
						}
					}
				}

				// Ground Decorations
				if ((mask & TYPE_FLOOR_DECORATION) != 0) {
					obj = rsGround.getFloorDecoration();
					if (obj != null) {
						rsObj = (org.rsbot.bot.accessors.RSObject) obj;
						if (rsObj.getID() != -1) {
							objects.add(new GameObject(rsObj, GameObject.Type.FLOOR_DECORATION, plane));
						}
					}
				}

				// Boundaries / Doors / Fences / Walls
				if ((mask & TYPE_BOUNDARY) != 0) {
					obj = rsGround.getBoundary1();
					if (obj != null) {
						rsObj = (org.rsbot.bot.accessors.RSObject) obj;
						if (rsObj.getID() != -1) {
							objects.add(new GameObject(rsObj, GameObject.Type.BOUNDARY, plane));
						}
					}

					obj = rsGround.getBoundary2();
					if (obj != null) {
						rsObj = (org.rsbot.bot.accessors.RSObject) obj;
						if (rsObj.getID() != -1) {
							objects.add(new GameObject(rsObj, GameObject.Type.BOUNDARY, plane));
						}
					}
				}

				// Wall Decorations
				if ((mask & TYPE_WALL_DECORATION) != 0) {
					obj = rsGround.getWallDecoration1();
					if (obj != null) {
						rsObj = (org.rsbot.bot.accessors.RSObject) obj;
						if (rsObj.getID() != -1) {
							objects.add(new GameObject(rsObj, GameObject.Type.WALL_DECORATION, plane));
						}
					}

					obj = rsGround.getWallDecoration2();
					if (obj != null) {
						rsObj = (org.rsbot.bot.accessors.RSObject) obj;
						if (rsObj.getID() != -1) {
							objects.add(new GameObject(rsObj, GameObject.Type.WALL_DECORATION, plane));
						}
					}
				}
			}
		} catch (final Exception ignored) {
		}
		return objects;
	}

	/**
	 * Returns the <tt>GameObject</tt> that is nearest out of all objects that are
	 * accepted by the provided Filter.
	 *
	 * @param filter Filters out unwanted objects.
	 * @return An <tt>GameObject</tt> representing the nearest object that was
	 *         accepted by the filter; or null if there are no matching objects
	 *         in the current region.
	 */
	public static GameObject getNearest(final Filter<GameObject> filter) {
		GameObject cur = null;
		double dist = -1;
		for (int x = 0; x < 104; x++) {
			for (int y = 0; y < 104; y++) {
				final Set<GameObject> objs = getAtLocal(x, y, -1);
				for (final GameObject o : objs) {
					try {
						if (o != null && filter.accept(o)) {
							final double distTmp = Calculations.distanceBetween(Players.getLocal().getLocation(), o.getLocation());
							if (cur == null) {
								dist = distTmp;
								cur = o;
							} else if (distTmp < dist) {
								cur = o;
								dist = distTmp;
							}
							break;
						}
					} catch (final NullPointerException ignored) {
					}
				}
			}
		}
		return cur;
	}

	/**
	 * Returns the <tt>GameObject</tt> that is nearest, out of all of the
	 * RSObjects with the provided ID(s).
	 *
	 * @param ids The ID(s) of the GameObject that you are searching.
	 * @return An <tt>GameObject</tt> representing the nearest object with one of
	 *         the provided IDs; or null if there are no matching objects in the
	 *         current region.
	 */
	public static GameObject getNearest(final int... ids) {
		return getNearest(new Filter<GameObject>() {
			public boolean accept(final GameObject o) {
				for (final int id : ids) {
					if (o.getID() == id) {
						return true;
					}
				}
				return false;
			}
		});
	}

	/**
	 * Returns the <tt>GameObject</tt> that is nearest, out of all of the
	 * RSObjects with the provided name(s).
	 *
	 * @param names The name(s) of the GameObject that you are searching.
	 * @return An <tt>GameObject</tt> representing the nearest object with one of
	 *         the provided names; or null if there are no matching objects in
	 *         the current region.
	 */
	public static GameObject getNearest(final String... names) {
		return getNearest(new Filter<GameObject>() {
			public boolean accept(final GameObject o) {
				final String name = o.getName();
				if (!name.isEmpty()) {
					for (final String n : names) {
						if (n != null && n.equalsIgnoreCase(name)) {
							return true;
						}
					}
				}
				return false;
			}
		});
	}

	/**
	 * Returns the top <tt>GameObject</tt> on the specified tile.
	 *
	 * @param t The tile on which to search.
	 * @return The top GameObject on the provided tile; or null if none found.
	 */
	public static GameObject getTopAt(final Tile t) {
		return getTopAt(t, -1);
	}

	/**
	 * Returns the top <tt>GameObject</tt> on the specified tile matching types
	 * specified by the flags in the provided mask.
	 *
	 * @param t    The tile on which to search.
	 * @param mask The type flags.
	 * @return The top GameObject on the provided tile matching the specified
	 *         flags; or null if none found.
	 */
	public static GameObject getTopAt(final Tile t, final int mask) {
		final GameObject[] objects = getAt(t, mask);
		return objects.length > 0 ? objects[0] : null;
	}
}

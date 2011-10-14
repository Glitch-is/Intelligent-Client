package org.rsbot.script.methods;

import org.rsbot.bot.Context;
import org.rsbot.bot.accessors.Client;
import org.rsbot.bot.concurrent.Task;
import org.rsbot.script.methods.input.Mouse;
import org.rsbot.script.methods.ui.Interfaces;
import org.rsbot.script.wrappers.NodePath;
import org.rsbot.script.wrappers.Path;
import org.rsbot.script.wrappers.Tile;
import org.rsbot.script.wrappers.TilePath;

import java.awt.*;

/**
 * Walking related operations.
 */
public class Walking {
	public interface Interface {
		public static final int RUN_ORB = 750;
	}

	/**
	 * Creates a new path based on a provided array of tile waypoints.
	 *
	 * @param tiles The waypoint tiles.
	 * @return An TilePath.
	 */
	public static TilePath newTilePath(final Tile[] tiles) {
		if (tiles == null) {
			throw new IllegalArgumentException("null waypoint list");
		}
		return new TilePath(tiles);
	}

	/**
	 * Generates a path from the player's current location to a destination
	 * tile.
	 *
	 * @param destination The destination tile.
	 * @return The path as an Path.
	 */
	public static Path getPath(final Tile destination) {
		return new NodePath(destination);
	}

	/**
	 * Determines whether or not a given tile is in the loaded map area.
	 *
	 * @param tile The tile to check.
	 * @return <tt>true</tt> if local; otherwise <tt>false</tt>.
	 */
	public static boolean isLocal(final Tile tile) {
		final int[][] flags = getCollisionFlags(Game.getPlane());
		final int x = tile.getX() - Game.getBaseX();
		final int y = tile.getY() - Game.getBaseY();
		return flags != null && x >= 0 && y >= 0 && x < flags.length && y < flags.length;
	}

	/**
	 * Walks to the given tile using the minimap with 1 tile randomness.
	 *
	 * @param t The tile to walk to.
	 * @return <tt>true</tt> if the tile was clicked; otherwise <tt>false</tt>.
	 * @see #walkTileMM(org.rsbot.script.wrappers.Tile, int, int)
	 */
	public static boolean walkTileMM(final Tile t) {
		return walkTileMM(t, 0, 0);
	}

	/**
	 * Walks to the given tile using the minimap with given randomness.
	 *
	 * @param t The tile to walk to.
	 * @param r The maximum deviation from the tile to allow.
	 * @return <tt>true</tt> if the tile was clicked; otherwise <tt>false</tt>.
	 */
	public static boolean walkTileMM(final Tile t, final int r) {
		return walkTileMM(t, r, r, 0, 0);
	}

	/**
	 * Walks to the given tile using the minimap with given randomness.
	 *
	 * @param t The tile to walk to.
	 * @param x The x randomness (between 0 and x-1).
	 * @param y The y randomness (between 0 and y-1).
	 * @return <tt>true</tt> if the tile was clicked; otherwise <tt>false</tt>.
	 */
	public static boolean walkTileMM(final Tile t, final int x, final int y) {
		return walkTileMM(t, x, y, 0, 0);
	}

	/**
	 * Walks to the given tile using the minimap with given randomness.
	 *
	 * @param t  The tile to walk to.
	 * @param x  The x randomness (between 0 and x-1).
	 * @param y  The y randomness (between 0 and y-1).
	 * @param rx The mouse gaussian randomness (x).
	 * @param ry The mouse gaussian randomness (y).
	 * @return <tt>true</tt> if the tile was clicked; otherwise <tt>false</tt>.
	 */
	public static boolean walkTileMM(final Tile t, final int x, final int y, final int rx, final int ry) {
		int xx = t.getX(), yy = t.getY();
		if (x > 0) {
			if (Task.random(1, 3) == Task.random(1, 3)) {
				xx += Task.random(0, x);
			} else {
				xx -= Task.random(0, x);
			}
		}
		if (y > 0) {
			if (Task.random(1, 3) == Task.random(1, 3)) {
				yy += Task.random(0, y);
			} else {
				yy -= Task.random(0, y);
			}
		}
		Tile dest = new Tile(xx, yy);
		if (!dest.isOnMap()) {
			dest = getClosestTileOnMap(dest);
		}
		final Point p = dest.getPointOnMap();
		if (p.x != -1 && p.y != -1) {
			Mouse.move(p, rx, ry);
			final Point p2 = dest.getPointOnMap();
			if (p2.x != -1 && p2.y != -1) {
				if (!Mouse.getLocation().equals(p2)) {//Perfect alignment.
					Mouse.move(p2);
				}
				if (!Mouse.getLocation().equals(p2)) {//We must've moved while walking, move again!
					Mouse.move(p2);
				}
				if (!Mouse.getLocation().equals(p2)) {//Get exact since we're moving... should be removed?
					Mouse.hop(p2);
				}
				Mouse.click(true);
				return true;
			}
		}
		return false;
	}

	/**
	 * Walks to a tile using onScreen clicks and not the MiniMap. If the tile is
	 * not on the screen, it will find the closest tile that is on screen and it
	 * will walk there instead.
	 *
	 * @param tileToWalk Tile to walk.
	 * @return True if successful.
	 */
	public static boolean walkTileOnScreen(final Tile tileToWalk) {
		return Tiles.getTileOnScreen(tileToWalk).interact("Walk ");
	}

	/**
	 * Rests until 100% energy
	 *
	 * @return <tt>true</tt> if rest was enabled; otherwise false.
	 * @see #rest(int)
	 */
	public static boolean rest() {
		return rest(100);
	}

	/**
	 * Rests until a certain amount of energy is reached.
	 *
	 * @param stopEnergy Amount of energy at which it should stop resting.
	 * @return <tt>true</tt> if rest was enabled; otherwise false.
	 */
	public static boolean rest(final int stopEnergy) {
		int energy = getEnergy();
		for (int d = 0; d < 5; d++) {
			Interfaces.getComponent(Interface.RUN_ORB, 1).interact("Rest");
			Task.sleep(Task.random(400, 600));
			final int anim = Players.getLocal().getAnimation();
			if (anim == 12108 || anim == 2033 || anim == 2716 || anim == 11786 || anim == 5713) {
				break;
			}
			if (d == 4) {
				return false;
			}
		}
		while (energy < stopEnergy) {
			Task.sleep(Task.random(250, 500));
			energy = getEnergy();
		}
		return true;
	}

	/**
	 * Turns run on or off using the game GUI controls.
	 *
	 * @param enable <tt>true</tt> to enable run, <tt>false</tt> to disable it.
	 */
	public static void setRun(final boolean enable) {
		if (isRunEnabled() != enable) {
			Interfaces.getComponent(Interface.RUN_ORB, 0).click(true);
		}
	}

	/**
	 * Returns the closest tile on the minimap to a given tile.
	 *
	 * @param tile The destination tile.
	 * @return Returns the closest tile to the destination on the minimap.
	 */
	public static Tile getClosestTileOnMap(final Tile tile) {
		if (!tile.isOnMap() && Game.isLoggedIn()) {
			final Tile loc = Players.getLocal().getLocation();
			final Tile walk = new Tile((loc.getX() + tile.getX()) / 2, (loc.getY() + tile.getY()) / 2);
			return walk.isOnMap() ? walk : getClosestTileOnMap(walk);
		}
		return tile;
	}

	/**
	 * Returns whether or not run is enabled.
	 *
	 * @return <tt>true</tt> if run mode is enabled; otherwise <tt>false</tt>.
	 */
	public static boolean isRunEnabled() {
		return Settings.get(Settings.RUN) == 1;
	}

	/**
	 * Returns the player's current run energy.
	 *
	 * @return The player's current run energy.
	 */
	public static int getEnergy() {
		try {
			return Integer.parseInt(Interfaces.getComponent(750, 5).getText());
		} catch (final NumberFormatException e) {
			return 0;
		}
	}

	/**
	 * Gets the destination tile (where the flag is on the minimap). If there is
	 * no destination currently, null will be returned.
	 *
	 * @return The current destination tile, or null.
	 */
	public static Tile getDestination() {
		final Client client = Context.get().client;
		if (client.getDestX() <= 0) {
			return null;
		}
		return new Tile(client.getDestX() + client.getBaseX(), client.getDestY() + client.getBaseY());
	}

	/**
	 * Gets the collision flags for a given floor level in the loaded region.
	 *
	 * @param plane The floor level (0, 1, 2 or 3).
	 * @return the collision flags.
	 */
	public static int[][] getCollisionFlags(final int plane) {
		return Context.get().client.getRSGroundDataArray()[plane].getBlocks().clone();
	}

	/**
	 * Returns the collision map offset from the current region base on a given
	 * plane.
	 *
	 * @param plane The floor level.
	 * @return The offset as an Tile.
	 */
	public static Tile getCollisionOffset(final int plane) {
		final org.rsbot.bot.accessors.RSGroundData data = Context.get().client.getRSGroundDataArray()[plane];
		return new Tile(data.getX(), data.getY());
	}
}

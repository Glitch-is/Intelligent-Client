package org.rsbot.script.methods;

import org.rsbot.bot.accessors.Client;
import org.rsbot.script.methods.input.Mouse;
import org.rsbot.script.wrappers.Tile;

import java.awt.*;

/**
 * Tile related operations.
 */
public class Tiles {
	/**
	 * Returns the Tile under the mouse.
	 *
	 * @return The <code>Tile</code> under the mouse, or null if the mouse is not over the viewport.
	 */
	public static Tile tileUnderMouse() {
		final Point p = Mouse.getLocation();
		if (!Calculations.isPointOnScreen(p)) {
			return null;
		}
		return tileUnderPoint(p);
	}

	/**
	 * Gets the tile under a point.
	 *
	 * @param p The point.
	 * @return RSTile at the point's location
	 */
	public static Tile tileUnderPoint(final Point p) {
		if (!Calculations.isPointOnScreen(p)) {
			return null;
		}
		Tile close = null;
		final Client client = org.rsbot.bot.Context.get().client;
		final int bX = client.getBaseX();
		final int bY = client.getBaseY();
		for (int x = 0; x < 104; x++) {
			for (int y = 0; y < 104; y++) {
				final Tile t = new Tile(x + bX, y + bY);
				final Point s = t.getCentralPoint();
				if (s.x != -1 && s.y != -1) {
					if (close == null) {
						close = t;
					}
					if (close.getCentralPoint().distance(p) > t.getCentralPoint().distance(p)) {
						close = t;
					}
				}
			}
		}
		return close;
	}

	/**
	 * Will return the closest tile that is on screen to the given tile.
	 *
	 * @param tile Tile you want to get to.
	 * @return <code>Tile</code> that is onScreen.
	 */
	public static Tile getTileOnScreen(final Tile tile) {
		try {
			if (tile.isOnScreen()) {
				return tile;
			} else {
				final Tile loc = Players.getLocal().getLocation();
				final Tile halfWayTile = new Tile((tile.getX() + loc.getX()) / 2, (tile.getY() + loc.getY()) / 2);
				if (halfWayTile.isOnScreen()) {
					return halfWayTile;
				} else {
					return getTileOnScreen(halfWayTile);
				}
			}
		} catch (final StackOverflowError soe) {
			return null;
		}
	}

	public static boolean interact(final int x, final int y, final String action, final String option) {
		return new Tile(x, y).interact(action, option);
	}

	public static boolean interact(final int x, final int y, final String action) {
		return new Tile(x, y).interact(action, null);
	}
}

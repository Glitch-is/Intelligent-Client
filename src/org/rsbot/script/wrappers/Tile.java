package org.rsbot.script.wrappers;

import org.rsbot.bot.Context;
import org.rsbot.bot.accessors.Client;
import org.rsbot.bot.concurrent.Task;
import org.rsbot.script.methods.Calculations;
import org.rsbot.script.methods.Game;
import org.rsbot.script.methods.Menu;
import org.rsbot.script.methods.Walking;
import org.rsbot.script.methods.input.Mouse;
import org.rsbot.script.util.Filter;

import java.awt.*;

/**
 * A tile at an absolute location in the game world.
 *
 * @author Timer
 */
public class Tile implements Entity {
	private static final Color TARGET_COLOR = Color.magenta;

	private final int x;
	private final int y;
	private final int z;

	public static interface Flag {
		static final int WALL_NORTHWEST = 0x1;
		static final int WALL_NORTH = 0x2;
		static final int WALL_NORTHEAST = 0x4;
		static final int WALL_EAST = 0x8;
		static final int WALL_SOUTHEAST = 0x10;
		static final int WALL_SOUTH = 0x20;
		static final int WALL_SOUTHWEST = 0x40;
		static final int WALL_WEST = 0x80;

		static final int OBJECT_TILE = 0x100;

		static final int WALL_BLOCK_NORTHWEST = 0x200;
		static final int WALL_BLOCK_NORTH = 0x400;
		static final int WALL_BLOCK_NORTHEAST = 0x800;
		static final int WALL_BLOCK_EAST = 0x1000;
		static final int WALL_BLOCK_SOUTHEAST = 0x2000;
		static final int WALL_BLOCK_SOUTH = 0x4000;
		static final int WALL_BLOCK_SOUTHWEST = 0x8000;
		static final int WALL_BLOCK_WEST = 0x10000;

		static final int OBJECT_BLOCK = 0x20000;
		static final int DECORATION_BLOCK = 0x40000;

		static final int WALL_ALLOW_RANGE_NORTHWEST = 0x400000;
		static final int WALL_ALLOW_RANGE_NORTH = 0x800000;
		static final int WALL_ALLOW_RANGE_NORTHEAST = 0x1000000;
		static final int WALL_ALLOW_RANGE_EAST = 0x2000000;
		static final int WALL_ALLOW_RANGE_SOUTHEAST = 0x4000000;
		static final int WALL_ALLOW_RANGE_SOUTH = 0x8000000;
		static final int WALL_ALLOW_RANGE_SOUTHWEST = 0x10000000;
		static final int WALL_ALLOW_RANGE_WEST = 0x20000000;

		static final int OBJECT_ALLOW_RANGE = 0x40000000;
	}

	/**
	 * @param x the x axel of the Tile
	 * @param y the y axel of the Tile
	 */
	public Tile(final int x, final int y) {
		this.x = x;
		this.y = y;
		z = 0;
	}

	/**
	 * @param x the x axel of the Tile
	 * @param y the y axel of the Tile
	 * @param z the z axel of the Tile( the floor)
	 */
	public Tile(final int x, final int y, final int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean verify() {
		final Tile base = Game.getMapBase();
		final int x = this.x - base.x;
		final int y = this.y - base.y;
		return x >= 0 && x < 104 && y >= 0 && y < 104;
	}

	/**
	 * {@inheritDoc}
	 */
	public Point getCentralPoint() {
		return toScreen(0.5D, 0.5D, 0);
	}

	/**
	 * Gets the central point of this tile.
	 *
	 * @param h The desired height to calculate.
	 * @return The <code>Point</code> that was calculated.
	 */
	public Point getCentralPoint(final int h) {
		return toScreen(0.5D, 0.5D, h);
	}

	/**
	 * {@inheritDoc}
	 */
	public Point getNextViewportPoint() {
		return toScreen(Task.random(0.1D, 0.9D), Task.random(0.1D, 0.9D), 0);
	}

	/**
	 * Determines the a viewport point.
	 *
	 * @param h The height to calculate.
	 * @return The <code>Point</code> that was calculated.
	 */
	public Point getNextViewportPoint(final int h) {
		return toScreen(Task.random(0.1D, 0.9D), Task.random(0.1D, 0.9D), 0);
	}

	/**
	 * @param offsetX The x offset.
	 * @param offsetY The y offset.
	 * @param height  The height to calculate.
	 * @return The <code>Point</code> that was calculated.
	 */
	public Point getNextViewportPoint(final double offsetX, final double offsetY, final int height) {
		return toScreen(offsetX, offsetY, height);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean contains(final Point point) {
		final Polygon[] polygons = getBounds();
		return polygons.length != 0 && polygons[0].contains(point);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isOnScreen() {
		return contains(getCentralPoint());
	}

	/**
	 * {@inheritDoc}
	 */
	public Polygon[] getBounds() {
		final Point localPoint1 = toScreen(0.0D, 0.0D, 0);
		final Point localPoint2 = toScreen(1.0D, 0.0D, 0);
		final Point localPoint3 = toScreen(0.0D, 1.0D, 0);
		final Point localPoint4 = toScreen(1.0D, 1.0D, 0);
		if (Calculations.isPointOnScreen(localPoint1) && Calculations.isPointOnScreen(localPoint2) &&
				Calculations.isPointOnScreen(localPoint3) && Calculations.isPointOnScreen(localPoint4)) {
			final Polygon localPolygon = new Polygon();
			localPolygon.addPoint(localPoint1.x, localPoint1.y);
			localPolygon.addPoint(localPoint2.x, localPoint2.y);
			localPolygon.addPoint(localPoint4.x, localPoint4.y);
			localPolygon.addPoint(localPoint3.x, localPoint3.y);
			return new Polygon[]{localPolygon};
		}
		return new Polygon[0];
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hover() {
		return Mouse.moveAndApply(this, new Filter<Point>() {
			public boolean accept(final Point point) {
				return true;
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean click(final boolean left) {
		return Mouse.moveAndApply(this, new Filter<Point>() {
			public boolean accept(Point point) {
				Mouse.click(left);
				return true;
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean interact(final String action) {
		return interact(action, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean interact(final String action, final String option) {
		return interact(action, option, 0.5D, 0.5D, 0);
	}

	/**
	 * Interacts with this entity.
	 *
	 * @param action The action to select.
	 * @param option The option to select.
	 * @param x      The tile x offset.
	 * @param y      The tile y offset.
	 * @param h      The tile height.
	 * @return <tt>true</tt> if interacted; otherwise <tt>false</tt>.
	 */
	public boolean interact(final String action, final String option, final double x, final double y, final int h) {
		if (verify() && isOnScreen()) {
			final Tile t = this;
			return Mouse.moveAndApply(new Entity() {
						public boolean verify() {
							return t.verify();
						}

						public Point getCentralPoint() {
							return t.toScreen(x, y, h);
						}

						public Point getNextViewportPoint() {
							return t.toScreen(x, y, h);
						}

						public boolean contains(final Point point) {
							final Point p = getCentralPoint();
							return point.distance(p) < 2D;
						}

						public boolean isOnScreen() {
							return t.isOnScreen();
						}

						public Polygon[] getBounds() {
							return null;
						}

						public boolean hover() {
							return false;
						}

						public boolean click(final boolean left) {
							return false;
						}

						public boolean interact(final String action) {
							return false;
						}

						public boolean interact(final String action, final String option) {
							return false;
						}

						public void draw(Graphics render) {
						}
					}, new Filter<Point>() {
				public boolean accept(final Point point) {
					return Menu.click(action, option);
				}
			}
			);
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public void draw(final Graphics render) {
		render.setColor(TARGET_COLOR);
		final Polygon[] bounds = getBounds();
		if (bounds != null && bounds.length != 0) {
			render.drawPolygon(bounds[0]);
		}
	}

	/**
	 * Gets this tile's X coordinate.
	 *
	 * @return This tile's X coordinate.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Gets this tile's Y coordinate.
	 *
	 * @return This tile's Y coordinate.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Gets this tile's floor level.
	 *
	 * @return This tile's floor level.
	 */
	public int getZ() {
		return z;
	}

	/**
	 * Checks whether or not a given tile is on the minimap.
	 *
	 * @return <tt>true</tt> if the Tile is on the minimap; otherwise <tt>false</tt>.
	 * @see #getPointOnMap()
	 */
	public boolean isOnMap() {
		return Calculations.distanceTo(this) < 17;
	}

	/**
	 * Returns the screen location of a Tile with given 3D x, y and height
	 * offset values.
	 *
	 * @param dX     Distance from bottom left of the tile to bottom right. Ranges from 0-1;
	 * @param dY     Distance from bottom left of the tile to top left. Ranges from 0-1;
	 * @param height Height offset (normal to the ground) to return the <code>Point</code> at.
	 * @return <code>Point</code> based on position on the game plane; otherwise <code>new Point(-1, -1)</code>.
	 */
	private Point toScreen(final double dX, final double dY, final int height) {
		final Client client = Context.get().client;
		return Calculations.groundToScreen((int) ((x - client.getBaseX() + dX) * 512),
				(int) ((y - client.getBaseY() + dY) * 512), height);
	}

	/**
	 * Returns the Point on screen where a given tile is shown on the minimap.
	 *
	 * @return <tt>Point</tt> within minimap; otherwise <tt>new Point(-1, -1)</tt>.
	 */
	public Point getPointOnMap() {
		return Calculations.worldToMinimap(x, y);
	}

	/**
	 * Clicks this tile on the minimap.
	 *
	 * @return <tt>true</tt> if clicked; otherwise <tt>false</tt>.
	 */
	public boolean walkOnMap() {
		return Walking.walkTileMM(this);
	}

	/**
	 * Walks to this tile on the screen.
	 *
	 * @return <tt>true</tt> if clicked; otherwise <tt>false</tt>.
	 */
	public boolean walkOnScreen() {
		return interact("Walk here");
	}

	/**
	 * Randomizes this tile.
	 *
	 * @param maxXDeviation Max X distance from tile x.
	 * @param maxYDeviation Max Y distance from tile y.
	 * @return The randomized tile
	 */
	public Tile randomize(final int maxXDeviation, final int maxYDeviation) {
		int x = getX();
		int y = getY();
		if (maxXDeviation > 0) {
			double d = Math.random() * 2 - 1.0;
			d *= maxXDeviation;
			x += (int) d;
		}
		if (maxYDeviation > 0) {
			double d = Math.random() * 2 - 1.0;
			d *= maxYDeviation;
			y += (int) d;
		}
		return new Tile(x, y, getZ());
	}

	/**
	 * Derives this tile.
	 *
	 * @param xDeviation X distance from tile x.
	 * @param yDeviation Y distance from tile y.
	 * @return The randomized tile
	 */
	public Tile derive(final int xDeviation, final int yDeviation) {
		return new Tile(x + xDeviation, y + yDeviation, getZ());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return x * 31 + y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Tile) {
			final Tile tile = (Tile) obj;
			return tile.x == x && tile.y == y && tile.z == z;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "(X: " + x + ", Y:" + y + ", Z:" + z + ")";
	}
}

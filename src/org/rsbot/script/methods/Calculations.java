package org.rsbot.script.methods;

import org.rsbot.bot.BotComposite;
import org.rsbot.bot.Context;
import org.rsbot.bot.accessors.Client;
import org.rsbot.bot.accessors.TileData;
import org.rsbot.script.methods.ui.Interfaces;
import org.rsbot.script.wrappers.Character;
import org.rsbot.script.wrappers.GameObject;
import org.rsbot.script.wrappers.InterfaceComponent;
import org.rsbot.script.wrappers.Tile;

import java.awt.*;

/**
 * Game world and projection calculations.
 */
public class Calculations {
	public static class Render {
		public float absoluteX1 = 0, absoluteX2 = 0;
		public float absoluteY1 = 0, absoluteY2 = 0;
		public int xMultiplier = 512, yMultiplier = 512;
		public int zNear = 50, zFar = 3500;
	}

	public static class RenderData {
		public float xOff = 0, xX = 32768, xY = 0, xZ = 0;
		public float yOff = 0, yX = 0, yY = 32768, yZ = 0;
		public float zOff = 0, zX = 0, zY = 0, zZ = 32768;
	}

	public static final int[] SIN_TABLE = new int[16384];
	public static final int[] COS_TABLE = new int[16384];

	static {
		final double d = 0.00038349519697141029D;
		for (int i = 0; i < 16384; i++) {
			Calculations.SIN_TABLE[i] = (int) (32768D * Math.sin(i * d));
			Calculations.COS_TABLE[i] = (int) (32768D * Math.cos(i * d));
		}
	}

	/**
	 * Returns the angle to a given tile in degrees anti-clockwise from the
	 * positive x axis (where the x-axis is from west to east).
	 *
	 * @param t The target tile
	 * @return The angle in degrees
	 */
	public static int angleToTile(final Tile t) {
		final Tile me = Players.getLocal().getLocation();
		final int angle = (int) Math.toDegrees(Math.atan2(t.getY() - me.getY(), t.getX() - me.getX()));
		return angle >= 0 ? angle : 360 + angle;
	}

	/**
	 * Determines whether or not a given Tile is reachable by the player.
	 *
	 * @param dest     The <code>Tile</code> to check.
	 * @param isObject True if an instance of <code>GameObject</code>.
	 * @return <tt>true</tt> if a path can be made to the specified tile; otherwise <tt>false</tt>.
	 */
	public static boolean canReach(final Tile dest, final boolean isObject) {
		return pathLengthTo(dest, isObject) != -1;
	}

	/**
	 * Calculates the distance between two points.
	 *
	 * @param curr The first point.
	 * @param dest The second point.
	 * @return The distance between the two points, using the distance formula.
	 * @see #distanceBetween(org.rsbot.script.wrappers.Tile, org.rsbot.script.wrappers.Tile)
	 */
	public static double distanceBetween(final Point curr, final Point dest) {
		if (curr == null || dest == null) {
			return -1;
		}
		return Math.sqrt((curr.x - dest.x) * (curr.x - dest.x) + (curr.y - dest.y) * (curr.y - dest.y));
	}

	/**
	 * Returns the diagonal distance (hypot) between two RSTiles.
	 *
	 * @param curr The starting tile.
	 * @param dest The destination tile.
	 * @return The diagonal distance between the two <code>Tile</code>s.
	 * @see #distanceBetween(Point, Point)
	 */
	public static double distanceBetween(final Tile curr, final Tile dest) {
		if (curr == null || dest == null) {
			return -1;
		}
		return Math.sqrt((curr.getX() - dest.getX()) * (curr.getX() - dest.getX()) + (curr.getY() - dest.getY()) * (curr.getY() - dest.getY()));
	}

	/**
	 * Returns the diagonal distance to a given Character.
	 *
	 * @param c The destination character.
	 * @return Distance to <code>Character</code>.
	 * @see #distanceTo(org.rsbot.script.wrappers.Tile)
	 */
	public static int distanceTo(final Character c) {
		return c == null ? -1 : distanceTo(c.getLocation());
	}

	/**
	 * Returns the diagonal distance to a given GameObject.
	 *
	 * @param o The destination object.
	 * @return Distance to <code>GameObject</code>.
	 * @see #distanceTo(org.rsbot.script.wrappers.Tile)
	 */
	public static int distanceTo(final GameObject o) {
		return o == null ? -1 : distanceTo(o.getLocation());
	}

	/**
	 * Returns the diagonal distance to a given Tile.
	 *
	 * @param t The destination tile.
	 * @return Distance to <code>Tile</code>.
	 */
	public static int distanceTo(final Tile t) {
		return t == null ? -1 : (int) distanceBetween(Players.getLocal().getLocation(), t);
	}

	/**
	 * Returns the screen location of a given point on the ground. This accounts
	 * for the height of the ground at the given location.
	 *
	 * @param x      x value based on the game plane.
	 * @param y      y value based on the game plane.
	 * @param height height offset (normal to the ground).
	 * @return <code>Point</code> based on screen; otherwise <code>new Point(-1, -1)</code>.
	 */
	public static Point groundToScreen(final int x, final int y, final int height) {
		final Client client = Context.get().client;
		if (client.getGroundByteArray() == null || client.getTileData() == null || x < 512 || y < 512 || x > 52224 || y > 52224) {
			return new Point(-1, -1);
		}
		final int z = tileHeight(x, y) + height;
		return worldToScreen(x, y, z);
	}

	/**
	 * Returns the length of the path generated to a given Tile.
	 *
	 * @param dest     The destination tile.
	 * @param isObject <tt>true</tt> if reaching any tile adjacent to the destination should be accepted.
	 * @return <tt>true</tt> if reaching any tile adjacent to the destination should be accepted.
	 */
	public static int pathLengthTo(final Tile dest, final boolean isObject) {
		final Tile curPos = Players.getLocal().getLocation();
		return pathLengthBetween(curPos, dest, isObject);
	}

	/**
	 * Returns the length of the path generates between two RSTiles.
	 *
	 * @param start    The starting tile.
	 * @param dest     The destination tile.
	 * @param isObject <tt>true</tt> if reaching any tile adjacent to the destination should be accepted.
	 * @return <tt>true</tt> if reaching any tile adjacent to the destination should be accepted.
	 */
	public static int pathLengthBetween(final Tile start, final Tile dest, final boolean isObject) {
		final Client client = Context.get().client;
		return dijkstraDist(start.getX() - client.getBaseX(), // startX
				start.getY() - client.getBaseY(), // startY
				dest.getX() - client.getBaseX(), // destX
				dest.getY() - client.getBaseY(), // destY
				isObject); // if it's an object, accept any adjacent tile
	}

	/**
	 * Checks whether a point is within the rectangle that determines the bounds
	 * of game screen. This will work fine when in fixed mode. In resizable mode
	 * it will exclude any points that are less than 253 pixels from the right
	 * of the screen or less than 169 pixels from the bottom of the screen,
	 * giving a rough area.
	 *
	 * @param check The point to check.
	 * @return <tt>true</tt> if the point is within the rectangle; otherwise
	 *         <tt>false</tt>.
	 */
	public static boolean isPointOnScreen(final Point check) {
		final int x = check.x, y = check.y;
		if (Game.isFixed()) {
			return x > 4 && x < Game.getWidth() - 253 && y > 4 && y < Game.getHeight() - 169;
		} else {
			return x > 0 && x < Game.getWidth() - 260 && y > 0 && y < Game.getHeight() - 149;
		}
	}

	/**
	 * Returns the height of the ground at the given location in the game world.
	 *
	 * @param x x value based on the game plane.
	 * @param y y value based on the game plane.
	 * @return The ground height at the given location; otherwise <code>0</code>.
	 */
	public static int tileHeight(final int x, final int y) {
		final Client client = Context.get().client;
		int p = client.getPlane();
		final int x1 = x >> 9;
		final int y1 = y >> 9;
		final byte[][][] settings = client.getGroundByteArray();
		if (settings != null && x1 >= 0 && x1 < 104 && y1 >= 0 && y1 < 104) {
			if (p <= 3 && (settings[1][x1][y1] & 2) != 0) {
				++p;
			}
			final TileData[] planes = client.getTileData();
			if (planes != null && p < planes.length && planes[p] != null) {
				final int[][] heights = planes[p].getHeights();
				if (heights != null) {
					final int x2 = x & 512 - 1;
					final int y2 = y & 512 - 1;
					final int start_h = heights[x1][y1] * (512 - x2) + heights[x1 + 1][y1] * x2 >> 9;
					final int end_h = heights[x1][1 + y1] * (512 - x2) + heights[x1 + 1][y1 + 1] * x2 >> 9;
					return start_h * (512 - y2) + end_h * y2 >> 9;
				}
			}
		}
		return 0;
	}

	/**
	 * Returns the screen Point of given absolute x and y values in the game's
	 * 3D plane.
	 *
	 * @param x x value based on the game plane.
	 * @param y y value based on the game plane.
	 * @return <code>Point</code> within minimap; otherwise <tt>new Point(-1, -1)</tt>.
	 */
	public static Point worldToMinimap(double x, double y) {
		if (distanceBetween(Players.getLocal().getLocation(), new Tile((int) x, (int) y)) > 17) {
			return new Point(-1, -1);
		}
		final Client client = Context.get().client;
		x -= client.getBaseX();
		y -= client.getBaseY();
		final int calculatedX = (int) (x * 4 + 2) - client.getMyRSPlayer().getX() / 128;
		final int calculatedY = (int) (y * 4 + 2) - client.getMyRSPlayer().getY() / 128;
		try {
			final org.rsbot.bot.accessors.RSInterface mm = Context.get().composite.getGameGUI().getMiniMapInterface();
			if (mm == null) {
				return new Point(-1, -1);
			}
			final InterfaceComponent mm2 = Interfaces.getComponent(mm.getID());
			final int actDistSq = calculatedX * calculatedX + calculatedY * calculatedY;
			final int mmDist = 10 + Math.max(mm2.getWidth() / 2, mm2.getHeight() / 2);
			if (mmDist * mmDist >= actDistSq) {
				int angle = 0x3fff & (int) client.getMinimapAngle();
				if (client.getMinimapSetting() != 4) {
					angle = 0x3fff & client.getMinimapOffset() + (int) client.getMinimapAngle();
				}
				int cs = Calculations.SIN_TABLE[angle];
				int cc = Calculations.COS_TABLE[angle];
				if (client.getMinimapSetting() != 4) {
					final int fact = 256 + client.getMinimapScale();
					cs = 256 * cs / fact;
					cc = 256 * cc / fact;
				}
				final int calcCenterX = cc * calculatedX + cs * calculatedY >> 15;
				final int calcCenterY = cc * calculatedY - cs * calculatedX >> 15;
				final int screenx = calcCenterX + mm2.getAbsLocation().x + mm2.getWidth() / 2;
				final int screeny = -calcCenterY + mm2.getAbsLocation().y + mm2.getHeight() / 2;
				return new Point(screenx, screeny);
			}
		} catch (final NullPointerException ignored) {
		}

		return new Point(-1, -1);
	}

	/**
	 * Returns the screen Point of given absolute x and y values in the game's
	 * 3D plane.
	 *
	 * @param x x value based on the game plane.
	 * @param y y value based on the game plane.
	 * @return <code>Point</code> otherwise <tt>new Point(-1, -1)</tt>.
	 */
	public static Point tileToPoint(double x, double y) {
		final Client client = Context.get().client;
		x -= client.getBaseX();
		y -= client.getBaseY();
		final int calculatedX = (int) (x * 4 + 2) - client.getMyRSPlayer().getX() / 128;
		final int calculatedY = (int) (y * 4 + 2) - client.getMyRSPlayer().getY() / 128;
		try {
			final org.rsbot.bot.accessors.RSInterface mm = Context.get().composite.getGameGUI().getMiniMapInterface();
			if (mm == null) {
				return new Point(-1, -1);
			}
			final InterfaceComponent mm2 = Interfaces.getComponent(mm.getID());
			int angle = 0x3fff & (int) client.getMinimapAngle();
			if (client.getMinimapSetting() != 4) {
				angle = 0x3fff & client.getMinimapOffset() + (int) client.getMinimapAngle();
			}
			int cs = Calculations.SIN_TABLE[angle];
			int cc = Calculations.COS_TABLE[angle];
			if (client.getMinimapSetting() != 4) {
				final int fact = 256 + client.getMinimapScale();
				cs = 256 * cs / fact;
				cc = 256 * cc / fact;
			}
			final int calcCenterX = cc * calculatedX + cs * calculatedY >> 15;
			final int calcCenterY = cc * calculatedY - cs * calculatedX >> 15;
			final int screenx = calcCenterX + mm2.getAbsLocation().x + mm2.getWidth() / 2;
			final int screeny = -calcCenterY + mm2.getAbsLocation().y + mm2.getHeight() / 2;
			return new Point(screenx, screeny);
		} catch (final NullPointerException ignored) {
		}

		return new Point(-1, -1);
	}

	/**
	 * Returns the screen location of a given 3D point in the game world.
	 *
	 * @param x x value on the game plane.
	 * @param y y value on the game plane.
	 * @param z z value on the game plane.
	 * @return <code>Point</code> based on screen; otherwise <code>new Point(-1, -1)</code>.
	 */
	public static Point worldToScreen(final int x, final int y, final int z) {
		// perspective projection: hooked viewport values are calculated in
		// client based on camera state
		// (so no need to project using camera values and sin/cos)
		// old developers named these fields very poorly
		final BotComposite composite = Context.get().composite;
		final Render render = composite.getRender();
		final RenderData renderData = composite.getRenderData();
		final float _z = renderData.zOff + (int) (renderData.zX * x + renderData.zY * z + renderData.zZ * y);
		if (_z >= render.zNear && _z <= render.zFar) {
			final int _x = (int) (render.xMultiplier * ((int) renderData.xOff + (int) (renderData.xX * x + renderData.xY
					* z + renderData.xZ * y)) / _z);
			final int _y = (int) (render.yMultiplier * ((int) renderData.yOff + (int) (renderData.yX * x + renderData.yY
					* z + renderData.yZ * y)) / _z);
			if (_x >= render.absoluteX1 && _x <= render.absoluteX2 && _y >= render.absoluteY1 && _y <= render.absoluteY2) {
				if (Game.isFixed()) {
					return new Point((int) (_x - render.absoluteX1) + 4, (int) (_y - render.absoluteY1) + 4);
				} else {
					final int sx = (int) (_x - render.absoluteX1), sy = (int) (_y - render.absoluteY1);
					return new Point(sx, sy);
				}
			}
		}
		return new Point(-1, -1);
	}

	/**
	 * @param startX       the startX (0 < startX < 104)
	 * @param startY       the startY (0 < startY < 104)
	 * @param destX        the destX (0 < destX < 104)
	 * @param destY        the destY (0 < destY < 104)
	 * @param findAdjacent if it's an object, it will find path which touches it.
	 * @return The distance of the shortest path to the destination; or -1 if no valid path to the destination was found.
	 */
	private static int dijkstraDist(final int startX, final int startY, final int destX, final int destY, final boolean findAdjacent) {
		try {
			final int[][] prev = new int[104][104];
			final int[][] dist = new int[104][104];
			final int[] path_x = new int[4000];
			final int[] path_y = new int[4000];
			for (int xx = 0; xx < 104; xx++) {
				for (int yy = 0; yy < 104; yy++) {
					prev[xx][yy] = 0;
					dist[xx][yy] = 99999999;
				}
			}
			int curr_x = startX;
			int curr_y = startY;
			prev[startX][startY] = 99;
			dist[startX][startY] = 0;
			int path_ptr = 0;
			int step_ptr = 0;
			path_x[path_ptr] = startX;
			path_y[path_ptr++] = startY;
			final int blocks[][] = Context.get().client.getRSGroundDataArray()[Game.getPlane()].getBlocks().clone();
			final int pathLength = path_x.length;
			boolean foundPath = false;
			while (step_ptr != path_ptr) {
				curr_x = path_x[step_ptr];
				curr_y = path_y[step_ptr];
				if (Math.abs(curr_x - destX) + Math.abs(curr_y - destY) == (findAdjacent ? 1 : 0)) {
					foundPath = true;
					break;
				}
				step_ptr = (step_ptr + 1) % pathLength;
				final int cost = dist[curr_x][curr_y] + 1;
				// south
				if (curr_y > 0 && prev[curr_x][curr_y - 1] == 0 && (blocks[curr_x + 1][curr_y] & 0x1280102) == 0) {
					path_x[path_ptr] = curr_x;
					path_y[path_ptr] = curr_y - 1;
					path_ptr = (path_ptr + 1) % pathLength;
					prev[curr_x][curr_y - 1] = 1;
					dist[curr_x][curr_y - 1] = cost;
				}
				// west
				if (curr_x > 0 && prev[curr_x - 1][curr_y] == 0 && (blocks[curr_x][curr_y + 1] & 0x1280108) == 0) {
					path_x[path_ptr] = curr_x - 1;
					path_y[path_ptr] = curr_y;
					path_ptr = (path_ptr + 1) % pathLength;
					prev[curr_x - 1][curr_y] = 2;
					dist[curr_x - 1][curr_y] = cost;
				}
				// north
				if (curr_y < 104 - 1 && prev[curr_x][curr_y + 1] == 0 && (blocks[curr_x + 1][curr_y + 2] &
						0x1280120) == 0) {
					path_x[path_ptr] = curr_x;
					path_y[path_ptr] = curr_y + 1;
					path_ptr = (path_ptr + 1) % pathLength;
					prev[curr_x][curr_y + 1] = 4;
					dist[curr_x][curr_y + 1] = cost;
				}
				// east
				if (curr_x < 104 - 1 && prev[curr_x + 1][curr_y] == 0 && (blocks[curr_x + 2][curr_y + 1] &
						0x1280180) == 0) {
					path_x[path_ptr] = curr_x + 1;
					path_y[path_ptr] = curr_y;
					path_ptr = (path_ptr + 1) % pathLength;
					prev[curr_x + 1][curr_y] = 8;
					dist[curr_x + 1][curr_y] = cost;
				}
				// south west
				if (curr_x > 0 && curr_y > 0 && prev[curr_x - 1][curr_y - 1] == 0 && (blocks[curr_x][curr_y] &
						0x128010e) == 0 && (blocks[curr_x][curr_y + 1] & 0x1280108) == 0 && (blocks[curr_x +
						1][curr_y] & 0x1280102) == 0) {
					path_x[path_ptr] = curr_x - 1;
					path_y[path_ptr] = curr_y - 1;
					path_ptr = (path_ptr + 1) % pathLength;
					prev[curr_x - 1][curr_y - 1] = 3;
					dist[curr_x - 1][curr_y - 1] = cost;
				}
				// north west
				if (curr_x > 0 && curr_y < 104 - 1 && prev[curr_x - 1][curr_y + 1] == 0 && (blocks[curr_x][curr_y + 2] & 0x1280138) == 0 && (blocks[curr_x][curr_y + 1] & 0x1280108) ==
						0 && (blocks[curr_x + 1][curr_y + 2] & 0x1280120) == 0) {
					path_x[path_ptr] = curr_x - 1;
					path_y[path_ptr] = curr_y + 1;
					path_ptr = (path_ptr + 1) % pathLength;
					prev[curr_x - 1][curr_y + 1] = 6;
					dist[curr_x - 1][curr_y + 1] = cost;
				}
				// south east
				if (curr_x < 104 - 1 && curr_y > 0 && prev[curr_x + 1][curr_y - 1] == 0 && (blocks[curr_x +
						2][curr_y] & 0x1280183) == 0 && (blocks[curr_x + 2][curr_y + 1] & 0x1280180) == 0 && (blocks[curr_x + 1][curr_y] & 0x1280102) == 0) {
					path_x[path_ptr] = curr_x + 1;
					path_y[path_ptr] = curr_y - 1;
					path_ptr = (path_ptr + 1) % pathLength;
					prev[curr_x + 1][curr_y - 1] = 9;
					dist[curr_x + 1][curr_y - 1] = cost;
				}
				// north east
				if (curr_x < 104 - 1 && curr_y < 104 - 1 && prev[curr_x + 1][curr_y + 1] == 0 && (blocks[curr_x
						+ 2][curr_y + 2] & 0x12801e0) == 0 && (blocks[curr_x + 2][curr_y + 1] & 0x1280180) == 0 && (blocks[curr_x + 1][curr_y + 2] & 0x1280120) == 0) {
					path_x[path_ptr] = curr_x + 1;
					path_y[path_ptr] = curr_y + 1;
					path_ptr = (path_ptr + 1) % pathLength;
					prev[curr_x + 1][curr_y + 1] = 12;
					dist[curr_x + 1][curr_y + 1] = cost;
				}
			}
			return foundPath ? dist[curr_x][curr_y] : -1;
		} catch (Exception e) {
			return -1;
		}
	}
}

package org.rsbot.script.wrappers;

import org.rsbot.bot.BotComposite;
import org.rsbot.bot.Context;
import org.rsbot.bot.accessors.Client;
import org.rsbot.bot.accessors.Model;
import org.rsbot.bot.accessors.ModelCapture;
import org.rsbot.bot.concurrent.Task;
import org.rsbot.script.methods.Calculations;
import org.rsbot.script.methods.Game;
import org.rsbot.script.methods.Menu;
import org.rsbot.script.methods.input.Mouse;
import org.rsbot.script.util.Filter;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A screen space model.
 *
 * @author Timer
 */
public abstract class GameModel implements Entity, Locatable {
	/**
	 * Returns a filter that matches against the array of point indices for the
	 * A vertices of each triangle. Use in scripts is discouraged.
	 *
	 * @param vertex_a The array of indices for A vertices.
	 * @return The vertex point index based model filter.
	 */
	public static Filter<GameModel> newVertexFilter(final short[] vertex_a) {
		return new Filter<GameModel>() {
			public boolean accept(final GameModel m) {
				return Arrays.equals(m.indices1, vertex_a);
			}
		};
	}

	public int[] xPoints;
	public final int[] yPoints;
	public int[] zPoints;

	public final short[] indices1;
	public final short[] indices2;
	public final short[] indices3;

	public final int numVertices;
	public final int numFaces;

	public GameModel(final Model model) {
		xPoints = model.getXPoints();
		yPoints = model.getYPoints();
		zPoints = model.getZPoints();
		indices1 = model.getIndices1();
		indices2 = model.getIndices2();
		indices3 = model.getIndices3();
		if (model instanceof ModelCapture) {
			numVertices = ((ModelCapture) model).getNumVertices();
			numFaces = ((ModelCapture) model).getNumFaces();
		} else {
			numVertices = Math.min(xPoints.length, Math.min(yPoints.length, zPoints.length));
			numFaces = Math.min(indices1.length, Math.min(indices2.length, indices3.length));
		}
	}

	public abstract int getLocalX();

	public abstract int getLocalY();

	protected abstract void update();

	/**
	 * {@inheritDoc}
	 */
	public boolean verify() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public Point getCentralPoint() {
		int totalXAverage = 0;
		int totalYAverage = 0;
		int totalHeightAverage = 0;
		int index = 0;

		final int x = getLocalX();
		final int y = getLocalY();
		final int height = Calculations.tileHeight(x, y);

		final int[][] points = projectVertices();

		while (index < numFaces) {
			final int index1 = indices1[index];
			final int index2 = indices2[index];
			final int index3 = indices3[index];
			if (points[index1][2] + points[index2][2] + points[index3][2] == 3) {
				totalXAverage += (xPoints[index1] + xPoints[index2] + xPoints[index3]) / 3;
				totalYAverage += (zPoints[index1] + zPoints[index2] + zPoints[index3]) / 3;
				totalHeightAverage += (yPoints[index1] + yPoints[index2] + yPoints[index3]) / 3;
			}
			index++;
		}

		final Point averagePoint = Calculations.worldToScreen(
				x + totalXAverage / numFaces,
				y + totalYAverage / numFaces,
				height + totalHeightAverage / numFaces
		);

		if (Calculations.isPointOnScreen(averagePoint)) {
			return averagePoint;
		}
		return new Point(-1, -1);
	}

	/**
	 * {@inheritDoc}
	 */
	public Point getNextViewportPoint() {
		final java.util.List<Point> foundPoints = new ArrayList<Point>();
		final int[][] points = projectVertices();
		for (int index = 0; index < numVertices; index++) {
			if (points[index][2] == 1) {
				foundPoints.add(new Point(points[index][0], points[index][1]));
			}
		}
		if (foundPoints.size() > 0 && isOnScreen()) {
			return foundPoints.get(Task.random(0, foundPoints.size()));
		}
		return new Point(-1, -1);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean contains(final Point p) {
		final Polygon[] polygons = getBounds();
		for (final Polygon triangle : polygons) {
			if (triangle.contains(p.x, p.y)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isOnScreen() {
		final int[][] points = projectVertices();
		for (int index = 0; index < numFaces; index++) {
			final int index1 = indices1[index];
			final int index2 = indices2[index];
			final int index3 = indices3[index];
			if (points[index1][2] + points[index2][2] + points[index3][2] == 3) {
				return true;
			}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public Polygon[] getBounds() {
		final int[][] points = projectVertices();
		ArrayList<Polygon> polys = new ArrayList<Polygon>(numFaces);
		for (int index = 0; index < numFaces; index++) {
			final int index1 = indices1[index];
			final int index2 = indices2[index];
			final int index3 = indices3[index];

			final int xPoints[] = new int[3];
			final int yPoints[] = new int[3];

			xPoints[0] = points[index1][0];
			yPoints[0] = points[index1][1];
			xPoints[1] = points[index2][0];
			yPoints[1] = points[index2][1];
			xPoints[2] = points[index3][0];
			yPoints[2] = points[index3][1];

			if (points[index1][2] + points[index2][2] + points[index3][2] == 3) {
				polys.add(new Polygon(xPoints, yPoints, 3));
			}
		}
		return polys.toArray(new Polygon[polys.size()]);
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
			public boolean accept(final Point point) {
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
		return Mouse.moveAndApply(this, new Filter<Point>() {
			public boolean accept(final Point point) {
				return Menu.click(action, option);
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	public Tile getLocation() {
		final Client client = Context.get().client;
		return new Tile(client.getBaseX() + getLocalX() / 512, client.getBaseY() + getLocalY() / 512, Game.getPlane());
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean canReach() {
		return Calculations.canReach(getLocation(), true);
	}

	/**
	 * {@inheritDoc}
	 */
	public void draw(final Graphics graphics) {
		final int[][] screen = projectVertices();

		// That was it for the projection part
		for (int index = 0; index < numFaces; index++) {
			int index1 = indices1[index];
			int index2 = indices2[index];
			int index3 = indices3[index];

			int point1X = screen[index1][0];
			int point1Y = screen[index1][1];
			int point2X = screen[index2][0];
			int point2Y = screen[index2][1];
			int point3X = screen[index3][0];
			int point3Y = screen[index3][1];

			if (screen[index1][2] + screen[index2][2] + screen[index3][2] == 3) {
				graphics.drawLine(point1X, point1Y, point2X, point2Y);
				graphics.drawLine(point2X, point2Y, point3X, point3Y);
				graphics.drawLine(point3X, point3Y, point1X, point1Y);
			}
		}
	}

	/**
	 * This projects all the models vertices to screen space.
	 *
	 * @return two dimensional array. The data format is
	 *         posX = result[vertexIndex][0]
	 *         posY = result[vertexIndex][1]
	 *         visibleOnScreen = (result[vertexIndex][2] == 1);
	 */
	private int[][] projectVertices() {
		final BotComposite composite = Context.get().composite;
		final Calculations.RenderData renderData = composite.getRenderData();
		final Calculations.Render render = composite.getRender();

		update();

		final int locX = getLocalX();
		final int locY = getLocalY();

		final int[][] screen = new int[numVertices][3];

		final float xOff = renderData.xOff;
		final float yOff = renderData.yOff;
		final float zOff = renderData.zOff;

		final float xX = renderData.xX;
		final float xY = renderData.xY;
		final float xZ = renderData.xZ;
		final float yX = renderData.yX;
		final float yY = renderData.yY;
		final float yZ = renderData.yZ;
		final float zX = renderData.zX;
		final float zY = renderData.zY;
		final float zZ = renderData.zZ;

		int xFactor = render.xMultiplier;
		int yFactor = render.yMultiplier;

		final boolean isFixed = Game.isFixed();

		int height = Calculations.tileHeight(locX, locY);
		for (int index = 0; index < numVertices; index++) {
			final int vertexX = xPoints[index] + locX;
			final int vertexY = yPoints[index] + height;
			final int vertexZ = zPoints[index] + locY;

			final float _z = zOff + (int) (zX * vertexX + zY * vertexY + zZ * vertexZ);
			if (_z >= render.zNear && _z <= render.zFar) {
				final int _x = (int) (xFactor * ((int) xOff + (int) (xX * vertexX + xY * vertexY + xZ * vertexZ)) / _z);
				final int _y = (int) (yFactor * ((int) yOff + (int) (yX * vertexX + yY * vertexY + yZ * vertexZ)) / _z);
				if (_x >= render.absoluteX1 && _x <= render.absoluteX2 && _y >= render.absoluteY1 && _y <= render.absoluteY2) {
					if (isFixed) {
						screen[index][0] = (int) (_x - render.absoluteX1) + 4;
						screen[index][1] = (int) (_y - render.absoluteY1) + 4;
						screen[index][2] = 1;
					} else {
						screen[index][0] = (int) (_x - render.absoluteX1);
						screen[index][1] = (int) (_y - render.absoluteY1);
						screen[index][2] = 1;
					}
				} else {
					screen[index][0] = -1;
					screen[index][1] = -1;
					screen[index][2] = 0;
				}
			} else {
				screen[index][0] = -1;
				screen[index][1] = -1;
				screen[index][2] = 0;
			}
		}
		return screen;
	}

	/**
	 * Returns true if the provided object is an GameModel with the same x, y and
	 * z points as this model. This method compares all of the values in the
	 * three vertex arrays.
	 *
	 * @return <tt>true</tt> if the provided object is a model with the same
	 *         points as this.
	 */
	@Override
	public boolean equals(final Object o) {
		if (o instanceof GameModel) {
			final GameModel m = (GameModel) o;
			return Arrays.equals(indices1, m.indices1)
					&& Arrays.equals(xPoints, m.xPoints)
					&& Arrays.equals(yPoints, m.yPoints)
					&& Arrays.equals(zPoints, m.zPoints);
		}
		return false;
	}

}
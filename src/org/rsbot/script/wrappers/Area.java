package org.rsbot.script.wrappers;

import org.rsbot.script.methods.Calculations;
import org.rsbot.script.methods.Players;

import java.awt.*;
import java.util.ArrayList;

/**
 * A class representing an area.
 *
 * @author Timer
 */
public class Area implements Renderable {
	private final Polygon area;
	private int plane;

	public Area(final Tile[] tiles, int plane) {
		this.plane = plane;
		area = tileArrayToPolygon(tiles);
	}

	public Area(final Tile[] tiles) {
		this(tiles, 0);
	}

	public Area(final Tile tile1, final Tile tile2, int plane) {
		this(new Tile[]{tile1, new Tile(tile2.getX(), tile1.getY()), new Tile(tile2.getX(), tile2.getY()), new Tile(tile1.getX(), tile2.getY())}, plane);
	}

	public Area(final Tile tile1, Tile tile2) {
		this(tile1, tile2, 0);
	}

	public Area(int x1, int y1, int x2, int y2) {
		this(new Tile(x1, y1), new Tile(x2, y2), 0);
	}

	public boolean contains(int x, int y) {
		return contains(new Tile[]{new Tile(x, y)});
	}

	public boolean contains(int plane, Tile[] containsTiles) {
		return this.plane == plane && contains(containsTiles);
	}

	public boolean contains(final Tile tile) {
		return contains(new Tile[]{tile});
	}

	public boolean contains(final Tile[] containsTiles) {
		final Tile[] areaTiles = getTileArray();
		for (final Tile tile2 : areaTiles) {
			for (final Tile tile : containsTiles) {
				if (tile2.equals(tile)) {
					return true;
				}
			}
		}
		return false;
	}

	public Tile getCentralTile() {
		if (this.area.npoints < 1) {
			return null;
		}
		int i = 0;
		int j = 0;
		int k = 0;
		while (k < this.area.npoints) {
			i += this.area.xpoints[k];
			j += this.area.ypoints[k];
			k++;
		}
		return new Tile(Math.round(i / this.area.npoints), Math.round(j / this.area.npoints));
	}

	public Tile getNearestTile() {
		return getNearestTile(Players.getLocal().getLocation());
	}

	public Tile getNearestTile(final Tile tile) {
		final Tile[] areaTiles = getTileArray();
		Tile nearestTile = null;
		double nearestDistance = Double.MAX_VALUE;
		for (final Tile areaTile : areaTiles) {
			final double currentDistance = Calculations.distanceBetween(areaTile, tile);
			if (currentDistance < nearestDistance) {
				nearestTile = areaTile;
				nearestDistance = currentDistance;
			}
		}
		return nearestTile;
	}

	public Tile[] getTileArray() {
		ArrayList<Tile> tileList = new ArrayList<Tile>();
		int i = getX();
		while (i <= getX() + getWidth()) {
			int j = getY();
			while (j <= getY() + getHeight()) {
				if (this.area.contains(i, j)) {
					tileList.add(new Tile(i, j, plane));
				}
				j++;
			}
			i++;
		}
		Tile[] tileArray = new Tile[tileList.size()];
		int j = 0;
		while (j < tileList.size()) {
			tileArray[j] = (tileList.get(j));
			j++;
		}
		return tileArray;
	}

	public Tile[][] getTiles() {
		Tile[][] tileArray = new Tile[getWidth()][getHeight()];
		int i = 0;
		while (i < getHeight()) {
			int j = 0;
			while (j < getWidth()) {
				if (this.area.contains(getX() + i, getY() + j)) {
					tileArray[i][j] = new Tile(getX() + i, getY() + j, plane);
				}
				j++;
			}
			i++;
		}
		return tileArray;
	}

	public int getWidth() {
		return this.area.getBounds().width;
	}

	public int getHeight() {
		return this.area.getBounds().height;
	}

	public int getX() {
		return this.area.getBounds().x;
	}

	public int getY() {
		return this.area.getBounds().y;
	}

	public int getPlane() {
		return this.plane;
	}

	public Rectangle getBounds() {
		return new Rectangle(this.area.getBounds().x, this.area.getBounds().y, getWidth(), getHeight());
	}

	private Polygon tileArrayToPolygon(final Tile[] paramArrayOfTile) {
		Polygon areaPolygon = new Polygon();
		for (final Tile localTile : paramArrayOfTile) {
			areaPolygon.addPoint(localTile.getX(), localTile.getY());
		}
		return areaPolygon;
	}

	public void draw(final Graphics render) {
		draw(render, Color.cyan);
	}

	public void draw(final Graphics render, final Color color) {
		render.setColor(color);
		final Rectangle r = getBounds();
		final Point localPoint1 = Calculations.tileToPoint(r.x, r.y);
		final Point localPoint2 = Calculations.tileToPoint(r.x + r.width, r.y);
		final Point localPoint3 = Calculations.tileToPoint(r.x + r.width, r.y + r.height);
		final Point localPoint4 = Calculations.tileToPoint(r.x, r.y + r.height);
		final Polygon localPolygon = new Polygon();
		localPolygon.addPoint(localPoint1.x, localPoint1.y);
		localPolygon.addPoint(localPoint2.x, localPoint2.y);
		localPolygon.addPoint(localPoint3.x, localPoint3.y);
		localPolygon.addPoint(localPoint4.x, localPoint4.y);
		render.drawPolygon(localPolygon);
		render.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
		render.fillPolygon(localPolygon);
	}
}
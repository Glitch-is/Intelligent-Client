package org.rsbot.script.wrappers;

import org.rsbot.bot.concurrent.Task;
import org.rsbot.script.methods.Calculations;
import org.rsbot.script.methods.Players;
import org.rsbot.script.methods.Walking;

import java.util.Arrays;
import java.util.EnumSet;

/**
 * A path consisting of a list of tile waypoints.
 *
 * @author Timer
 */
public class TilePath extends Path {
	protected Tile[] tiles;
	protected Tile[] orig;

	private boolean end;

	public TilePath(final Tile[] tiles) {
		orig = tiles;
		this.tiles = Arrays.copyOf(tiles, tiles.length);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean traverse(final EnumSet<TraversalOption> options) {
		final Tile next = getNext();
		if (next == null) {
			return false;
		}
		if (next.equals(getEnd())) {
			if (Calculations.distanceTo(next) <= 1 || end && (!Players.getLocal().isMoving() || Calculations.distanceTo(next) < 3)) {
				return false;
			}
			end = true;
		} else {
			end = false;
		}
		if (options != null && options.contains(TraversalOption.HANDLE_RUN) && !Walking.isRunEnabled() && Walking.getEnergy() > 50) {
			Walking.setRun(true);
			Task.sleep(300);
		}
		if (options != null && options.contains(TraversalOption.SPACE_ACTIONS)) {
			final Tile dest = Walking.getDestination();
			if (dest != null && Players.getLocal().isMoving() && Calculations.distanceTo(dest) > 5 && Calculations.distanceBetween(next, dest) < 7) {
				return true;
			}
		}
		return Walking.walkTileMM(next, 0, 0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isValid() {
		return tiles.length > 0 && getNext() != null && !Players.getLocal().getLocation().equals(getEnd());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Tile getNext() {
		for (int i = tiles.length - 1; i >= 0; --i) {
			final Tile dest = Walking.getDestination();
			if (tiles[i].isOnMap() && (Calculations.canReach(tiles[i], false) || (i != 0 && (dest != null ? Calculations.distanceBetween(dest, tiles[i - 1]) < 3 : Calculations.distanceTo(tiles[i - 1]) < 7)))) {
				return tiles[i];
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Tile getStart() {
		return tiles[0];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Tile getEnd() {
		return tiles[tiles.length - 1];
	}

	/**
	 * Randomize this path. The original path is stored so
	 * this method may be called multiple times without the
	 * waypoints drifting far from their original locations.
	 *
	 * @param maxX The max deviation on the X axis
	 * @param maxY The max deviation on the Y axis
	 * @return This path.
	 */
	public TilePath randomize(final int maxX, final int maxY) {
		for (int i = 0; i < tiles.length; ++i) {
			tiles[i] = orig[i].randomize(maxX, maxY);
		}
		return this;
	}

	/**
	 * Reverses this path.
	 *
	 * @return This path.
	 */
	public TilePath reverse() {
		Tile[] reversed = new Tile[tiles.length];
		for (int i = 0; i < orig.length; ++i) {
			reversed[i] = orig[tiles.length - 1 - i];
		}
		orig = reversed;
		reversed = new Tile[tiles.length];
		for (int i = 0; i < tiles.length; ++i) {
			reversed[i] = tiles[tiles.length - 1 - i];
		}
		tiles = reversed;
		return this;
	}

	/**
	 * Returns an array containing all of the vertices in this path.
	 *
	 * @return an array containing all of the vertices in this path.
	 */
	public Tile[] toArray() {
		final Tile[] a = new Tile[tiles.length];
		System.arraycopy(tiles, 0, a, 0, tiles.length);
		return a;
	}
}
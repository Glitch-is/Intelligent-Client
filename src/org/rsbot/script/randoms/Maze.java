package org.rsbot.script.randoms;

import org.rsbot.bot.concurrent.Task;
import org.rsbot.bot.event.listener.PaintListener;
import org.rsbot.script.Detector;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.methods.*;
import org.rsbot.script.util.Filter;
import org.rsbot.script.wrappers.GameObject;
import org.rsbot.script.wrappers.GameObjectDefinition;
import org.rsbot.script.wrappers.Path.TraversalOption;
import org.rsbot.script.wrappers.Tile;

import java.awt.*;
import java.util.*;
import java.util.List;

@ScriptManifest(authors = {"Equinox_"}, name = "Maze Solver")
public class Maze extends Detector implements PaintListener {
	public static final int[] DOOR_IDS = {3628};
	public static final int[] SHRINE_IDS = {3634};
	public static final Tile end_tile = new Tile(2910, 4576);
	private AdvancedPath path_to_center;

	public static class AdvancedPath {
		public static final int WALL_NORTH_WEST = 0x1;
		public static final int WALL_NORTH = 0x2;
		public static final int WALL_NORTH_EAST = 0x4;
		public static final int WALL_EAST = 0x8;
		public static final int WALL_SOUTH_EAST = 0x10;
		public static final int WALL_SOUTH = 0x20;
		public static final int WALL_SOUTH_WEST = 0x40;
		public static final int WALL_WEST = 0x80;
		public static final int BLOCKED = 0x100;

		protected Tile end;
		protected Tile base;
		protected int[][] flags;
		int offX;
		protected int offY;
		private boolean ended;
		protected Tile[] tiles;

		public AdvancedPath(Tile end) {
			this.end = end;
		}

		public boolean traverse() {
			return traverse(EnumSet.of(TraversalOption.HANDLE_RUN, TraversalOption.SPACE_ACTIONS));
		}

		public boolean traverse(EnumSet<TraversalOption> options) {
			int nIdx = getNextIndex();
			Tile next = nIdx >= 0 ? tiles[nIdx] : null;
			if (next == null) {
				return false;
			}
			Obstacle obstacle = Obstacles.getObstacleAt(tiles[Math.min(nIdx + 1, tiles.length - 1)]);
			if (obstacle != null && obstacle.isOnScreen() && obstacle.canAct()) {
				return obstacle.act();
			} else {
				if (next.equals(getEnd())) {
					if (Calculations.distanceTo(next) <= 1 || (ended && Players.getLocal().isMoving()) || next.equals(Walking.getDestination())) {
						return false;
					}
					ended = true;
				} else {
					ended = false;
				}
				if (options != null && options.contains(TraversalOption.HANDLE_RUN) && !Walking.isRunEnabled() && Walking.getEnergy() > 50) {
					Walking.setRun(true);
					Task.sleep(300);
				}
				if (options != null && options.contains(TraversalOption.SPACE_ACTIONS)) {
					Tile dest = Walking.getDestination();
					if (dest != null && Players.getLocal().isMoving() && Calculations.distanceTo(dest) > 5 && Calculations.distanceBetween(next, dest) < 7) {
						return true;
					}
				}
				return Walking.walkTileMM(next, 0, 0);
			}
		}

		/**
		 * Is this path valid
		 */
		public boolean isValid() {
			return getNext() != null
					&& !Players.getLocal().getLocation().equals(getEnd());
		}

		/**
		 * Gets the next tile
		 */
		public Tile getNext() {
			int index = getNextIndex();
			if (index >= 0) {
				return tiles[index];
			}
			return null;
		}

		/**
		 * Gets the index of the next tile
		 *
		 * @return index of the next tile
		 */
		public int getNextIndex() {
			if (!recheckPath()) {
				return -1;
			}
			if (tiles != null) {
				for (int i = tiles.length - 1; i >= 0; --i) {
					if (tiles[i] != null) {
						if (tiles[i].isOnMap()) {
							return getTileFor(i);
						}
					}
				}
			}
			return -1;
		}

		/**
		 * Updates the path
		 *
		 * @return is successful
		 */
		private boolean recheckPath() {
			if (base == null || Game.getMapBase().equals(base)) {
				int[][] flags = Walking.getCollisionFlags(Game.getPlane());
				if (flags != null) {
					base = Game.getMapBase();
					Tile start = Players.getLocal().getLocation();
					tiles = findPath(start, end);
					if (tiles == null) {
						base = null;
						return false;
					}
				}
			}
			return true;
		}

		/**
		 * Gets the first reachable tile between your location and the end tile.
		 *
		 * @param endIndex
		 * @return the index
		 */
		private int getTileFor(int endIndex) {
			int startIndex = getNearestIndex();
			for (int i = startIndex; i <= endIndex; i++) {
				if (Obstacles.isObstacleAt(tiles[i])) {
					return Math.max(0, i - 1);
				}
			}
			return endIndex;
		}

		/**
		 * Gets the nearest tile index to your location
		 *
		 * @return index
		 */
		private int getNearestIndex() {
			if (!recheckPath()) {
				return -1;
			}
			int bIdx = -1;
			double bLen = Integer.MAX_VALUE;
			for (int i = 0; i < tiles.length; i++) {
				double dist = Calculations.distanceTo(tiles[i]);
				if (dist < bLen) {
					bIdx = i;
					bLen = dist;
				}
			}
			return bIdx;
		}

		/**
		 * Gets the starting tile
		 */
		public Tile getStart() {
			return tiles != null && tiles.length > 0 ? tiles[0] : null;
		}

		/**
		 * Gets the ending tile
		 */
		public Tile getEnd() {
			return end;
		}

		/**
		 * Returns the Calculationsulated Tile array that is currently providing
		 * data to this AdvancedPath.
		 *
		 * @return The current Tile array; or <code>null</code>.
		 */
		public Tile[] getCurrentTiles() {
			return tiles;
		}

		protected class Node {
			public int x, y;
			public Node prev;
			public double g, f;
			public boolean border;

			public Node(int x, int y, boolean border) {
				this.border = border;
				this.x = x;
				this.y = y;
				g = f = 0;
			}

			public Node(int x, int y) {
				this.x = x;
				this.y = y;
				g = f = 0;
			}

			@Override
			public int hashCode() {
				return (x << 4) | y;
			}

			@Override
			public boolean equals(Object o) {
				if (o instanceof Node) {
					Node n = (Node) o;
					return x == n.x && y == n.y;
				}
				return false;
			}

			@Override
			public String toString() {
				return "(" + x + "," + y + ")";
			}

			public Tile toTile(int baseX, int baseY) {
				return new Tile(x + baseX, y + baseY);
			}
		}

		public Tile[] findPath(Tile start, Tile end) {
			return findPath(start, end, false);
		}

		private void updateFlags() {
			flags = Walking.getCollisionFlags(Game.getPlane());
			Tile offset = Walking.getCollisionOffset(Game.getPlane());
			offX = offset.getX();
			offY = offset.getY();
		}

		private Tile[] findPath(Tile start, Tile end, boolean remote) {
			int base_x = base.getX(), base_y = base.getY();
			int curr_x = start.getX() - base_x, curr_y = start.getY() - base_y, curr_z = start.getZ();
			int dest_x = end.getX() - base_x, dest_y = end.getY() - base_y, dest_z = end.getZ();

			updateFlags();

			if (flags == null || curr_x < 0 || curr_y < 0
					|| curr_x >= flags[curr_z].length
					|| curr_y >= flags[curr_z].length || dest_x < 0
					|| dest_y < 0 || dest_x >= flags[dest_z].length
					|| dest_y >= flags[dest_z].length) {
				return null;
			}

			HashSet<Node> open = new HashSet<Node>();
			HashSet<Node> closed = new HashSet<Node>();
			Node curr = new Node(curr_x, curr_y);
			Node dest = new Node(dest_x, dest_y);

			curr.f = heuristic(curr, dest);
			open.add(curr);

			while (!open.isEmpty()) {
				curr = lowest_f(open);
				if (curr.equals(dest)) {
					return path(curr, base_x, base_y);
				}
				open.remove(curr);
				closed.add(curr);
				for (Node next : successors(curr)) {
					if (!closed.contains(next)) {
						double t = curr.g + dist(curr, next);
						boolean use_t = false;
						if (!open.contains(next)) {
							open.add(next);
							use_t = true;
						} else if (t < next.g) {
							use_t = true;
						}
						if (use_t) {
							next.prev = curr;
							next.g = t;
							next.f = t + heuristic(next, dest);
						}
					}
				}
			}

			if (!remote || Calculations.distanceTo(end) < 10) {
				return null;
			}
			return findPath(start, pull(end));
		}

		private Tile pull(Tile tile) {
			Tile p = Players.getLocal().getLocation();
			int x = tile.getX(), y = tile.getY();
			if (p.getX() < x) {
				x -= 1;
			} else if (p.getX() > x) {
				x += 1;
			}
			if (p.getZ() < y) {
				y -= 1;
			} else if (p.getY() > y) {
				y += 1;
			}
			return new Tile(x, y);
		}

		private double heuristic(Node start, Node end) {
			double dx = start.x - end.x;
			double dy = start.y - end.y;
			if (dx < 0) {
				dx = -dx;
			}
			if (dy < 0) {
				dy = -dy;
			}
			return dx < dy ? dy : dx;
		}

		private double dist(Node start, Node end) {
			if (start.x != end.x && start.y != end.y) {
				return 1.41421356;
			} else {
				return 1.0;
			}
		}

		private Node lowest_f(Set<Node> open) {
			Node best = null;
			for (Node t : open) {
				if (best == null || t.f < best.f) {
					best = t;
				}
			}
			return best;
		}

		private Tile[] path(Node end, int base_x, int base_y) {
			LinkedList<Tile> path = new LinkedList<Tile>();
			Node p = end;
			while (p != null) {
				path.addFirst(p.toTile(base_x, base_y));
				p = p.prev;
			}
			return path.toArray(new Tile[path.size()]);
		}

		private List<Node> successors(Node t) {
			LinkedList<Node> tiles = new LinkedList<Node>();
			int x = t.x, y = t.y;
			int f_x = x - offX, f_y = y - offY;
			int here = flags[f_x][f_y];
			int upper = flags.length - 1;

			if (base == null) {
				base = Game.getMapBase();
			}

			boolean isobstacle = Obstacles.isObstacleAt(new Tile(x
					+ base.getX(), y + base.getY()));
			if ((f_y > 0 && ((here & WALL_SOUTH) == 0 || isobstacle ||
					Obstacles.isObstacleAt(new Tile(x + base.getX(), y + base.getY() - 1))) && ((flags[f_x][f_y - 1] & BLOCKED) == 0))) {
				tiles.add(new Node(x, y - 1));
			}
			if ((f_x > 0 && ((here & WALL_WEST) == 0 || isobstacle ||
					Obstacles.isObstacleAt(new Tile(x + base.getX() - 1, y + base.getY()))) && ((flags[f_x - 1][f_y] & BLOCKED) == 0))) {
				tiles.add(new Node(x - 1, y));
			}
			if ((f_y < upper && ((here & WALL_NORTH) == 0 || isobstacle) && ((flags[f_x][f_y + 1] & BLOCKED) == 0))) {
				tiles.add(new Node(x, y + 1));
			}
			if ((f_x < upper && ((here & WALL_EAST) == 0 || isobstacle ||
					Obstacles.isObstacleAt(new Tile(x + base.getX() + 1, y + base.getY()))) && ((flags[f_x + 1][f_y] & BLOCKED) == 0))) {
				tiles.add(new Node(x + 1, y));
			}

			if (f_x > 0 && f_y > 0
					&& (here & (WALL_SOUTH_WEST | WALL_SOUTH | WALL_WEST)) == 0
					&& (flags[f_x - 1][f_y - 1] & BLOCKED) == 0
					&& (flags[f_x][f_y - 1] & (BLOCKED | WALL_WEST)) == 0
					&& (flags[f_x - 1][f_y] & (BLOCKED | WALL_SOUTH)) == 0) {
				tiles.add(new Node(x - 1, y - 1));
			}
			if (f_x > 0 && f_y < upper
					&& (here & (WALL_NORTH_WEST | WALL_NORTH | WALL_WEST)) == 0
					&& (flags[f_x - 1][f_y + 1] & BLOCKED) == 0
					&& (flags[f_x][f_y + 1] & (BLOCKED | WALL_WEST)) == 0
					&& (flags[f_x - 1][f_y] & (BLOCKED | WALL_NORTH)) == 0) {
				tiles.add(new Node(x - 1, y + 1));
			}
			if (f_x < upper && f_y > 0
					&& (here & (WALL_SOUTH_EAST | WALL_SOUTH | WALL_EAST)) == 0
					&& (flags[f_x + 1][f_y - 1] & BLOCKED) == 0
					&& (flags[f_x][f_y - 1] & (BLOCKED | WALL_EAST)) == 0
					&& (flags[f_x + 1][f_y] & (BLOCKED | WALL_SOUTH)) == 0) {
				tiles.add(new Node(x + 1, y - 1));
			}
			if (f_x > 0 && f_y < upper
					&& (here & (WALL_NORTH_EAST | WALL_NORTH | WALL_EAST)) == 0
					&& (flags[f_x + 1][f_y + 1] & BLOCKED) == 0
					&& (flags[f_x][f_y + 1] & (BLOCKED | WALL_EAST)) == 0
					&& (flags[f_x + 1][f_y] & (BLOCKED | WALL_NORTH)) == 0) {
				tiles.add(new Node(x + 1, y + 1));
			}

			return tiles;
		}

	}

	public static class Obstacles {
		public final static Filter<GameObject> OBSTACLE_FILTER = new Filter<GameObject>() {
			public boolean accept(GameObject o) {
				for (int id : DOOR_IDS) {
					if (o.getID() == id) {
						return true;
					}
				}
				return false;
			}
		};

		public static boolean isObstacle(GameObject object) {
			return OBSTACLE_FILTER.accept(object);
		}

		public static Obstacle[] getLoaded() {
			List<Obstacle> Obstacles = new ArrayList<Obstacle>();
			for (GameObject o : Objects.getLoaded(OBSTACLE_FILTER)) {
				Obstacles.add(new Obstacle(o.getLocation(), "Open"));
			}
			return Obstacles.toArray(new Obstacle[Obstacles.size()]);
		}

		public static Obstacle getObstacleAt(Tile tile) {
			Obstacle obstacle = new Obstacle(tile, "Open");
			return obstacle.isObstacle() ? obstacle : null;
		}

		public static boolean isObstacleAt(Tile tile) {
			Obstacle obstacle = getObstacleAt(tile);
			return obstacle != null;
		}

		public static boolean isActableObstacleAt(Tile tile) {
			Obstacle obstacle = getObstacleAt(tile);
			return obstacle != null && obstacle.canAct();
		}
	}

	public static class Obstacle {
		private final Tile location;
		private String action;

		public Obstacle(Tile location, String action) {
			this.location = location;
			this.action = action;
		}

		public boolean isObstacle() {
			return getObject() != null;
		}

		public Tile getLocation() {
			return location;
		}

		public GameObject getObject() {
			GameObject[] ObjectsA = Objects.getAt(location, Objects.TYPE_INTERACTABLE | Objects.TYPE_BOUNDARY);
			for (GameObject o : ObjectsA) {
				if (Obstacles.isObstacle(o)) {
					return o;
				}
			}
			return null;
		}

		public boolean canAct() {
			GameObject obj = getObject();
			if (obj != null) {
				GameObjectDefinition def = obj.getDefinition();
				if (def != null && def.getActions() != null) {
					for (String s : def.getActions()) {
						if (s != null && s.contains(action)) {
							return true;
						}
					}
				}
			}
			return false;
		}

		public boolean act() {
			GameObject obstacle = getObject();
			if (obstacle != null) {
				if (obstacle.interact(action)) {
					for (byte b = 0; b < 50 && !canAct() && Players.getLocal().getAnimation() == -1; b++) {
						Task.sleep(125);
					}
				}
				return true;
			}
			return false;
		}

		public boolean isOnScreen() {
			GameObject obstacle = getObject();
			return obstacle != null && obstacle.isOnScreen();
		}
	}

	public void onRepaint(Graphics render) {
		render.setColor(new Color(0f, 1f, 0f, 0.25f));
		if (path_to_center != null && path_to_center.tiles != null) {
			for (Tile t : path_to_center.tiles) {
				Polygon p = getMinimapPolygon(t);
				if (p != null) {
					render.fillPolygon(p);
				}
			}
		}
	}

	public Polygon getMinimapPolygon(final Tile t) {
		if (t != null) {
			Polygon poly = new Polygon();
			Point pt = t.getPointOnMap();
			poly.addPoint(pt.x, pt.y);
			pt = new Tile(t.getX(), t.getY() + 1).getPointOnMap();
			poly.addPoint(pt.x, pt.y);
			pt = new Tile(t.getX() + 1, t.getY() + 1).getPointOnMap();
			poly.addPoint(pt.x, pt.y);
			pt = new Tile(t.getX() + 1, t.getY()).getPointOnMap();
			poly.addPoint(pt.x, pt.y);
			for (int i = 0; i < poly.npoints; i++) {
				if (poly.xpoints[i] < 0 || poly.ypoints[i] < 0) {
					return null;
				}
			}
			return poly;
		}
		return null;
	}

	@Override
	public int loop() {
		if (path_to_center == null) {
			path_to_center = new AdvancedPath(end_tile);
			log.info("Solving maze...");
			if (path_to_center.getNext() == null) {
				log.severe("Failed to find path");
				return -1;
			} else {
				log.info("Success");
			}
		} else if (path_to_center.isValid()) {
			path_to_center.traverse();
		} else {
			GameObject door = getShrineDoor();
			GameObject shrine = Objects.getNearest(SHRINE_IDS);
			if (shrine != null) {
				if (door != null && !shrine.getArea().contains(Players.getLocal().getLocation())) {
					door.interact("Open");
					Task.sleep(1250);
				} else {
					shrine.interact("Touch");
					Task.sleep(2500);
				}
			}
		}
		return 500;
	}

	public GameObject getShrineDoor() {
		GameObject shrine = Objects.getNearest(SHRINE_IDS);
		if (shrine == null) {
			return null;
		}
		GameObject bst = null;
		double bDist = Double.MAX_VALUE;
		for (GameObject o : Objects.getLoaded(DOOR_IDS)) {
			double dist = Calculations.distanceBetween(o.getLocation(), shrine.getArea().getCentralTile());
			if (dist < bDist) {
				bst = o;
				bDist = dist;
			}
		}
		return bst;
	}

	@Override
	public boolean isDetected() {
		return Objects.getNearest(DOOR_IDS) != null;
	}
}
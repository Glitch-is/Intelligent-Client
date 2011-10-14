package org.rsbot.script.wrappers;

import org.rsbot.bot.Context;
import org.rsbot.bot.accessors.Client;
import org.rsbot.bot.accessors.Model;
import org.rsbot.bot.accessors.RSAnimable;
import org.rsbot.script.methods.Calculations;
import org.rsbot.script.methods.Nodes;
import org.rsbot.script.methods.input.Mouse;
import org.rsbot.script.util.Filter;

import java.awt.*;

/**
 * @author Timer
 */
public class GameObject implements Entity, Locatable {
	private static final Color TARGET_COLOR = new Color(0, 255, 0, 75);

	public static enum Type {
		INTERACTABLE, FLOOR_DECORATION, BOUNDARY, WALL_DECORATION
	}

	private final org.rsbot.bot.accessors.RSObject obj;
	private final Type type;
	private final int plane;

	public GameObject(final org.rsbot.bot.accessors.RSObject obj, final Type type, final int plane) {
		this.obj = obj;
		this.type = type;
		this.plane = plane;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean verify() {
		return this.getDefinition() != null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Point getCentralPoint() {
		final GameModel model = getModel();
		return model != null ? model.getCentralPoint() : getLocation().getCentralPoint();
	}

	/**
	 * {@inheritDoc}
	 */
	public Point getNextViewportPoint() {
		final GameModel model = getModel();
		return model != null ? model.getNextViewportPoint() : getLocation().getNextViewportPoint();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean contains(final Point point) {
		final GameModel model = getModel();
		return model != null ? model.contains(point) : getLocation().contains(point);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isOnScreen() {
		final GameModel model = getModel();
		return model != null ? model.isOnScreen() : getLocation().isOnScreen();
	}

	/**
	 * {@inheritDoc}
	 */
	public Polygon[] getBounds() {
		final GameModel model = getModel();
		return model != null ? model.getBounds() : getLocation().getBounds();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hover() {
		return Mouse.moveAndApply(this, new Filter<Point>() {
			public boolean accept(Point point) {
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
		final GameModel model = getModel();
		return model != null ? model.interact(action, option) : getLocation().interact(action, option);
	}


	/**
	 * {@inheritDoc}
	 */
	public Tile getLocation() {
		final Client client = Context.get().client;
		return new Tile(client.getBaseX() + obj.getX() / 512, client.getBaseY() + obj.getY() / 512, plane);
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
	public void draw(final Graphics render) {
		render.setColor(TARGET_COLOR);
		final GameModel model = getModel();
		if (model != null) {
			model.draw(render);
			return;
		}
		getLocation().draw(render);
	}

	/**
	 * Gets the ID of this object.
	 *
	 * @return The ID.
	 */
	public int getID() {
		return obj.getID();
	}

	/**
	 * Gets the object definition of this object.
	 *
	 * @return The GameObjectDefinition if available, otherwise <code>null</code>.
	 */
	public GameObjectDefinition getDefinition() {
		final org.rsbot.bot.accessors.Node ref = Nodes.lookup(Context.get().client.getRSObjectDefLoader(), getID());
		if (ref != null) {
			if (ref instanceof org.rsbot.bot.accessors.HardReference) {
				return new GameObjectDefinition((org.rsbot.bot.accessors.RSObjectDef) ((org.rsbot.bot.accessors.HardReference) ref).get());
			} else if (ref instanceof org.rsbot.bot.accessors.SoftReference) {
				final Object def = ((org.rsbot.bot.accessors.SoftReference) ref).getReference().get();
				if (def != null) {
					return new GameObjectDefinition((org.rsbot.bot.accessors.RSObjectDef) def);
				}
			}
		}
		return null;
	}

	/**
	 * Gets the area of tiles covered by this object.
	 *
	 * @return The RSArea containing all the tiles on which this object can be found.
	 */
	public Area getArea() {
		if ((this.obj instanceof RSAnimable)) {
			final RSAnimable localObject = (RSAnimable) this.obj;
			final Client client = Context.get().client;
			final Tile tile1 = new Tile(client.getBaseX() + localObject.getX1(), client.getBaseY() + localObject.getY1());
			final Tile tile2 = new Tile(client.getBaseX() + localObject.getX2(), client.getBaseY() + localObject.getY2());
			return new Area(tile1, tile2, this.plane);
		}
		final Tile location = getLocation();
		return new Area(location, location, this.plane);
	}

	/**
	 * Gets the Model of this object.
	 *
	 * @return The GameModel, or null if unavailable.
	 */
	public GameModel getModel() {
		try {
			final Model model = obj.getModel();
			if (model != null && model.getXPoints() != null) {
				return new GameObjectModel(model, obj);
			}
		} catch (final AbstractMethodError ignored) {
		}
		return null;
	}

	/**
	 * Returns the name of the object.
	 *
	 * @return The object name if the definition is available; otherwise "".
	 */
	public String getName() {
		final GameObjectDefinition objectDefinition = getDefinition();
		return objectDefinition != null ? objectDefinition.getName() : "";
	}

	/**
	 * Returns this object's type.
	 *
	 * @return The type of the object.
	 */
	public Type getType() {
		return type;
	}

	@Override
	public boolean equals(final Object o) {
		return o instanceof GameObject && ((GameObject) o).obj == obj;
	}

	@Override
	public int hashCode() {
		return obj.hashCode();
	}
}

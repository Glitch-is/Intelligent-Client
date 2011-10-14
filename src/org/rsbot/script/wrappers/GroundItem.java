package org.rsbot.script.wrappers;

import org.rsbot.bot.Context;
import org.rsbot.bot.accessors.Client;
import org.rsbot.bot.accessors.RSGroundEntity;
import org.rsbot.bot.accessors.RSGroundObject;
import org.rsbot.bot.concurrent.Task;
import org.rsbot.script.methods.Calculations;
import org.rsbot.script.methods.Game;
import org.rsbot.script.methods.input.Mouse;
import org.rsbot.script.util.Filter;

import java.awt.*;

/**
 * Represents an item on a tile.
 *
 * @author Timer
 */
public class GroundItem implements Entity, Locatable {
	private static final Color TARGET_COLOR = new Color(255, 255, 0, 75);

	private final Item groundItem;
	private final Tile location;

	public GroundItem(final Tile location, final Item groundItem) {
		this.location = location;
		this.groundItem = groundItem;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean verify() {
		return groundItem != null && groundItem.verify();
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
		final GameModel model = getModel();
		if (model != null) {
			return model.hover();
		}
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
		final GameModel model = getModel();
		if (model != null) {
			return model.click(left);
		}
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
		final GameModel model = getModel();
		if (model != null) {
			return model.interact(action, option);
		}
		return getLocation().interact(action, option, Task.random(0.45, 0.55), Task.random(0.45, 0.55), 0);
	}

	/**
	 * {@inheritDoc}
	 */
	public Tile getLocation() {
		return location;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean canReach() {
		return Calculations.canReach(getLocation(), false);
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
	 * Gets the top model on the tile of this ground item.
	 *
	 * @return The top model on the tile of this ground item.
	 */
	public GameModel getModel() {
		final int x = location.getX() - Game.getBaseX();
		final int y = location.getY() - Game.getBaseY();
		final Client client = Context.get().client;
		final int plane = client.getPlane();
		final org.rsbot.bot.accessors.RSGround rsGround = client.getRSGroundArray()[plane][x][y];

		if (rsGround != null) {
			final RSGroundEntity obj = rsGround.getGroundObject();
			if (obj != null) {
				final org.rsbot.bot.accessors.Model model = ((RSGroundObject) rsGround.getGroundObject()).getModel();
				if (model != null) {
					return new AnimableModel(model, obj);
				}
			}
		}
		return null;
	}

	/**
	 * Gets this GroundItem's Item.
	 *
	 * @return This GroundItem's Item.
	 */
	public Item getItem() {
		return groundItem;
	}
}

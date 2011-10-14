package org.rsbot.script.wrappers;

import org.rsbot.bot.Context;
import org.rsbot.bot.accessors.Client;
import org.rsbot.script.methods.Calculations;
import org.rsbot.script.methods.Menu;
import org.rsbot.script.methods.input.Mouse;
import org.rsbot.script.util.Filter;

import java.awt.*;
import java.lang.ref.SoftReference;

public class Projectile implements Entity, Locatable {
	protected SoftReference<org.rsbot.bot.accessors.Projectile> accessor;

	public Projectile(org.rsbot.bot.accessors.Projectile paramProjectile) {
		this.accessor = new SoftReference<org.rsbot.bot.accessors.Projectile>(paramProjectile);
	}


	/**
	 * {@inheritDoc}
	 */
	public boolean verify() {
		return accessor.get() != null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Point getCentralPoint() {
		final GameModel model = getModel();
		if (model != null) {
			return model.getCentralPoint();
		}
		return getLocation().getCentralPoint((int) (getHeight() / 2.0D));
	}

	/**
	 * {@inheritDoc}
	 */
	public Point getNextViewportPoint() {
		final GameModel model = getModel();
		if (model != null) {
			return model.getNextViewportPoint();
		}
		return getLocation().getNextViewportPoint((int) (getHeight() / 2.0D));
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean contains(final Point p) {
		final GameModel model = getModel();
		if (model != null) {
			return model.contains(p);
		}
		return getLocation().contains(p);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isOnScreen() {
		final GameModel model = getModel();
		if (model != null) {
			return model.isOnScreen();
		}
		return getLocation().isOnScreen();
	}

	/**
	 * {@inheritDoc}
	 */
	public Polygon[] getBounds() {
		final GameModel model = getModel();
		if (model != null) {
			return model.getBounds();
		}
		return getLocation().getBounds();
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
		if (model != null) {
			return model.interact(action, option);
		}
		return Mouse.moveAndApply(this, new Filter<Point>() {
			public boolean accept(Point point) {
				return Menu.click(action, option);
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	public void draw(final Graphics render) {
		final GameModel model = getModel();
		if (model != null) {
			model.draw(render);
			return;
		}
		final Tile location = getLocation();
		location.draw(render);
		final int height = (int) Math.round(getHeight() / 2);
		final Point p = location.getCentralPoint(height);
		render.fillRect((int) p.getX() - 5, (int) p.getY() - 5, 10, 10);
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
	public Tile getLocation() {
		org.rsbot.bot.accessors.Projectile localProjectile = this.accessor.get();
		if (localProjectile == null) {
			return new Tile(-1, -1);
		}
		final Client client = Context.get().client;
		int i = client.getBaseX() + ((int) localProjectile.getLocalX() >> 9);
		int j = client.getBaseY() + ((int) localProjectile.getLocalY() >> 9);
		return new Tile(i, j, localProjectile.getPlane());
	}

	public Tile getSpawn() {
		org.rsbot.bot.accessors.Projectile localProjectile = this.accessor.get();
		if (localProjectile == null) {
			return new Tile(-1, -1);
		}
		final Client client = Context.get().client;
		int i = client.getBaseX() + (localProjectile.getX() >> 9);
		int j = client.getBaseY() + (localProjectile.getY() >> 9);
		return new Tile(i, j, localProjectile.getPlane());
	}

	public double getHeight() {
		return this.accessor.get().getLocalZ();
	}

	public GameModel getModel() {
		return null;
	}
}

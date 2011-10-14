package org.rsbot.bot.concurrent.task;

import org.rsbot.bot.Bot;
import org.rsbot.bot.Context;
import org.rsbot.bot.accessors.Client;
import org.rsbot.bot.concurrent.Task;
import org.rsbot.bot.event.listener.PaintListener;
import org.rsbot.script.util.Filter;
import org.rsbot.script.wrappers.Entity;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A task that will move the mouse to an entity, obeying a Filter.
 *
 * @author Timer
 */
public class MouseTask extends Task implements PaintListener {
	private final Entity entity;
	private final Filter<Point> filter;

	private final List<ForceModifier> modifiers = new ArrayList<ForceModifier>();
	private final Vector2D velocity = new Vector2D();

	private final Client client;
	private final Bot bot;

	private boolean running = false;
	public boolean accepted = false;

	private static final long TIMEOUT = random(4000, 7000);
	private static final Color TARGET_COLOR = new Color(0, 255, 0, 75);

	/**
	 * Creates a new mouse task and considers it running.
	 *
	 * @param entity The entity to interact with.
	 * @param filter The filter to apply to the mouse.
	 */
	public MouseTask(final Entity entity, final Filter<Point> filter) {
		this.entity = entity;
		this.filter = filter;

		bot = Context.get().bot;
		client = bot.composite.client;

		setModifiers();

		running = true;
	}

	/**
	 * Runs this mouse task until completion.
	 */
	public void run() {
		final long start = System.currentTimeMillis();
		Point lastCentral = null;
		Point lastTargetPoint = null;

		while (running && entity.verify() && System.currentTimeMillis() - start < TIMEOUT) {
			/* Retrieves central point of the entity. */
			Point centralPoint = entity.getCentralPoint();

			final Point targetPoint = new Point(-1, -1);

			/* Checks if the last targeted point is not null. */
			if (lastTargetPoint != null) {
				/* Sets the target point to be the same as the last. */
				targetPoint.x = lastTargetPoint.x;
				targetPoint.y = lastTargetPoint.y;
			}

			/* Determines if the clicking area has changed at all to prevent unnecessary calculations. */
			if (lastCentral == null || lastTargetPoint == null || !lastCentral.equals(centralPoint)) {
				/* Retrieves the view port point of the entity. */
				final Point viewPortPoint = entity.getNextViewportPoint();

				/* Ensure the entity is on screen. */
				if (viewPortPoint.x == -1) {
					Task.sleep(random(25, 51));
					continue;
				}

				/* Make the center point a view port point if center not on screen. */
				if (centralPoint.x == -1) {
					centralPoint = viewPortPoint;
				}

				lastCentral = centralPoint;

				/* Get the NE and SE point. */
				final Point point1 = new Point(
						Math.min(viewPortPoint.x, centralPoint.x),
						Math.min(viewPortPoint.y, centralPoint.y)
				);
				final Point point2 = new Point(
						Math.max(viewPortPoint.x, centralPoint.x),
						Math.max(viewPortPoint.y, centralPoint.y)
				);

				/* Attempt to get a target point within the center and desired point. */
				int tries = 0;
				while (!entity.contains(targetPoint) && tries < 15) {
					targetPoint.x = Task.random(point1.x, point2.x, (point2.x - point1.x) / 2);
					targetPoint.y = Task.random(point1.y, point2.y, (point2.y - point1.y) / 2);
					tries++;
				}

				/* Prevent getting stuck in an infinite loop of finding a point to click (this should not happen often). */
				if (tries >= 15 && !entity.contains(targetPoint)) {
					targetPoint.setLocation(viewPortPoint);
				}
			}

			/* Check if the last target point is still valid. */
			if (!entity.contains(targetPoint)) {
				lastTargetPoint = null;
				continue;
			}

			lastTargetPoint = targetPoint;

			/* Check if our mouse is within the bounds, if so, attempt to execute the filter. */
			final Point currentPosition = client.getMouse().getPoint();

			/* If the mouse is at the destination, attempt to click. */
			if (targetPoint.distance(currentPosition) < 3 && entity.contains(currentPosition) && filter.accept(currentPosition)) {
				accepted = true;
				break;
			}

			/* Create the delta time and force vector to calculate next point. */
			final double deltaTime = random(5D, 8D) / 1000D;
			final Vector2D force = new Vector2D();

			/* Apply all force modifiers to the force vector. */
			for (final ForceModifier modifier : modifiers) {
				final Vector2D f = modifier.apply(deltaTime, targetPoint);
				if (f == null) {
					continue;
				}
				force.add(f);
			}

			/* Verifies that the force is a real number. */
			if (Double.isNaN(force.xUnits) || Double.isNaN(force.yUnits)) {
				break;
			}

			/* Add the force to the velocity. */
			velocity.add(force.multiply(deltaTime));

			/* Move the mouse to the point determined by the vector. */
			final Vector2D deltaPosition = velocity.multiply(deltaTime);
			if (deltaPosition.xUnits != 0 && deltaPosition.yUnits != 0) {
				int x = client.getMouse().getX() + (int) deltaPosition.xUnits;
				int y = client.getMouse().getY() + (int) deltaPosition.yUnits;
				if (x <= 0 || x >= bot.getCanvas().getWidth() || y <= 0 || y >= bot.getCanvas().getHeight()) {
					final Point entryPoint = getEntryPoint();
					x = entryPoint.x;
					y = entryPoint.y;
				}
				bot.composite.inputManager.hopMouse(x, y);
			}

			/* Sleep to create a human-like speed. */
			Task.sleep((int) (deltaTime * 1000D / bot.composite.mouseMultiplier));
		}
		running = false;
	}

	/**
	 * Determines the entry point to hop to.
	 *
	 * @return The <code>Point</code> location to hop to.
	 */
	private Point getEntryPoint() {
		int x = 1;
		int y = 0;
		switch (bot.composite.inputManager.side) {
			case 1:
				x = 1;
				y = random(0, client.getCanvas().getHeight());
				break;
			case 2:
				x = random(0, client.getCanvas().getWidth());
				y = client.getCanvas().getHeight() - 1;
				break;
			case 3:
				x = client.getCanvas().getWidth() - 1;
				y = random(0, client.getCanvas().getHeight());
				break;
			case 4:
				x = random(0, client.getCanvas().getWidth());
				y = 1;
				break;
		}
		return new Point(x, y);
	}

	/**
	 * Instantiates the modifiers to apply to the mouse vector.
	 */
	private void setModifiers() {
		modifiers.add(new ForceModifier() {
			public Vector2D apply(final double deltaTime, final Point pTarget) {
				final Vector2D force = new Vector2D();
				final Vector2D toTarget = new Vector2D();
				toTarget.xUnits = pTarget.x - client.getMouse().getX();
				toTarget.yUnits = pTarget.y - client.getMouse().getY();
				if (toTarget.xUnits == 0 && toTarget.yUnits == 0) {
					return null;
				}

				final double alpha = toTarget.getAngle();
				final double acc = random(1500, 2000);
				force.xUnits = Math.cos(alpha) * acc;
				force.yUnits = Math.sin(alpha) * acc;

				return force;
			}
		});

		modifiers.add(new ForceModifier() {
			public Vector2D apply(final double deltaTime, final Point pTarget) {
				return velocity.multiply(-1);
			}
		});

		modifiers.add(new ForceModifier() {
			private int offset = random(300, 500);
			private double offsetAngle = -1;

			public Vector2D apply(final double deltaTime, final Point pTarget) {
				if (offsetAngle == -1) {
					offsetAngle = random(-Math.PI, Math.PI);
				}
				final Vector2D toTarget = new Vector2D();
				toTarget.xUnits = pTarget.x - client.getMouse().getX();
				toTarget.yUnits = pTarget.y - client.getMouse().getY();
				if (offset > 0 && toTarget.getLength() > random(25, 55)) {
					final Vector2D force = new Vector2D();
					force.xUnits = Math.cos(offsetAngle) * offset;
					force.yUnits = Math.sin(offsetAngle) * offset;
					offset -= random(0, 6);
					return force;
				}
				return null;
			}
		});

		modifiers.add(new ForceModifier() {
			public Vector2D apply(final double deltaTime, final Point pTarget) {
				final Vector2D toTarget = new Vector2D();
				toTarget.xUnits = pTarget.x - client.getMouse().getX();
				toTarget.yUnits = pTarget.y - client.getMouse().getY();
				final double length = toTarget.getLength();
				if (length < random(75, 125)) {
					Vector2D force = new Vector2D();

					final double speed = velocity.getLength();
					final double rh = speed * speed;
					final double s = toTarget.xUnits * toTarget.xUnits + toTarget.yUnits * toTarget.yUnits;
					if (s == 0) {
						return null;
					}
					final double f = Math.sqrt(rh / s);
					final Vector2D adjustedToTarget = toTarget.multiply(f);

					force.xUnits = (adjustedToTarget.xUnits - velocity.xUnits) / (deltaTime);
					force.yUnits = (adjustedToTarget.yUnits - velocity.yUnits) / (deltaTime);

					final double v = 4.0D / length;
					if (v < 1.0D) {
						force = force.multiply(v);
					}
					if (length < 10.0D) {
						force = force.multiply(0.5D);
					}
					return force;
				}
				return null;
			}
		});

		modifiers.add(new ForceModifier() {
			public Vector2D apply(final double deltaTime, final Point pTarget) {
				final int mouseX = client.getMouse().getX();
				final int mouseY = client.getMouse().getY();
				if (mouseX == pTarget.x && mouseY == pTarget.y) {
					velocity.xUnits = 0;
					velocity.yUnits = 0;
				}
				return null;
			}
		});
	}

	public void onRepaint(final Graphics render) {
		render.setColor(TARGET_COLOR);
		if (entity.verify()) {
			entity.draw(render);
		}
	}

	/**
	 * An interface that represents a force modifier.
	 *
	 * @author Kosaki
	 */
	private interface ForceModifier {
		/**
		 * Creates a vector from the given params.
		 *
		 * @param deltaTime The time to make the vector space through.
		 * @param pTarget   The <code>Point</code> of target.
		 * @return The <code>Vector2D</code> that has been created.
		 */
		public Vector2D apply(final double deltaTime, final Point pTarget);
	}

	/**
	 * A class that represents a vector with additional methods.
	 *
	 * @author Koskai
	 */
	private class Vector2D {
		public double xUnits;
		public double yUnits;

		/**
		 * Adds together two vectors.
		 *
		 * @param vector The second <code>Vector2D</code>.
		 * @return The resulting <code>Vector2D</code> from the sum.
		 */
		public Vector2D sum(final Vector2D vector) {
			final Vector2D out = new Vector2D();
			out.xUnits = xUnits + vector.xUnits;
			out.yUnits = xUnits + vector.yUnits;
			return out;
		}

		/**
		 * Adds the provided vector to this vector.
		 *
		 * @param vector The <code>Vector2D</code> to get the units from.
		 */
		public void add(final Vector2D vector) {
			xUnits += vector.xUnits;
			yUnits += vector.yUnits;
		}

		/**
		 * Multiplies this vectors by a factor.
		 *
		 * @param factor The factor to multiply by.
		 * @return The resulting <code>Vector2D</code> from the factor.
		 */
		public Vector2D multiply(final double factor) {
			final Vector2D out = new Vector2D();
			out.xUnits = xUnits * factor;
			out.yUnits = yUnits * factor;
			return out;
		}

		/**
		 * Gets the length of this vector.
		 *
		 * @return The length.
		 */
		public double getLength() {
			return Math.sqrt(xUnits * xUnits + yUnits * yUnits);
		}

		/**
		 * Gets the angle this vector is traveling.
		 *
		 * @return The angle of travel.
		 */
		public double getAngle() {
			return Math.atan2(yUnits, xUnits);
		}
	}
}

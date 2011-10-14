package org.rsbot.script.wrappers;

import org.rsbot.bot.Context;
import org.rsbot.bot.accessors.*;
import org.rsbot.bot.concurrent.Task;
import org.rsbot.script.methods.Calculations;
import org.rsbot.script.methods.Game;
import org.rsbot.script.methods.Menu;
import org.rsbot.script.methods.Nodes;
import org.rsbot.script.methods.input.Mouse;
import org.rsbot.script.util.Filter;

import java.awt.*;

/**
 * @author Timer
 */
public abstract class Character implements Entity, Locatable {
	private static final Color TARGET_COLOR = Color.magenta;

	/**
	 * Retrieves a reference to the client accessor. For internal use. The
	 * reference should be stored in a SoftReference by subclasses to allow for
	 * garbage collection when appropriate.
	 *
	 * @return The client accessor.
	 */
	public abstract org.rsbot.bot.accessors.RSCharacter getAccessor();

	/**
	 * {@inheritDoc}
	 */
	public boolean verify() {
		return getAccessor() != null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Point getCentralPoint() {
		final org.rsbot.bot.accessors.RSCharacter c = getAccessor();
		final GameModel model = getModel();
		if (model == null) {
			return Calculations.groundToScreen(c.getX(), c.getY(), c.getHeight() / 2);
		} else {
			return model.getCentralPoint();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Point getNextViewportPoint() {
		final org.rsbot.bot.accessors.RSCharacter c = getAccessor();
		final GameModel model = getModel();
		if (model == null) {
			return Calculations.groundToScreen(c.getX(), c.getY(), c.getHeight() / 2);
		} else {
			return model.getNextViewportPoint();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean contains(final Point point) {
		final GameModel model = getModel();
		if (model != null) {
			return model.contains(point);
		}
		final org.rsbot.bot.accessors.RSCharacter c = getAccessor();
		if (c != null) {
			final Point p = Calculations.groundToScreen(c.getX(), c.getY(), c.getHeight() / 2);
			return p.distance(point.x, point.y) < Task.random(0, 8);
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isOnScreen() {
		final GameModel model = getModel();
		if (model == null) {
			return Calculations.isPointOnScreen(getNextViewportPoint());
		} else {
			return Calculations.isPointOnScreen(model.getNextViewportPoint());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Polygon[] getBounds() {
		final GameModel model = getModel();
		if (model != null) {
			return model.getBounds();
		}
		final org.rsbot.bot.accessors.RSCharacter c = getAccessor();
		if (c != null) {
			final Point p = Calculations.groundToScreen(c.getX(), c.getY(), c.getHeight() / 2);
			return new Polygon[]{
					new Polygon(
							new int[]{p.x - 5, p.x + 5, p.x + 5, p.x - 5},
							new int[]{p.y - 5, p.y - 5, p.y + 5, p.y + 5},
							4
					)
			};
		}
		return new Polygon[0];
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
		if (verify()) {
			final GameModel model = getModel();
			if (model != null) {
				return model.interact(action, option);
			}
			return Mouse.moveAndApply(this, new Filter<Point>() {
				public boolean accept(final Point point) {
					return Menu.click(action, option);
				}
			});
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public void draw(final Graphics render) {
		final Point p = getCentralPoint();
		render.setColor(Color.black);
		render.drawLine(p.x - 2, p.y - 2, p.x + 2, p.y + 2);

		render.setColor(TARGET_COLOR);
		if (getModel() != null) {
			getModel().draw(render);
			return;
		}

		getLocation().draw(render);
	}

	/**
	 * {@inheritDoc}
	 */
	public Tile getLocation() {
		final org.rsbot.bot.accessors.RSCharacter c = getAccessor();
		if (c == null) {
			return new Tile(-1, -1);
		}
		final Client client = Context.get().client;
		final int x = client.getBaseX() + (c.getX() >> 9);
		final int y = client.getBaseY() + (c.getY() >> 9);
		return new Tile(x, y, Game.getPlane());
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean canReach() {
		return Calculations.canReach(getLocation(), false);
	}

	/**
	 * Gets the name of this Character.
	 *
	 * @return The name.
	 */
	public abstract String getName();

	/**
	 * Gets the level of this Character
	 *
	 * @return The level.
	 */
	public abstract int getLevel();

	/**
	 * Gets the animation from the animator.
	 *
	 * @return The animation id.
	 */
	public int getAnimation() {
		final Animator animator = getAccessor().getAnimator();
		if (animator != null) {
			final Sequence sequence = animator.getSequence();
			return sequence == null ? -1 : sequence.getID();
		}
		return -1;
	}

	/**
	 * Gets the stance animation from the animator.
	 *
	 * @return The stance animation id.
	 */
	public int getStance() {
		final Animator animator = getAccessor().getPassiveAnimator();
		if (animator != null) {
			final Sequence sequence = animator.getSequence();
			return sequence == null ? -1 : sequence.getID();
		}
		return -1;
	}

	/**
	 * Gets the graphic of this character.
	 *
	 * @return The graphic.
	 */
	public int getGraphic() {
		return getAccessor().getGraphicsData()[0].getID();
	}

	/**
	 * Determines the height this character is at.
	 *
	 * @return The height of this character.
	 */
	public int getHeight() {
		return getAccessor().getHeight();
	}

	/**
	 * Determines if this player is moving.
	 *
	 * @return <tt>true</tt> if moving; otherwise <tt>false</tt>.
	 */
	public boolean isMoving() {
		return getAccessor().isMoving() != 0;
	}

	/**
	 * Retreives the model of this character.
	 *
	 * @return The <code>GameModel</code> rendering this character.
	 */
	public GameModel getModel() {
		final org.rsbot.bot.accessors.RSCharacter c = getAccessor();
		if (c != null) {
			final Model model = c.getModel();
			if (model != null) {
				return new CharacterModel(model, c);
			}
		}
		return null;
	}

	/**
	 * Determines if this NPC is dead or alive.
	 *
	 * @return <tt>true</tt> if dead; otherwise <tt>false</tt>.
	 */
	public boolean isDead() {
		return !verify() || getHPPercent() == 0 || getAnimation() == 836;
	}

	/**
	 * Gets the character that this character is interacting with.
	 *
	 * @return The <code>Character</code> that this character is interacting with.
	 */
	public Character getInteracting() {
		final int interact = getAccessor().getInteracting();
		if (interact == -1) {
			return null;
		}
		final Client client = Context.get().client;
		if (interact < 32768) {
			final Node node = Nodes.lookup(client.getRSNPCNC(), interact);
			if (node == null || !(node instanceof RSNPCNode)) {
				return null;
			}
			return new NPC(((RSNPCNode) node).getRSNPC());
		} else {
			int index = interact - 32768;
			if (index == client.getSelfInteracting()) {
				index = 2047;
			}
			return new Player(client.getRSPlayerArray()[index]);
		}
	}

	/**
	 * @return The % of HP remaining
	 */
	public int getHPPercent() {
		return isInCombat() ? getAccessor().getHPRatio() * 100 / 255 : 100;
	}

	/**
	 * Determines if this character is in combat.
	 *
	 * @return <tt>true</tt> if in combat; otherwise <tt>false</tt>.
	 */
	public boolean isInCombat() {
		return Game.isLoggedIn() && Context.get().client.getLoopCycle() < getAccessor().getLoopCycleStatus();
	}

	/**
	 * Retreives the message that this player is displaying.
	 *
	 * @return The <code>String</code> message that the character is displaying.
	 */
	public String getMessage() {
		final RSMessageData messageData = getAccessor().getMessageData();
		return messageData != null ? messageData.getMessage() : null;
	}

	/**
	 * Gets the orientation of this character.
	 * <p/>
	 * 0 = north
	 * 90 = east
	 * 180 = south
	 * 270 = west
	 *
	 * @return The orientation.
	 */
	public int getOrientation() {
		return (180 + (getAccessor().getOrientation() * 45 / 2048)) % 360;
	}

	/**
	 * Checks if this character is interacting with our entity.
	 *
	 * @return <tt>true</tt> if this <code>Character</code> is interacting with our entity.
	 */
	public boolean isInteractingWithLocalPlayer() {
		return getAccessor().getInteracting() - 32768 == Context.get().client.getSelfInteracting();
	}

	/**
	 * Determines if this character is the same as the evaluated object.
	 *
	 * @param obj The object to evaluate.
	 * @return <tt>true</tt> if accessors match; otherwise <tt>false</tt>.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof Character) {
			final Character cha = (Character) obj;
			return cha.getAccessor() == getAccessor();
		}
		return false;
	}

	/**
	 * Creates a hash code unique to this character.
	 *
	 * @return The hash.
	 */
	@Override
	public int hashCode() {
		return System.identityHashCode(getAccessor());
	}

	/**
	 * Creates a string representation of this character.
	 *
	 * @return The <code>String</code> that represents this <code>Character</code>.
	 */
	@Override
	public String toString() {
		final Character inter = getInteracting();
		final String msg = getMessage();
		return "[anim=" + getAnimation() + (msg != null ? ",msg=" + getMessage() : "") + ",interact=" + (inter == null ? "null" : inter.verify() ? inter.getName() : "Invalid") + "]";
	}
}
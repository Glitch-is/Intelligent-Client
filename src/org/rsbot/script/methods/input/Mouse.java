package org.rsbot.script.methods.input;

import org.rsbot.bot.BotComposite;
import org.rsbot.bot.Context;
import org.rsbot.bot.concurrent.Task;
import org.rsbot.bot.concurrent.task.MouseTask;
import org.rsbot.bot.event.MouseTargetEvent;
import org.rsbot.bot.event.handler.EventManager;
import org.rsbot.script.util.Filter;
import org.rsbot.script.wrappers.Entity;

import java.awt.*;

/**
 * An utility class that handles Mouse operations.
 *
 * @author Timer
 */
public class Mouse {
	/**
	 * An enumeration of the possible mouse crosshairs in game.
	 *
	 * @author Timer
	 */
	public enum Crosshair {
		NONE, VIEWPORT_CLICK, INTERACTION
	}

	/**
	 * The location of the bot's mouse; or Point(-1, -1) if off screen.
	 *
	 * @return A <tt>Point</tt> containing the bot's mouse's x & y coordinates.
	 */
	public static Point getLocation() {
		final org.rsbot.bot.accessors.input.Mouse m = Context.get().client.getMouse();
		return new Point(m.getX(), m.getY());
	}

	/**
	 * @return The <tt>Point</tt> at which the bot's mouse was last clicked.
	 */
	public static Point getPressLocation() {
		final org.rsbot.bot.accessors.input.Mouse m = Context.get().client.getMouse();
		return new Point(m.getPressX(), m.getPressY());
	}

	/**
	 * @return The system time when the bot's mouse was last pressed.
	 */
	public static long getPressTime() {
		final org.rsbot.bot.accessors.input.Mouse mouse = Context.get().client.getMouse();
		return mouse == null ? 0 : mouse.getPressTime();
	}

	/**
	 * @return <tt>true</tt> if the bot's mouse is present.
	 */
	public static boolean isPresent() {
		final org.rsbot.bot.accessors.input.Mouse mouse = Context.get().client.getMouse();
		return mouse != null && mouse.isPresent();
	}

	/**
	 * @return <tt>true</tt> if the bot's mouse is pressed.
	 */
	public static boolean isPressed() {
		final org.rsbot.bot.accessors.input.Mouse mouse = Context.get().client.getMouse();
		return mouse != null && mouse.isPressed();
	}

	/**
	 * Hops mouse to the specified point.
	 *
	 * @param p The coordinate point.
	 * @see #hop(java.awt.Point, int, int)
	 */
	public static synchronized void hop(final Point p) {
		Context.get().composite.inputManager.hopMouse(p.x, p.y);
	}

	/**
	 * Hops mouse to the certain point.
	 *
	 * @param p     The coordinate point.
	 * @param randX The x coordinate randomization.
	 * @param randY The y coordinate randomization.
	 * @see #hop(java.awt.Point)
	 */
	public static void hop(final Point p, final int randX, final int randY) {
		final Point l = p.getLocation();
		p.translate(Task.random(-randX, randX), Task.random(-randY, randY));
		hop(p);
		p.setLocation(l);
	}

	/**
	 * Hops mouse to the specified point.
	 *
	 * @param x The x coordinate point.
	 * @param y The y coordinate point.
	 * @see #hop(int, int, int, int)
	 */
	public static void hop(final int x, final int y) {
		hop(new Point(x, y));
	}

	/**
	 * Hops mouse to the certain point.
	 *
	 * @param x     The x coordinate point.
	 * @param y     The y coordinate point.
	 * @param randX The x coordinate randomization.
	 * @param randY The y coordinate randomization.
	 * @see #hop(int, int)
	 */
	public static void hop(final int x, final int y, final int randX, final int randY) {
		hop(new Point(x, y), randX, randY);
	}

	/**
	 * Moves the mouse to a point.
	 *
	 * @param p The <code>Point</code> desired to position the mouse at.
	 * @see #move(java.awt.Point, int, int)
	 */
	public static void move(final Point p) {
		move(p, 0, 0);
	}

	/**
	 * Moves the mouse to the specified <code>Point</code> within randomization.
	 *
	 * @param p     The <code>Point</code> to move to.
	 * @param randX The random x translation.
	 * @param randY The random y translation.
	 * @see #move(java.awt.Point)
	 */
	public static synchronized void move(final Point p, final int randX, final int randY) {
		if (p.x != -1 && p.y != -1) {
			Context.get().composite.inputManager.moveMouse(p.x, p.y, randX, randY);
		}
	}

	/**
	 * Moves the mouse to a point.
	 *
	 * @param x The x location.
	 * @param y The y location.
	 * @see #move(int, int, int, int)
	 */
	public static void move(final int x, final int y) {
		move(new Point(x, y));
	}

	/**
	 * Moves the mouse to the specified <code>Point</code> within randomization.
	 *
	 * @param x     The x location to move to.
	 * @param y     The y location to move to.
	 * @param randX The random x translation.
	 * @param randY The random y translation.
	 * @see #move(int, int)
	 */
	public static void move(final int x, final int y, final int randX, final int randY) {
		move(new Point(x, y), randX, randY);
	}

	/**
	 * Clicks the mouse at its current location.
	 *
	 * @param leftClick <tt>true</tt> to left-click, <tt>false</tt>to right-click.
	 * @see #click(java.awt.Point, boolean)
	 * @see #click(java.awt.Point, int, int, boolean)
	 */
	public static synchronized void click(final boolean leftClick) {
		Context.get().composite.inputManager.clickMouse(leftClick);
	}

	/**
	 * Moves the mouse to a given location then clicks.
	 *
	 * @param p         The point to click.
	 * @param leftClick <tt>true</tt> to left-click, <tt>false</tt>to right-click.
	 * @see #click(boolean)
	 * @see #click(java.awt.Point, int, int, boolean)
	 */
	public static synchronized void click(final Point p, final boolean leftClick) {
		move(p);
		Task.sleep(Task.random(50, 350));
		click(leftClick);
	}

	/**
	 * Moves the mouse to a given location and clicks.
	 *
	 * @param p         The <code>Point</code> to click.
	 * @param randX     The random x translation.
	 * @param randY     The random y translation.
	 * @param leftClick <tt>true</tt> to left-click, <tt>false</tt> to right-click.
	 * @see #click(boolean)
	 * @see #click(java.awt.Point, boolean)
	 */
	public static void click(final Point p, final int randX, final int randY, final boolean leftClick) {
		final Point l = p.getLocation();
		p.translate(Task.random(-randX, randX), Task.random(-randY, randY));
		click(p, leftClick);
		p.setLocation(l);
	}

	/**
	 * Moves the mouse to a given location then clicks.
	 *
	 * @param x         The x location.
	 * @param y         The y location.
	 * @param leftClick <tt>true</tt> to left-click, <tt>false</tt>to right-click.
	 * @see #click(boolean)
	 * @see #click(java.awt.Point, int, int, boolean)
	 */
	public static void click(final int x, final int y, final boolean leftClick) {
		click(new Point(x, y), leftClick);
	}

	/**
	 * Moves the mouse to a given location and clicks.
	 *
	 * @param x         The x location.
	 * @param y         The y location.
	 * @param randX     The random x translation.
	 * @param randY     The random y translation.
	 * @param leftClick <tt>true</tt> to left-click, <tt>false</tt> to right-click.
	 * @see #click(boolean)
	 * @see #click(java.awt.Point, boolean)
	 */
	public static void click(final int x, final int y, final int randX, final int randY, final boolean leftClick) {
		click(new Point(x, y), randX, randY, leftClick);
	}

	/**
	 * Moves the mouse to an entity.
	 *
	 * @param entity The <code>Entity</code> to move the mouse and apply a filter to.
	 * @param action The filter to apply to this entity.
	 * @return <tt>true</tt> if this filter was applied; otherwise <tt>false</tt>.
	 */
	public synchronized static boolean moveAndApply(final Entity entity, final Filter<Point> action) {
		final MouseTask mouseTask = new MouseTask(entity, action);

		final EventManager eventManager = Context.get().composite.eventManager;
		final BotComposite composite = Context.get().composite;
		final boolean paint = composite.paintMouseApplication;

		if (paint) {
			eventManager.addListener(mouseTask);
		}

		composite.eventManager.dispatchEvent(new MouseTargetEvent(entity));

		mouseTask.run();

		if (paint) {
			eventManager.removeListener(mouseTask);
		}
		return mouseTask.accepted;
	}

	/**
	 * Scrolls the mouse.
	 *
	 * @param up <tt>true</tt> to scroll up, <tt>false</tt> to scroll down.
	 */
	public static void scroll(boolean up) {
		Context.get().composite.inputManager.scrollMouse(up);
	}

	/**
	 * Drag the mouse from the current position to a certain other position.
	 *
	 * @param p The point to drag to.
	 * @see #drag(java.awt.Point, int, int)
	 */
	public static void drag(final Point p) {
		Context.get().composite.inputManager.dragMouse(p.x, p.y);
	}

	/**
	 * Drag the mouse from the current position to a certain other position.
	 *
	 * @param p     The point to drag to.
	 * @param randX The random x translation.
	 * @param randY The random y translation.
	 * @see #drag(java.awt.Point)
	 */
	public static void drag(final Point p, int randX, int randY) {
		final Point l = p.getLocation();
		p.translate(Task.random(-randX, randX), Task.random(-randY, randY));
		drag(p);
		p.setLocation(l);
	}

	/**
	 * Returns the current crosshair type.
	 *
	 * @return <tt>int</tt> value of current crosshair.
	 * @see Crosshair
	 */
	public static Crosshair getCurrentCrosshair() {
		final int index = Context.get().client.getCrosshairColor();
		final Crosshair[] crosshairs = Crosshair.values();
		return index > -1 && index < crosshairs.length ? crosshairs[index] : null;
	}

	/**
	 * Enables or disables shift click.
	 *
	 * @param enabled <tt>true</tt> if you wish it to be enabled; otherwise <tt>false</tt>.
	 */
	public static void setShiftClick(final boolean enabled) {
		Context.get().client.setShiftClick(enabled);
	}

	/**
	 * Gets the mouse speed.
	 *
	 * @return the current mouse speed.
	 * @see #setPrecisionSpeed(int)
	 */
	public static int getPrecisionSpeed() {
		return Context.get().composite.inputManager.getSpeed();
	}

	/**
	 * Changes the mouse speed
	 *
	 * @param speed The speed to move the mouse at. 4-10 is advised, 1 being the fastest.
	 * @see #getPrecisionSpeed()
	 */
	public static void setPrecisionSpeed(final int speed) {
		Context.get().composite.inputManager.setSpeed(speed);
	}

	/**
	 * The application mouse speed multiplier.
	 *
	 * @param d The desired multiplier for the mouse.
	 */
	public static void setApplicationMultiplier(final double d) {
		Context.get().composite.mouseMultiplier = d <= 0.0D ? 1.0D : d;
	}

	/**
	 * @return The application multiplier the client is using for mouse operations.
	 */
	public static double getApplicationMultiplier() {
		return Context.get().composite.mouseMultiplier;
	}
}

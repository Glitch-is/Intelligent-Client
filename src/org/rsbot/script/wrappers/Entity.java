package org.rsbot.script.wrappers;

import java.awt.*;

/**
 * Represents an interactive entity in Runescape.
 *
 * @author Timer
 */
public interface Entity extends Renderable {
	/**
	 * Verify that this entity exists.
	 *
	 * @return <tt>true</tt> if this entity is still valid; otherwise <tt>false</tt>.
	 */
	public boolean verify();

	/**
	 * Determines the central point of the entity.
	 *
	 * @return The <code>Point</code> of the center of the entity.
	 */
	public Point getCentralPoint();

	/**
	 * Determines the next viewport point of the entity.
	 *
	 * @return The <code>Point</code> of the next viewport point of the entity.
	 */
	public Point getNextViewportPoint();

	/**
	 * Checks if the entity contains the provided point.
	 *
	 * @param point The <code>Point</code> you wish to check.
	 * @return <tt>true</tt> if the entity contains it; otherwise <tt>false</tt>.
	 */
	public boolean contains(final Point point);

	/**
	 * Determines if this entity is onscreen.
	 *
	 * @return <tt>true</tt> if this entity is onscreen; otherwise <tt>false</tt>.
	 */
	public boolean isOnScreen();

	/**
	 * Returns the boundaries of the entity.
	 *
	 * @return The <code>Polygon[]</code> of the bounds.
	 */
	public Polygon[] getBounds();

	/**
	 * Hovers this entity.
	 *
	 * @return <tt>true</tt> if hovered; otherwise <tt>false</tt>.
	 */
	public boolean hover();

	/**
	 * Clicks this entity.
	 *
	 * @param left Left click or right click.
	 * @return <tt>true</tt> if clicked; otherwise <tt>false</tt>.
	 */
	public boolean click(final boolean left);

	/**
	 * Interacts with this entity.
	 *
	 * @param action The action to perform.
	 * @return <tt>true</tt> if interacted; otherwise <tt>false</tt>.
	 */
	public boolean interact(final String action);

	/**
	 * Interacts with this entity.
	 *
	 * @param action The action to perform.
	 * @param option The option to perform.
	 * @return <tt>true</tt> if interacted; otherwise <tt>false</tt>.
	 */
	public boolean interact(final String action, final String option);
}

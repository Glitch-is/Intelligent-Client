package org.rsbot.script.wrappers;

/**
 * Represents an entity that can be located with a global tile.
 *
 * @author Timer
 */
public interface Locatable {
	/**
	 * @return The <code>Tile</code> location of this instance.
	 */
	public Tile getLocation();

	/**
	 * Determines if you can reach this locatable entity.
	 *
	 * @return <tt>true</tt> if can reach; otherwise <tt>false</tt>.
	 */
	public boolean canReach();
}

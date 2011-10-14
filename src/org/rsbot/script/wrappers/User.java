package org.rsbot.script.wrappers;

public interface User {
	/**
	 * Gets the name of this user.
	 *
	 * @return the name of this user
	 */
	public String getName();

	/**
	 * Gets the world number that this user is on.
	 *
	 * @return the world number or -1 if unavailable
	 */
	public int getWorldNumber();

	/**
	 * Checks whether this user is in the lobby.
	 *
	 * @return <tt>true</tt> if this user is in this lobby; otherwise <tt>false</tt>
	 */
	public boolean isInLobby();
}

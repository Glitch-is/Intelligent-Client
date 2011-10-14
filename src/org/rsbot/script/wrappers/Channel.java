package org.rsbot.script.wrappers;

public interface Channel {
	/**
	 * Gets the name of this channel.
	 *
	 * @return the name of this channel.
	 */
	public String getName();

	/**
	 * Gets the name of this channel's owner
	 *
	 * @return the name of this channel's owner
	 */
	public String getOwner();

	/**
	 * Gets the first user matching with any of the provided names on the channel.
	 *
	 * @param names the names to look for
	 * @return an instance of <code>User</code>; otherwise <code>null</code> if invalid
	 */
	public ChannelUser getUser(String... names);

	/**
	 * Gets all the users on the channel.
	 *
	 * @return an array instance of <code>ChannelUser</code>
	 */
	public ChannelUser[] getUsers();

	/**
	 * Gets all the users matching with any of the provided names on the channel.
	 *
	 * @param names the names to look for
	 * @return an array instance of <code>ChannelUser</code>; otherwise <code>null</code> if invalid
	 */
	public ChannelUser[] getUsers(String... names);

	/**
	 * Updates this instance's components. For this method
	 * to be successful, the friends chat tab must be opened.
	 *
	 * @see #update(boolean)
	 */
	public void update();

	/**
	 * Updates this instance's components. For this method
	 * to be successful, the friends chat tab must be opened.
	 *
	 * @param openTab <tt>true</tt> to open the friends chat tab; otherwise <tt>false</tt>
	 */
	public void update(boolean openTab);
}

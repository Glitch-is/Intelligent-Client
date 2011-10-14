package org.rsbot.script.methods.tabs;

import org.rsbot.bot.concurrent.Task;
import org.rsbot.script.methods.Game;
import org.rsbot.script.methods.input.Keyboard;
import org.rsbot.script.methods.ui.Interfaces;
import org.rsbot.script.methods.ui.Lobby;
import org.rsbot.script.wrappers.Channel;
import org.rsbot.script.wrappers.ChannelUser;
import org.rsbot.script.wrappers.Interface;
import org.rsbot.script.wrappers.InterfaceComponent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Friends chat related operations.
 */
public class FriendsChat {
	public static final int WIDGET = 1109;
	public static final int COMPONENT_JOIN = 27;
	public static final int COMPONENT_LEAVE = 27;

	/**
	 * An enum representing each chat rank.
	 */
	public enum ChatRank {
		RECRUIT(6226), CORPORAL(6225), SERGEANT(6224),
		LIEUTENANT(6232), CAPTAIN(6233), GENERAL(6231),
		ADMIN(6228), DEPUTY_OWNER(6629), OWNER(6227),
		FRIEND(1004), GUEST(-1);

		private final int textureId;

		private ChatRank(int textureId) {
			this.textureId = textureId;
		}

		/**
		 * Gets the texture id of this rank.
		 *
		 * @return the texture id of this rank
		 */
		public int getTextureID() {
			return textureId;
		}
	}

	/**
	 * Gets the channel the local player is on.
	 *
	 * @return an instance of <code>Channel</code>; otherwise <code>null</code> if unavailable
	 */
	public static Channel getChannel() {
		if (Lobby.isValid()) {
			return Lobby.FriendsChat.getChannel();
		}
		Interface w = getWidget();
		if (w != null) {
			return new Room(w);
		}
		return null;
	}

	/**
	 * Gets the last message said in the friends chat.
	 *
	 * @return the last message said in the friends chat; otherwise an empty <code>String</code>
	 */
	public static String getLastMessage() {
		if (Lobby.isValid()) {
			return Lobby.FriendsChat.getLastMessage();
		}
		// 137 - 180-279
		// "<col=0000>[</col><col=00f>Z</col><col=0000>] Montyman: </col>Nvm"
		Interface w = Interfaces.get(137);
		if (w != null) {
			String message = "";
			InterfaceComponent c;
			for (int i = 279; i >= 180; i--) {
				c = w.getComponent(i);
				String text = c.getText();
				if (text.startsWith("<col=")) {
					text = text.substring(text.lastIndexOf(": </col>"));
					message = text.substring(8);
					break;
				}
			}
			return message;
		}
		return "";
	}

	/**
	 * Gets the widget representing the friends chat interface.
	 *
	 * @return an instance of <code>Widget</code>; otherwise <code>null</code> if invalid
	 */
	public static Interface getWidget() {
		openTab();
		return Interfaces.get(WIDGET);
	}

	/**
	 * Checks whether the local player is on a friends chat channel.
	 *
	 * @return <tt>true</tt> if the user is on a channel; otherwise <tt>false</tt>
	 */
	public static boolean isOnChannel() {
		if (Lobby.isValid()) {
			return Lobby.FriendsChat.isOnChannel();
		}
		InterfaceComponent c = getWidget().getComponent(COMPONENT_JOIN);
		for (String action : c.getActions()) {
			// "Leave chat"
			if (action.equals("Leave chat")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Joins the given channel. If already on a channel,
	 * {@link #leave()} will be called.
	 *
	 * @param channel the channel to join
	 * @return <tt>true</tt> if the channel was joined; otherwise <tt>false</tt>
	 */
	public static boolean join(String channel) {
		if (channel != null && !channel.isEmpty()) {
			if (Lobby.isValid()) {
				return Lobby.FriendsChat.join(channel);
			} else if (isOnChannel()) {
				if (!leave()) {
					return false;
				}
			}
			InterfaceComponent c = getWidget().getComponent(COMPONENT_JOIN);
			if (c != null) {
				c.click(true);
				Task.sleep(Task.random(300, 550));
				Keyboard.sendText(channel, true);
				Task.sleep(Task.random(1550, 2100));
				return isOnChannel();
			}
		}
		return false;
	}

	/**
	 * Leaves the channel.
	 *
	 * @return <tt>true</tt> if not in a channel; otherwise <tt>false</tt>
	 */
	public static boolean leave() {
		if (isOnChannel()) {
			if (Lobby.isValid()) {
				return Lobby.FriendsChat.leave();
			}
			InterfaceComponent c = getWidget().getComponent(COMPONENT_LEAVE);
			return c != null && c.click(true);
		}
		return false;
	}

	/**
	 * Opens the friends chat tab if not already opened.
	 */
	public static void openTab() {
		if (Lobby.isValid()) {
			Lobby.openTab(Lobby.LobbyTab.FRIENDS_CHAT);
		} else if (Game.getCurrentTab() != Game.Tabs.FRIENDS_CHAT) {
			Game.openTab(Game.Tabs.FRIENDS_CHAT);
		}
	}

	/**
	 * Sends the given message. A slash will be added
	 * if necessary. This does not check whether the local
	 * player is on a channel.
	 *
	 * @param message the message to send
	 * @see #sendMessage(String, boolean)
	 */
	public static void sendMessage(String message) {
		sendMessage(message, false);
	}

	/**
	 * Sends the given message. A slash will be added
	 * if necessary. This does not check whether the local
	 * player is on a channel.
	 *
	 * @param message the message to send
	 * @param instant <tt>true</tt> to send the message instantly; otherwise <tt>false</tt>
	 */
	public static void sendMessage(String message, boolean instant) {
		if (message != null && !message.isEmpty()) {
			if (!message.startsWith("/")) {
				message = "/" + message;
			}
			if (instant) {
				Keyboard.sendTextInstant(message, true);
			} else {
				Keyboard.sendText(message, true);
			}
		}
	}

	/**
	 * Channel related operations.
	 */
	public static class Room implements Channel {

		public static final int COMPONENT_LABEL_ROOM_NAME = 1;
		public static final int COMPONENT_LABEL_ROOM_OWNER = 1;
		public static final int COMPONENT_LIST_USERS = 5;
		public static final int COMPONENT_LIST_RANKS = 6;
		public static final int COMPONENT_LIST_WORLDS = 8;

		private Interface widget;

		private Room(Interface widget) {
			if (widget == null) {
				throw new IllegalArgumentException("widget cannot be null");
			}
			this.widget = widget;
		}

		/**
		 * Gets the name of this channel.
		 *
		 * @return the name of this channel
		 */
		public String getName() {
			String name = widget.getComponent(COMPONENT_LABEL_ROOM_NAME).getText();
			name = name.substring(name.indexOf(62) + 1);
			return name.substring(0, name.indexOf(60));
		}

		/**
		 * Gets the owner of this channel.
		 *
		 * @return the owner of this channel
		 */
		public String getOwner() {
			String name = widget.getComponent(COMPONENT_LABEL_ROOM_OWNER).getText();
			return name.substring(name.lastIndexOf(62) + 1);
		}

		/**
		 * Gets the first user matching with any of the provided names.
		 *
		 * @param names the names to look for
		 * @return an instance of <code>ChannelUser</code>; otherwise <code>null</code> if invalid
		 */
		public ChannelUser getUser(String... names) {
			if (names != null && names.length > 0) {
				ChannelUser[] users = getUsers(names);
				if (users != null && users.length > 0) {
					return users[0];
				}
			}
			return null;
		}

		/**
		 * Gets all the users on the channel.
		 *
		 * @return an array instance of <code>ChannelUser</code>
		 */
		public ChannelUser[] getUsers() {
			List<ChannelUser> users = new LinkedList<ChannelUser>();
			InterfaceComponent list = widget.getComponent(COMPONENT_LIST_USERS);
			for (InterfaceComponent c : list.getComponents()) {
				if (c == null) {
					continue;
				}
				String name = c.getText();
				if (name == null || name.isEmpty()) {
					continue;
				} else if (name.contains(".")) {
					String[] actions = c.getActions();
					if (actions == null) {
						continue;
					}
					for (String action : actions) {
						if (action == null) {
							continue;
						}
						if (action.contains("Add") || action.contains("Remove")) {
							name = action.substring(action.indexOf(32, action.indexOf(32) + 1) + 1);
							break;
						}
					}
				}
				int index = c.getComponentIndex();
				InterfaceComponent rank = widget.getComponent(COMPONENT_LIST_RANKS);
				rank = rank.getComponent(index);
				InterfaceComponent world = widget.getComponent(COMPONENT_LIST_WORLDS);
				world = world.getComponent(index * 2 + 1);
				users.add(new ChannelUser(name.trim(), rank, world));
			}
			return users.toArray(new ChannelUser[users.size()]);
		}

		/**
		 * Gets all the users matching with any of the provided names.
		 *
		 * @param names the names to look for
		 * @return an array instance of <code>ChannelUser</code>; otherwise <code>null</code> if invalid
		 */
		public ChannelUser[] getUsers(String... names) {
			if (names != null && names.length > 0) {
				List<ChannelUser> users = new ArrayList<ChannelUser>();
				for (ChannelUser channelUser : getUsers()) {
					for (String name : names) {
						if (channelUser.getName().equals(name)) {
							users.add(channelUser);
						}
					}
				}
				return users.toArray(new ChannelUser[users.size()]);
			}
			return new ChannelUser[0];
		}

		/**
		 * Updates this instance's components. For this method
		 * to be successful, the friends chat tab must be opened.
		 */
		public void update() {
			update(true);
		}

		/**
		 * Updates this instance's components. For this method
		 * to be successful, the friends chat tab must be opened.
		 *
		 * @param openTab <tt>true</tt> to open the friends chat tab; otherwise <tt>false</tt>
		 */
		public void update(boolean openTab) {
			Interface w = openTab ? getWidget() : Interfaces.get(WIDGET);
			if (w != null) {
				widget = w;
			}
		}
	}

	public static final int WIDGET_FRIENDS_CHAT = 1109;
	public static final int WIDGET_FRIENDS_CHAT_BUTTON_JOIN = 27;
	public static final int WIDGET_FRIENDS_CHAT_BUTTON_LEAVE = 27;
	public static final int WIDGET_FRIENDS_CHAT_LABEL_NAME = 1;
	public static final int WIDGET_FRIENDS_CHAT_LABEL_OWNER = 1;

	public static final int WIDGET_FRIENDS_CHAT_LOBBY = 589;
	public static final int WIDGET_FRIENDS_CHAT_LOBBY_BUTTON_JOIN = 41;
	public static final int WIDGET_FRIENDS_CHAT_LOBBY_BUTTON_LEAVE = 41;
	public static final int WIDGET_FRIENDS_CHAT_LOBBY_LABEL_NAME = 19;
	public static final int WIDGET_FRIENDS_CHAT_LOBBY_LABEL_OWNER = 20;
	public static final int WIDGET_FRIENDS_CHAT_LOBBY_LIST_NAME = 55;
	public static final int WIDGET_FRIENDS_CHAT_LOBBY_LIST_RANK = 56;
	public static final int WIDGET_FRIENDS_CHAT_LOBBY_LIST_WORLD = 57;
	public static final int WIDGET_FRIENDS_CHAT_LOBBY_LIST_CHAT = 23;

	public static final int WIDGET_LOBBY_PROMPT = 589;
	public static final int WIDGET_LOBBY_PROMPT_LABEL_WINDOW_TITLE = 149;
	public static final int WIDGET_LOBBY_PROMPT_TEXT_INPUT = 151;
	public static final int WIDGET_LOBBY_PROMPT_BUTTON_OK = 159;
	public static final int WIDGET_LOBBY_PROMPT_BUTTON_CANCEL = 161;

	/**
	 * Friends list related operations. Does not yet handle the lobby.
	 */
	public static class FriendsList {
		public static final int WIDGET_FRIENDSLIST = 550;
		public static final int WIDGET_FRIENDSLIST_BUTTON_ADD_FRIEND = 23;
		public static final int WIDGET_FRIENDSLIST_BUTTON_REMOVE_FRIEND = 28;
		public static final int WIDGET_FRIENDSLIST_LABEL_FRIENDS_COUNT = 18;
		public static final int WIDGET_FRIENDSLIST_LIST_FRIENDS = 6;

		public static interface User {
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
			public int getWorld();

			/**
			 * Checks whether this user is in lobby.
			 *
			 * @return <tt>true</tt> if in lobby; otherwise <tt>false</tt>
			 */
			public boolean isInLobby();
		}

		/**
		 * Adds a friend.
		 *
		 * @param name the name of the friend to add
		 * @return <tt>true</tt> if successful; otherwise <tt>false</tt>
		 */
		public static boolean add(String name) {
			if (name != null && !name.isEmpty()) {
				openTab();
				InterfaceComponent c = Interfaces.getComponent(WIDGET_FRIENDSLIST, WIDGET_FRIENDSLIST_BUTTON_ADD_FRIEND);
				if (c != null) {
					c.click(true);
					Task.sleep(Task.random(300, 550));
					Keyboard.sendText(name, true);
					Task.sleep(Task.random(600, 800));
					return getFriend(name) != null;
				}
			}
			return false;
		}

		/**
		 * Adds a friend.
		 *
		 * @param user the instance of <code>ChannelUser</code> to add
		 * @return <tt>true</tt> if successful; otherwise <tt>false</tt>
		 */
		public static boolean add(User user) {
			if (user != null) {
				Friend friend = getFriend(user.getName());
				if (friend == null) {
					return add(user.getName());
				}
			}
			return false;
		}

		/**
		 * Gets the count of this end-user's friends.
		 *
		 * @return the count of this end-user's friends
		 */
		public static int getCount() {
			openTab();
			InterfaceComponent c = Interfaces.getComponent(WIDGET_FRIENDSLIST, WIDGET_FRIENDSLIST_LABEL_FRIENDS_COUNT);
			if (c != null) {
				String text = c.getText();
				return Integer.parseInt(text.split(" ")[0]);
			}
			return -1;
		}

		/**
		 * Gets the first friend matching with any of the provided names.
		 *
		 * @param names the names to look for
		 * @return an instance of <code>Friend</code> or <code>null</code> if no results
		 */
		public static Friend getFriend(String... names) {
			Friend[] friends = getFriends();
			for (String name : names) {
				for (Friend friend : friends) {
					if (name.equalsIgnoreCase(friend.getName())) {
						return friend;
					}
				}
			}
			return null;
		}

		/**
		 * Gets the end-user's friends from the friends list.
		 *
		 * @return an array instance of <code>Friend</code>
		 */
		public static Friend[] getFriends() {
			openTab();
			InterfaceComponent list = Interfaces.getComponent(WIDGET_FRIENDSLIST, WIDGET_FRIENDSLIST_LIST_FRIENDS);
			if (list != null) {
				java.util.LinkedList<Friend> friends = new java.util.LinkedList<Friend>();
				for (InterfaceComponent c : list.getComponents()) {
					if (c == null) {
						continue;
					}
					String name = c.getComponentName();
					name = name.substring(name.indexOf(62) + 1);
					InterfaceComponent world = Interfaces.getComponent(WIDGET_FRIENDSLIST, 5);
					world = world.getComponent(c.getIndex());
					friends.add(new Friend(name, world));
				}
				return friends.toArray(new Friend[friends.size()]);
			}
			return new Friend[0];
		}

		/**
		 * Gets all the friends matching with any of the provided names.
		 *
		 * @param names the names to look for
		 * @return an array instance of <code>Friend</code>
		 */
		public static Friend[] getFriends(String... names) {
			java.util.LinkedList<Friend> friends = new java.util.LinkedList<Friend>();
			for (String name : names) {
				for (Friend friend : getFriends()) {
					if (name.equalsIgnoreCase(friend.getName())) {
						friends.add(friend);
					}
				}
			}
			return friends.toArray(new Friend[friends.size()]);
		}

		/**
		 * Checks whether the friends list of this end-user is full.
		 *
		 * @return <tt>true</tt> if full; otherwise <tt>false</tt>
		 */
		public static boolean isFull() {
			return getCount() == 200;
		}

		/**
		 * Opens the friends list tab if not already opened.
		 */
		public static void openTab() {
			Game.Tabs tab = Game.Tabs.FRIENDS;
			if (Game.getCurrentTab() != tab) {
				Game.openTab(tab);
			}
		}

		/**
		 * Removes a friend.
		 *
		 * @param name the name of the friend to remove
		 * @return <tt>true</tt> if successful; otherwise <tt>false</tt>
		 */
		public static boolean remove(String name) {
			if (name != null && getFriend(name) != null) {
				InterfaceComponent c = Interfaces.getComponent(WIDGET_FRIENDSLIST, WIDGET_FRIENDSLIST_BUTTON_REMOVE_FRIEND);
				if (c != null) {
					c.click(true);
					Task.sleep(Task.random(300, 550));
					Keyboard.sendText(name, true);
					Task.sleep(Task.random(600, 800));
					return getFriend(name) != null;
				}
			}
			return false;
		}

		/**
		 * Removes a friend.
		 *
		 * @param user the instance of <code>ChannelUser</code> to remove
		 * @return <tt>true</tt> if successful; otherwise <tt>false</tt>
		 */
		public static boolean remove(User user) {
			if (user != null) {
				Friend friend = getFriend(user.getName());
				if (friend != null) {
					return remove(friend.getName());
				}
			}
			return false;
		}

		public static class Friend implements User {
			private String name;
			private int worldNumber;
			private boolean isOffline;
			private boolean isInLobby;

			public Friend(String name, InterfaceComponent world) {
				this.name = name;
				String text = world.getText();
				isOffline = text.contains("Of");
				isInLobby = text.contains("Lo");
				if (!isOffline && !isInLobby && !text.endsWith(".")) {
					worldNumber = Integer.parseInt(text);
				} else {
					worldNumber = -1;
				}
			}

			public String getName() {
				return name;
			}

			public int getWorld() {
				return worldNumber;
			}

			/**
			 * Checks whether this friend is offline.
			 *
			 * @return <tt>true</tt> if offline; otherwise <tt>false</tt>
			 */
			public boolean isOffline() {
				return isOffline;
			}

			public boolean isInLobby() {
				return isInLobby;
			}

			/**
			 * Checks whether this friend is online.
			 *
			 * @return <tt>true</tt> if online; otherwise <tt>false</tt>
			 */
			public boolean isOnline() {
				return !isOffline && !isInLobby;
			}
		}
	}
}
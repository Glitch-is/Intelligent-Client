package org.rsbot.script.methods.ui;

import org.rsbot.bot.concurrent.Task;
import org.rsbot.script.methods.Game;
import org.rsbot.script.methods.input.Keyboard;
import org.rsbot.script.wrappers.Channel;
import org.rsbot.script.wrappers.ChannelUser;
import org.rsbot.script.wrappers.Interface;
import org.rsbot.script.wrappers.InterfaceComponent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Lobby related operations.
 */
public class Lobby {
	public static final int WIDGET_LOBBY = 906;

	public static final int WIDGET_PLAYER_INFO = 907;
	public static final int WIDGET_WORLD_SELECT = 910;
	public static final int WIDGET_FRIENDS = 909;
	public static final int WIDGET_FRIENDS_CHAT = 589;
	public static final int WIDGET_CLAN_CHAT = 912;
	public static final int WIDGET_OPTIONS = 978;

	public static final int[] ALL_WIDGETS = {
			WIDGET_PLAYER_INFO, WIDGET_WORLD_SELECT,
			WIDGET_FRIENDS, WIDGET_FRIENDS_CHAT,
			WIDGET_CLAN_CHAT, WIDGET_OPTIONS
	};

	public static final int TAB_PLAYER_INFO = 199;
	public static final int TAB_WORLD_SELECT = 200;
	public static final int TAB_FRIENDS = 201;
	public static final int TAB_FRIENDS_CHAT = 202;
	public static final int TAB_CLAN_CHAT = 203;
	public static final int TAB_OPTIONS = 204;

	public static final int[] ALL_TABS = {
			TAB_PLAYER_INFO, TAB_WORLD_SELECT,
			TAB_FRIENDS, TAB_FRIENDS_CHAT,
			TAB_CLAN_CHAT, TAB_OPTIONS};

	public static final int WIDGET_LOBBY_BUTTON_PLAY = 171;
	public static final int WIDGET_LOBBY_COMPONENT_ALERT_BOX = 148;
	public static final int WIDGET_LOBBY_COMPONENT_ALERT_BOX_LABEL_TEXT = 151;
	public static final int WIDGET_LOBBY_COMPONENT_ALERT_BOX_BUTTON_CLOSE = 155;

	public static final int WIDGET_PROMPT = 589;
	public static final int COMPONENT_PROMPT_OK = 159;

	public enum LobbyTab {
		PLAYER_INFO(907, 199), WORLD_SELECT(910, 200),
		FRIENDS(909, 201), FRIENDS_CHAT(589, 202),
		CLAN_CHAT(912, 203), OPTIONS(978, 204);

		private final int widget;
		private final int lobbyComponent;

		private LobbyTab(int widget, int lobbyComponent) {
			this.widget = widget;
			this.lobbyComponent = lobbyComponent;
		}

		/**
		 * Gets the widget of this tab.
		 *
		 * @return the widget of this tab
		 */
		public int getInterface() {
			return widget;
		}

		/**
		 * Gets the component id for this tab of the lobby widget
		 *
		 * @return the component id for this tab of the lobby widget
		 */
		public int getLobbyComponent() {
			return lobbyComponent;
		}
	}

	/**
	 * Clicks the static play button.
	 *
	 * @return <tt>true</tt> if the button was clicked; otherwise <tt>false</tt>
	 */
	public static boolean clickPlay() {
		if (isValid()) {
			InterfaceComponent c = getInterface().getComponent(WIDGET_LOBBY_BUTTON_PLAY);
			return c.click(true);
		}
		return false;
	}

	/**
	 * Closes an alert box.
	 *
	 * @return <tt>true</tt> if the alert was closed or <tt>false</tt> if not.
	 */
	public static boolean closeAlert() {
		if (isValid() && isAlertOpen()) {
			InterfaceComponent c = getInterface().getComponent(WIDGET_LOBBY_COMPONENT_ALERT_BOX_BUTTON_CLOSE);
			return c.click(true) && !isAlertOpen();
		}
		return false;
	}

	/**
	 * Gets the text of an alert box.
	 *
	 * @return the text in the alert box or an empty <code>String</code>
	 */
	public static String getAlertText() {
		if (isValid() && isAlertOpen()) {
			InterfaceComponent c = getInterface().getComponent(WIDGET_LOBBY_COMPONENT_ALERT_BOX_LABEL_TEXT);
			return c.getText().replaceAll("<br>", "\r\n");
		}
		return "";
	}

	/**
	 * Gets the current lobby tab shown.
	 *
	 * @return the current <code>LobbyTab</code>; otherwise <tt>null</tt>
	 */
	public static LobbyTab getCurrentTab() {
		if (isValid()) {
			for (LobbyTab lobbyTab : LobbyTab.values()) {
				InterfaceComponent c = Interfaces.getComponent(lobbyTab.getInterface(), 0);
				if (c != null && c.isVisible()) {
					return lobbyTab;
				}
			}
		}
		return null;
	}

	/**
	 * Gets the lobby widget.
	 *
	 * @return the widget of the lobby screen
	 */
	public static Interface getInterface() {
		return Interfaces.get(WIDGET_LOBBY);
	}

	/**
	 * Checks whether an alert box is open.
	 *
	 * @return <tt>true</tt> if the alert box is open else <tt>false</tt> if not.
	 */
	public static boolean isAlertOpen() {
		if (isValid()) {
			return getInterface().getComponent(WIDGET_LOBBY_COMPONENT_ALERT_BOX).isVisible();
		}
		return false;
	}

	/**
	 * Checks whether the lobby screen is valid (open).
	 *
	 * @return <tt>true</tt> if valid; otherwise <tt>false</tt>
	 */
	public static boolean isValid() {
		Interface w = getInterface();
		return w != null && w.isValid();
	}

	/**
	 * Opens the given lobby tab.
	 *
	 * @param lobbyTab the tab to open
	 * @return <tt>true</tt> if the tab was clicked; otherwise <tt>false</tt>
	 */
	public static boolean openTab(LobbyTab lobbyTab) {
		if (lobbyTab != null && isValid()) {
			if (getCurrentTab() != lobbyTab) {
				InterfaceComponent c = getInterface().getComponent(lobbyTab.getLobbyComponent());
				return c != null && c.click(true);
			}
			return true;
		}
		return false;
	}

	public static class PlayerInfo {
		public static final int WIDGET_PLAYER_INFO_LABEL_HOSTNAME = 1;
		public static final int WIDGET_PLAYER_INFO_LABEL_EMAIL = 39;
		public static final int WIDGET_PLAYER_INFO_LABEL_RECOVERY_QUESTIONS = 27;
		public static final int WIDGET_PLAYER_INFO_LABEL_MESSAGE = 15;
		public static final int WIDGET_PLAYER_INFO_LABEL_MEMBERSHIP = 53;
		public static final int WIDGET_PLAYER_INFO_LABEL_MOTW = 19;

		/**
		 * Gets the hostname that was last used.
		 *
		 * @return the hostname or an empty <code>String</code>
		 */
		public static String getHostname() {
			if (isValid()) {
				InterfaceComponent c = getInterface().getComponent(WIDGET_PLAYER_INFO_LABEL_HOSTNAME);
				String host = c.getText();
				return host.substring(host.indexOf(58) + 1);
			}
			return "";
		}

		/**
		 * Gets the widget of the tab player info.
		 *
		 * @return the widget of this tab
		 */
		public static Interface getInterface() {
			if (!getCurrentTab().equals(LobbyTab.PLAYER_INFO)) {
				openTab(LobbyTab.PLAYER_INFO);
			}
			return Interfaces.get(WIDGET_PLAYER_INFO);
		}

		/**
		 * Gets the message of the week.
		 *
		 * @return the message of the week
		 */
		public static String getMessageOfTheWeek() {
			if (isValid()) {
				InterfaceComponent c = Interfaces.getComponent(908, WIDGET_PLAYER_INFO_LABEL_MOTW);
				return c.getText();
			}
			return "";
		}

		/**
		 * Checks whether this player has membership.
		 *
		 * @return <tt>true</tt> if this player has membership; otherwise <tt>false</tt>
		 */
		public static boolean hasMembership() {
			if (isValid()) {
				InterfaceComponent c = getInterface().getComponent(WIDGET_PLAYER_INFO_LABEL_MEMBERSHIP);
				return !c.getText().equals("Not a Member");
			}
			return false;
		}

		/**
		 * Checks whether this player has recovery questions set.
		 *
		 * @return <tt>true</tt> if recovery questions are set; otherwise <tt>false</tt>
		 */
		public static boolean hasRecoveryQuestions() {
			if (isValid()) {
				InterfaceComponent c = getInterface().getComponent(WIDGET_PLAYER_INFO_LABEL_RECOVERY_QUESTIONS);
				return c.getText().equals("Set");
			}
			return false;
		}

		/**
		 * Checks whether this player has an email registered.
		 *
		 * @return <tt>true</tt> if an email is registered; otherwise <tt>false</tt>
		 */
		public static boolean hasRegistered() {
			if (isValid()) {
				InterfaceComponent c = getInterface().getComponent(WIDGET_PLAYER_INFO_LABEL_EMAIL);
				return !c.getText().equals("Unregistered");
			}
			return false;
		}

		/**
		 * Checks whether or not this player has at least one unread message.
		 *
		 * @return <tt>true</tt> if there is at least one unread message; otherwise <tt>false</tt>
		 */
		public static boolean hasUnreadMessage() {
			if (isValid()) {
				InterfaceComponent c = getInterface().getComponent(WIDGET_PLAYER_INFO_LABEL_MESSAGE);
				return !c.getText().equals("0 Unread");
			}
			return false;
		}
	}

	public static class FriendsChat {
		public static final int COMPONENT_JOIN = 41;
		public static final int COMPONENT_LEAVE = 41;
		public static final int COMPONENT_CHAT = 23;

		public static Channel getChannel() {
			if (!isValid()) {
				return org.rsbot.script.methods.tabs.FriendsChat.getChannel();
			}
			Interface w = getInterface();
			if (w != null) {
				return new Room(w);
			}
			return null;
		}

		/**
		 * Gets the last message said in the friends chat. Only works when at lobby - for now.
		 *
		 * @return the last message said in the friends chat; otherwise an empty <code>String</code>
		 */
		public static String getLastMessage() {
			if (isOnChannel()) {
				if (!isValid()) {
					return org.rsbot.script.methods.tabs.FriendsChat.getLastMessage();
				}
				InterfaceComponent c = getInterface().getComponent(COMPONENT_CHAT);
				if (c != null) {
					String message = "";
					InterfaceComponent[] messages = c.getComponents();
					for (int i = 0; i < messages.length; i++) {
						String text = messages[i].getText();
						if (text.startsWith("[")) {
							text = text.substring(text.indexOf(62, text.indexOf(58)) + 1);
							message = text;
							break;
						}
					}
					return message;
				}
			}
			return "";
		}

		/**
		 * Gets the widget representing the friends chat interface in the lobby.
		 *
		 * @return an instance of <code>Interface</code>; otherwise <code>null</code> if invalid
		 */
		public static Interface getInterface() {
			Lobby.openTab(LobbyTab.FRIENDS_CHAT);
			return Interfaces.get(WIDGET_FRIENDS_CHAT);
		}

		/**
		 * Checks whether the local player is on a channel.
		 *
		 * @return <tt>true</tt> if the user is on a channel; otherwise <tt>false</tt>
		 */
		public static boolean isOnChannel() {
			if (!isValid()) {
				return org.rsbot.script.methods.tabs.FriendsChat.isOnChannel();
			}
			InterfaceComponent c = getInterface().getComponent(COMPONENT_JOIN);
			return c != null && c.getText().contains("Leave chat");
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
				if (!isValid()) {
					return org.rsbot.script.methods.tabs.FriendsChat.join(channel);
				} else if (isOnChannel()) {
					if (!leave()) {
						return false;
					}
				}
				InterfaceComponent c = getInterface().getComponent(COMPONENT_JOIN);
				if (c != null) {
					c.click(true);
					switch (Task.random(0, 7)) {
						case 3:
							Interface w = Interfaces.get(WIDGET_PROMPT);
							if (w != null) {
								Keyboard.sendText(channel, false);
								c = w.getComponent(COMPONENT_PROMPT_OK);
								if (c != null) {
									if (!c.click(true)) {
										Keyboard.sendText("", true);
									}
								}
							}
							break;
						default:
							Keyboard.sendText(channel, true);
					}
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
				if (!isValid()) {
					return org.rsbot.script.methods.tabs.FriendsChat.leave();
				}
				InterfaceComponent c = getInterface().getComponent(COMPONENT_LEAVE);
				return c != null && c.click(true);
			}
			return false;
		}

		/**
		 * Opens the friends chat tab if not already opened.
		 */
		public static void openTab() {
			Lobby.openTab(LobbyTab.FRIENDS_CHAT);
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
			org.rsbot.script.methods.tabs.FriendsChat.sendMessage(message, false);
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
			org.rsbot.script.methods.tabs.FriendsChat.sendMessage(message, instant);
		}

		public static class Room implements Channel {

			public static final int COMPONENT_LABEL_ROOM_NAME = 19;
			public static final int COMPONENT_LABEL_ROOM_OWNER = 20;
			public static final int COMPONENT_LIST_USERS = 55;
			public static final int COMPONENT_LIST_RANKS = 56;
			public static final int COMPONENT_LIST_WORLDS = 57;

			private Interface widget;

			private Room(Interface widget) {
				if (widget == null) {
					throw new IllegalArgumentException("widget cannot be null");
				}
				this.widget = widget;
			}

			public String getName() {
				return widget.getComponent(COMPONENT_LABEL_ROOM_NAME).getText();
			}

			public String getOwner() {
				return widget.getComponent(COMPONENT_LABEL_ROOM_OWNER).getText();
			}

			public ChannelUser getUser(String... names) {
				if (names != null && names.length > 0) {
					ChannelUser[] users = getUsers(names);
					if (users != null && users.length > 0) {
						return users[0];
					}
				}
				return null;
			}

			public ChannelUser[] getUsers() {
				List<ChannelUser> users = new LinkedList<ChannelUser>();
				InterfaceComponent list = widget.getComponent(COMPONENT_LIST_USERS);
				for (InterfaceComponent c : list.getComponents()) {
					if (c == null) {
						continue;
					}
					String name = c.getText();
					int index = c.getComponentIndex();
					InterfaceComponent rank = widget.getComponent(COMPONENT_LIST_RANKS);
					rank = rank.getComponent(index);
					InterfaceComponent world = widget.getComponent(COMPONENT_LIST_WORLDS);
					world = world.getComponent(index);
					users.add(new ChannelUser(name.trim(), rank, world));
				}
				return users.toArray(new ChannelUser[users.size()]);
			}

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

			public void update() {
				update(true);
			}

			public void update(boolean openTab) {
				Interface w = openTab ? getInterface() : Interfaces.get(WIDGET_FRIENDS_CHAT);
				if (w != null) {
					widget = w;
				}
			}
		}
	}

	public static int getSelectedWorld() {
		if (!isValid()) {
			return -1;
		}
		openTab(LobbyTab.WORLD_SELECT);
		if (Interfaces.getComponent(WIDGET_WORLD_SELECT, 11).isValid()) {
			final String worldText = Interfaces.getComponent(WIDGET_WORLD_SELECT, 11).getText().trim().substring(Interfaces.getComponent(WIDGET_WORLD_SELECT, 11).getText().trim().indexOf("World ") + 6);
			return Integer.parseInt(worldText);
		}
		return -1;
	}

	/**
	 * Enters a world from the lobby.
	 *
	 * @param world The world to switch to.
	 * @param enter To enter the world or not.
	 * @return <tt>true</tt> If correctly entered the world else <tt>false</tt>
	 * @see org.rsbot.script.methods.Game#switchWorld(int world)
	 */
	public static boolean switchWorlds(final int world, final boolean enter) {
		if (!isValid() || Game.getClientState() == 9 || Game.getClientState() == 11) {
			return false;
		}
		if (!Interfaces.get(WIDGET_WORLD_SELECT).isValid() || getCurrentTab() != LobbyTab.WORLD_SELECT) {
			openTab(LobbyTab.WORLD_SELECT);
			Task.sleep(Task.random(600, 800));
		}
		if (getSelectedWorld() == world) {
			if (enter) {
				Interfaces.getComponent(WIDGET_LOBBY, WIDGET_LOBBY_BUTTON_PLAY).click(true);
			}
			return true;
		}
		final InterfaceComponent comp = getWorldComponent(world);
		if (comp != null) {
			Interfaces.scrollTo(comp, Interfaces.getComponent(WIDGET_WORLD_SELECT, 86));
			comp.click(true);
			Task.sleep(Task.random(500, 800));
			if (getSelectedWorld() == world) {
				if (enter) {
					Interfaces.getComponent(WIDGET_LOBBY, WIDGET_LOBBY_BUTTON_PLAY).click(true);
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the component of any world on the lobby interface
	 *
	 * @param world The world to get the component of.
	 * @return The component corresponding to the world.
	 */
	public static InterfaceComponent getWorldComponent(final int world) {
		if (!isValid()) {
			return null;
		}
		if (!Interfaces.get(WIDGET_WORLD_SELECT).isValid()) {
			openTab(LobbyTab.WORLD_SELECT);
		}
		for (int i = 0; i < Interfaces.getComponent(WIDGET_WORLD_SELECT, 69).getComponents().length; i++) {
			final InterfaceComponent comp = Interfaces.getComponent(WIDGET_WORLD_SELECT, 69).getComponents()[i];
			if (comp != null) {
				final String number = comp.getText();
				if (Integer.parseInt(number) == world) {
					return Interfaces.getComponent(WIDGET_WORLD_SELECT, 77).getComponents()[i];
				}
			}
		}
		return null;
	}

	/**
	 * Enters a world from the lobby.
	 *
	 * @param world The world to switch to.
	 * @return <tt>true</tt> If correctly entered the world else <tt>false</tt>
	 * @see org.rsbot.script.methods.Game#switchWorld(int world)
	 */
	public static boolean switchWorlds(final int world) {
		return switchWorlds(world, true);
	}
}

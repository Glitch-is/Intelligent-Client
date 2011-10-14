package org.rsbot.script.methods.tabs;

import org.rsbot.script.methods.Game;
import org.rsbot.script.methods.ui.Interfaces;
import org.rsbot.script.wrappers.ChannelUser;
import org.rsbot.script.wrappers.InterfaceComponent;

/**
 * Clan chat related operations. Does not yet handle the lobby.
 */
public class ClanChat {
	public static final int WIDGET_CLAN_CHAT = 1110;
	public static final int WIDGET_CLAN_CHAT_LABEL_INFO = 55;
	public static final int WIDGET_CLAN_CHAT_LIST_USERS = 9;

	/**
	 * Gets the first clanmate matching with any of the provided names.
	 *
	 * @param names the names to look for
	 * @return an instance of <code>ClanMate</code> or <code>null</code> if no results
	 */
	public static ClanMate getClanMate(String... names) {
		if (isInClan()) {
			ClanMate[] clanMates = getClanMates();
			for (String name : names) {
				for (ClanMate clanMate : clanMates) {
					if (name.equalsIgnoreCase(clanMate.getName())) {
						return clanMate;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Gets the clanmates.
	 *
	 * @return an array instance of <code>ClanMate</code>
	 */
	public static ClanMate[] getClanMates() {
		if (isInClan()) {
			InterfaceComponent c = Interfaces.getComponent(ClanChat.WIDGET_CLAN_CHAT, ClanChat.WIDGET_CLAN_CHAT_LIST_USERS);
			if (c != null) {
				java.util.ArrayList<ClanMate> mates = new java.util.ArrayList<ClanMate>();
				for (InterfaceComponent user : c.getComponents()) {
					if (user == null || user.getComponentName().isEmpty()) {
						continue;
					}
					String name = user.getComponentName();
					int userIndex = user.getComponentIndex();
					InterfaceComponent rank = Interfaces.getComponent(ClanChat.WIDGET_CLAN_CHAT, 10);
					rank = rank.getComponent(userIndex);
					InterfaceComponent world = Interfaces.getComponent(ClanChat.WIDGET_CLAN_CHAT, 11);
					world = world.getComponent(userIndex);
					mates.add(new ClanMate(name, rank, world));
				}
				return mates.toArray(new ClanMate[mates.size()]);
			}
		}
		return new ClanMate[0];
	}

	/**
	 * Gets all the clanmates matching with any of the provided names.
	 *
	 * @param names the names to look for
	 * @return an array instance of <code>ClanMate</code>
	 */
	public static ClanMate[] getClanMates(String... names) {
		if (isInClan()) {
			java.util.ArrayList<ClanMate> clanMates = new java.util.ArrayList<ClanMate>();
			for (String name : names) {
				for (ClanMate clanMate : getClanMates()) {
					if (name.equalsIgnoreCase(clanMate.getName())) {
						clanMates.add(clanMate);
					}
				}
			}
			return clanMates.toArray(new ClanMate[clanMates.size()]);
		}
		return new ClanMate[0];
	}

	/**
	 * Checks whether the user is a member of a clan.
	 *
	 * @return <tt>true</tt> if the user is member of a clan; otherwise <tt>false</tt>
	 */
	public static boolean isInClan() {
		openTab();
		InterfaceComponent c = Interfaces.getComponent(ClanChat.WIDGET_CLAN_CHAT, ClanChat.WIDGET_CLAN_CHAT_LABEL_INFO);
		if (c != null) {
			String text = c.getText();
			// I'm not sure if components with no text are null or simply empty
			// so I've added a check for both just in case :>
			return text == null || text.isEmpty();
		}
		return false;
	}

	/**
	 * Opens the clan chat tab if not already opened.
	 */
	public static void openTab() {
		if (Game.getCurrentTab() != Game.Tabs.CLAN_CHAT) {
			Game.openTab(Game.Tabs.CLAN_CHAT);
		}
	}

	/**
	 * Sends the given message. It is not necessary to include the double slash. This does not check if the user is
	 * member of a clan.
	 *
	 * @param msg the message to send
	 */
	public static void sendMessage(String msg) {
		sendMessage(msg, false);
	}

	/**
	 * Sends the given message. It is not necessary to include the double slash. This does not check if the user is
	 * member of a clan.
	 *
	 * @param msg     the message to send
	 * @param instant if <tt>true</tt>, message will be sent instantly
	 */
	public static void sendMessage(String msg, boolean instant) {
		if (msg != null && !msg.isEmpty()) {
			FriendsChat.sendMessage("/".concat(msg), instant);
		}
	}

	public static class ClanMate extends ChannelUser {
		public ClanMate(String name, InterfaceComponent rank, InterfaceComponent world) {
			super(name, rank, world);
		}
	}
}
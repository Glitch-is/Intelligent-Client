package org.rsbot.script.wrappers;

import org.rsbot.script.methods.tabs.FriendsChat;

/**
 * Represents a user on a channel.
 */
public class ChannelUser implements User {
	private String name;

	private int worldNumber;

	private boolean isInLobby;

	private FriendsChat.ChatRank chatRank = FriendsChat.ChatRank.GUEST;

	public ChannelUser(String name, InterfaceComponent rank, InterfaceComponent world) {
		this.name = name;
		int texture = rank.getTextureID();
		for (FriendsChat.ChatRank chatRank : FriendsChat.ChatRank.values()) {
			if (chatRank.getTextureID() == texture) {
				this.chatRank = chatRank;
				break;
			}
		}
		String text = world.getText().trim();
		isInLobby = text.contains("L");
		if (text.indexOf(32) != -1) {
			text = text.substring(text.indexOf(32) + 1);
		}
		try {
			worldNumber = Integer.parseInt(text);
		} catch (NumberFormatException ignored) {
			worldNumber = -1;
		}
	}

	/**
	 * Gets this user's chat rank.
	 *
	 * @return an instance of <code>ChatRank</code>
	 */
	public FriendsChat.ChatRank getChatRank() {
		return chatRank;
	}

	public String getName() {
		return name;
	}

	public int getWorldNumber() {
		return worldNumber;
	}

	public boolean isInLobby() {
		return isInLobby;
	}
}

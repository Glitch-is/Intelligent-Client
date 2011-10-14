package org.rsbot.script.methods;

import org.rsbot.bot.BotComposite;
import org.rsbot.bot.Context;
import org.rsbot.bot.accessors.Client;
import org.rsbot.bot.concurrent.Task;
import org.rsbot.script.methods.input.Keyboard;
import org.rsbot.script.methods.tabs.*;
import org.rsbot.script.methods.ui.Bank;
import org.rsbot.script.methods.ui.Interfaces;
import org.rsbot.script.methods.ui.Lobby;
import org.rsbot.script.wrappers.Interface;
import org.rsbot.script.wrappers.InterfaceComponent;
import org.rsbot.script.wrappers.Tile;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

/**
 * Game state and GUI operations.
 */
public class Game {
	/**
	 * Different Types of Chat Modes
	 */
	public enum ChatMode {
		VIEW, ON, FRIENDS, OFF, HIDE, ALL, FILTER
	}

	/**
	 * The chat filter buttons
	 */
	public enum ChatButton {
		ALL(2, -1, 31),
		GAME(3, 30, 28, ChatMode.ALL, ChatMode.FILTER),
		PUBLIC(4, 27, 25, ChatMode.ON, ChatMode.FRIENDS, ChatMode.OFF, ChatMode.HIDE),
		PRIVATE(5, 24, 22, ChatMode.ON, ChatMode.FRIENDS, ChatMode.OFF),
		FRIENDS(7, 35, 33, ChatMode.ON, ChatMode.FRIENDS, ChatMode.OFF),
		CLAN(6, 21, 19, ChatMode.ON, ChatMode.FRIENDS, ChatMode.OFF),
		TRADE(8, 18, 16, ChatMode.ON, ChatMode.FRIENDS, ChatMode.OFF),
		ASSIST(9, 15, 13, ChatMode.ON, ChatMode.FRIENDS, ChatMode.OFF);

		private final int component;
		private final int textComponent;
		private final int selectComponent;
		private final ChatMode[] options;

		ChatButton(final int component, final int textComponent, final int selectComponent, final ChatMode... options) {
			this.component = component;
			this.textComponent = textComponent;
			this.selectComponent = selectComponent;
			this.options = options;
		}

		public boolean hasMode(final ChatMode mode) {
			if (mode == ChatMode.VIEW) {
				return true;
			}
			for (ChatMode option : options) {
				if (mode == option) {
					return true;
				}
			}
			return false;
		}

		public int idx() {
			return component;
		}

		public int selectIdx() {
			return selectComponent;
		}

		public int textIdx() {
			return textComponent;
		}
	}

	/**
	 * The game tabs
	 *
	 * @author kiko
	 */
	public enum Tabs {
		NONE(-1, "None", 0, -1),
		ATTACK(0, "Combat Styles", KeyEvent.VK_F5, 884),
		TASK(1, "Task System", 0, 1056),
		STATS(2, "Stats", 0, Skills.Interface.TAB_STATS),
		QUESTS(3, "Quest Journals", 0, Quests.WIDGET),
		INVENTORY(4, "Inventory", KeyEvent.VK_F1, Inventory.WIDGET),
		EQUIPMENT(5, "Worn Equipment", KeyEvent.VK_F2, Equipment.WIDGET),
		PRAYER(6, "Prayer List", KeyEvent.VK_F3, Prayer.WIDGET_PRAYER),
		MAGIC(7, "Magic Spellbook", KeyEvent.VK_F4, 192),
		FRIENDS(9, "Friends List", 0, 550),
		FRIENDS_CHAT(10, "Friends Chat", 0, FriendsChat.WIDGET_FRIENDS_CHAT),
		CLAN_CHAT(11, "Clan Chat", 0, ClanChat.WIDGET_CLAN_CHAT),
		OPTIONS(12, "Options", 0, 261),
		EMOTES(13, "Emotes", 0, 464),
		MUSIC(14, "Music Player", 0, 187),
		NOTES(15, "Notes", 0, 34),
		LOGOUT(16, "Exit", 0, Game.INTERFACE_LOGOUT);

		final String description;
		final int functionKey;
		final int index;
		final int inter;

		Tabs(final int index, final String description, final int functionKey, final int inter) {
			this.description = description;
			this.functionKey = functionKey;
			this.index = index;
			this.inter = inter;
		}

		public String description() {
			return description;
		}

		public int functionKey() {
			return functionKey;
		}

		public boolean hasFunctionKey() {
			return functionKey != 0;
		}

		public int index() {
			return index;
		}

		public int interfaceID() {
			return inter;
		}
	}

	public static final int[] INDEX_LOGGED_IN = {11, 12};
	public static final int INDEX_LOGIN_SCREEN = 3;
	public static final int INDEX_LOBBY_SCREEN = 7;
	public static final int INDEX_FIXED = 746;

	public static final int CHAT_OPTION = 751;

	public static final int INTERFACE_CHAT_BOX = 137;
	public static final int INTERFACE_GAME_SCREEN = 548;
	public static final int INTERFACE_LEVEL_UP = 740;
	public static final int INTERFACE_LOGOUT = 182;
	public static final int INTERFACE_LOGOUT_LOBBY = 1;
	public static final int INTERFACE_LOGOUT_COMPLETE = 6;
	public static final int INTERFACE_LOGOUT_BUTTON_FIXED = 181;
	public static final int INTERFACE_LOGOUT_BUTTON_RESIZED = 172;
	public static final int INTERFACE_WELCOME_SCREEN = 907;
	public static final int INTERFACE_WELCOME_SCREEN_CHILD = 150;
	public static final int INTERFACE_WELCOME_SCREEN_PLAY = 18;

	public static final int INTERFACE_HP_ORB = 748;
	public static final int INTERFACE_PRAYER_ORB = 749;

	public static final int[] INTERFACE_TALKS = {211, 241, 251, 101, 242, 102, 161, 249, 243, 64, 65, 244, 255, 249, 230, 372, 421};
	public static final int[] INTERFACE_OPTIONS = {230, 228};


	/**
	 * Closes the currently open tab if in re-sizable mode.
	 */
	public static void closeTab() {
		final Tabs tab = getCurrentTab();
		if (isFixed() || tab == Tabs.LOGOUT) {
			return;
		}
		final BotComposite botComposite = Context.get().composite;
		final org.rsbot.bot.accessors.RSInterface iTab = botComposite.getGameGUI().getTab(tab);
		if (iTab != null) {
			Interfaces.getComponent(iTab.getID()).click(true);
		}
	}

	/**
	 * Turns accept aid off if it isn't already.
	 *
	 * @return <tt>true</tt> if the setting was clicked; otherwise <tt>false</tt>.
	 */
	public static boolean disableAid() {
		if (Settings.get(427) == 1 && openTab(Tabs.OPTIONS)) {
			final InterfaceComponent toggle = Interfaces.getComponent(Tabs.OPTIONS.interfaceID(), 7);
			return toggle != null && toggle.click(true);
		}
		return false;
	}

	/**
	 * Gets the x coordinate of the loaded map area (far west).
	 *
	 * @return The region base x.
	 */
	public static int getBaseX() {
		return Context.get().client.getBaseX();
	}

	/**
	 * Gets the y coordinate of the loaded map area (far south).
	 *
	 * @return The region base y.
	 */
	public static int getBaseY() {
		return Context.get().client.getBaseY();
	}

	/**
	 * Gets the game state.
	 *
	 * @return The game state.
	 */
	public static int getClientState() {
		return Context.get().client.getLoginIndex();
	}

	/**
	 * Gets a color corresponding to x and y coordinates from the current game screen.
	 *
	 * @param x The x coordinate at which to get the color.
	 * @param y The y coordinate at which to get the color.
	 * @return Color
	 * @see java.awt.color
	 */
	public static Color getColorAtPoint(final int x, final int y) {
		final BufferedImage image = Environment.takeScreenshot(false);
		return new Color(image.getRGB(x, y));
	}

	/**
	 * Access the last message spoken by a player.
	 *
	 * @return The last message spoken by a player or "" if none.
	 */
	public static String getLastMessage() {
		final Interface chatBox = Interfaces.get(INTERFACE_CHAT_BOX);
		for (int i = 279; i >= 180; i--) {// Valid text is from 180 to 279, was 58-157
			final String text = chatBox.getComponent(i).getText();
			if (!text.isEmpty() && text.contains("<")) {
				return text;
			}
		}
		return "";
	}

	/**
	 * Gets the (x, y) coordinate pair of the south-western tile at the base of
	 * the loaded map area.
	 *
	 * @return The region base tile.
	 */
	public static Tile getMapBase() {
		final Client client = Context.get().client;
		return new Tile(client.getBaseX(), client.getBaseY());
	}

	/**
	 * Gets the plane we are currently on. Typically 0 (ground level), but will
	 * increase when going up ladders. You cannot be on a negative plane. Most
	 * dungeons/basements are on plane 0 elsewhere on the world map.
	 *
	 * @return The current plane.
	 */
	public static int getPlane() {
		return Context.get().client.getPlane();
	}

	/**
	 * Gets the currently open tab.
	 *
	 * @return The currently open tab.
	 */
	public static Tabs getCurrentTab() {
		for (Tabs t : Tabs.values()) {
			final org.rsbot.bot.accessors.RSInterface tab = Context.get().composite.getGameGUI().getTab(t);
			if (tab != null && tab.getTextureID() != -1) {
				return t;
			}
		}
		final Interface logout = Interfaces.get(INTERFACE_LOGOUT);
		return logout != null && logout.isValid() ? Tabs.LOGOUT : Tabs.NONE;
	}

	/**
	 * Returns the valid chat component.
	 *
	 * @return <tt>InterfaceComponent</tt> of the current valid talk interface; otherwise null.
	 * @see #INTERFACE_TALKS
	 */
	public static InterfaceComponent getTalkInterface() {
		for (final int talk : INTERFACE_TALKS) {
			final InterfaceComponent child = Interfaces.getComponent(talk, 0);
			if (child.isValid()) {
				return child;
			}
		}
		return null;
	}

	/**
	 * Gets the canvas height.
	 *
	 * @return The canvas' width.
	 */
	public static int getWidth() {
		return Context.get().bot.getCanvas().getWidth();
	}

	/**
	 * Gets the canvas height.
	 *
	 * @return The canvas' height.
	 */
	public static int getHeight() {
		return Context.get().bot.getCanvas().getHeight();
	}

	public static boolean isButtonSelected(final ChatButton button) {
		return Interfaces.getComponent(CHAT_OPTION, button.selectIdx()).getTextureID() == 1022;
	}

	/**
	 * Determines whether or not the client is currently in the fixed display mode.
	 *
	 * @return <tt>true</tt> if in fixed mode; otherwise <tt>false</tt>.
	 */
	public static boolean isFixed() {
		return Context.get().client.getGUIRSInterfaceIndex() != INDEX_FIXED;
	}

	/**
	 * Determines whether or not the client is currently logged in to an account.
	 *
	 * @return <tt>true</tt> if logged in; otherwise <tt>false</tt>.
	 */
	public static boolean isLoggedIn() {
		Context context;
		try {
			context = Context.get();
		} catch (RuntimeException e) {
			return false;
		}
		final org.rsbot.bot.accessors.Client client = context.client;
		if (client == null) {
			return true;
		}
		final int index = client.getLoginIndex();
		return index == 11 || index == 12;
	}

	/**
	 * Determines whether or not the client is showing the login screen.
	 *
	 * @return <tt>true</tt> if the client is showing the login screen;
	 *         otherwise <tt>false</tt>.
	 */
	public static boolean isLoginScreen() {
		return Context.get().client.getLoginIndex() == INDEX_LOGIN_SCREEN;
	}

	/**
	 * Determines whether or not the welcome screen is open.
	 *
	 * @return <tt>true</tt> if the client is showing the welcome screen;
	 *         otherwise <tt>false</tt>.
	 */
	public static boolean isWelcomeScreen() {
		return Interfaces.getComponent(INTERFACE_WELCOME_SCREEN, INTERFACE_WELCOME_SCREEN_CHILD).getAbsLocation().y > 2;
	}

	/**
	 * Closes the bank if it is open and logs out.
	 *
	 * @param lobby <tt>true</tt> if player should be logged out to the lobby
	 * @return <tt>true</tt> if the player was logged out.
	 */
	public static boolean logout(final boolean lobby) {
		if (Bank.isOpen()) {
			if (Bank.close()) {
				Task.sleep(Task.random(200, 400));
			}
		}
		if (Bank.isOpen()) {
			return false;
		}
		final Client client = Context.get().client;
		if (client.isSpellSelected() || Inventory.isItemSelected()) {
			final Tabs currentTab = Game.getCurrentTab();
			int randomTab = Task.random(1, 6);
			while (randomTab == currentTab.index()) {
				randomTab = Task.random(1, 6);
			}
			if (Game.openTab(Tabs.values()[randomTab])) {
				Task.sleep(Task.random(400, 800));
			}
		}
		if (client.isSpellSelected() || Inventory.isItemSelected()) {
			return false;
		}
		if (getCurrentTab() != Tabs.LOGOUT) {
			final int idx = client.getGUIRSInterfaceIndex();
			InterfaceComponent exitComponent = Interfaces.getComponent(idx, isFixed() ? 181 : 173);
			if (exitComponent == null || !exitComponent.click(true)) {
				return false;
			}
			long time = System.currentTimeMillis();
			while (getCurrentTab() != Tabs.LOGOUT) {
				if (System.currentTimeMillis() - time > 2000) {
					break;
				}
				Task.sleep(Task.random(50, 100));
			}
		}
		InterfaceComponent exitToComponent = Interfaces.getComponent(INTERFACE_LOGOUT, lobby ? 2 : 13);
		if (exitToComponent != null && exitToComponent.click(true)) {
			Task.sleep(Task.random(1500, 2000));
		}
		return !isLoggedIn();
	}

	/**
	 * Click the specified chat button.
	 *
	 * @param button One of ChatButton
	 * @param left   true to left click, false for right click.
	 * @return <tt>true</tt> if the button was successfully clicked.
	 */
	public static boolean mouseChatButton(final ChatButton button, final boolean left) {
		if (button == null || (left && isButtonSelected(button))) {
			return false;
		}
		final InterfaceComponent chatButton = Interfaces.getComponent(CHAT_OPTION, button.idx());
		return chatButton.isValid() && chatButton.click(left);
	}

	/**
	 * Opens the specified game tab.
	 *
	 * @param tab The tab to open.
	 * @return <tt>true</tt> if tab successfully selected; otherwise <tt>false</tt>.
	 * @see #openTab(org.rsbot.script.methods.Game.Tabs tab, boolean functionKey)
	 */
	public static boolean openTab(final Tabs tab) {
		return openTab(tab, false);
	}

	/**
	 * Opens the specified game tab.
	 *
	 * @param tab         The tab to open, functionKey if wanting to use function keys to switch.
	 * @param functionKey Use function keys to open the tab or not.
	 * @return <tt>true</tt> if tab successfully selected; otherwise <tt>false</tt>.
	 */
	public static boolean openTab(final Tabs tab, final boolean functionKey) {
		if (tab == Tabs.NONE) {
			return false;
		}

		if (getCurrentTab() == tab) {
			return true;
		}

		if (functionKey && tab.hasFunctionKey()) {
			Keyboard.pressKey((char) tab.functionKey());
			Task.sleep(Task.random(60, 200));
			Keyboard.releaseKey((char) tab.functionKey());
		} else {
			final org.rsbot.bot.accessors.RSInterface iTab = Context.get().composite.getGameGUI().getTab(tab);
			if (iTab == null || !Interfaces.getComponent(iTab.getID()).click(true)) {
				return false;
			}
		}

		boolean opened = false;
		for (int i = 0; i < 4; i++) {
			if (!opened) {
				if (getCurrentTab() == tab) {
					opened = true;
					i--;
					continue;
				}
			} else if (Interfaces.get(tab.interfaceID()).isValid()) {
				return true;
			}
			Task.sleep(Task.random(100, 200));
		}
		return getCurrentTab() == tab;
	}

	/**
	 * Sets the specified chat mode
	 *
	 * @param option one of ChatButton
	 * @param mode   one of ChatMode
	 * @return <tt>true</tt> if item was clicked correctly; otherwise <tt>false</tt>.
	 */
	public static boolean setChatOption(final ChatButton option, final ChatMode mode) {
		if (option == null || !option.hasMode(mode)) {
			return false;
		}
		if (mode == ChatMode.VIEW) {
			return mouseChatButton(option, true);
		}
		final InterfaceComponent chat = Interfaces.getComponent(CHAT_OPTION, option.textIdx());
		if (chat != null) {
			String setting = chat.getText();
			setting = setting.substring(setting.indexOf(">") + 1);
			if (setting.toUpperCase().equals(mode.toString())) {
				return false;
			}
		}
		mouseChatButton(option, false);
		return Menu.click(mode.toString());
	}

	/**
	 * Switches to a given world.
	 *
	 * @param world the world to switch to, must be valid.
	 * @return If worlds were switched.
	 */
	public static boolean switchWorld(final int world) {
		if (Game.isLoggedIn()) {
			Game.logout(true);
			for (int i = 0; i < 50; i++) {
				Task.sleep(100);
				if (Interfaces.get(906).isValid() && getClientState() == INDEX_LOBBY_SCREEN) {
					break;
				}
			}
		}

		if (!Interfaces.get(906).isValid()) {
			return false;
		}
		if (Lobby.switchWorlds(world)) {
			Task.sleep(Task.random(1000, 2000));
			return true;
		}
		return false;
	}

	/**
	 * Gets Current World
	 *
	 * @return current or selected world
	 */
	public static int getCurrentWorld() {
		int world = 0;
		if (Game.isLoggedIn()) {
			if (Game.getCurrentTab() != Tabs.FRIENDS) {
				Game.openTab(Tabs.FRIENDS);
			}
			world = Integer.parseInt(Interfaces.getComponent(550, 19).getText().replaceAll("Friends List<br>RuneScape ", ""));
		} else if (Game.getClientState() == Game.INDEX_LOBBY_SCREEN) {
			world = Lobby.getSelectedWorld();
		}
		return world;
	}
}

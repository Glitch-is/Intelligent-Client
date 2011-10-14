package org.rsbot.script.randoms;

import org.rsbot.script.Detector;
import org.rsbot.script.methods.Account;
import org.rsbot.script.methods.Game;
import org.rsbot.script.methods.input.Keyboard;
import org.rsbot.script.methods.input.Mouse;
import org.rsbot.script.methods.ui.Interfaces;
import org.rsbot.script.methods.ui.Lobby;
import org.rsbot.script.wrappers.InterfaceComponent;
import org.rsbot.ui.AccountManager;

import java.awt.*;

/**
 * A task to login to the game of Runescape.
 *
 * @author Timer
 */
public class Login extends Detector {
	public static final int INTERFACE_LOGIN_SCREEN = 596;
	public static final int INTERFACE_LOGIN_SCREEN_ENTER_GAME = 60;
	public static final int INTERFACE_LOGIN_SCREEN_USERNAME_TEXT = 73;
	public static final int INTERFACE_LOGIN_SCREEN_PASSWORD_TEXT = 79;
	public static final int INTERFACE_LOGIN_SCREEN_ALERT_TEXT = 14;
	public static final int INTERFACE_LOGIN_SCREEN_ALERT_BACK = 68;
	public static final int INTERFACE_GRAPHICS_NOTICE = 976;
	public static final int INTERFACE_GRAPHICS_LEAVE_ALONE = 6;
	public static final int INTERFACE_LOBBY_HIGH_RISK_WORLD_TEXT = 98;

	public static int world = -1;

	public static final int INTERFACE_LOBBY_HIGH_RISK_WORLD_LOGIN_BUTTON = 104;

	private enum LoginEvent {
		NO_REPLY("no reply from login server", 1500),
		GAME_UPDATE("update", -1),
		BANNED("disable", -1),
		SERVER_LAG_1("your account has not logged out", 5000),
		INVALID_LOGIN_1("invalid", 0),
		INVALID_LOGIN_2("incorrect", 0),
		ERROR("error connecting", 0),
		LIMIT_1("login limit exceeded", 0),

		BAD_WORLD("total skill level of", -1),
		LIMIT_2("login limit exceeded", 0),
		SERVER_LAG_2("your account has not logged out", 5000),
		NON_MEMBER("member", -1),

		WAIT_1("world", 1500),
		WAIT_2("performing login", 1500);

		public final String message;
		public final int sleep;

		LoginEvent(final String message, final int sleep) {
			this.message = message;
			this.sleep = sleep;
		}
	}

	@Override
	protected int loop() {
		if (Lobby.isValid()) {
			if (Lobby.getCurrentTab() != Lobby.LobbyTab.PLAYER_INFO) {
				Lobby.openTab(Lobby.LobbyTab.PLAYER_INFO);
				return random(500, 800);
			}
			if (world != -1 && Lobby.getSelectedWorld() != world) {
				Lobby.switchWorlds(world, false);
				return random(500, 800);
			}
			if (Lobby.clickPlay()) {
				for (int i = 0; i < 4 && Game.getClientState() == 6; i++) {
					sleep(500);
				}
				final String returnText = Interfaces.getComponent(Lobby.WIDGET_LOBBY, Lobby.WIDGET_LOBBY_COMPONENT_ALERT_BOX_LABEL_TEXT).getText().toLowerCase().trim();
				if (Interfaces.getComponent(Lobby.WIDGET_LOBBY, Lobby.WIDGET_LOBBY_COMPONENT_ALERT_BOX_BUTTON_CLOSE).isValid()) {
					Interfaces.getComponent(Lobby.WIDGET_LOBBY, Lobby.WIDGET_LOBBY_COMPONENT_ALERT_BOX_BUTTON_CLOSE).click(true);
				}
				for (final LoginEvent event : LoginEvent.values()) {
					if (returnText.contains(event.message.toLowerCase())) {
						log("Handling login event: " + event.name());
						if (event.sleep == -1) {
							container.get().setRunning(false);
						}
						return event.sleep;
					}
				}
			}
		}
		if (Game.getClientState() == Game.INDEX_LOGIN_SCREEN) {
			if (Interfaces.getComponent(INTERFACE_GRAPHICS_NOTICE, INTERFACE_GRAPHICS_LEAVE_ALONE).isValid()) {
				Interfaces.getComponent(INTERFACE_GRAPHICS_NOTICE, INTERFACE_GRAPHICS_LEAVE_ALONE).click(true);
				return random(500, 600);
			}
			final String returnText = Interfaces.getComponent(INTERFACE_LOGIN_SCREEN, INTERFACE_LOGIN_SCREEN_ALERT_TEXT).getText().toLowerCase().trim();
			if (Interfaces.getComponent(INTERFACE_LOGIN_SCREEN, INTERFACE_LOGIN_SCREEN_ALERT_BACK).isValid()) {
				Interfaces.getComponent(INTERFACE_LOGIN_SCREEN, INTERFACE_LOGIN_SCREEN_ALERT_BACK).click(true);
			}
			if (Interfaces.getComponent(Lobby.WIDGET_LOBBY, INTERFACE_LOBBY_HIGH_RISK_WORLD_TEXT).getText().toLowerCase().trim().contains("high-risk wilderness world")) {
				return Interfaces.getComponent(Lobby.WIDGET_LOBBY, INTERFACE_LOBBY_HIGH_RISK_WORLD_LOGIN_BUTTON).click(true) ? 500 : 0;
			}
			for (final LoginEvent event : LoginEvent.values()) {
				if (returnText.contains(event.message.toLowerCase())) {
					log("Handling login event: " + event.name());
					if (event.sleep == -1) {
						container.get().setRunning(false);
					}
					return event.sleep;
				}
			}
			if (isUsernameCorrect() && isPasswordValid()) {
				attemptLogin();
				return random(1200, 1500);
			}
			if (!isUsernameCorrect()) {
				final String username = Account.getName().toLowerCase().trim();
				atLoginInterface(Interfaces.getComponent(INTERFACE_LOGIN_SCREEN, INTERFACE_LOGIN_SCREEN_USERNAME_TEXT));
				sleep(random(500, 700));
				final int textLength = Interfaces.getComponent(INTERFACE_LOGIN_SCREEN, INTERFACE_LOGIN_SCREEN_USERNAME_TEXT).getText().length();
				if (textLength > 0) {
					for (int i = 0; i <= textLength + random(1, 5); i++) {
						Keyboard.sendText("\b", false);
						if (random(0, 2) == 1) {
							sleep(random(25, 100));
						}
					}
					return random(500, 600);
				}
				Keyboard.sendText(username, false);
				return random(500, 600);
			}
			if (isUsernameCorrect() && !isPasswordValid()) {
				atLoginInterface(Interfaces.getComponent(INTERFACE_LOGIN_SCREEN, INTERFACE_LOGIN_SCREEN_PASSWORD_TEXT));
				sleep(random(500, 700));
				final int textLength = Interfaces.getComponent(INTERFACE_LOGIN_SCREEN, INTERFACE_LOGIN_SCREEN_PASSWORD_TEXT).getText().length();
				if (textLength > 0) {
					for (int i = 0; i <= textLength + random(1, 5); i++) {
						Keyboard.sendText("\b", false);
						if (random(0, 2) == 1) {
							sleep(random(25, 100));
						}
					}
					return random(500, 600);
				}
				String passWord = AccountManager.getPassword(Account.getName());
				Keyboard.sendText(passWord, false);
				return random(500, 600);
			}
		}
		return Game.isLoggedIn() ? -1 : random(100, 500);
	}

	private boolean atLoginInterface(final InterfaceComponent i) {
		if (!i.isValid()) {
			return false;
		}
		final Rectangle pos = i.getBoundingRect();
		if (pos.x == -1 || pos.y == -1 || pos.width == -1 || pos.height == -1) {
			return false;
		}
		final int dy = (int) (pos.getHeight() - 4) / 2;
		final int maxRandomX = (int) (pos.getMaxX() - pos.getCenterX());
		final int midx = (int) pos.getCenterX();
		final int midy = (int) (pos.getMinY() + pos.getHeight() / 2);
		if (i.getIndex() == INTERFACE_LOGIN_SCREEN_PASSWORD_TEXT) {
			Mouse.click(minX(i), midy + random(-dy, dy), true);
		} else {
			Mouse.click(midx + random(1, maxRandomX), midy + random(-dy, dy), true);
		}
		return true;
	}

	private int minX(final InterfaceComponent a) {
		int x = 0;
		final Rectangle pos = a.getBoundingRect();
		final int dx = (int) (pos.getWidth() - 4) / 2;
		final int midx = (int) (pos.getMinX() + pos.getWidth() / 2);
		if (pos.x == -1 || pos.y == -1 || pos.width == -1 || pos.height == -1) {
			return 0;
		}
		for (int i = 0; i < Interfaces.getComponent(INTERFACE_LOGIN_SCREEN, INTERFACE_LOGIN_SCREEN_PASSWORD_TEXT).getText().length(); i++) {
			x += 11;
		}
		if (x > 44) {
			return (int) (pos.getMinX() + x + 15);
		} else {
			return midx + random(-dx, dx);
		}
	}

	private void attemptLogin() {
		if (random(0, 2) == 0) {
			Keyboard.sendKey('\n');
		} else {
			Interfaces.getComponent(INTERFACE_LOGIN_SCREEN, INTERFACE_LOGIN_SCREEN_ENTER_GAME).click(true);
		}
	}

	private boolean isUsernameCorrect() {
		final String userName = Account.getName().toLowerCase().trim();
		return Interfaces.getComponent(INTERFACE_LOGIN_SCREEN, INTERFACE_LOGIN_SCREEN_USERNAME_TEXT).getText().toLowerCase().equalsIgnoreCase(userName);
	}

	private boolean isPasswordValid() {
		String passWord = AccountManager.getPassword(Account.getName());
		return Interfaces.getComponent(INTERFACE_LOGIN_SCREEN, INTERFACE_LOGIN_SCREEN_PASSWORD_TEXT).getText().length() == (passWord == null ? 0 : passWord.length());
	}

	@Override
	public boolean isDetected() {
		final int idx = Game.getClientState();
		return (idx == Game.INDEX_LOGIN_SCREEN || idx == Game.INDEX_LOBBY_SCREEN) && Account.getName() != null;
	}
}

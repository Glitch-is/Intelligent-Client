package org.rsbot.bot.input;

import org.rsbot.bot.Bot;
import org.rsbot.bot.accessors.Client;
import org.rsbot.bot.accessors.input.Canvas;
import org.rsbot.bot.accessors.input.Mouse;
import org.rsbot.bot.concurrent.Task;

import java.applet.Applet;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * A handler for input.
 *
 * @author Timer
 */
public class InputManager {
	public final MouseHandler mouseHandler = new MouseHandler(this);
	private final Bot bot;
	private int mouseSpeed;

	private byte dragLength = 0;

	/**
	 * The side of the screen off which the mouse last moved.
	 */
	public int side = Task.random(1, 5);

	public InputManager(final Bot bot) {
		this.bot = bot;
		mouseSpeed = 12;
	}

	public int getX() {
		return getClient().getMouse().getX();
	}

	public int getY() {
		return getClient().getMouse().getY();
	}

	private boolean isOnCanvas(final int x, final int y) {
		return x > 0 && x < bot.getCanvas().getWidth() && y > 0 && y < bot.getCanvas().getHeight();
	}

	private void gainFocus() {
		final Canvas cw = getCanvasWrapper();
		if (!cw.hasFocus()) {
			cw.setFocused(true);
		}
	}

	private void loseFocus() {
		final Canvas cw = getCanvasWrapper();
		if (cw.hasFocus()) {
			cw.setFocused(false);
		}
	}

	public void hopMouse(final int x, final int y) {
		moveMouse(x, y);
	}

	private void moveMouse(final int x, final int y) {
		// Firstly invoke drag events
		if (getClient().getMouse().isPressed()) {
			final MouseEvent me = new MouseEvent(getTarget(), MouseEvent.MOUSE_DRAGGED, System.currentTimeMillis(), 0, x, y, 0, false);
			getClient().getMouse().sendEvent(me);
			if ((dragLength & 0xFF) != 0xFF) {
				dragLength++;
			}
		}

		if (!getClient().getMouse().isPresent()) {
			if (isOnCanvas(x, y)) { // Entered
				final MouseEvent me = new MouseEvent(getTarget(), MouseEvent.MOUSE_ENTERED, System.currentTimeMillis(), 0, x, y, 0, false);
				getClient().getMouse().sendEvent(me);
			}
		} else if (!isOnCanvas(x, y)) {
			final MouseEvent me = new MouseEvent(getTarget(), MouseEvent.MOUSE_EXITED, System.currentTimeMillis(), 0, x, y, 0, false);
			getClient().getMouse().sendEvent(me);
			final int w = bot.getCanvas().getWidth(), h = bot.getCanvas().getHeight(), d = 50;
			if (x < d) {
				if (y < d) {
					side = 4; // top
				} else if (y > h + d) {
					side = 2; // bottom
				} else {
					side = 1; // left
				}
			} else if (x > w) {
				side = 3; // right
			} else {
				side = Task.random(1, 5);
			}
		} else if (!getClient().getMouse().isPressed()) {
			final MouseEvent me = new MouseEvent(getTarget(), MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, x, y, 0, false);
			getClient().getMouse().sendEvent(me);
		}
	}

	/**
	 * Moves the mouse to the specified point at a certain speed.
	 *
	 * @param x       the x value
	 * @param y       the y value
	 * @param randomX x-axis randomness (added to x)
	 * @param randomY y-axis randomness (added to y)
	 */
	public void moveMouse(final int x, final int y, final int randomX, final int randomY) {
		int thisX = getX(), thisY = getY();
		if (!isOnCanvas(thisX, thisY)) {
			// on which side of canvas should it enter
			switch (side) {
				case 1:
					thisX = -1;
					thisY = Task.random(0, bot.getCanvas().getHeight());
					break;
				case 2:
					thisX = Task.random(0, bot.getCanvas().getWidth());
					thisY = bot.getCanvas().getHeight() + 1;
					break;
				case 3:
					thisX = bot.getCanvas().getWidth() + 1;
					thisY = Task.random(0, bot.getCanvas().getHeight());
					break;
				case 4:
					thisX = Task.random(0, bot.getCanvas().getWidth());
					thisY = -1;
					break;
			}
		}
		windMouse(thisX, thisY, Task.random(x, x + randomX), Task.random(y, y + randomY));
	}

	private void pressMouse(final int x, final int y, final boolean left) {
		if (getClient().getMouse().isPressed() || !getClient().getMouse().isPresent()) {
			return;
		}
		final MouseEvent me = new MouseEvent(getTarget(), MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(), 0, x, y, 1, false, left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3);
		getClient().getMouse().sendEvent(me);
	}

	private void releaseMouse(final int x, final int y, final boolean leftClick) {
		if (!getClient().getMouse().isPressed()) {
			return;
		}
		getClient().getMouse().sendEvent(
				new MouseEvent(getTarget(), MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(), 0, x, y, 1, false, leftClick ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3)
		);

		if ((dragLength & 0xFF) <= 3) {
			getClient().getMouse().sendEvent(
					new MouseEvent(getTarget(), MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, x, y, 1, false, leftClick ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3)
			);
		}
		dragLength = 0;
	}

	public void clickMouse(final boolean left) {
		if (!getClient().getMouse().isPresent()) {
			return; // Can't click off the canvas
		}
		pressMouse(getX(), getY(), left);
		Task.sleep(Task.random(50, 100));
		releaseMouse(getX(), getY(), left);
	}

	/**
	 * Drag the mouse from the current position to a certain other position.
	 *
	 * @param x the x coordinate to drag to
	 * @param y the y coordinate to drag to
	 */
	public void dragMouse(final int x, final int y) {
		pressMouse(getX(), getY(), true);
		Task.sleep(Task.random(300, 500));
		windMouse(getX(), getY(), x, y);
		Task.sleep(Task.random(300, 500));
		releaseMouse(x, y, true);
	}

	public void scrollMouse(final boolean up) {
		final Mouse localMouse = getClient().getMouse();
		scrollMouse(localMouse.getX(), localMouse.getY(), 3, up);
	}

	private void scrollMouse(final int x, final int y, final int ticks, final boolean up) {
		getClient().getMouse().sendEvent(
				new MouseWheelEvent(getTarget(), 507, System.currentTimeMillis(), 0, x, y, 0, false, 0, ticks, up ? -1 : 1)
		);
	}

	public void pressKey(final char ch) {
		KeyEvent ke;
		ke = new KeyEvent(getTarget(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, ch, getKeyChar(ch));
		getClient().getKeyboard()._keyPressed(ke);
	}

	public void releaseKey(final char ch) {
		getClient().getKeyboard()._keyReleased(
				new KeyEvent(getTarget(), KeyEvent.KEY_RELEASED, System.currentTimeMillis(), InputEvent.ALT_DOWN_MASK, ch, getKeyChar(ch))
		);
	}

	public void holdKey(final int keyCode, final int ms) {
		getClient().getKeyboard()._keyPressed(
				new KeyEvent(getTarget(), KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, keyCode, (char) keyCode)
		);

		if (ms > 500) {
			getClient().getKeyboard()._keyPressed(
					new KeyEvent(getTarget(), KeyEvent.KEY_PRESSED, System.currentTimeMillis() + 500, 0, keyCode, (char) keyCode)
			);
			final int ms2 = ms - 500;
			for (int i = 37; i < ms2; i += Task.random(20, 40)) {
				getClient().getKeyboard()._keyPressed(
						new KeyEvent(getTarget(), KeyEvent.KEY_PRESSED, System.currentTimeMillis() + i + 500, 0, keyCode, (char) keyCode)
				);
			}
		}
		final int delay2 = ms + Task.random(-30, 30);
		getClient().getKeyboard()._keyReleased(
				new KeyEvent(getTarget(), KeyEvent.KEY_RELEASED, System.currentTimeMillis() + delay2, 0, keyCode, (char) keyCode)
		);
	}

	public void sendKey(final char c) {
		sendKey(c, 0);
	}

	public void sendKey(final char ch, final int delay) {
		boolean shift = false;
		int code = ch;
		if ((ch >= 'a') && (ch <= 'z')) {
			code -= 32;
		} else if ((ch >= 'A') && (ch <= 'Z')) {
			shift = true;
		}
		if ((code == KeyEvent.VK_LEFT) || (code == KeyEvent.VK_UP) || (code == KeyEvent.VK_DOWN)) {
			getClient().getKeyboard()._keyPressed(
					new KeyEvent(getTarget(), KeyEvent.KEY_PRESSED, System.currentTimeMillis() + delay, 0, code, getKeyChar(ch), KeyEvent.KEY_LOCATION_STANDARD)
			);
			final int delay2 = Task.random(50, 120) + Task.random(0, 100);
			getClient().getKeyboard()._keyReleased(
					new KeyEvent(getTarget(), KeyEvent.KEY_RELEASED, System.currentTimeMillis() + delay2, 0, code, getKeyChar(ch), KeyEvent.KEY_LOCATION_STANDARD)
			);
		} else {
			if (!shift) {
				getClient().getKeyboard()._keyPressed(
						new KeyEvent(getTarget(), KeyEvent.KEY_PRESSED, System.currentTimeMillis() + delay, 0, code, getKeyChar(ch), KeyEvent.KEY_LOCATION_STANDARD)
				);
				// Event Typed
				getClient().getKeyboard()._keyTyped(
						new KeyEvent(getTarget(), KeyEvent.KEY_TYPED, System.currentTimeMillis() + 0, 0, 0, ch, 0)
				);
				// Event Released
				final int delay2 = Task.random(50, 120) + Task.random(0, 100);
				getClient().getKeyboard()._keyReleased(
						new KeyEvent(getTarget(), KeyEvent.KEY_RELEASED, System.currentTimeMillis() + delay2, 0, code, getKeyChar(ch), KeyEvent.KEY_LOCATION_STANDARD)
				);
			} else {
				// Event Pressed for shift key
				final int s1 = Task.random(25, 60) + Task.random(0, 50);
				getClient().getKeyboard()._keyPressed(
						new KeyEvent(getTarget(), KeyEvent.KEY_PRESSED, System.currentTimeMillis() + s1, InputEvent.SHIFT_DOWN_MASK, KeyEvent.VK_SHIFT, (char) KeyEvent.VK_UNDEFINED, KeyEvent.KEY_LOCATION_LEFT)
				);

				// Event Pressed for char to send
				getClient().getKeyboard()._keyPressed(
						new KeyEvent(getTarget(), KeyEvent.KEY_PRESSED, System.currentTimeMillis() + delay, InputEvent.SHIFT_DOWN_MASK, code, getKeyChar(ch), KeyEvent.KEY_LOCATION_STANDARD)
				);
				// Event Typed for char to send
				getClient().getKeyboard()._keyTyped(
						new KeyEvent(getTarget(), KeyEvent.KEY_TYPED, System.currentTimeMillis() + 0, InputEvent.SHIFT_DOWN_MASK, 0, ch, 0)
				);
				// Event Released for char to send
				final int delay2 = Task.random(50, 120) + Task.random(0, 100);
				getClient().getKeyboard()._keyReleased(
						new KeyEvent(getTarget(), KeyEvent.KEY_RELEASED, System.currentTimeMillis() + delay2, InputEvent.SHIFT_DOWN_MASK, code, getKeyChar(ch), KeyEvent.KEY_LOCATION_STANDARD)
				);

				// Event Released for shift key
				final int s2 = Task.random(25, 60) + Task.random(0, 50);
				getClient().getKeyboard()._keyReleased(
						new KeyEvent(getTarget(), KeyEvent.KEY_RELEASED, System.currentTimeMillis() + s2, InputEvent.SHIFT_DOWN_MASK, KeyEvent.VK_SHIFT, (char) KeyEvent.VK_UNDEFINED, KeyEvent.KEY_LOCATION_LEFT)
				);
			}
		}
	}

	public void sendKeys(final String text, final boolean pressEnter) {
		sendKeys(text, pressEnter, 100, 200);
	}

	public void sendKeys(final String text, final boolean pressEnter, final int delay) {
		sendKeys(text, pressEnter, delay, delay);
	}

	public void sendKeys(final String text, final boolean pressEnter, final int minDelay, final int maxDelay) {
		final char[] chs = text.toCharArray();
		for (final char element : chs) {
			sendKey(element, Task.random(minDelay, maxDelay));
			Task.sleep(Task.random(minDelay, maxDelay));
		}
		if (pressEnter) {
			sendKey((char) KeyEvent.VK_ENTER, Task.random(minDelay, maxDelay));
		}
	}

	public void sendKeysInstant(final String text, final boolean pressEnter) {
		for (final char c : text.toCharArray()) {
			sendKey(c, 0);
		}
		if (pressEnter) {
			sendKey((char) KeyEvent.VK_ENTER, 0);
		}
	}

	/**
	 * Moves the mouse from a certain point to another, with specified speed.
	 *
	 * @param curX    the x value to move from
	 * @param curY    the y value to move from
	 * @param targetX the x value to move to
	 * @param targetY the y value to move to
	 */
	public void windMouse(final int curX, final int curY, final int targetX, final int targetY) {
		mouseHandler.moveMouse(mouseSpeed, curX, curY, targetX, targetY, 0, 0);
	}

	public void setSpeed(final int speed) {
		mouseSpeed = speed;
	}

	public int getSpeed() {
		return mouseSpeed;
	}

	private Applet getTarget() {
		return (Applet) getClient();
	}

	private Canvas getCanvasWrapper() {
		return (Canvas) getTarget().getComponent(0);
	}

	private Client getClient() {
		return bot.composite.client;
	}

	public static char getKeyChar(final char c) {
		if ((c >= 36) && (c <= 40)) {
			return KeyEvent.VK_UNDEFINED;
		} else {
			return c;
		}
	}
}

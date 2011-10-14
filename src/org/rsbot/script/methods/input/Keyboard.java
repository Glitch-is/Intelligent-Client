package org.rsbot.script.methods.input;

import org.rsbot.bot.Context;
import org.rsbot.bot.input.InputManager;

/**
 * A utility to manipulate the use of the Keyboard.
 *
 * @author Timer
 */
public class Keyboard {
	/**
	 * Presses a key.
	 *
	 * @param ch The <code>char</code> to press.
	 */
	public static void pressKey(final char ch) {
		Context.get().composite.inputManager.pressKey(ch);
	}

	/**
	 * Releases a key.
	 *
	 * @param ch The <code>char</code> to release.
	 */
	public static void releaseKey(final char ch) {
		Context.get().composite.inputManager.releaseKey(ch);
	}

	/**
	 * Holds a key down for x milliseconds.
	 *
	 * @param ch The <code>char</code> to hold.
	 * @param ms The milliseconds for the char to remain pressed.
	 */
	public static void holdKey(final char ch, final int ms) {
		Context.get().composite.inputManager.holdKey(InputManager.getKeyChar(ch), ms);
	}

	/**
	 * Sends a key.
	 *
	 * @param ch The <code>char</code> to send.
	 */
	public static void sendKey(final char ch) {
		Context.get().composite.inputManager.sendKey(ch);
	}

	/**
	 * Sends a key.
	 *
	 * @param ch    The <code>char</code> to send.
	 * @param delay The delay to hold it down for.
	 */
	public static void sendKey(final char ch, final int delay) {
		Context.get().composite.inputManager.sendKey(ch, delay);
	}

	/**
	 * Sends text into game.
	 *
	 * @param text       The text to send.
	 * @param pressEnter Press enter or not.
	 */
	public static void sendText(final String text, final boolean pressEnter) {
		Context.get().composite.inputManager.sendKeys(text, pressEnter);
	}

	/**
	 * Sends text into game.
	 *
	 * @param text       The text to send.
	 * @param pressEnter Press enter or not.
	 * @param delay      The delay to hold down keys for.
	 */
	public static void sendText(final String text, final boolean pressEnter, final int delay) {
		Context.get().composite.inputManager.sendKeys(text, pressEnter, delay);
	}

	/**
	 * Sends text into game.
	 *
	 * @param text       The text to send.
	 * @param pressEnter Press enter or not.
	 * @param minDelay   The min wait per key.
	 * @param maxDelay   The max wait per key.
	 */
	public static void sendText(final String text, final boolean pressEnter, final int minDelay, final int maxDelay) {
		Context.get().composite.inputManager.sendKeys(text, pressEnter, minDelay, maxDelay);
	}

	/**
	 * Sends this text instantly.
	 *
	 * @param text       The text.
	 * @param pressEnter Press enter or not.
	 */
	public static void sendTextInstant(final String text, final boolean pressEnter) {
		Context.get().composite.inputManager.sendKeysInstant(text, pressEnter);
	}
}
package org.rsbot.script.methods;

import org.rsbot.Configuration;
import org.rsbot.bot.Bot;
import org.rsbot.bot.Context;
import org.rsbot.util.ScreenshotUtil;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Bot environment related operations.
 *
 * @author Timer
 */
public class Environment {
	public static final int INPUT_MOUSE = 1, INPUT_KEYBOARD = 2;

	/**
	 * Controls the available means of user input when user input is disabled.
	 * <p/>
	 * <br />
	 * Disable all: <code>setUserInput(0);</code> <br />
	 * Enable keyboard only:
	 * <code>setUserInput(Environment.INPUT_KEYBOARD);</code> <br />
	 * Enable mouse & keyboard:
	 * <code>setUserInput(Environment.INPUT_MOUSE | Environment.INPUT_KEYBOARD);</code>
	 *
	 * @param mask flags indicating which types of input to allow
	 */
	public static void setUserInput(final int mask) {
		final Bot bot = Context.get().bot;
		bot.composite.concurrentDispatch.updateInput(bot, mask);
	}

	/**
	 * Returns the current user input flags.
	 *
	 * @return The input mask (INPUT_MOUSE | INPUT_KEYBOARD).
	 */
	public static int getUserInput() {
		return Context.get().bot.composite.inputFlags;
	}

	/**
	 * Gets the storage directory for information.
	 *
	 * @return The <code>File</code> instance of the directory.
	 */
	public static File getStorageDirectory() {
		return new File(Configuration.Paths.getStorageDirectory());
	}

	/**
	 * Determines if the current script is running or is stopped.
	 *
	 * @return <tt>true</tt> if running; otherwise <tt>false</tt>.
	 */
	public static boolean isRunning() {
		return Context.get().composite.concurrentDispatch.isRunning();
	}

	/**
	 * Determines if the current script is paused.
	 *
	 * @return <tt>true</tt> if paused; otherwise <tt>false</tt>.
	 */
	public static boolean isPaused() {
		return Context.get().composite.concurrentDispatch.isPaused();
	}

	/**
	 * Takes and saves a screenshot.
	 *
	 * @param hideUsername <tt>true</tt> to cover the player's username; otherwise
	 *                     <tt>false</tt>
	 */
	public static void saveScreenshot(final boolean hideUsername) {
		ScreenshotUtil.saveScreenshot(Context.get().bot, hideUsername);
	}

	public static void saveScreenshot(final boolean hideUsername, final String filename) {
		ScreenshotUtil.saveScreenshot(Context.get().bot, hideUsername, new File(filename), "png");
	}

	/**
	 * Takes a screenshot.
	 *
	 * @param hideUsername <tt>true</tt> to cover the player's username; otherwise
	 *                     <tt>false</tt>
	 * @return The screen capture image.
	 */
	public static BufferedImage takeScreenshot(final boolean hideUsername) {
		return ScreenshotUtil.takeScreenshot(Context.get().bot, hideUsername);
	}

	/**
	 * Enables or disables painting of the client's mouse.
	 *
	 * @param paint Paint or not.
	 */
	public static void setMousePainting(final boolean paint) {
		Context.get().composite.paintMouseApplication = paint;
	}
}

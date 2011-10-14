package org.rsbot.util;

import org.rsbot.Configuration;
import org.rsbot.bot.Bot;
import org.rsbot.bot.Context;
import org.rsbot.script.methods.Game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Takes and saves screenshots of the game client.
 *
 * @author Jacmob
 */
public class ScreenshotUtil {
	private static final Logger log = Logger.getLogger(ScreenshotUtil.class.getName());
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-hhmmss");

	public static void saveScreenshot() {
		saveScreenshot(Context.get().bot);
	}

	public static void saveScreenshot(final boolean hideUsername) {
		saveScreenshot(Context.get().bot, hideUsername);
	}

	public static void saveScreenshot(final boolean hideUsername, final File file) {
		saveScreenshot(Context.get().bot, hideUsername, file);
	}

	/**
	 * Saves a screenshot of the screen to the given file, as the given type.
	 *
	 * @param hideUsername <tt>True</tt> to hide the username.
	 * @param file         The file you'd like to save in.
	 * @param type         The type you'd like to save as.
	 */
	public static void saveScreenshot(final boolean hideUsername, final File file, final String type) {
		saveScreenshot(Context.get().bot, hideUsername, file, type);
	}

	public static void saveScreenshot(final Bot bot) {
		saveImage(bot.getImage());
	}

	public static void saveScreenshot(final Bot bot, final boolean hideUsername) {
		saveImage(bot.getImage(), hideUsername);
	}

	public static void saveScreenshot(final Bot bot, final boolean hideUsername, final File file) {
		saveImage(bot.getImage(), hideUsername, file);
	}

	public static void saveScreenshot(final Bot bot, final boolean hideUsername, final File file, final String type) {
		saveImage(bot.getImage(), hideUsername, file, type);
	}

	public static void saveImage(final BufferedImage bufferedImage) {
		saveImage(bufferedImage, false);
	}

	public static void saveImage(final BufferedImage bufferedImage, final boolean hideUsername) {
		final String name = ScreenshotUtil.dateFormat.format(new Date()) + ".png";
		final File dir = new File(Configuration.Paths.getScreenshotsDirectory());
		if (dir.isDirectory() || dir.mkdirs()) {
			saveImage(bufferedImage, hideUsername, new File(dir, name));
		}
	}

	public static void saveImage(final BufferedImage bufferedImage, final boolean hideUsername, final File file) {
		saveImage(bufferedImage, hideUsername, file, "png");
	}

	/**
	 * Saves the given image to the given file, as the given type. Hides username if true.
	 *
	 * @param bufferedImage The image.
	 * @param hideUsername  <tt>True</tt> to hide the username.
	 * @param file          The file you'd like to save in.
	 * @param type          The type you'd like to save as.
	 */
	public static void saveImage(final BufferedImage bufferedImage, final boolean hideUsername, final File file, final String type) {
		try {
			BufferedImage image = bufferedImage;
			if (hideUsername) {
				image = takeScreenshot(bufferedImage, hideUsername);
			}
			ImageIO.write(image, type, file);
			log.info("Screenshot saved to: " + file.getPath());
		} catch (final Exception e) {
			log.log(Level.SEVERE, "Failed to take screenshot.", e);
		}
	}

	/**
	 * Saves a sub-image of the given image.
	 *
	 * @param bufferedImage The image.
	 * @param rectangle     The rectangle.
	 */
	public static void saveImage(final BufferedImage bufferedImage, final Rectangle rectangle) {
		saveImage(bufferedImage.getSubimage(rectangle.x, rectangle.y, rectangle.width, rectangle.height));
	}

	public static void saveImage(final Rectangle rectangle) {
		saveImage(Context.get().bot, rectangle);
	}

	/**
	 * Saves a sub-image of the bot screen.
	 *
	 * @param bot       The bot.
	 * @param rectangle The rectangle.
	 */
	public static void saveImage(final Bot bot, final Rectangle rectangle) {
		saveImage(bot.getImage(), rectangle);
	}

	public static BufferedImage takeScreenshot(final Bot bot) {
		return takeScreenshot(bot, false);
	}

	public static BufferedImage takeScreenshot(final Bot bot, final boolean hideUsername) {
		return takeScreenshot(bot.getImage(), hideUsername);
	}

	public static BufferedImage takeScreenshot(final BufferedImage bufferedImage) {
		return takeScreenshot(bufferedImage, false);
	}

	public static BufferedImage takeScreenshot(final BufferedImage source, final boolean hideUsername) {
		final WritableRaster raster = source.copyData(null);
		final BufferedImage bufferedImage = new BufferedImage(source.getColorModel(), raster, source.isAlphaPremultiplied(), null);
		final Graphics2D graphics = bufferedImage.createGraphics();
		if (hideUsername) {
			graphics.setColor(Color.BLACK);
			if (Game.isFixed()) {
				graphics.fill(new Rectangle(9, 459, 100, 15));
			} else {
				graphics.drawRect(8, 555, 100, 15);
			}
			graphics.dispose();
		}
		return source;
	}
}

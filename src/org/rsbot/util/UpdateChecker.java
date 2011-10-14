package org.rsbot.util;

import org.rsbot.Configuration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Checks for updates to the bot.
 *
 * @author Timer
 */
public final class UpdateChecker {
	private static int latest = -1;
	private static final Logger log = Logger.getLogger(UpdateChecker.class.getName());

	public static int getLatestVersion() {
		if (latest != -1) {
			return latest;
		}
		try {
			final File cache = Configuration.Paths.getCachableResources().get(Configuration.Paths.URLs.VERSION);
			latest = Integer.parseInt(IOHelper.readString(cache).trim());
		} catch (final NumberFormatException ignored) {
			latest = Configuration.getVersion();
		} catch (final NullPointerException ignored) {
			latest = Configuration.getVersion();
		}
		return latest;
	}

	public static void update() throws IOException, InterruptedException {
		log.info("An update is being applied (" + Configuration.getVersion() + " -> " + latest + ").");
		final File newJar = new File(Configuration.Paths.getStorageDirectory(), Configuration.NAME + "-" + latest + ".jar");
		log.fine("Downloading...");
		HttpClient.download(new URL(Configuration.Paths.URLs.DOWNLOAD), newJar);
		log.fine("Executing new jar.");
		Runtime.getRuntime().exec("java -jar " + newJar.getAbsolutePath().replace('\\', '/')).waitFor();
		log.fine("Terminating client.");
		System.exit(0);
	}
}

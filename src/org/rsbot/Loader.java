package org.rsbot;

import org.rsbot.loader.ClientLoader;
import org.rsbot.loader.script.ParseException;
import org.rsbot.ui.Chrome;
import org.rsbot.util.HttpClient;
import org.rsbot.util.IOHelper;
import org.rsbot.util.UpdateChecker;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A loader for the bot.
 *
 * @author Timer
 */
public class Loader {
	private static final Logger log = Logger.getLogger(Chrome.class.getName());
	private static final LinkedList<Objective> objectives = new LinkedList<Objective>();
	public static boolean loading = false;
	public static Objective currentObjective = null;

	public static void load() {
		Configuration.bootstrap();
		Configuration.mkdirs();
	}

	public static void init() {
		objectives.addLast(new Objective() {
			public String getAction() {
				return "Registering";
			}

			public void run() {
				Configuration.setupLog();
				Application.chrome.setupLabel();
			}
		});
		objectives.addLast(new Objective() {
			public String getAction() {
				return "Enforcing security policy";
			}

			public void run() {
				System.setProperty("java.io.tmpdir", Configuration.Paths.getStorageDirectory());
			}
		});
		objectives.addLast(new Objective() {
			public String getAction() {
				return "Loading resources";
			}

			public void run() {
				for (Map.Entry<String, File> entry : Configuration.Paths.getCachableResources().entrySet()) {
					try {
						HttpClient.download(new URL(entry.getKey()), entry.getValue());
					} catch (final IOException ignored) {
					}
				}
			}
		});
		objectives.addLast(new Objective() {
			public String getAction() {
				return "Checking for updates";
			}

			public void run() {
				if (Configuration.getVersion() < UpdateChecker.getLatestVersion()) {
					try {
						UpdateChecker.update();
					} catch (final IOException ignored) {
					} catch (final InterruptedException ignored) {
					}
				}
			}
		});
		objectives.addLast(new Objective() {
			public String getAction() {
				return "Extracting resources";
			}

			public void run() {
				Extractor.run();
			}
		});
		objectives.addLast(new Objective() {
			public String getAction() {
				return "Loading client";
			}

			public void run() {
				ClientLoader.update();
				if (!ClientLoader.isOutdated()) {
					try {
						ClientLoader.load();
					} catch (final IOException ex) {
						log.severe("Unable to load client: " + ex.getMessage());
					} catch (final ParseException pe) {
						log.severe("Unable to load client: " + pe.getMessage());
					}
				}
			}
		});
	}

	public static void run() {
		loading = true;
		try {
			while (objectives.size() > 0) {
				try {
					currentObjective = objectives.pollFirst();
					Application.chrome.panel.repaint();
					currentObjective.run();
				} catch (final Throwable throwable) {
					if (currentObjective != null) {
						log.log(Level.SEVERE, "Unable to complete \"" + currentObjective.getAction() + "\": ", throwable);
					} else {
						log.severe("Unable to complete an objective");
					}
				}
			}
		} catch (final Throwable throwable) {
			log.log(Level.SEVERE, "Unable to load " + Configuration.NAME + ": ", throwable);
		} finally {
			loading = false;
		}
	}

	public interface Objective extends Runnable {
		public String getAction();
	}

	private static class Extractor {
		public static void run() {
			if (Configuration.RUNNING_FROM_JAR) {
				IOHelper.write(Configuration.Paths.getRunningJarPath(), new File(Configuration.Paths.getJarPathFile()));
			}
			final String[] extract;
			if (Configuration.getCurrentOperatingSystem() == Configuration.OperatingSystem.WINDOWS) {
				extract = new String[]{Configuration.Paths.Resources.COMPILE_SCRIPTS_BAT, Configuration.Paths.Resources.COMPILE_FIND_JDK};
			} else {
				extract = new String[]{Configuration.Paths.Resources.COMPILE_SCRIPTS_SH};
			}
			for (final String item : extract) {
				try {
					IOHelper.write(Configuration.getResourceURL(item).openStream(), new File(Configuration.Paths.getHomeDirectory(), new File(item).getName()));
				} catch (final IOException ignored) {
				}
			}
		}
	}
}

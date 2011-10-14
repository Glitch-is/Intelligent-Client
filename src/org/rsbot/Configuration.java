package org.rsbot;

import org.rsbot.ui.component.log.LogFormatter;
import org.rsbot.ui.component.log.LogOutputStream;
import org.rsbot.ui.component.log.SystemConsoleHandler;
import org.rsbot.ui.component.log.TextAreaLogHandler;
import org.rsbot.util.IOHelper;
import org.rsbot.util.StringUtil;

import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Global configuration available via static access for the bot.
 *
 * @author Timer
 */
public class Configuration {
	public enum OperatingSystem {
		MAC, WINDOWS, LINUX, UNKNOWN
	}

	public static class Paths {
		public static interface Resources {
			public static final String ROOT = "resources";
			public static final String ROOT_IMG = ROOT + "/images";
			public static final String ICON = Resources.ROOT_IMG + "/icon.png";
			public static final String ICON_APP_ADD = Resources.ROOT_IMG + "/application_add.png";
			public static final String ICON_APP_DEL = Resources.ROOT_IMG + "/application_delete.png";
			public static final String ICON_DELETE = Resources.ROOT_IMG + "/delete.png";
			public static final String ICON_PLAY = Resources.ROOT_IMG + "/control_play_blue.png";
			public static final String ICON_PAUSE = Resources.ROOT_IMG + "/control_pause.png";
			public static final String ICON_ADD = Resources.ROOT_IMG + "/add.png";
			public static final String ICON_ADD_OVER = Resources.ROOT_IMG + "/add_over.png";
			public static final String ICON_ADD_DOWN = Resources.ROOT_IMG + "/add_down.png";
			public static final String ICON_HOME = Resources.ROOT_IMG + "/home.png";
			public static final String ICON_BOT = Resources.ROOT_IMG + "/bot.png";
			public static final String ICON_PHOTO = Resources.ROOT_IMG + "/photo.png";
			public static final String ICON_CLOSE = Resources.ROOT_IMG + "/close.png";
			public static final String ICON_CLOSE_OVER = Resources.ROOT_IMG + "/close_over.png";
			public static final String ICON_TICK = Resources.ROOT_IMG + "/tick.png";
			public static final String ICON_MOUSE = Resources.ROOT_IMG + "/mouse.png";
			public static final String ICON_KEYBOARD = Resources.ROOT_IMG + "/keyboard.png";
			public static final String ICON_CONNECT = Resources.ROOT_IMG + "/connect.png";
			public static final String ICON_DISCONNECT = Resources.ROOT_IMG + "/disconnect.png";
			public static final String ICON_START = Resources.ROOT_IMG + "/control_play.png";
			public static final String ICON_REPORTKEY = ROOT_IMG + "/report_key.png";
			public static final String ICON_REPORT_DISK = ROOT_IMG + "/report_disk.png";
			public static final String ICON_WEBLINK = ROOT_IMG + "/world_link.png";
			public static final String ICON_GITHUB = ROOT_IMG + "/github.png";
			public static final String ICON_INFO = ROOT_IMG + "/information.png";
			public static final String ICON_SCRIPT_DRM = Resources.ROOT_IMG + "/script_drm.png";
			public static final String ICON_SCRIPT_PRE = Resources.ROOT_IMG + "/script_pre.png";
			public static final String ICON_SCRIPT_SRC = Resources.ROOT_IMG + "/script_src.png";

			public static final String VERSION = Resources.ROOT + "/version.txt";

			public static final String COMPILE_SCRIPTS_BAT = ROOT + "/Compile-Scripts.bat";
			public static final String COMPILE_SCRIPTS_SH = ROOT + "/compile-scripts.sh";
			public static final String COMPILE_FIND_JDK = ROOT + "/FindJDK.bat";
		}

		public static interface URLs {
			public static final String BASE = "http://scripters.powerbot.org/files/209765/renatus/";
			public static final String SITE = "http://powerbot.org/";
			public static final String DOWNLOAD = BASE + "RenatusBot.jar";
			public static final String CLIENT_PATCH = BASE + "modscript.gz";
			public static final String VERSION = BASE + "version.txt";
			public static final String PROJECT = "https://github.com/Timer/RenatusBot";
			public static final String SDN_MANIFEST = BASE + "sdn-manifest.txt";
			public static final String AD_INFO = BASE + "botad-info.txt";
		}

		public static final String SCRIPTS_NAME_OUT = "Scripts";

		public static String getHomeDirectory() {
			final String env = System.getenv(Configuration.NAME.toUpperCase() + "_HOME");
			if ((env == null) || env.isEmpty()) {
				return (Configuration.getCurrentOperatingSystem() == OperatingSystem.WINDOWS ? FileSystemView.getFileSystemView().getDefaultDirectory().getAbsolutePath() : Paths.getUnixHome()) + File.separator + Configuration.NAME;
			} else {
				return env;
			}
		}

		public static String getAccountsFile() {
			final String path;
			if (Configuration.getCurrentOperatingSystem() == OperatingSystem.WINDOWS) {
				path = System.getenv("APPDATA") + File.separator + Configuration.NAME + "_Accounts.ini";
			} else {
				path = Paths.getUnixHome() + File.separator + "." + Configuration.NAME_LOWERCASE + "acct";
			}
			return path;
		}

		public static String getLogsDirectory() {
			return Paths.getHomeDirectory() + File.separator + "Logs";
		}

		public static String getJarPathFile() {
			return Paths.getSettingsDirectory() + File.separator + "path.txt";
		}

		public static String getUIDsFile() {
			return Paths.getSettingsDirectory() + File.separator + "uid.txt";
		}

		public static String getScreenshotsDirectory() {
			return Paths.getHomeDirectory() + File.separator + "Screenshots";
		}

		public static String getScriptsDirectory() {
			return Paths.getHomeDirectory() + File.separator + Paths.SCRIPTS_NAME_OUT;
		}

		public static String getScriptsSourcesDirectory() {
			return Paths.getScriptsDirectory() + File.separator + "Sources";
		}

		public static String getScriptsPrecompiledDirectory() {
			return Paths.getScriptsDirectory() + File.separator + "Precompiled";
		}

		public static String getCacheDirectory() {
			return Paths.getHomeDirectory() + File.separator + "Cache";
		}

		public static String getStorageDirectory() {
			return Paths.getHomeDirectory() + File.separator + "Storage";
		}

		public static String getVersionCacheFile() {
			return Paths.getCacheDirectory() + File.separator + "info.dat";
		}

		public static String getClientPatchCacheFile() {
			return Paths.getCacheDirectory() + File.separator + "ms.dat";
		}

		public static String getSettingsDirectory() {
			return Paths.getHomeDirectory() + File.separator + "Settings";
		}

		public static String getUnixHome() {
			final String home = System.getProperty("user.home");
			return home == null ? "~" : home;
		}

		public static String getRunningJarPath() {
			if (!RUNNING_FROM_JAR) {
				return null;
			}
			String path = new File(Configuration.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getAbsolutePath();
			try {
				path = URLDecoder.decode(path, "UTF-8");
			} catch (final UnsupportedEncodingException ignored) {
			}
			return path;
		}

		private static Map<String, File> cachableResources;

		public static Map<String, File> getCachableResources() {
			if (cachableResources == null) {
				cachableResources = new HashMap<String, File>(3);
				cachableResources.put(URLs.VERSION, new File(getCacheDirectory(), "version-latest.txt"));
				cachableResources.put(URLs.AD_INFO, new File(getCacheDirectory(), "ad-info.txt"));
				cachableResources.put(URLs.SDN_MANIFEST, new File(getCacheDirectory(), "sdn-manifest.txt"));
			}
			return cachableResources;
		}
	}

	public static final String NAME = "RenatusBot";
	public static final String NAME_LOWERCASE = NAME.toLowerCase();
	private static OperatingSystem CURRENT_OS;
	public static boolean RUNNING_FROM_JAR = false;

	public static URL getResourceURL(final String path) throws MalformedURLException {
		return RUNNING_FROM_JAR ? Configuration.class.getResource("/" + path) : new File(path).toURI().toURL();
	}

	public static Image getImage(final String resource) {
		try {
			return Toolkit.getDefaultToolkit().getImage(getResourceURL(resource));
		} catch (final Exception ignored) {
		}
		return null;
	}

	public static OperatingSystem getCurrentOperatingSystem() {
		return Configuration.CURRENT_OS;
	}

	public static int getVersion() {
		final URL src;
		try {
			src = getResourceURL(Paths.Resources.VERSION);
		} catch (final MalformedURLException ignored) {
			return -1;
		}
		return Integer.parseInt(IOHelper.readString(src).trim());
	}

	public static String getVersionFormatted() {
		return StringUtil.formatVersion(getVersion());
	}

	protected static void bootstrap() {
		final URL resource = Configuration.class.getClassLoader().getResource(Paths.Resources.VERSION);
		if (resource != null) {
			Configuration.RUNNING_FROM_JAR = true;
		}

		final String os = System.getProperty("os.name");
		if (os.contains("Mac")) {
			CURRENT_OS = OperatingSystem.MAC;
		} else if (os.contains("Windows")) {
			CURRENT_OS = OperatingSystem.WINDOWS;
		} else if (os.contains("Linux")) {
			CURRENT_OS = OperatingSystem.LINUX;
		} else {
			CURRENT_OS = OperatingSystem.UNKNOWN;
		}
		Logger.getLogger("").setLevel(Level.INFO);
		Logger.getLogger("").addHandler(new SystemConsoleHandler());
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			private final Logger log = Logger.getLogger("EXCEPTION");

			public void uncaughtException(final Thread t, final Throwable e) {
				log.logp(Level.SEVERE, "EXCEPTION", "", "Unhandled exception in thread " + t.getName() + ": ", e);
			}
		});
		System.setErr(new PrintStream(new LogOutputStream(Logger.getLogger("STDERR"), Level.SEVERE), true));
	}

	protected static void setupLog() {
		Properties logging = new Properties();
		String logFormatter = LogFormatter.class.getCanonicalName();
		String fileHandler = FileHandler.class.getCanonicalName();
		logging.setProperty("handlers", TextAreaLogHandler.class.getCanonicalName() + "," + fileHandler);
		logging.setProperty(".level", "INFO");
		logging.setProperty(SystemConsoleHandler.class.getCanonicalName() + ".formatter", logFormatter);
		logging.setProperty(fileHandler + ".formatter", logFormatter);
		logging.setProperty(TextAreaLogHandler.class.getCanonicalName() + ".formatter", logFormatter);
		logging.setProperty(fileHandler + ".pattern", Configuration.Paths.getLogsDirectory() + File.separator + "%u.%g.log");
		logging.setProperty(fileHandler + ".count", "10");
		final ByteArrayOutputStream logout = new ByteArrayOutputStream();
		try {
			logging.store(logout, "");
			LogManager.getLogManager().readConfiguration(new ByteArrayInputStream(logout.toByteArray()));
		} catch (final Exception ignored) {
		}
	}

	protected static void mkdirs() {
		final String[] directories = {
				Paths.getHomeDirectory(),
				Paths.getLogsDirectory(),
				Paths.getCacheDirectory(),
				Paths.getStorageDirectory(),
				Paths.getSettingsDirectory(),
				Paths.getScriptsDirectory(),
				Paths.getScriptsSourcesDirectory(),
				Paths.getScriptsPrecompiledDirectory()
		};

		for (final String name : directories) {
			final File dir = new File(name);
			if (!dir.isDirectory()) {
				dir.mkdirs();
			}
		}
	}
}
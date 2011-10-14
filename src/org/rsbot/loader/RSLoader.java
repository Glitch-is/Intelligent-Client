package org.rsbot.loader;

import org.rsbot.Application;
import org.rsbot.Configuration;
import org.rsbot.bot.accessors.Loader;
import org.rsbot.loader.script.ParseException;

import java.applet.Applet;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Qauters
 */
public class RSLoader extends Applet implements Runnable, Loader {
	private final Logger log = Logger.getLogger(RSLoader.class.getName());
	private static final long serialVersionUID = 6288499508495040201L;

	/**
	 * The applet of the client
	 */
	private Applet client;

	private Runnable loadedCallback;
	private String targetName;
	private Dimension size = Application.getPanelSize();
	/**
	 * The game class loader
	 */
	private RSClassLoader classLoader;

	@Override
	public final synchronized void destroy() {
		if (client != null) {
			client.destroy();
		}
	}

	@Override
	public boolean isShowing() {
		return true;
	}

	@Override
	public final synchronized void init() {
		if (client != null) {
			client.init();
		}
	}

	/**
	 * The run void of the loader
	 */
	public void run() {
		try {
			final Class<?> c = classLoader.loadClass("client");
			client = (Applet) c.newInstance();
			loadedCallback.run();
			c.getMethod("provideLoaderApplet", new Class[]{java.applet.Applet.class}).invoke(null, this);
			client.init();
			client.start();
		} catch (final Throwable e) {
			log.severe("Unable to load client, please check your firewall and internet connection.");
			final File versionFile = new File(Configuration.Paths.getVersionCacheFile());
			if (versionFile.exists() && !versionFile.delete()) {
				log.warning("Unable to clear cache.");
			}

			log.log(Level.SEVERE, "Error reason:", e);
		}
	}

	public Applet getClient() {
		return client;
	}

	public void load() {
		try {
			ClientLoader.update();
			ClientLoader.load();
			targetName = ClientLoader.getTargetName();
			classLoader = new RSClassLoader(ClientLoader.getClasses(), new URL("http://" + targetName + ".com/"));
		} catch (final IOException ex) {
			log.severe("Unable to load client: " + ex.getMessage());
		} catch (final ParseException pe) {
			log.severe("Unable to load client: " + pe.getMessage());
		}
	}

	public void setCallback(final Runnable r) {
		loadedCallback = r;
	}

	public String getTargetName() {
		return targetName;
	}

	/**
	 * Overridden void start()
	 */
	@Override
	public final synchronized void start() {
		if (client != null) {
			client.start();
		}
	}

	/**
	 * Overridden void deactivate()
	 */
	@Override
	public final synchronized void stop() {
		if (client != null) {
			client.stop();
		}
	}

	/**
	 * Overridden void update(Graphics)
	 */
	@Override
	public final void update(final Graphics graphics) {
		if (client != null) {
			client.update(graphics);
		} else {
			paint(graphics);
		}
	}

	@Override
	public final void setSize(final int width, final int height) {
		super.setSize(width, height);
		size = new Dimension(width, height);
	}

	@Override
	public final Dimension getSize() {
		return size;
	}
}
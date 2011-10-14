package org.rsbot;

import org.rsbot.bot.Bot;
import org.rsbot.ui.Chrome;

import java.awt.*;

/**
 * The main class of RSBot.
 *
 * @author Timer
 */
public class Application {
	protected static Chrome chrome = null;

	public static void main(final String[] args) {
		Loader.load();
		display();
	}

	public static void display() {
		if (chrome != null) {
			return;
		}
		final Thread startThread = new Thread(new Runnable() {
			public void run() {
				chrome = new Chrome();
				chrome.setTitle("<init>");
				chrome.setVisible(true);
				chrome.toFront();
				Loader.init();
				Loader.run();
				chrome.setTitle(null);
				chrome.addBot();
			}
		});
		startThread.setPriority(Thread.MAX_PRIORITY);
		startThread.setDaemon(false);
		startThread.setName(Configuration.NAME + "'s_" + Chrome.class.getName() + "_initializer");
		startThread.start();
	}


	public static Bot getBot(final Object o) {
		return chrome.getBot(o);
	}

	public static Dimension getPanelSize() {
		return chrome.getPanel().getSize();
	}
}

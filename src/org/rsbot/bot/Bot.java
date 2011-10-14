package org.rsbot.bot;

import org.rsbot.Application;
import org.rsbot.bot.accessors.Client;
import org.rsbot.bot.accessors.input.Canvas;
import org.rsbot.bot.concurrent.Task;
import org.rsbot.bot.event.PaintEvent;
import org.rsbot.bot.event.TextPaintEvent;
import org.rsbot.loader.BotStub;
import org.rsbot.loader.RSLoader;
import org.rsbot.script.event.handler.EventContainer;
import org.rsbot.ui.AccountManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Constructor;
import java.util.EventListener;
import java.util.Map;
import java.util.TreeMap;

public class Bot {
	private BotStub botStub;
	private final ThreadGroup tg;
	private Component panel;
	private PaintEvent paintEvent;
	private TextPaintEvent textPaintEvent;
	private BufferedImage backBuffer;
	private BufferedImage image;
	private RSLoader loader;
	private Map<String, EventListener> listeners;
	public BotComposite composite;

	public Bot() {
		tg = new ThreadGroup("RSClient-" + hashCode());
		composite = new BotComposite(this);
		composite.scriptEventContainer = new EventContainer(this);
		loader = new RSLoader();
		final Dimension size = Application.getPanelSize();
		loader.setCallback(new Runnable() {
			public void run() {
				try {
					setClient((Client) loader.getClient());
					resize(size.width, size.height);
				} catch (Exception ignored) {
				}
			}
		});
		backBuffer = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
		image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
		paintEvent = new PaintEvent();
		textPaintEvent = new TextPaintEvent();
		listeners = new TreeMap<String, EventListener>();
	}

	public void start() {
		try {
			loader.paint(image.getGraphics());
			loader.load();
			if (loader.getTargetName() == null) {
				return;
			}
			botStub = new BotStub(loader);
			loader.setStub(botStub);
			composite.eventManager.start();
			botStub.setActive(true);
			final Thread thread = new Thread(tg, loader, "Loader");
			thread.setPriority(Thread.NORM_PRIORITY);
			thread.start();
			Context.put(tg, this);
		} catch (Exception ignored) {
		}
	}

	public void stop() {
		composite.eventManager.killThread(false);
		try {
			composite.concurrentDispatch.get().setRunning(false);
		} catch (final NullPointerException ignored) {
		}
		loader.stop();
		loader.destroy();
		Context.remove(tg);
		loader = null;
	}

	public void resize(final int width, final int height) {
		backBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		// client reads size of loader applet for drawing
		loader.setSize(width, height);
		// simulate loader repaint awt event dispatch
		loader.update(backBuffer.getGraphics());
		loader.paint(backBuffer.getGraphics());
	}

	public boolean setAccount(final String name) {
		boolean exist = false;
		for (String s : AccountManager.getAccountNames()) {
			if (s.toLowerCase().equals(name.toLowerCase())) {
				exist = true;
			}
		}
		if (exist) {
			composite.account = name;
			return true;
		}
		composite.account = null;
		return false;
	}

	public void setPanel(final Component c) {
		this.panel = c;
	}

	public void addListener(final Class<?> clazz) {
		EventListener el = instantiateListener(clazz);
		listeners.put(clazz.getName(), el);
		composite.eventManager.addListener(el);
	}

	public void removeListener(final Class<?> clazz) {
		final EventListener el = listeners.get(clazz.getName());
		listeners.remove(clazz.getName());
		composite.eventManager.removeListener(el);
	}

	public boolean hasListener(final Class<?> clazz) {
		return clazz != null && listeners.get(clazz.getName()) != null;
	}

	public Canvas getCanvas() {
		if (composite.client == null) {
			return null;
		}
		return (Canvas) composite.client.getCanvas();
	}

	public Graphics getBufferGraphics() {
		final Graphics back = backBuffer.getGraphics();
		paintEvent.graphics = back;
		textPaintEvent.graphics = back;
		textPaintEvent.idx = 0;
		composite.eventManager.processEvent(paintEvent);
		composite.eventManager.processEvent(textPaintEvent);
		back.dispose();
		image.getGraphics().drawImage(backBuffer, 0, 0, null);
		if (panel != null) {
			panel.repaint();
		}
		return backBuffer.getGraphics();
	}

	public BufferedImage getImage() {
		return image;
	}

	public BotStub getBotStub() {
		return botStub;
	}

	public RSLoader getLoader() {
		return loader;
	}

	private void setClient(final Client cl) {
		composite.client = cl;
		composite.client.setCallback(new CallbackImpl(this));
		composite.gameGUI = new GameGUI();
		Context.put(tg, this);//Update once the bot has fully loaded itself (re-load client).
		new Thread(new SafeMode(composite)).start();
	}

	private EventListener instantiateListener(final Class<?> clazz) {
		try {
			EventListener listener;
			try {
				Constructor<?> constructor = clazz.getConstructor(Bot.class);
				listener = (EventListener) constructor.newInstance(this);
			} catch (Exception e) {
				listener = clazz.asSubclass(EventListener.class).newInstance();
			}
			return listener;
		} catch (Exception ignored) {
		}
		return null;
	}

	/**
	 * The thread group specific to this bot.
	 *
	 * @return The <code>ThreadGroup</code>.
	 */
	public ThreadGroup getThreadGroup() {
		return tg;
	}

	class SafeMode implements Runnable {
		private final BotComposite bot;

		SafeMode(final BotComposite bot) {
			this.bot = bot;
		}

		public void run() {
			while (true) {
				if (bot.client.getKeyboard() == null) {
					Task.sleep(500);
					continue;
				}
				bot.inputManager.sendKey('s');
				break;
			}
		}
	}
}

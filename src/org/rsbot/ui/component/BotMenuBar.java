package org.rsbot.ui.component;

import org.rsbot.Configuration;
import org.rsbot.bot.Bot;
import org.rsbot.bot.event.impl.*;
import org.rsbot.bot.event.listener.PaintListener;
import org.rsbot.bot.event.listener.TextPaintListener;
import org.rsbot.ui.locale.Messages;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.*;

public class BotMenuBar extends JMenuBar {
	public static final Map<String, Class<?>> DEBUG_MAP = new LinkedHashMap<String, Class<?>>();
	public static final String[] TITLES;
	public static final String[][] ELEMENTS;
	private Bot bot;

	static {
		DEBUG_MAP.put("Mouse", DrawMouse.class);
		DEBUG_MAP.put("Players", DrawPlayers.class);
		DEBUG_MAP.put("NPCs", DrawNPCs.class);
		DEBUG_MAP.put("Objects", DrawObjects.class);
		DEBUG_MAP.put("Projectiles", DrawProjectiles.class);
		DEBUG_MAP.put("Models", DrawModels.class);
		DEBUG_MAP.put("Collision", DrawCollision.class);
		DEBUG_MAP.put("Inventory", DrawInventory.class);

		DEBUG_MAP.put("Animator", TAnimation.class);
		DEBUG_MAP.put("Location", TPlayerLocation.class);
		DEBUG_MAP.put("Plane", TPlane.class);
		DEBUG_MAP.put("Game State", TLoginIndex.class);
		DEBUG_MAP.put("Interaction Menu", TMenu.class);
		DEBUG_MAP.put("Mouse Location", TMousePosition.class);
		DEBUG_MAP.put("FPS", TFPS.class);

		DEBUG_MAP.put("Messages", MessageLogger.class);

		TITLES = new String[]{Messages.FILE, Messages.EDIT, Messages.VIEW, Messages.HELP};
		ELEMENTS = new String[][]{
				{Messages.NEW_BOT, Messages.CLOSE_BOT, Messages.MENU_SEPERATOR,
						Messages.RUN_SCRIPT, Messages.STOP_SCRIPT, Messages.RESUME_SCRIPT, Messages.PAUSE_SCRIPT, Messages.MENU_SEPERATOR,
						Messages.SAVE_SCREENSHOT, Messages.MENU_SEPERATOR,
						Messages.EXIT},
				{Messages.ACCOUNTS, Messages.MENU_SEPERATOR,
						Messages.TOGGLEFALSE + Messages.FORCE_INPUT, Messages.TOGGLEFALSE + Messages.LESS_CPU, Messages.MENU_SEPERATOR,
						Messages.TOGGLEFALSE + Messages.DISABLE_ANTI_RANDOMS, Messages.TOGGLEFALSE + Messages.DISABLE_LOGIN},
				constructDebugs(),
				{Messages.SITE, Messages.PROJECT, Messages.ABOUT}};
	}

	private static String[] constructDebugs() {
		final List<String> debugItems = new ArrayList<String>();
		debugItems.add(Messages.EXPAND_LOG);
		debugItems.add(Messages.MENU_SEPERATOR);
		for (String key : DEBUG_MAP.keySet()) {
			Class<?> el = DEBUG_MAP.get(key);
			if (PaintListener.class.isAssignableFrom(el)) {
				debugItems.add(key);
			}
		}
		debugItems.add(Messages.MENU_SEPERATOR);
		for (String key : DEBUG_MAP.keySet()) {
			final Class<?> el = DEBUG_MAP.get(key);
			if (TextPaintListener.class.isAssignableFrom(el)) {
				debugItems.add(key);
			}
		}
		debugItems.add(Messages.MENU_SEPERATOR);
		for (String key : DEBUG_MAP.keySet()) {
			Class<?> el = DEBUG_MAP.get(key);
			if (!(TextPaintListener.class.isAssignableFrom(el)) && !(PaintListener.class.isAssignableFrom(el))) {
				debugItems.add(key);
			}
		}
		for (ListIterator<String> it = debugItems.listIterator(); it.hasNext(); ) {
			String s = it.next();
			if (!s.equals(Messages.MENU_SEPERATOR)) {
				it.set(Messages.TOGGLEFALSE + s);
			}
		}
		return debugItems.toArray(new String[debugItems.size()]);
	}

	private void constructItemIcons() {
		final HashMap<String, String> map = new HashMap<String, String>();
		map.put(Messages.NEW_BOT, Configuration.Paths.Resources.ICON_APP_ADD);
		map.put(Messages.CLOSE_BOT, Configuration.Paths.Resources.ICON_APP_DEL);
		map.put(Messages.RUN_SCRIPT, Configuration.Paths.Resources.ICON_PLAY);
		map.put(Messages.STOP_SCRIPT, Configuration.Paths.Resources.ICON_DELETE);
		map.put(Messages.RESUME_SCRIPT, Configuration.Paths.Resources.ICON_PLAY);
		map.put(Messages.PAUSE_SCRIPT, Configuration.Paths.Resources.ICON_PAUSE);
		map.put(Messages.SAVE_SCREENSHOT, Configuration.Paths.Resources.ICON_PHOTO);
		map.put(Messages.EXIT, Configuration.Paths.Resources.ICON_CLOSE_OVER);
		map.put(Messages.SITE, Configuration.Paths.Resources.ICON_WEBLINK);
		map.put(Messages.PROJECT, Configuration.Paths.Resources.ICON_GITHUB);
		map.put(Messages.ABOUT, Configuration.Paths.Resources.ICON_INFO);
		map.put(Messages.ACCOUNTS, Configuration.Paths.Resources.ICON_REPORTKEY);
		for (final Map.Entry<String, String> item : map.entrySet()) {
			final JMenuItem menu = commandMenuItem.get(item.getKey());
			menu.setIcon(new ImageIcon(Configuration.getImage(item.getValue())));
		}
	}

	private final Map<String, JCheckBoxMenuItem> eventCheckMap = new HashMap<String, JCheckBoxMenuItem>();
	private final Map<String, JCheckBoxMenuItem> commandCheckMap = new HashMap<String, JCheckBoxMenuItem>();
	private final Map<String, JMenuItem> commandMenuItem = new HashMap<String, JMenuItem>();
	private final ActionListener listener;

	public BotMenuBar(final ActionListener listener) {
		this.listener = listener;
		for (int i = 0; i < TITLES.length; i++) {
			String title = TITLES[i];
			String[] elems = ELEMENTS[i];
			add(constructMenu(title, elems));
		}
		constructItemIcons();
	}

	public void setBot(final Bot bot) {
		this.bot = bot;
		update();
	}

	public JCheckBoxMenuItem getCheckBox(final String key) {
		return commandCheckMap.get(key);
	}

	private void enable(final String item, final boolean selected) {
		commandCheckMap.get(item).setSelected(selected);
		commandCheckMap.get(item).setEnabled(true);
	}

	private void disable(final String... items) {
		for (String item : items) {
			commandCheckMap.get(item).setSelected(false);
			commandCheckMap.get(item).setEnabled(false);
		}
	}

	public void update() {
		if (bot == null) {
			commandMenuItem.get(Messages.CLOSE_BOT).setVisible(false);
			commandMenuItem.get(Messages.RUN_SCRIPT).setVisible(true);
			commandMenuItem.get(Messages.RUN_SCRIPT).setEnabled(false);
			commandMenuItem.get(Messages.STOP_SCRIPT).setVisible(false);
			commandMenuItem.get(Messages.RESUME_SCRIPT).setVisible(false);
			commandMenuItem.get(Messages.PAUSE_SCRIPT).setVisible(true);
			commandMenuItem.get(Messages.PAUSE_SCRIPT).setEnabled(false);
			commandMenuItem.get(Messages.SAVE_SCREENSHOT).setEnabled(false);
			for (JCheckBoxMenuItem item : eventCheckMap.values()) {
				item.setSelected(false);
				item.setEnabled(false);
			}
			disable(Messages.FORCE_INPUT, Messages.LESS_CPU, Messages.DISABLE_ANTI_RANDOMS, Messages.DISABLE_LOGIN);
		} else {
			commandMenuItem.get(Messages.CLOSE_BOT).setVisible(true);
			final boolean scriptRunning = bot.composite.concurrentDispatch.isRunning();
			final boolean scriptPaused = bot.composite.concurrentDispatch.isPaused();
			commandMenuItem.get(Messages.RUN_SCRIPT).setEnabled(true);
			commandMenuItem.get(Messages.RUN_SCRIPT).setVisible(!scriptRunning);
			commandMenuItem.get(Messages.STOP_SCRIPT).setVisible(scriptRunning);
			commandMenuItem.get(Messages.RESUME_SCRIPT).setVisible(scriptRunning && scriptPaused);
			commandMenuItem.get(Messages.RESUME_SCRIPT).setEnabled(scriptRunning && scriptPaused);
			commandMenuItem.get(Messages.PAUSE_SCRIPT).setVisible(!scriptPaused);
			commandMenuItem.get(Messages.PAUSE_SCRIPT).setEnabled(scriptRunning && !scriptPaused);
			commandMenuItem.get(Messages.SAVE_SCREENSHOT).setEnabled(true);
			for (Map.Entry<String, JCheckBoxMenuItem> entry : eventCheckMap.entrySet()) {
				entry.getValue().setEnabled(true);
				entry.getValue().setSelected(bot.hasListener(DEBUG_MAP.get(entry.getKey())));
			}
			enable(Messages.FORCE_INPUT, bot.composite.overrideInput);
			enable(Messages.LESS_CPU, bot.composite.disableRendering);
			enable(Messages.DISABLE_ANTI_RANDOMS, bot.composite.disableRandoms);
			enable(Messages.DISABLE_LOGIN, bot.composite.disableAutoLogin);
		}
	}

	private JMenu constructMenu(final String title, final String[] elems) {
		final JMenu menu = new JMenu(title);
		for (String e : elems) {
			if (e.equals(Messages.MENU_SEPERATOR)) {
				menu.add(new JSeparator());
			} else {
				JMenuItem jmi;

				if (e.startsWith(Messages.TOGGLE)) {
					e = e.substring(Messages.TOGGLE.length());
					char state = e.charAt(0);
					e = e.substring(2);
					jmi = new JCheckBoxMenuItem(e);
					if ((state == 't') || (state == 'T')) {
						jmi.setSelected(true);
					}
					if (DEBUG_MAP.containsKey(e)) {
						JCheckBoxMenuItem ji = (JCheckBoxMenuItem) jmi;
						eventCheckMap.put(e, ji);
					}
					JCheckBoxMenuItem ji = (JCheckBoxMenuItem) jmi;
					commandCheckMap.put(e, ji);
				} else {
					jmi = new JMenuItem(e);

					commandMenuItem.put(e, jmi);
				}
				jmi.addActionListener(listener);
				jmi.setActionCommand(title + "." + e);
				menu.add(jmi);
			}
		}
		return menu;
	}
}

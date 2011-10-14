package org.rsbot.script.methods;

import org.rsbot.bot.Context;
import org.rsbot.bot.accessors.Client;
import org.rsbot.bot.accessors.MenuGroupNode;
import org.rsbot.bot.accessors.MenuItemNode;
import org.rsbot.bot.concurrent.Task;
import org.rsbot.script.methods.input.Mouse;
import org.rsbot.script.util.Timer;
import org.rsbot.script.wrappers.internal.Deque;
import org.rsbot.script.wrappers.internal.Queue;

import java.awt.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.regex.Pattern;

/**
 * Context menu related operations.
 */
public class Menu {
	private static final Pattern HTML_TAG = Pattern.compile("(^[^<]+>|<[^>]+>|<[^>]+$)");

	/**
	 * Clicks the menu option. Will left-click if the menu item is the first,
	 * otherwise open menu and click the option.
	 *
	 * @param action The action (or action substring) to click.
	 * @return <tt>true</tt> if the menu item was clicked; otherwise
	 *         <tt>false</tt>.
	 */
	public static boolean click(final String action) {
		return click(action, null);
	}

	/**
	 * Clicks the menu option. Will left-click if the menu item is the first,
	 * otherwise open menu and click the option.
	 *
	 * @param action The action (or action substring) to click.
	 * @param option The option (or option substring) of the action to click.
	 * @return <tt>true</tt> if the menu item was clicked; otherwise
	 *         <tt>false</tt>.
	 */
	public static boolean click(final String action, final String option) {
		int idx = getIndex(action, option);
		if (!isOpen()) {
			if (idx == -1) {
				return false;
			}
			if (idx == 0) {
				Mouse.click(true);
				return true;
			}
			Mouse.click(false);
			final Timer t = new Timer(100);
			while (t.isRunning() && !isOpen()) {
				Task.sleep(5);
			}
			idx = getIndex(action, option);

			return idx != -1 && clickIndex(idx);
		} else if (idx == -1) {
			while (isOpen()) {
				Mouse.move(0, 0);
				Task.sleep(Task.random(100, 500));
			}
			return false;
		}
		return clickIndex(idx);
	}

	/**
	 * Checks whether or not a given action (or action substring) is present in
	 * the menu.
	 *
	 * @param action The action or action substring.
	 * @return <tt>true</tt> if present, otherwise <tt>false</tt>.
	 */
	public static boolean contains(final String action) {
		return getIndex(action) != -1;
	}

	/**
	 * Checks whether or not a given action with given option is present
	 * in the menu.
	 *
	 * @param action The action or action substring.
	 * @param option The option or option substring.
	 * @return <tt>true</tt> if present, otherwise <tt>false</tt>.
	 */
	public static boolean contains(final String action, final String option) {
		return getIndex(action, option) != -1;
	}

	/**
	 * Left clicks at the given index.
	 *
	 * @param i The index of the item.
	 * @return <tt>true</tt> if the mouse was clicked; otherwise <tt>false</tt>.
	 */
	public static boolean clickIndex(final int i) {
		if (!isOpen()) {
			return false;
		}
		final String[] items = getItems();
		if (items.length <= i) {
			return false;
		}
		if (isCollapsed()) {
			final Queue<MenuGroupNode> groups = new Queue<MenuGroupNode>(Context.get().client.getCollapsedMenuItems());
			int idx = 0, mainIdx = 0;
			for (MenuGroupNode g = groups.getHead(); g != null; g = groups.getNext(), ++mainIdx) {
				final Queue<MenuItemNode> subItems = new Queue<MenuItemNode>(g.getItems());
				int subIdx = 0;
				for (MenuItemNode item = subItems.getHead(); item != null; item = subItems.getNext(), ++subIdx) {
					if (idx++ == i) {
						return subIdx == 0 ? clickMain(items, mainIdx) : clickSub(items, mainIdx, subIdx);
					}
				}
			}
			return false;
		} else {
			return clickMain(items, i);
		}
	}

	private static boolean clickMain(final String[] items, final int i) {
		final Point menu = getLocation();
		final int xOff = Task.random(4, items[i].length() * 4);
		final int yOff = 21 + 16 * i + Task.random(3, 12);
		Mouse.move(menu.x + xOff, menu.y + yOff, 2, 2);
		if (isOpen()) {
			Mouse.click(true);
			return true;
		}
		return false;
	}

	private static boolean clickSub(final String[] items, final int mIdx, final int sIdx) {
		final Point menuLoc = getLocation();
		int x = Task.random(4, items[mIdx].length() * 4);
		int y = 21 + 16 * mIdx + Task.random(3, 12);
		Mouse.move(menuLoc.x + x, menuLoc.y + y, 2, 2);
		Task.sleep(Task.random(125, 150));
		if (isOpen()) {
			final Point subLoc = getSubMenuLocation();
			final Point start = Mouse.getLocation();
			int subOff = subLoc.x - start.x;
			int moves = Task.random(subOff, subOff + Task.random(0, items[sIdx].length() * 2));
			x = Task.random(4, items[sIdx].length() * 4);
			if (subOff > 0) {
				final int speed = Mouse.getPrecisionSpeed() / 3;
				for (int c = 0; c < moves; c++) {
					Mouse.hop(start.x + c, start.y);
					Task.sleep(Task.random(speed / 2, speed));
				}
			} else {
				Mouse.move(subLoc.x + x, Mouse.getLocation().y, 2, 0);
			}
			Task.sleep(Task.random(125, 150));
			if (isOpen()) {
				y = 16 * sIdx + Task.random(3, 12) + 21;
				Mouse.move(subLoc.x + x, subLoc.y + y, 0, 2);
				Task.sleep(Task.random(125, 150));
				if (isOpen()) {
					Mouse.click(true);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns an array of the first parts of each item in the current menu
	 * context.
	 *
	 * @return The first half. "Walk here", "Trade with", "Follow".
	 */
	public static String[] getActions() {
		return getMenuItemPart(true);
	}

	/**
	 * Returns the index in the menu for a given action. Starts at 0.
	 *
	 * @param action The action that you want the index of.
	 * @return The index of the given option in the context menu; otherwise -1.
	 */
	public static int getIndex(String action) {
		action = action.toLowerCase();
		final String[] items = getItems();
		for (int i = 0; i < items.length; i++) {
			if (items[i].toLowerCase().contains(action)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns the index in the menu for a given action with a given option.
	 * Starts at 0.
	 *
	 * @param action The action of the menu entry of which you want the index.
	 * @param option The option of the menu entry of which you want the index.
	 *               If option is null, operates like getIndex(String action).
	 * @return The index of the given option in the context menu; otherwise -1.
	 */
	public static int getIndex(String action, String option) {
		if (option == null) {
			return getIndex(action);
		}
		action = action.toLowerCase();
		option = option.toLowerCase();
		final String[] actions = getActions();
		final String[] options = getOptions();
		/* Throw exception if lengths unequal? */
		for (int i = 0; i < Math.min(actions.length, options.length); i++) {
			if (actions[i].toLowerCase().contains(action) && options[i].toLowerCase().contains(option)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns an array of each item in the current menu context.
	 *
	 * @return First half + second half. As displayed in the game.
	 */
	public static String[] getItems() {
		String[] options = getOptions();
		String[] actions = getActions();

		final LinkedList<String> output = new LinkedList<String>();

		final int len = Math.min(options.length, actions.length);
		for (int i = 0; i < len; i++) {
			final String option = options[i];
			final String action = actions[i];
			if (option != null && action != null) {
				final String text = action + " " + option;
				output.add(text.trim());
			}
		}

		return output.toArray(new String[output.size()]);
	}

	/**
	 * Returns the menu's location.
	 *
	 * @return The screen space point if the menu is open; otherwise null.
	 */
	public static Point getLocation() {
		if (isOpen()) {
			final Client client = Context.get().client;
			return new Point(client.getMenuX(), client.getMenuY());
		}
		return null;
	}

	private static String[] getMenuItemPart(final boolean firstPart) {
		final LinkedList<String> itemsList = new LinkedList<String>();
		if (isCollapsed()) {
			final Queue<MenuGroupNode> menu = new Queue<MenuGroupNode>(Context.get().client.getCollapsedMenuItems());
			try {
				for (MenuGroupNode mgn = menu.getHead(); mgn != null; mgn = menu.getNext()) {
					final Queue<MenuItemNode> submenu = new Queue<MenuItemNode>(mgn.getItems());
					for (MenuItemNode min = submenu.getHead(); min != null; min = submenu.getNext()) {
						itemsList.addLast(firstPart ? min.getAction() : min.getOption());
					}
				}
			} catch (final NullPointerException ignored) {
			}
		} else {
			try {
				final Deque<MenuItemNode> menu = new Deque<MenuItemNode>(Context.get().client.getMenuItems());
				for (MenuItemNode min = menu.getHead(); min != null; min = menu.getNext()) {
					itemsList.addLast(firstPart ? min.getAction() : min.getOption());
				}
			} catch (final Throwable ignored) {
			}
		}
		final String[] items = itemsList.toArray(new String[itemsList.size()]);
		final LinkedList<String> output = new LinkedList<String>();
		for (int i = items.length - 1; i >= 0; i--) {
			final String item = items[i];
			output.add(item == null ? "" : stripFormatting(item));
		}
		if (output.size() > 1 && (isCollapsed() ? output.getLast() : output.getFirst()).equals(firstPart ? "Cancel" : "")) {
			Collections.reverse(output);
		}
		return output.toArray(new String[output.size()]);
	}

	/**
	 * Returns an array of the second parts of each item in the current menu
	 * context.
	 *
	 * @return The second half, (Use <tt>Bank</tt>).
	 */
	public static String[] getOptions() {
		return getMenuItemPart(false);
	}

	/**
	 * Returns the menu's item count.
	 *
	 * @return The menu size.
	 */
	public static int getSize() {
		return getItems().length;
	}

	/**
	 * Returns the submenu's location.
	 *
	 * @return The screen space point of the submenu if the menu is collapsed; otherwise null.
	 */
	public static Point getSubMenuLocation() {
		if (isCollapsed()) {
			final Client client = Context.get().client;
			return new Point(client.getSubMenuX() + 4, client.getSubMenuY() + 4);
		}
		return null;
	}

	/**
	 * Checks whether or not the menu is collapsed.
	 *
	 * @return <tt>true</tt> if the menu is collapsed; otherwise <tt>false</tt>.
	 */
	public static boolean isCollapsed() {
		return Context.get().client.isMenuCollapsed();
	}

	/**
	 * Checks whether or not the menu is open.
	 *
	 * @return <tt>true</tt> if the menu is open; otherwise <tt>false</tt>.
	 */
	public static boolean isOpen() {
		return Context.get().client.isMenuOpen();
	}

	/**
	 * Strips HTML tags.
	 *
	 * @param input The string you want to parse.
	 * @return The parsed {@code String}.
	 */
	private static String stripFormatting(final String input) {
		return HTML_TAG.matcher(input).replaceAll("");
	}
}
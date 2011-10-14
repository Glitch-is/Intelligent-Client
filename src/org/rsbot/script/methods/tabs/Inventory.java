package org.rsbot.script.methods.tabs;

import org.rsbot.bot.concurrent.Task;
import org.rsbot.script.methods.Game;
import org.rsbot.script.methods.Menu;
import org.rsbot.script.methods.input.Mouse;
import org.rsbot.script.methods.ui.Interfaces;
import org.rsbot.script.util.Filter;
import org.rsbot.script.wrappers.*;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Inventory tab related operations.
 */
public class Inventory {
	public static final int WIDGET = 679;
	public static final int WIDGET_PRICE_CHECK = 204;
	public static final int WIDGET_EQUIPMENT_BONUSES = 670;
	public static final int WIDGET_BANK = 763;
	public static final int WIDGET_EXCHANGE = 644;
	public static final int WIDGET_SHOP = 621;
	public static final int WIDGET_DUNGEONEERING_SHOP = 957;
	public static final int WIDGET_BEAST_OF_BURDEN_STORAGE = 665;

	public static final int[] ALT_WIDGETS = {WIDGET_PRICE_CHECK,
			WIDGET_EQUIPMENT_BONUSES, WIDGET_BANK, WIDGET_EXCHANGE,
			WIDGET_SHOP, WIDGET_DUNGEONEERING_SHOP,
			WIDGET_BEAST_OF_BURDEN_STORAGE};

	/**
	 * Clicks on the selected inventory item.
	 *
	 * @param leftClick <tt>true</tt> to left-click otherwise; <tt>false</tt> to right-click
	 * @return <tt>true</tt> if the inventory item was clicked on; otherwise <tt>false</tt>
	 */
	public static boolean clickSelectedItem(boolean leftClick) {
		Item item = getSelectedItem();
		return item != null && item.click(leftClick);
	}

	/**
	 * Left-clicks on the selected inventory item.
	 *
	 * @return <tt>true</tt> if the selected inventory item was clicked on; otherwise </tt>false</tt>
	 * @see #clickSelectedItem(boolean)
	 */
	public static boolean clickSelectedItem() {
		return clickSelectedItem(true);
	}

	/**
	 * Checks whether the inventory contains the provided item id.
	 *
	 * @param itemId the item id to look for
	 * @return <tt>true</tt> if the inventory contains the provided item id; otherwise <tt>false</tt>
	 * @see #containsOneOf(int...)
	 * @see #containsAll(int...)
	 */
	public static boolean contains(int itemId) {
		return getItem(itemId) != null;
	}

	/**
	 * Checks whether or not your inventory contains the provided item name.
	 *
	 * @param name The item(s) you wish to evaluate.
	 * @return <tt>true</tt> if your inventory contains an item with the name provided; otherwise <tt>false</tt>.
	 */
	public static boolean contains(final String name) {
		return getItem(name) != null;
	}

	/**
	 * Checks whether the inventory contains all of the provided item ids.
	 *
	 * @param itemIds the item ids to look for
	 * @return <tt>true</tt> if the inventory contains all of the provided item ids; otherwise <tt>false</tt>
	 * @see #containsOneOf(int...)
	 */
	public static boolean containsAll(int... itemIds) {
		for (int itemId : itemIds) {
			if (getItem(itemId) == null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks whether the inventory contains all of the provided item ids.
	 *
	 * @param ids   the item ids to look for
	 * @param count the item ids count to look for
	 * @return <tt>true</tt> if the inventory contains all of the provided item ids; otherwise <tt>false</tt>
	 * @see #containsOneOf(int...)
	 */
	public static boolean containsAll(final int[] ids, final int[] count) {
		int i = 0;
		for (int itemId : ids) {
			if (getItem(itemId) == null || getItem(itemId).getStackSize() < count[i]) {
				return false;
			}
			i++;
		}
		return true;
	}

	/**
	 * Checks whether the inventory contains one of the provided item ids.
	 *
	 * @param itemIds the item ids to check for
	 * @return <tt>true</tt> if the inventory contains one of the provided items; otherwise <tt>false</tt>
	 * @see #containsAll(int...)
	 */
	public static boolean containsOneOf(final int... itemIds) {
		return getItems(itemIds).length > 0;
	}

	/**
	 * Drags an item to the specified slot. Slot must be in the range of 0 and
	 * 27.
	 *
	 * @param itemId the item id
	 * @param slot   the slot
	 * @return <tt>true</tt> if dragged; otherwise <tt>false</tt>
	 */
	public static boolean drag(int itemId, int slot) {
		return drag(getItem(itemId), slot);
	}

	/**
	 * Drags an item to the specified inventory slot, which must be in the range
	 * of 0 and 27.
	 *
	 * @param item    the inventory item
	 * @param invSlot the inventory slot
	 * @return <tt>true</tt> if dragged; otherwise <tt>false</tt>
	 */
	public static boolean drag(Item item, int invSlot) {
		if (item != null) {
			if (invSlot >= 0 && invSlot <= 27) {
				InterfaceComponent slot = getComponent().getComponents()[invSlot];
				if (slot != null) {
					Rectangle slotRectangle = slot.getBoundingRect();
					Rectangle itemRectangle = item.getComponent().getBoundingRect();
					if (slotRectangle.contains(itemRectangle)) {
						return true;
					}
					Mouse.move(new Point((int) itemRectangle.getCenterX(), (int) itemRectangle.getCenterY()), 5, 5);
					Mouse.drag(new Point((int) slotRectangle.getCenterX(), (int) slotRectangle.getCenterY()), 0, 0);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Drops all inventory items excepting those matching with any of the
	 * provided ids.
	 *
	 * @param leftToRight <tt>true</tt> to span row by row (horizontal precedence);
	 *                    <tt>false</tt> to span column by column (vertical precedence).
	 * @param itemIds     the item ids to exclude
	 */
	public static void dropAllExcept(boolean leftToRight, int... itemIds) {
		if (getCountExcept(itemIds) != 0) {
			if (!leftToRight) {
				for (int c = 0; c < 4; c++) {
					for (int r = 0; r < 7; r++) {
						boolean found = false;
						for (int i = 0; i < itemIds.length && !found; ++i) {
							found = itemIds[i] == getAllItems()[c + r * 4].getID();
						}
						if (!found) {
							dropItem(c, r);
						}
					}
				}
			} else {
				for (int r = 0; r < 7; r++) {
					for (int c = 0; c < 4; c++) {
						boolean found = false;
						for (int i = 0; i < itemIds.length && !found; ++i) {
							found = itemIds[i] == getAllItems()[c + r * 4].getID();
						}
						if (!found) {
							dropItem(c, r);
						}
					}
				}
			}
			Task.sleep(Task.random(500, 800));
		}
	}

	/**
	 * Drops all inventory items vertically (going down the inventory) excepting
	 * those matching with any of the provided ids.
	 *
	 * @param itemIds the item ids to exclude
	 * @see #dropAllExcept(boolean, int...)
	 */
	public static void dropAllExcept(int... itemIds) {
		dropAllExcept(false, itemIds);
	}

	/**
	 * Drops the inventory item of the specified column and row.
	 *
	 * @param col the column the inventory item is in
	 * @param row the row the inventory item is in
	 */
	public static void dropItem(int col, int row) {
		if (col < 0 || col > 3 || row < 0 || row > 6) {
			return;
		}
		if (getAllItems()[col + row * 4].getID() == -1) {
			return;
		}
		Point p;
		p = Mouse.getLocation();
		if (p.x < 563 + col * 42 || p.x >= 563 + col * 42 + 32
				|| p.y < 213 + row * 36 || p.y >= 213 + row * 36 + 32) {
			if (!Mouse.moveAndApply(getComponent().getComponents()[row * 4 + col], new Filter<Point>() {
				public boolean accept(Point point) {
					return Menu.contains("Drop");
				}
			})) {
				return;
			}
		}
		Mouse.click(false);
		Task.sleep(Task.random(10, 25));
		Menu.click("Drop");
		Task.sleep(Task.random(25, 50));
	}

	/**
	 * Gets all the inventory items. If the tab is not currently open, it will
	 * not open it and will return the last known array of items.
	 *
	 * @return an array instance of <code>Item</code>
	 */
	public static Item[] getCachedItems() {
		InterfaceComponent invIface = Interfaces.getComponent(WIDGET, 0);
		if (invIface != null) {
			InterfaceComponent[] components = invIface.getComponents();
			if (components.length > 0) {
				List<Item> items = new LinkedList<Item>();
				for (int i = 0; i < 28; ++i) {
					if (components[i].getComponentID() != -1) {
						items.add(new Item(components[i]));
					}
				}
				return items.toArray(new Item[items.size()]);
			}
		}
		return new Item[0];
	}

	/**
	 * Gets the inventory component.
	 *
	 * @return the inventory component
	 */
	public static InterfaceComponent getComponent() {
		for (int widget : ALT_WIDGETS) {
			InterfaceComponent inventory = Interfaces.getComponent(widget, 0);
			if (inventory != null && inventory.getAbsLocation().x > 50) {
				return inventory;
			}
		}

		// Tab has to be open for us to getAntibans content
		openTab();

		return Interfaces.getComponent(WIDGET, 0);
	}

	/**
	 * Gets the count of all inventory items ignoring stack sizes.
	 *
	 * @return the count
	 * @see #getCount(boolean)
	 */
	public static int getCount() {
		return getCount(false);
	}

	/**
	 * Gets the count of all inventory items.
	 *
	 * @param includeStacks <tt>false</tt> if stacked items should be counted as single items; otherwise <tt>true</tt>
	 * @return the count
	 */
	public static int getCount(boolean includeStacks) {
		int count = 0;
		Item[] items = getItems();
		for (Item item : items) {
			if (item == null) {
				continue;
			}
			int itemId = item.getID();
			if (itemId != -1) {
				count += includeStacks ? item.getStackSize() : 1;
			}
		}
		return count;
	}

	/**
	 * Gets the count of all the inventory items matching with any of the
	 * provided ids ignoring stack sizes.
	 *
	 * @param itemIds the item ids to include
	 * @return the count
	 * @see #getCount(boolean, int...)
	 */
	public static int getCount(int... itemIds) {
		return getCount(false, itemIds);
	}

	/**
	 * Gets the count of all the inventory items matching with any of the
	 * provided ids.
	 *
	 * @param includeStacks <tt>true</tt> to count the stack sizes of each item; otherwise <tt>false</tt>
	 * @param itemIds       the item ids to include
	 * @return the count
	 */
	public static int getCount(boolean includeStacks, int... itemIds) {
		int count = 0;
		Item[] items = getItems(itemIds);
		for (Item item : items) {
			if (item == null) {
				continue;
			}
			int itemId = item.getID();
			if (itemId != -1) {
				count += includeStacks ? item.getStackSize() : 1;
			}
		}
		return count;
	}

	/**
	 * Gets the count of all the inventory items excluding the provided ids
	 * ignoring stack sizes.
	 *
	 * @param ids the ids to exclude
	 * @return the count
	 * @see #getCountExcept(boolean, int...)
	 */
	public static int getCountExcept(int... ids) {
		return getCountExcept(false, ids);
	}

	/**
	 * Gets the count of all the inventory items excluding any of the provided
	 * ids.
	 *
	 * @param includeStacks <tt>true</tt> to count the stack sizes of each item; otherwise <tt>false</tt>
	 * @param ids           the ids to exclude
	 * @return the count
	 */
	public static int getCountExcept(boolean includeStacks, int... ids) {
		int count = 0;
		Item[] items = getItems();
		outer:
		for (Item item : items) {
			if (item == null) {
				continue;
			}
			int itemId = item.getID();
			for (int id : ids) {
				if (itemId == id) {
					continue outer;
				}
			}
			count += includeStacks ? item.getStackSize() : 1;
		}
		return count;
	}

	/**
	 * Gets the first inventory item matching with any of the provided ids.
	 *
	 * @param ids the ids to look for
	 * @return the first inventory item matching with any of the provided ids; otherwise <code>null</code>
	 */
	public static Item getItem(int... ids) {
		Item[] items = getItems(ids);
		for (Item item : items) {
			if (item != null) {
				return item;
			}
		}
		return null;
	}

	/**
	 * Gets the first item in the inventory containing any of the provided names.
	 *
	 * @param names The names of the item to find.
	 * @return The first <tt>Item</tt> for the given name(s); otherwise null.
	 */
	public static Item getItem(final String... names) {
		for (final org.rsbot.script.wrappers.Item item : getItems()) {
			String name = item.getName();
			if (name != null) {
				name = name.toLowerCase();
				for (final String n : names) {
					if (n != null && name.contains(n.toLowerCase())) {
						return item;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Gets the inventory item at the specified index.
	 *
	 * @param index the index of the inventory item
	 * @return the <code>Item</code>; otherwise <code>null</code> if invalid
	 */
	public static Item getItemAt(int index) {
		InterfaceComponent comp = getComponent().getComponent(index);
		return 0 <= index && index < 28 && comp != null && comp.getComponentID() != -1 ? new Item(comp) : null;
	}

	/**
	 * Gets all the valid inventory items.
	 *
	 * @return an array instance of <code>Item</code> of the current valid inventory items
	 */
	public static Item[] getItems() {
		InterfaceComponent invIface = getComponent();
		if (invIface != null) {
			InterfaceComponent[] comps = invIface.getComponents();
			if (comps.length > 27) {
				List<Item> items = new LinkedList<Item>();
				for (int i = 0; i < 28; ++i) {
					if (comps[i].getComponentID() != -1) {
						items.add(new Item(comps[i]));
					}
				}
				return items.toArray(new Item[items.size()]);
			}
		}
		return new Item[0];
	}

	/**
	 * Gets all the inventory items (including empty ones).
	 *
	 * @return an array instance of <code>Item</code> of the current inventory items
	 */
	public static Item[] getAllItems() {
		Item[] items = new Item[28];
		InterfaceComponent invIface = getComponent();
		if (invIface != null) {
			InterfaceComponent[] comps = invIface.getComponents();
			if (comps.length > 27) {
				for (int i = 0; i < 28; ++i) {
					items[i] = new Item(comps[i]);
				}
			}
		}
		return items;
	}

	/**
	 * Gets all the inventory items matching with any of the provided ids.
	 *
	 * @param ids the item ids
	 * @return an array instance of <code>Item</code>
	 */
	public static Item[] getItems(int... ids) {
		InterfaceComponent invIface = getComponent();
		if (invIface != null) {
			InterfaceComponent[] comps = invIface.getComponents();
			if (comps.length > 27) {
				List<Item> items = new LinkedList<Item>();
				for (int i = 0; i < 28; ++i) {
					if (comps[i].getComponentID() == -1) {
						continue;
					}
					Item currItem = new Item(comps[i]);
					int itemID = currItem.getID();
					for (int id : ids) {
						if (itemID == id) {
							items.add(currItem);
							break;
						}
					}
				}
				return items.toArray(new Item[items.size()]);
			}
		}
		return new Item[0];
	}

	/**
	 * Gets the first id of an inventory item with the given name.
	 *
	 * @param name the name of the inventory item to look for
	 * @return the id of the inventory item; otherwise -1
	 */
	public static int getItemID(String name) {
		for (Item item : getItems()) {
			if (item == null) {
				continue;
			}
			ItemDefinition itemDef = item.getDefinition();
			if (itemDef != null && itemDef.getName().toLowerCase().contains(name.toLowerCase())) {
				return item.getID();
			}
		}
		return -1;
	}

	/**
	 * @param ids the ids of all items to accept
	 * @return the last item in the inventory with an ID equal to accepted
	 */
	public static Item getLast(int... ids) {
		InterfaceComponent invIface = getComponent();
		if (invIface != null) {
			InterfaceComponent[] comps = invIface.getComponents();
			if (comps.length > 27) {
				for (int i = 27; i >= 0; --i) {
					if (comps[i].getComponentID() == -1) {
						continue;
					}
					Item currItem = new Item(comps[i]);
					int itemID = currItem.getID();
					for (int id : ids) {
						if (itemID == id) {
							return currItem;
						}
					}
				}
			}
		}
		return null;
	}


	/**
	 * @param ID the ID of the item to look for
	 * @param n  The Nth occurance of the item to search for
	 * @return the Nth occurance of the item with the provided ID
	 */
	public static Item getNthItem(int ID, int n) {
		Item[] items = getItems(ID);
		return n <= items.length ? items[n - 1] : null;
	}

	/**
	 * Gets the selected inventory item.
	 *
	 * @return the selected inventory item; otherwise <code>null</code> if none is selected
	 */
	public static Item getSelectedItem() {
		int index = getSelectedItemIndex();
		return index == -1 ? null : getItemAt(index);
	}

	/**
	 * Gets the selected inventory item's index.
	 *
	 * @return the index of the current selected inventory item; otherwise -1 if none is selected
	 */
	public static int getSelectedItemIndex() {
		Item[] items = getItems();
		for (Item item : items) {
			if (item == null) {
				continue;
			}
			InterfaceComponent comp = item.getComponent();
			if (comp.getBorderThickness() == 2) {
				return comp.getIndex();
			}
		}
		return -1;
	}

	/**
	 * Returns the index of the first occurrence of an item in the inventory
	 * matching with the provided id.
	 *
	 * @param id the item id
	 * @return the index; otherwise <tt>-1</tt>.
	 */
	public static int indexOf(int id) {
		Item[] items = getItems();
		for (int i = 0; i < items.length; i++) {
			if (id == items[i].getID()) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns the index of the first occurrence of an item in the inventory
	 * matching with the provided name. Case-insensitive.
	 *
	 * @param name the name of the item
	 * @return the index; otherwise <tt>-1</tt>.
	 */
	public static int indexOf(String name) {
		if (name != null && !name.isEmpty()) {
			Item[] items = getItems();
			for (int i = 0; i < items.length; i++) {
				if (items[i].getName().equalsIgnoreCase(name)) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * Checks whether the inventory is full.
	 *
	 * @return <tt>true</tt> if the inventory contains 28 items; otherwise <tt>false</tt>
	 */
	public static boolean isFull() {
		return getCount() == 28;
	}

	/**
	 * Checks whether an inventory item is selected.
	 *
	 * @return <tt>true</tt> if an inventory item is selected; otherwise <tt>false</tt>
	 */
	public static boolean isItemSelected() {
		return getSelectedItemIndex() != -1;
	}

	/**
	 * Opens the inventory tab if not already opened.
	 */
	public static void openTab() {
		Game.Tabs tabInventory = Game.Tabs.INVENTORY;
		if (Game.getCurrentTab() != tabInventory) {
			Game.openTab(tabInventory);
		}
	}

	public static boolean useItem(int id, int ID) {
		return id != ID ? useItem(getItem(id), getItem(ID)) : useItem(getNthItem(ID, 1), getNthItem(ID, 2));
	}

	/**
	 * Uses two inventory items together.
	 *
	 * @param item   the inventory item to use on another inventory item
	 * @param target the other inventory item to be used on
	 * @return <tt>true</tt> if the "Use" action had been used on both inventory items; otherwise <tt>false</tt>
	 */
	public static boolean useItem(Item item, Item target) {
		return item != null && target != null && useItem(item, (Object) target);
	}

	/**
	 * Uses an item on a game object.
	 *
	 * @param item   the item to use
	 * @param target the game object to be used on by the item
	 * @return <tt>true</tt> if the "Use" action had been used on both the inventory item and the game object; otherwise <tt>false</tt>
	 */
	public static boolean useItem(Item item, GameObject target) {
		if (item != null && target != null) {
			for (int i = 0, r = Task.random(5, 8); i < r; i++) {
				if (!isItemSelected()) {
					if (item.interact("Use")) {
						for (int j = 0; j < 10 && !isItemSelected(); j++) {
							Task.sleep(Task.random(100, 200));
						}
					} else {
						return false;
					}
				}
				// just make sure in case something bad happened
				if (isItemSelected()) {
					final String itemName = item.getName();
					final GameObjectDefinition targetDef = target.getDefinition();
					final GameModel targetModel = target.getModel();
					if (targetDef != null && itemName != null && targetModel != null) {
						final String targetName = targetDef.getName();
						targetModel.hover();
						final String action = "Use " + itemName.replace("<col=ff9040>", "") + " -> " + targetName.replace("<col=ff9040>", "");
						for (int j = 0, s = Task.random(5, 8); j < s; j++) {
							if (Menu.contains(action) && Menu.click(action)) {
								return true;
							} else {
								targetModel.hover();
							}
						}
					}
					// kay, since that failed, let's try just use
					if (target.interact("Use")) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Uses an inventory item on either another inventory item or a game object.
	 *
	 * @param item   the inventory item to use
	 * @param target the inventory item or the game object to be used on by the inventory item
	 * @return <tt>true</tt> if the "Use" action had been used on both the
	 *         inventory item and the game object/other inventory item;
	 *         otherwise <tt>false</tt>
	 */
	private static boolean useItem(Item item, Object target) {
		if (isItemSelected()) {
			Item selectedItem = getSelectedItem();
			int selectedItemId = selectedItem.getID();
			if (item.getID() != selectedItemId) {
				if (!selectedItem.interact("Cancel")) {
					return false;
				}
			} else if (target instanceof Item) {
				Item t = (Item) target;
				if (selectedItemId != t.getID()
						&& selectedItemId != item.getID()) {
					if (!selectedItem.interact("Cancel")) {
						return false;
					}
				}
			}
		}
		for (int i = 0, r = Task.random(5, 8); i < r; i++) {
			if (isItemSelected()) {
				boolean success = false;
				for (int j = 0, k = Task.random(5, 8); j < k; j++) {
					try {
						Item t = (Item) target;
						if (t.interact("Use")) {
							success = true;
							break;
						}
						Task.sleep(Task.random(150, 300));
					} catch (final ClassCastException e) {
						return false;
					}
				}
				return success;
			}
			item.interact("Use");
			Task.sleep(Task.random(150, 300));
		}
		return false;
	}
}

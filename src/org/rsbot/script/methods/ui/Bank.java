package org.rsbot.script.methods.ui;

import org.rsbot.bot.concurrent.Task;
import org.rsbot.script.methods.*;
import org.rsbot.script.methods.input.Keyboard;
import org.rsbot.script.methods.input.Mouse;
import org.rsbot.script.methods.tabs.Inventory;
import org.rsbot.script.util.Timer;
import org.rsbot.script.wrappers.Interface;
import org.rsbot.script.wrappers.InterfaceComponent;
import org.rsbot.script.wrappers.Item;
import org.rsbot.script.wrappers.NPC;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Bank widget related operations.
 */
public class Bank {
	// 4456, 4457, 4458, 4459 Objects: 2012 2015 2019 Deposit Box: 2045
	public static final int[] BANKERS = new int[]{44, 45, 494, 495, 496, 497,
			498, 499, 553, 909, 958, 1036, 2271, 2354, 2355, 2759, 3198, 3824,
			4456, 4457, 4458, 4459, 5488, 5901, 5912, 5913, 6362, 6532, 6533, 6534,
			6535, 7605, 8948, 9710, 14367, 3416, 3293, 2718, 3418};
	public static final int[] BANK_BOOTHS = new int[]{782, 2213, 2995, 5276,
			6084, 10517, 11402, 11758, 12759, 14367, 19230, 20325, 24914, 11338,
			25808, 26972, 29085, 52589, 34752, 35647, 36786, 2012, 2015, 2019, 42217, 42377, 42378};
	public static final int[] BANK_CHESTS = new int[]{4483, 12308, 20607, 21301, 27663, 42192};
	public static final int[] DO_NOT_DEPOSIT = new int[]{1265, 1267, 1269, 1273, 1271, 1275, 1351, 590, 303};
	public static final int WIDGET = 762;
	public static final int BUTTON_CLOSE = 43;
	public static final int BUTTON_DEPOSIT_BEAST = 38;
	public static final int BUTTON_DEPOSIT_CARRIED = 34;
	public static final int BUTTON_DEPOSIT_WORN = 36;
	public static final int BUTTON_HELP = 44;
	public static final int BUTTON_INSERT = 15;
	public static final int BUTTON_ITEM = 19;
	public static final int BUTTON_NOTE = 19;
	public static final int BUTTON_SEARCH = 17;
	public static final int BUTTON_SWAP = 15;
	public static final int BUTTON_OPEN_EQUIPMENT = 117;
	public static final int COMPONENT_INVENTORY = 93;
	public static final int COMPONENT_F2P_ITEM_COUNT = 29;
	public static final int COMPONENT_F2P_ITEM_MAX = 30;
	public static final int COMPONENT_P2P_ITEM_COUNT = 31;
	public static final int COMPONENT_P2P_ITEM_MAX = 32;
	public static final int COMPONENT_SCROLL_BAR = 114;
	public static final int COMPONENT_SEARCH = 752;
	public static final int COMPONENT_SEARCH_INPUT = 5;
	public static final int SETTING_BANK_STATE = 1248;
	public static final int WIDGET_EQUIP = 667;
	public static final int COMPONENT_EQUIP_INVENTORY = 7;
	public static final int WIDGET_COLLECTION_BOX = 105;
	public static final int BUTTON_COLLECTION_BOX_CLOSE = 13;
	public static final int[] COMPONENT_BANK_TABS = new int[]{63, 61, 59, 57, 55, 53, 51, 49, 47};
	public static final int[] COMPONENT_BANK_FIRST_ITEMS = new int[]{78, 79, 80, 81, 82, 83, 84, 85, 86};

	/**
	 * Closes the bank. Supports deposit boxes.
	 *
	 * @return <tt>true</tt> if the bank is no longer open; otherwise <tt>false</tt>
	 */
	public static boolean close() {
		if (isOpen()) {
			Interfaces.getComponent(WIDGET, BUTTON_CLOSE).click(true);
			Task.sleep(Task.random(500, 600));
			return !isOpen();
		}
		return !org.rsbot.script.methods.ui.DepositBox.isOpen() || org.rsbot.script.methods.ui.DepositBox.close();
	}

	/**
	 * Tries to deposit an item.
	 *
	 * @param itemId the item id
	 * @param count  the amount to deposit. 0 deposits All. 1,5,10 deposit
	 *               corresponding amount while other numbers deposit X
	 * @return <tt>true</tt> if successful; otherwise <tt>false</tt>
	 */
	public static boolean deposit(int itemId, int count) {
		if (isOpen()) {
			if (count >= 0) {
				Item item = Inventory.getItem(itemId);
				if (item != null) {
					int invCount = Inventory.getCount(true);
					int itemCount = Inventory.getCount(true, itemId);
					switch (count) {
						case 0:
						case 1:
						case 5:
							String action = itemCount > 1 ? count == 0 ? "-All"
									: "-" + count : "";
							if (item.interact("Deposit" + action)) {
								break;
							}
							return false;
						default:
							if (!item.interact("Deposit-" + count)) {
								if (item.interact("Deposit-X")) {
									Task.sleep(Task.random(1000, 1300));
									Keyboard.sendText(String.valueOf(count), true);
								}
							}
					}
					for (int i = 0; i < 1500; i += 20) {
						Task.sleep(20);
						int newInvCount = Inventory.getCount(true);
						if (invCount < invCount || newInvCount == 0) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Deposits all items.
	 *
	 * @return <tt>true</tt> if the button was clicked; otherwise <tt>false</tt>
	 */
	public static boolean depositAll() {
		return isOpen()
				&& Interfaces.getComponent(WIDGET, BUTTON_DEPOSIT_CARRIED).click(true);
	}

	/**
	 * Deposit everything worn by the local player.
	 *
	 * @return <tt>true</tt> if the button was clicked; otherwise <tt>false</tt>
	 */
	public static boolean depositAllEquipped() {
		return isOpen()
				&& Interfaces.getComponent(WIDGET, BUTTON_DEPOSIT_WORN).click(true);
	}

	/**
	 * Deposits all items in inventory except for the given ids.
	 *
	 * @param itemIds the item ids to exclude
	 * @return <tt>true</tt> if the items were deposited; otherwise
	 *         <tt>false</tt>
	 */
	public static boolean depositAllExcept(int... itemIds) {
		if (isOpen()) {
			if (!Inventory.containsOneOf(itemIds)) {
				return depositAll();
			}
			boolean deposit = true;
			int invCount = Inventory.getCount(true);
			outer:
			for (int i = 0; i < 28; i++) {
				try {
					InterfaceComponent item = Inventory.getItemAt(i).getComponent();
					if (item != null && item.getComponentID() != -1) {
						for (int id : itemIds) {
							if (item.getComponentID() == id) {
								continue outer;
							}
						}
						for (int tries = 0; tries < 5; tries++) {
							deposit(item.getComponentID(), 0);
							Task.sleep(Task.random(600, 900));
							int cInvCount = Inventory.getCount(true);
							if (cInvCount < invCount) {
								invCount = cInvCount;
								continue outer;
							}
						}
						deposit = false;
					}
				} catch (final NullPointerException ignored) {
				}
			}
			return deposit;
		}
		return false;
	}

	/**
	 * Deposits everything the familiar is carrying.
	 *
	 * @return <tt>true</tt> if the button was clicked; otherwise <tt>false</tt>
	 */
	public static boolean depositAllFamiliar() {
		return isOpen() && Interfaces.getComponent(WIDGET, BUTTON_DEPOSIT_BEAST).click(true);
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
	 * Gets the count of all items.
	 *
	 * @param includeStacks <tt>false</tt> if stacked items should be counted as single
	 *                      items; otherwise <tt>true</tt>
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
	 * Gets the count of all the items matching with any of the provided ids
	 * including stack sizes.
	 *
	 * @param itemIds the item ids to include
	 * @return the count
	 * @see #getCount(boolean, int...)
	 */
	public static int getCount(int... itemIds) {
		return getCount(true, itemIds);
	}

	/**
	 * Gets the count of all the items matching with any of the provided ids.
	 *
	 * @param includeStacks <tt>true</tt> to count the stack sizes of each item; otherwise
	 *                      <tt>false</tt>
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
	 * Gets the count of all the items excluding the provided ids and ignoring
	 * stack sizes.
	 *
	 * @param ids the ids to exclude
	 * @return the count
	 * @see #getCountExcept(boolean, int...)
	 */
	public static int getCountExcept(int... ids) {
		return getCountExcept(false, ids);
	}

	/**
	 * Gets the count of all the items excluding any of the provided ids.
	 *
	 * @param includeStacks <tt>true</tt> to count the stack sizes of each item; otherwise
	 *                      <tt>false</tt>
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
	 * Get current tab open in the bank.
	 *
	 * @return the index of the current tab open (0-8), or -1 if none.
	 */
	public static int getCurrentTab() {
		return ((Settings.get(SETTING_BANK_STATE) >>> 24) - 136) / 8;
	}

	/**
	 * Gets the worn items from one of the bank's components.
	 *
	 * @return an array instance of <code>Item</code>
	 */
	public static Item[] getEquipped() {
		InterfaceComponent equipComp = Interfaces.getComponent(WIDGET_EQUIP, COMPONENT_EQUIP_INVENTORY);
		if (equipComp != null && equipComp.isValid()) {
			InterfaceComponent[] components = equipComp.getComponents();
			Item[] items = new Item[components.length];
			for (int i = 0; i < items.length; i++) {
				items[i] = new Item(components[i]);
			}
			return items;
		}
		return new Item[0];
	}

	/**
	 * Gets a worn item matching with the provided id.
	 *
	 * @param id the item id
	 * @return an <code>Item</code>; otherwise <code>null</code> if invalid
	 */
	public static Item getEquipped(int id) {
		Item[] items = getEquipped();
		if (items != null) {
			for (Item item : items) {
				if (item.getID() == id) {
					return item;
				}
			}
		}
		return null;
	}

	/**
	 * Gets the first item matching with any of the provided ids.
	 *
	 * @param ids the ids to look for
	 * @return the first inventory item matching with any of the provided ids;
	 *         otherwise <code>null</code>
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
	 * Gets the item at the specified index.
	 *
	 * @param index the index of the item
	 * @return the <code>Item</code>; otherwise <code>null</code> if invalid
	 */
	public static Item getItemAt(int index) {
		Interface w = getInterface();
		if (w != null) {
			try {
				InterfaceComponent c = w.getComponent(COMPONENT_INVENTORY)
						.getComponents()[index];
				if (c != null) {
					return new Item(c);
				}
			} catch (IndexOutOfBoundsException ignored) {
			}
		}
		return null;
	}

	/**
	 * Gets all the items in the bank.
	 *
	 * @return an array instance of <code>Item</code> of the items in the bank.
	 */
	public static Item[] getItems() {
		Interface widget = getInterface();
		if (widget != null) {
			InterfaceComponent component = widget.getComponent(COMPONENT_INVENTORY);
			if (component != null) {
				List<Item> items = new LinkedList<Item>();
				for (InterfaceComponent c : component.getComponents()) {
					if (c != null && c.getComponentID() != -1) {
						items.add(new Item(c));
					}
				}
				return items.toArray(new Item[items.size()]);
			}
		}
		return new Item[0];
	}

	/**
	 * Gets all the items matching with any of the provided ids.
	 *
	 * @param ids the item ids
	 * @return an array instance of <code>Item</code>
	 */
	public static Item[] getItems(int... ids) {
		List<Item> items = new LinkedList<Item>();
		for (Item item : getItems()) {
			if (item == null) {
				continue;
			}
			int itemId = item.getID();
			for (int id : ids) {
				if (itemId == id) {
					items.add(item);
					break;
				}
			}
		}
		return items.toArray(new Item[items.size()]);
	}

	/**
	 * Gets the bank widget.
	 *
	 * @return the bank <code>Interface</code>
	 */
	public static Interface getInterface() {
		return Interfaces.get(WIDGET);
	}

	/**
	 * Checks whether the bank is open.
	 *
	 * @return <tt>true</tt> if the bank widget is valid; otherwise
	 *         <tt>false</tt>
	 */
	public static boolean isOpen() {
		final Interface widget = getInterface();
		return widget.isValid() && widget.getChildCount() > 0 && !widget.getComponent(0).getAbsLocation().equals(new Point(0, 0));
		// return Settings.get(SETTING_BANK_STATE) == 0x88000000;
	}

	/**
	 * Checks whether the widget for searching the bank is valid.
	 *
	 * @return <tt>true</tt> if currently searching the bank; otherwise
	 *         <tt>false</tt>
	 */
	public static boolean isSearchOpen() {
		// Setting SETTING_BANK_STATE is -2147483648 when search is enabled and -2013265920
		return Settings.get(SETTING_BANK_STATE) == 0x80000000;
	}

	/**
	 * Tries to open the bank through one of the supported nearby bankers,
	 * booths or chests.
	 *
	 * @return <tt>true</tt> if the bank was opened; otherwise <tt>false</tt>
	 */
	public static boolean open() {
		if (!isOpen()) {
			Interactable i = null;
			final int interactChoice = Task.random(0, 2);
			switch (interactChoice) {
				case 0:
					i = new Npc("Bank ", BANKERS);
					break;
				case 1:
					i = new GameObject("Use-quickly", BANK_BOOTHS);
					break;
			}
			if (i == null || !i.isValid() || !i.isOnScreen()) {
				// now lets try hierarchy
				i = new GameObject("Use-quickly", BANK_BOOTHS);
				if (!i.isValid() || !i.isOnScreen()) {
					i = new Npc("Bank ", BANKERS);
					if (!i.isValid() || !i.isOnScreen()) {
						i = new GameObject("Bank", BANK_CHESTS);
					}
				}
			}
			if (!i.isValid()) {
				return false;
			}
			if (i.interact()) {
				if (i.getDistance() > 1) {
					long time = System.currentTimeMillis();
					int max = Task.random(2000, 4000);
					while ((System.currentTimeMillis() - time) < max) {
						if (Players.getLocal().isMoving()) {
							do {
								Task.sleep(Task.random(5, 15));
							} while (Players.getLocal().isMoving() || !i.isOnScreen());
							break;
						}
						Task.sleep(Task.random(5, 15));
					}
				}
				for (int j = 0; j < 10 && !isOpen(); j++) {
					Task.sleep(Task.random(100, 200));
				}
				// Ensures that the widget becomes valid
				Task.sleep(Task.random(700, 900));
				return isOpen();
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * Opens the equipment tab.
	 *
	 * @return <tt>true</tt> if the button was clicked; otherwise <tt>false</tt>
	 */
	public static boolean openEquipment() {
		InterfaceComponent c = getInterface().getComponent(BUTTON_OPEN_EQUIPMENT);
		return c.isValid() && c.click(true);
	}

	/**
	 * Searches for an item in the bank.
	 *
	 * @param itemName the name of the item to search for
	 * @return <tt>true</tt> if successfully searched; otherwise <tt>false</tt>
	 */
	public static boolean search(String itemName) {
		if (isOpen()) {
			InterfaceComponent c = getInterface().getComponent(BUTTON_SEARCH);
			if (c != null && c.interact("Search")) {
				for (int i = 0; i < 15 && !isSearchOpen(); i++) {
					Task.sleep(20);
				}
				if (isSearchOpen()) {
					Keyboard.sendText(itemName, false);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Sets the bank rearrange mode to insert.
	 *
	 * @return <tt>true</tt> on success; otherwise <tt>false</tt>
	 */
	public static boolean setRearrangeModeToInsert() {
		if (isOpen()) {
			if (Settings.get(Settings.BANK_REARRANGE_MODE) != 1) {
				InterfaceComponent c = getInterface().getComponent(BUTTON_INSERT);
				if (c != null) {
					c.click(true);
					for (int i = 0; i < 15 && Settings.get(Settings.BANK_REARRANGE_MODE) != 1; i++) {
						Task.sleep(20);
					}
				}
			}
		}
		return Settings.get(Settings.BANK_REARRANGE_MODE) == 1;
	}

	/**
	 * Sets the bank rearrange mode to swap.
	 *
	 * @return <tt>true</tt> on success; otherwise <tt>false</tt>
	 */
	public static boolean setRearrangeModeToSwap() {
		if (isOpen()) {
			if (Settings.get(Settings.BANK_REARRANGE_MODE) != 0) {
				InterfaceComponent c = getInterface().getComponent(BUTTON_SWAP);
				if (c != null) {
					c.click(true);
					for (int i = 0; i < 15 && Settings.get(Settings.BANK_REARRANGE_MODE) != 0; i++) {
						Task.sleep(20);
					}
				}
			}
		}
		return Settings.get(Settings.BANK_REARRANGE_MODE) == 0;
	}

	/**
	 * Sets the bank withdraw mode to item.
	 *
	 * @return <tt>true</tt> on success; otherwise <tt>false</tt>
	 */
	public static boolean setWithdrawModeToItem() {
		if (isOpen()) {
			if (Settings.get(Settings.BANK_WITHDRAW_MODE) != 0) {
				InterfaceComponent c = getInterface().getComponent(BUTTON_ITEM);
				if (c != null) {
					c.click(true);
					for (int i = 0; i < 15 && Settings.get(Settings.BANK_WITHDRAW_MODE) != 0; i++) {
						Task.sleep(20);
					}
				}
			}
		}
		return Settings.get(Settings.BANK_REARRANGE_MODE) == 0;
	}

	/**
	 * Sets the bank withdraw mode to note.
	 *
	 * @return <tt>true</tt> on success; otherwise <tt>false</tt>
	 */
	public static boolean setWithdrawModeToNote() {
		if (isOpen()) {
			if (Settings.get(Settings.BANK_WITHDRAW_MODE) != 1) {
				InterfaceComponent c = getInterface().getComponent(BUTTON_ITEM);
				if (c != null) {
					c.click(true);
					for (int i = 0; i < 15 && Settings.get(Settings.BANK_WITHDRAW_MODE) != 1; i++) {
						Task.sleep(20);
					}
				}
			}
		}
		return Settings.get(Settings.BANK_REARRANGE_MODE) == 1;
	}

	/**
	 * Tries to withdraw an item. -1 is All but one. 0 is All. 1, 5 and 10 use the respective
	 * options while other numbers use "Withdraw-X".
	 *
	 * @param itemId the item id
	 * @param count  the number to withdraw
	 * @return <tt>true</tt> if withdrawn; otherwise <tt>false</tt>
	 */
	public static boolean withdraw(int itemId, int count) {
		if (isOpen()) {
			if (count >= -1) {
				Interface widget = getInterface();
				Item item = getItem(itemId);
				if (item != null) {
					InterfaceComponent itemComponent = item.getComponent();
					if (itemComponent == null) {
						return false;
					}
					if (itemComponent.getRelativeX() == 0) {
						InterfaceComponent bankTab = widget.getComponent(COMPONENT_BANK_TABS[0]);
						if (bankTab.isValid()) {
							bankTab.click(true);
							Task.sleep(Task.random(1000, 1300));
						}
					}
					InterfaceComponent container = widget.getComponent(93);
					Rectangle rectangle = container.getBoundingRect();
					if (!rectangle.contains(itemComponent.getBoundingRect())) {
						Point p = container.getAbsLocation();
						Mouse.move(Task.random(p.x, p.x + rectangle.width, rectangle.width / 2), Task.random(p.y, p.y + rectangle.height, rectangle.height / 2));
						Timer limit = new Timer(5000);
						while (!rectangle.contains(itemComponent.getBoundingRect()) && limit.isRunning()) {
							Mouse.scroll(itemComponent.getAbsLocation().y < container.getAbsLocation().y);
							Task.sleep(Task.random(20, 150));
						}
						if (!rectangle.contains(itemComponent.getBoundingRect())) {
							return false;
						}
					}
					int invCount = Inventory.getCount(true);
					countSwitch:
					switch (count) {
						case -1:
							itemComponent.interact("Withdraw-All but one");
							break;
						case 1:
							itemComponent.click(true);
							break;
						case 0:
							itemComponent.interact("Withdraw-All");
							break;
						default:
							String exactAction = "Withdraw-" + count;
							String[] actions = item.getComponent().getActions();
							if (actions != null && actions.length > 0) {
								for (final String action : item.getComponent().getActions()) {
									if (action != null && action.equals(exactAction) && item.interact(action)) {
										break countSwitch;
									}
								}
								if (itemComponent.interact("Withdraw-X")) {
									Task.sleep(Task.random(1000, 1300));
									Keyboard.sendText(String.valueOf(count), true);
								}
							} else {
								return false;
							}
					}
					for (int i = 0; i < 1500; i += 20) {
						Task.sleep(20);
						int newInvCount = Inventory.getCount(true);
						if (newInvCount > invCount || Inventory.isFull()) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	private static interface Interactable {
		public int getDistance();

		boolean interact();

		boolean isOnScreen();

		boolean isValid();
	}

	private static class GameObject implements Interactable {

		private final String action;

		private final org.rsbot.script.wrappers.GameObject gameObject;

		private GameObject(String action, int... ids) {
			this.action = action;
			gameObject = Objects.getNearest(ids);
		}

		public int getDistance() {
			return Calculations.distanceTo(gameObject);
		}

		public boolean interact() {
			return gameObject.interact(action);
		}

		public boolean isOnScreen() {
			return gameObject.isOnScreen();
		}

		public boolean isValid() {
			return gameObject != null;
		}

	}

	private static class Npc implements Interactable {
		private final String action;

		private final NPC npc;

		public Npc(String action, int... ids) {
			this.action = action;
			npc = NPCs.getNearest(ids);
		}

		public int getDistance() {
			return Calculations.distanceTo(npc);
		}

		public boolean interact() {
			return npc.interact(action);
		}

		public boolean isOnScreen() {
			return npc.isOnScreen();
		}

		public boolean isValid() {
			return npc != null;
		}
	}
}
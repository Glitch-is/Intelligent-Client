package org.rsbot.script.methods.ui;

import org.rsbot.bot.concurrent.Task;
import org.rsbot.script.methods.*;
import org.rsbot.script.methods.input.Keyboard;
import org.rsbot.script.methods.input.Mouse;
import org.rsbot.script.wrappers.GameObject;
import org.rsbot.script.wrappers.Interface;
import org.rsbot.script.wrappers.InterfaceComponent;
import org.rsbot.script.wrappers.Item;

import java.util.LinkedList;

/**
 * Deposit Box related operations.
 */
public class DepositBox {

	public static final int[] DEPOSIT_BOXES = {2045, 9398, 20228, 24995, 25937, 26969, 32924, 32930, 32931, 34755, 36788, 39830, 45079};
	public static final int WIDGET_DEPOSIT_BOX = 11;
	public static final int BUTTON_DEPOSIT_BOX_CLOSE = 15;
	public static final int COMPONENT_INVENTORY = 17;
	public static final int BUTTON_DEPOSIT_BOX_DEPOSIT_BEAST = 23;
	public static final int BUTTON_DEPOSIT_BOX_DEPOSIT_CARRIED = 19;
	public static final int BUTTON_DEPOSIT_BOX_DEPOSIT_WORN = 21;

	/**
	 * Gets the deposit box widget.
	 *
	 * @return The deposit box <code>Interface</code>.
	 */
	public static Interface getInterface() {
		return Interfaces.get(WIDGET_DEPOSIT_BOX);
	}

	/**
	 * Gets the inventory component of the deposit box.
	 *
	 * @return The deposit box's inventory <code>Component</code>.
	 */
	public static InterfaceComponent getComponent() {
		return Interfaces.getComponent(WIDGET_DEPOSIT_BOX, COMPONENT_INVENTORY);
	}

	/**
	 * If deposit box is open, deposits specified amount of an item.
	 *
	 * @param itemID The ID of the item.
	 * @param number The amount to deposit. 0 deposits All. 1,5,10 deposit
	 *               corresponding amount while other numbers deposit X.
	 * @return <tt>true</tt> if successful; otherwise <tt>false</tt>.
	 */
	public static boolean deposit(int itemID, int number) {
		if (isOpen()) {
			if (number < 0) {
				throw new IllegalArgumentException("number < 0 (" + number + ")");
			}
			int invCount = getCount(true);
			Item item = getItem(itemID);
			if (item == null) {
				return false;
			}
			int itemCount = getCount(true, itemID);
			if (number == getCount(true, itemID)) {
				number = 0;
			}
			switch (number) {
				case 0: // Deposit All
					if (item.interact(itemCount > 1 ? "Deposit-All" : "Deposit")) {
						break;
					} else {
						return false;
					}
				case 1:
					if (item.interact("Deposit")) {
						break;
					} else {
						return false;
					}
				case 5:
					if (item.interact("Deposit-" + number)) {
						break;
					} else {
						return false;
					}
				default: // Deposit x
					if (!item.interact("Deposit-" + number)) {
						if (item.interact("Deposit-X")) {
							Task.sleep(Task.random(1000, 1300));
							Keyboard.sendText(String.valueOf(number), true);
						} else {
							return false;
						}
					}
					break;
			}
			for (int i = 0; i < 1500; i += 20) {
				Task.sleep(20);
				int cInvCount = getCount(true);
				if (cInvCount < invCount || cInvCount == 0) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Deposits all items in except for the given IDs.
	 *
	 * @param ids The items not to deposit.
	 * @return true on success.
	 */
	public static boolean depositAllExcept(int... ids) {
		if (!isOpen()) {
			return false;
		}
		if (!containsOneOf(ids)) {
			return depositAll();
		}
		// outer:
		for (int i = 0; i < 4; i++) {
			Item[] inventory = getItems();
			for (Item item : inventory) {
				boolean skip = false;
				for (int id : ids) {
					if (item.getID() == id) {
						skip = true;
						break;
					}
				}
				int itemId = item.getID();
				if (!skip && itemId != -1) {
					deposit(item.getID(), 0);
				}
			}
		}
		return true;
	}

	/**
	 * Returns the sum of the count of the given items in the bank.
	 *
	 * @param includeStacks - <tt>true</tt> to count the stack sizes of each item;
	 *                      <tt>false</tt> to count a stack as a single item.
	 * @param ids           The array of items.
	 * @return The sum of the stacks of the items.
	 */
	public static int getCount(boolean includeStacks, int... ids) {
		if (!isOpen()) {
			return -1;
		}
		int count = 0;
		Item[] inventory = getItems();
		for (Item item : inventory) {
			for (int i : ids) {
				if (item.getID() == i) {
					count += includeStacks ? item.getStackSize() : 1;
				}
			}
		}
		return count;
	}

	/**
	 * Returns the sum of the count of the given items in the bank,
	 * excluding stacks
	 *
	 * @return The number of items in the inventory
	 */
	public static int getCount() {
		return getCount(false);
	}

	/**
	 * Returns the sum of the count of the given items in the bank.
	 *
	 * @param includeStacks - <tt>true</tt> to count the stack sizes of each item;
	 *                      <tt>false</tt> to count a stack as a single item.
	 * @return The sum of the stacks of the items.
	 */
	public static int getCount(boolean includeStacks) {
		if (!isOpen()) {
			return -1;
		}
		int count = 0;
		for (Item item : getItems()) {
			if (item.getID() != -1) {
				count += includeStacks ? item.getStackSize() : 1;
			}
		}
		return count;
	}

	/**
	 * Gets all the items in the deposit box.
	 *
	 * @return an <code>Item</code> array of the deposit box's contents.
	 */
	public static Item[] getItems() {
		if (!isOpen()) {
			return new Item[0];
		}
		LinkedList<Item> out = new LinkedList<Item>();
		try {
			for (InterfaceComponent c : getInterface().getComponent(COMPONENT_INVENTORY).getComponents()) {
				if (c != null && c.isValid()) {
					out.add(new Item(c));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out.toArray(new Item[out.size()]);
	}

	/**
	 * Gets the first item in the deposit box with any of the provided IDs.
	 *
	 * @param ids The IDs of the item to find.
	 * @return The first <tt>Item</tt> for the given IDs; otherwise
	 *         <tt>null</tt>.
	 */
	public static Item getItem(int... ids) {
		if (isOpen()) {
			for (Item item : getItems()) {
				for (int id : ids) {
					if (item.getID() == id) {
						return item;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Gets all the items in the deposit box matching any of the provided
	 * IDs.
	 *
	 * @param ids The IDs of the item to find.
	 * @return <tt>Item</tt> array of the matching deposit box items.
	 */
	public static Item[] getItems(int... ids) {
		if (!isOpen()) {
			return new Item[0];
		}
		LinkedList<Item> out = new LinkedList<Item>();
		for (Item item : getItems()) {
			for (int id : ids) {
				if (item.getID() == id) {
					out.add(item);
				}
			}
		}
		return out.toArray(new Item[out.size()]);
	}

	/**
	 * Checks whether or not deposit box contains the provided item ID.
	 *
	 * @param id The item ID to check for.
	 * @return <tt>true</tt> if deposit box contains an item with the ID
	 *         provided; otherwise <tt>false</tt>.
	 */
	public static boolean contains(int id) {
		return getItem(id) != null;
	}

	/**
	 * Checks whether or not deposit box contains at least one of the
	 * provided item IDs.
	 *
	 * @param ids The item IDs to check for.
	 * @return <tt>true</tt> if deposit box contains an item with one of the
	 *         IDs provided; otherwise <tt>false</tt>.
	 */
	public static boolean containsOneOf(int... ids) {
		if (!isOpen()) {
			return false;
		}
		for (Item i : getItems()) {
			for (int id : ids) {
				if (i.getID() == id) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks whether or not the deposit box contains all of the provided
	 * item IDs.
	 *
	 * @param ids The item IDs to check for.
	 * @return <tt>true</tt> if the deposit box contains all of the item IDs
	 *         provided; otherwise <tt>false</tt>.
	 */
	public static boolean containsAll(int... ids) {
		if (!isOpen()) {
			return false;
		}
		Item[] items = getItems();
		outer:
		for (int id : ids) {
			for (Item item : items) {
				if (item.getID() == id) {
					continue outer;
				}
			}
			return false;
		}
		return true;
	}

	/**
	 * Deposit everything your player has equipped.
	 *
	 * @return <tt>true</tt> on success.
	 */
	public static boolean depositAllEquipped() {
		return isOpen() && getInterface().getComponent(BUTTON_DEPOSIT_BOX_DEPOSIT_WORN).click(true);
	}

	/**
	 * Deposits everything your familiar is carrying.
	 *
	 * @return <tt>true</tt> on success
	 */
	public static boolean depositAllFamiliar() {
		return isOpen() && getInterface().getComponent(BUTTON_DEPOSIT_BOX_DEPOSIT_BEAST).click(true);
	}

	/**
	 * Deposits all items.
	 *
	 * @return <tt>true</tt> on success.
	 */
	public static boolean depositAll() {
		if (isOpen() && getInterface().getComponent(BUTTON_DEPOSIT_BOX_DEPOSIT_CARRIED).click(true)) {
			for (int i = 0; i < 3000; i += 20) {
				if (getCount(false) == 0) {
					return true;
				}
				Task.sleep(20);
			}
		}
		return false;
	}

	/**
	 * Checks whether or not the deposit box is open.
	 *
	 * @return <tt>true</tt> if the deposit box widget is open; otherwise
	 *         <tt>false</tt>.
	 */
	public static boolean isOpen() {
		return getInterface().isValid();
	}

	/**
	 * Closes the deposit box widget.
	 *
	 * @return <tt>true</tt> if the deposit box widget is no longer open.
	 */
	public static boolean close() {
		if (isOpen()) {
			if (getInterface().getComponent(BUTTON_DEPOSIT_BOX_CLOSE).click(true)) {
				for (int i = 0; i < 10 && Game.getCurrentTab() != Game.Tabs.INVENTORY; i++) {
					Task.sleep(Task.random(100, 200));
				}
			}
			if (!isOpen()) {
				return true;
			}
		}
		return !isOpen();
	}

	/**
	 * Opens one of the supported deposit boxes nearby. If they are not
	 * nearby,
	 * and they are not null, it will automatically walk to the closest one.
	 *
	 * @return <tt>true</tt> if the deposit box was opened; otherwise <tt>false</tt>.
	 */
	public static boolean open() {
		try {
			if (!isOpen()) {
				if (Menu.isOpen()) {
					Mouse.move(Task.random(0, 100), Task.random(0, 100));
					Task.sleep(Task.random(20, 30));
				}
				GameObject depositBox = Objects.getNearest(DEPOSIT_BOXES);
				if (depositBox != null) {
					if (!depositBox.getLocation().isOnScreen()) {
						Walking.getClosestTileOnMap(depositBox.getLocation()).walkOnMap();
					} else if (depositBox.interact("Deposit")) {
						int count = 0;
						while (!isOpen() && ++count < 10) {
							Task.sleep(Task.random(200, 400));
							if (Players.getLocal().isMoving()) {
								count = 0;
							}
						}
					}
				}
			}
			return isOpen();
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
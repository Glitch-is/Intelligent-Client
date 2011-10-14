package org.rsbot.script.methods.tabs;

import org.rsbot.bot.concurrent.Task;
import org.rsbot.script.methods.Game;
import org.rsbot.script.methods.Players;
import org.rsbot.script.methods.ui.Bank;
import org.rsbot.script.methods.ui.Interfaces;
import org.rsbot.script.wrappers.Interface;
import org.rsbot.script.wrappers.InterfaceComponent;
import org.rsbot.script.wrappers.Item;

/**
 * An utility class that handles the Equipment tab.
 *
 * @author Timer
 */
public class Equipment {
	public static final int WIDGET = 387;
	public static final int WIDGET_BANK = 667;
	public static final int COMPONENT_EQUIP_INVENTORY = 7;

	public static final int NUM_APPEARANCE_SLOTS = 9;
	public static final int NUM_SLOTS = 12;

	/**
	 * An enumeration of the Equipment slots
	 *
	 * @author Timer
	 */
	public enum Slot {
		HELMET(8, 0, 0),
		CAPE(11, 1, 1),
		NECK(14, 2, 2),
		WEAPON(17, 3, 3),
		BODY(20, 4, 4),
		SHIELD(23, 5, 5),
		LEGS(26, 7, 7),
		HANDS(29, 9, 9),
		FEET(32, 10, 10),
		RING(35, 12, -1),
		AMMO(38, 13, -1),
		AURA(50, 14, -1);

		private final int componentIndex;
		private final int bankComponentIndex;
		private final int appearanceIndex;

		Slot(final int componentIndex, final int bankComponentIndex, final int appearanceIndex) {
			this.componentIndex = componentIndex;
			this.bankComponentIndex = bankComponentIndex;
			this.appearanceIndex = appearanceIndex;
		}

		/**
		 * Gets the component index of the slot
		 *
		 * @return the index of the slot for either the bank or the regular Equipment tab
		 */
		public int getIndex() {
			return getIndex(Bank.isOpen());
		}

		/**
		 * Gets the component index of the slot
		 *
		 * @param bank bank <tt>true</tt> to get component index of bank equipment component otherwise <tt>false</tt> for regular index
		 * @return the index of the slot based on the bank parameter
		 */
		public int getIndex(final boolean bank) {
			return bank ? getBankComponentIndex() : getComponentIndex();
		}

		/**
		 * Gets the regular component index of the slot
		 *
		 * @return the regular component index
		 */
		public int getComponentIndex() {
			return componentIndex;
		}

		/**
		 * Gets the bank component index of the slot
		 *
		 * @return the bank component index
		 */
		public int getBankComponentIndex() {
			return bankComponentIndex;
		}

		/**
		 * Gets the visible equipment index of the slot
		 *
		 * @return the visible equipment index
		 */
		public int getAppearanceIndex() {
			return appearanceIndex;
		}
	}

	/**
	 * Opens the game tab relating to this utility.
	 *
	 * @return <tt>true</tt> if the tab was opened, otherwise <tt>false</tt>.
	 */
	public static boolean openTab() {
		final Game.Tabs tab = Game.Tabs.EQUIPMENT;
		return Bank.isOpen() || Game.getCurrentTab() == tab || Game.openTab(tab, true);
	}

	/**
	 * Gets the equipment interface.
	 *
	 * @return The equipment <code>Interface</code>.
	 */
	public static Interface getInterface() {
		return Bank.isOpen() ? Interfaces.get(WIDGET_BANK) : Interfaces.get(WIDGET);
	}

	/**
	 * Gets the visibly equipped item id at the given slot.
	 *
	 * @param slot The <code>Slot</code> of the item.
	 * @return The item id at the specified <code>Slot</code>; otherwise <code>-1</code> if not visible.
	 */
	public static int getAppearanceID(final Slot slot) {
		if (slot.getAppearanceIndex() == -1) {
			return -1;
		}
		final int slotId = Players.getLocal().getAppearance()[slot.getAppearanceIndex()];
		return slotId > 0 ? slotId : -1;
	}

	/**
	 * Gets all the visibly equipped items.
	 *
	 * @return An array instance of integers representing the local player's visible equipment.
	 */
	public static int[] getAppearanceIDs() {
		final int[] equipmentIds = new int[NUM_APPEARANCE_SLOTS];
		final int[] visibleEquipment = Players.getLocal().getAppearance();
		final Slot[] slots = Slot.values();
		for (int i = 0; i < equipmentIds.length; i++) {
			final int index = slots[i].getAppearanceIndex();
			if (index != -1) {
				final int id = visibleEquipment[index];
				equipmentIds[i] = id > 0 ? id : -1;
			}
		}
		return equipmentIds;
	}

	/**
	 * Gets the equipped item at the given index.
	 *
	 * @param slot The <code>Slot</code> of the item.
	 * @return An <code>Item</code>; otherwise <code>null</code> if invalid.
	 */
	public static Item getItem(final Slot slot) {
		final Interface widget = getInterface();
		if (openTab() && widget != null && widget.isValid()) {
			try {
				final InterfaceComponent itemComp = (widget.getIndex() == WIDGET_BANK) ?
						widget.getComponent(COMPONENT_EQUIP_INVENTORY).getComponent(slot.getBankComponentIndex()) : widget.getComponent(slot.getComponentIndex());
				if (itemComp != null) {
					return new Item(itemComp);
				}
			} catch (final IndexOutOfBoundsException ignored) {
			}
		}
		return null;
	}

	/**
	 * Gets the first item in the inventory with an id matching the argument(s)
	 *
	 * @param itemIds The id(s) to search for
	 * @return An <code>Item</code> with an id matching the argument(s).
	 */
	public static Item getItem(final int... itemIds) {
		if (!openTab()) {
			return null;
		}
		for (final Item item : getItems()) {
			if (item == null) {
				continue;
			}
			for (final int itemId : itemIds) {
				if (itemId == item.getID()) {
					return item;
				}
			}
		}
		return null;
	}

	/**
	 * Gets all the equipped items.
	 *
	 * @return An array instance of <code>Item</code>.
	 */
	public static Item[] getItems() {
		final Interface widget = getInterface();
		if (openTab() && widget != null) {
			final boolean isBank = widget.getIndex() != WIDGET;
			final InterfaceComponent[] equip = (isBank) ? widget.getComponent(COMPONENT_EQUIP_INVENTORY).getComponents() : widget.getComponents();
			if (equip.length > 0) {
				if (!isBank) {
					final Item[] items = new Item[NUM_SLOTS];
					for (int i = 0; i < items.length; i++) {
						items[i] = new Item(equip[i * 3 + 8]);
					}
					return items;
				} else {
					final Item[] items = new Item[equip.length];
					for (int i = 0; i < items.length; i++) {
						items[i] = new Item(equip[i]);
					}
					return items;
				}
			}
		}
		return new Item[0];
	}

	/**
	 * Gets the count of all equipped items matching with any of the provided ids ignoring stack sizes.
	 *
	 * @param itemIds The item ids to look for.
	 * @return The count of the items matching the item ids.
	 */
	public static int getCount(final int... itemIds) {
		int count = 0;
		if (!openTab()) {
			return count;
		}
		for (final Item item : getItems()) {
			if (item == null) {
				continue;
			}
			final int itemId = item.getID();
			for (final int id : itemIds) {
				if (itemId == id) {
					count++;
					break;
				}
			}
		}
		return count;
	}

	/**
	 * Gets the count of equipped items ignoring stack sizes.
	 *
	 * @return The count of equipped items.
	 */
	public static int getCount() {
		return NUM_SLOTS - getCount(-1);
	}


	/**
	 * Checks whether all of the provided item ids are visibly equipped items.
	 *
	 * @param itemIds the item ids to look for
	 * @return <tt>true</tt> if all of the provided item ids are visible items; otherwise <tt>false</tt>
	 * @see #appearanceContainsOneOf(int...)
	 */
	public static boolean appearanceContainsAll(final int... itemIds) {
		final int[] visibleEquipment = getAppearanceIDs();
		for (final int id : itemIds) {
			boolean hasItem = false;
			for (final int i : visibleEquipment) {
				if (i == id) {
					hasItem = true;
					break;
				}
			}
			if (!hasItem) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks whether one of the provided item ids is a visibly equipped item.
	 *
	 * @param itemIds the item ids to check for
	 * @return <tt>true</tt> if one of the provided items is visibly equipped; otherwise <tt>false</tt>
	 * @see #appearanceContainsAll(int...)
	 */
	public static boolean appearanceContainsOneOf(final int... itemIds) {
		for (final int id : getAppearanceIDs()) {
			for (final int i : itemIds) {
				if (i == id) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks whether all of the provided item ids are equipped items.
	 *
	 * @param itemIds the item ids to look for
	 * @return <tt>true</tt> if all of the provided item ids are equipped items; otherwise <tt>false</tt>
	 * @see #containsOneOf(int...)
	 */
	public static boolean containsAll(final int... itemIds) {
		if (!openTab()) {
			return false;
		}
		final Item[] items = getItems();
		for (final Item item : items) {
			if (item == null) {
				continue;
			}
			boolean hasItem = false;
			final int itemId = item.getID();
			for (final int id : itemIds) {
				if (itemId == id) {
					hasItem = true;
					break;
				}
			}
			if (!hasItem) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks whether one of the provided item ids is an equipped item.
	 *
	 * @param itemIds the item ids to check for
	 * @return <tt>true</tt> if one of the provided items is an equipped item; otherwise <tt>false</tt>
	 * @see #containsAll(int...)
	 */
	public static boolean containsOneOf(final int... itemIds) {
		if (!openTab()) {
			return false;
		}
		for (final Item item : getItems()) {
			final int itemId = item.getID();
			for (final int id : itemIds) {
				if (itemId == id) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Equips an item if it is in the inventory.
	 *
	 * @param itemIds The ids of the item that needs to be equipped.
	 * @return <tt>true</tt> if an item was successfully equipped; otherwise <tt>false</tt>.
	 */
	public static boolean equip(final int... itemIds) {
		final Item item = Inventory.getItem(itemIds);
		if (item != null) {
			int index = -1;
			for (final int itemId : itemIds) {
				if ((index = Inventory.indexOf(itemId)) != -1) {
					break;
				}
			}
			if (!item.interact("Equip")) {
				if (!item.interact("Wear")) {
					item.interact("Wield");
				}
			}
			for (int i = 0; i < 100; i++) {
				if (!item.equals(Inventory.getItemAt(index))) {
					return true;
				}
				Task.sleep(10);
			}
		}
		return false;
	}

	/**
	 * Unequips an item if it is present in the current equipment
	 *
	 * @param itemIds The ids of the item(s) that need(s) to be unequipped.
	 * @return <tt>true</tt> if an item was successfully unequipped; otherwise <tt>false</tt>.
	 */
	public static boolean unequip(final int... itemIds) {
		if (!openTab()) {
			return false;
		}
		final int count = getCount(itemIds);
		final Item item = getItem(itemIds);
		if (item != null) {
			if (item.interact("Remove")) {
				Task.sleep(Task.random(250, 500));
				return getCount(itemIds) < count;
			}
		}
		return false;
	}
}

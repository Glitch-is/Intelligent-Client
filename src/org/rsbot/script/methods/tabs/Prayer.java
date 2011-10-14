package org.rsbot.script.methods.tabs;

import org.rsbot.bot.concurrent.Task;
import org.rsbot.script.methods.Game;
import org.rsbot.script.methods.Settings;
import org.rsbot.script.methods.Skills;
import org.rsbot.script.methods.ui.Interfaces;
import org.rsbot.script.wrappers.InterfaceComponent;

import java.util.LinkedList;
import java.util.List;

/**
 * Prayer tab related operations.
 */
public class Prayer {
	public static final int SETTING_MODERN_SET_ACTIVE = 1584;
	public static final int SETTING_MODERN_ACTIVE_EFFECTS = 1395;
	public static final int SETTING_MODERN_QUICK_PRAYERS = 1397;
	public static final int SETTING_ANCIENT_ACTIVE_EFFECTS = 1582;
	public static final int SETTING_ANCIENT_QUICK_PRAYERS = 1587;
	public static final int SETTING_QUICK_PRAYER_ACTIVE = 1396;
	public static final int TEXTURE_QUICK_PRAYER_SELECTED = 181;
	public static final int WIDGET_PRAYER_ORB = 749;
	public static final int WIDGET_PRAYER = 271;
	public static final int WIDGET_QUICK_PRAYER_CONFIRM = 43;
	public static final int WIDGET_QUICK_PRAYER_SELECTION = 42;

	public interface Effect {
		/**
		 * Returns the index of the effect's component within the prayer tab.
		 *
		 * @return the index of the effect's component within the prayer tab.
		 */
		public int getComponentIndex();

		/**
		 * Returns the value that indicates the effect within the setting array.
		 *
		 * @return the value that indicates the effect within the setting array.
		 */
		public int getSettingValue();

		/**
		 * Returns the required prayer level to use the prayer.
		 *
		 * @return the required prayer level to use the prayer.
		 */
		public int getRequiredLevel();

		/**
		 * Returns the name of the effect.
		 *
		 * @return the name of the effect.
		 */
		public String getName();
	}

	public enum Modern implements Effect {
		THICK_SKIN(0, 0x1, 1, "Thick skin"),
		BURST_OF_STRENGTH(1, 0x2, 4, "Burst of strength"),
		CLARITY_OF_THOUGHT(2, 0x4, 7, "Clarity of thought"),
		SHARP_EYE(3, 0x40000, 8, "Sharp eye"),
		MYSTIC_WILL(4, 0x80000, 9, "Mystic will"),
		ROCK_SKIN(5, 0x8, 10, "Rock skin"),
		SUPERHUMAN_STRENGTH(6, 0x10, 13, "Superhuman strength"),
		IMPROVED_REFLEXES(7, 0x20, 16, "Improved reflexes"),
		RAPID_RESTORE(8, 0x40, 19, "Rapid restore"),
		RAPID_HEAL(9, 0x80, 22, "Rapid heal"),
		PROTECT_ITEM_REGULAR(10, 0x100, 25, "Protect item"),
		HAWK_EYE(11, 0x100000, 26, "Hawk eye"),
		MYSTIC_LORE(12, 0x200000, 27, "Mystic lore"),
		STEEL_SKIN(13, 0x200, 28, "Steel skin"),
		ULTIMATE_STRENGTH(14, 0x400, 31, "Ultimate strength"),
		INCREDIBLE_REFLEXES(15, 0x800, 34, "Incredible reflexes"),
		PROTECT_FROM_SUMMONING(16, 0x1000000, 35, "Protect from summoning"),
		PROTECT_FROM_MAGIC(17, 0x1000, 37, "Protect from magic"),
		PROTECT_FROM_MISSILES(18, 0x2000, 40, "Protect from missiles"),
		PROTECT_FROM_MELEE(19, 0x4000, 43, "Protect from melee"),
		EAGLE_EYE(20, 0x400000, 44, "Eagle eye"),
		MYSTIC_MIGHT(21, 0x800000, 45, "Mystic might"),
		RETRIBUTION(22, 0x8000, 46, "Retribution"),
		REDEMPTION(23, 0x10000, 49, "Redemption"),
		SMITE(24, 0x20000, 52, "Smite"),
		CHIVALRY(25, 0x2000000, 60, "Chivalry"),
		RAPID_RENEWAL(26, 0x8000000, 65, "Rapid renewal"),
		PIETY(27, 0x4000000, 70, "Piety"),
		RIGOUR(28, 0x10000000, 74, "Rigour"),
		AUGURY(29, 0x20000000, 77, "Augury");

		private final int index;
		private final int settingValue;
		private final int level;
		private final String name;

		/**
		 * @param index        index of the effect's component within the prayer tab
		 *                     widget.
		 * @param settingValue index within the setting array.
		 * @param level        prayer level required to use the effect.
		 * @param name         name of the effect.
		 */
		private Modern(final int index, final int settingValue, final int level, final String name) {
			this.index = index;
			this.settingValue = settingValue;
			this.level = level;
			this.name = name;
		}

		public int getComponentIndex() {
			return index;
		}

		public int getSettingValue() {
			return settingValue;
		}

		public int getRequiredLevel() {
			return level;
		}

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return name + " - Modern (Level: " + getRequiredLevel() + ")";
		}
	}

	public enum Ancient implements Effect {
		PROTECT_ITEM_CURSE(0, 0x1, 50, "Protect item"),
		SAP_WARRIOR(1, 0x2, 50, "Sap warrior"),
		SAP_RANGER(2, 0x4, 52, "Sap ranger"),
		SAP_MAGE(3, 0x8, 54, "Sap mage"),
		SAP_SPIRIT(4, 0x10, 56, "Sap spirit"),
		BERSERKER(5, 0x20, 59, "Berserker"),
		DEFLECT_SUMMONING(6, 0x40, 62, "Deflect summoning"),
		DEFLECT_MAGIC(7, 0x80, 65, "Deflect magic"),
		DEFLECT_MISSILE(8, 0x100, 68, "Deflect missile"),
		DEFLECT_MELEE(9, 0x200, 71, "Deflect melee"),
		LEECH_ATTACK(10, 0x400, 74, "Leach attack"),
		LEECH_RANGE(11, 0x800, 76, "Leach range"),
		LEECH_MAGIC(12, 0x1000, 78, "Leach magic"),
		LEECH_DEFENCE(13, 0x2000, 80, "Leach defence"),
		LEECH_STRENGTH(14, 0x4000, 82, "Leach strength"),
		LEECH_ENERGY(15, 0x8000, 84, "Leach energy"),
		LEECH_SPECIAL_ATTACK(16, 0x10000, 86, "Leech special attack"),
		WRATH(17, 0x20000, 89, "Wrath"),
		SOUL_SPLIT(18, 0x40000, 92, "Soul split"),
		TURMOIL(19, 0x80000, 95, "Turmoil");

		private final int index;
		private final int settingValue;
		private final int level;
		private final String name;

		/**
		 * @param index        index of the effect's component within the prayer tab
		 *                     widget.
		 * @param settingValue index within the setting array.
		 * @param level        prayer level required to use the effect.
		 * @param name         name of the effect.
		 */
		private Ancient(final int index, final int settingValue, final int level, final String name) {
			this.index = index;
			this.settingValue = settingValue;
			this.level = level;
			this.name = name;
		}

		public int getComponentIndex() {
			return index;
		}

		public int getSettingValue() {
			return settingValue;
		}

		public int getRequiredLevel() {
			return level;
		}

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return name + " - Ancient (Level: " + getRequiredLevel() + ")";
		}
	}

	/**
	 * Returns an array of all current active effects.
	 *
	 * @return an array of all current active effects.
	 */
	public static Effect[] getActiveEffects() {
		final int bookSetting = isModernSetActive() ? SETTING_MODERN_ACTIVE_EFFECTS : SETTING_ANCIENT_ACTIVE_EFFECTS;
		final List<Effect> activeEffects = new LinkedList<Effect>();
		for (Effect effect : isModernSetActive() ? Modern.values() : Ancient.values()) {
			if ((Settings.get(bookSetting) & (effect.getSettingValue())) == effect.getSettingValue()) {
				activeEffects.add(effect);
			}
		}
		return activeEffects.toArray(new Effect[activeEffects.size()]);
	}

	/**
	 * Returns an array of all quick prayers.
	 *
	 * @return an array of all quick prayers.
	 */
	public static Effect[] getQuickPrayers() {
		final int bookSetting = isModernSetActive() ? SETTING_MODERN_QUICK_PRAYERS : SETTING_ANCIENT_QUICK_PRAYERS;
		final List<Effect> quickPrayers = new LinkedList<Effect>();
		for (Effect effect : isModernSetActive() ? Modern.values() : Ancient.values()) {
			if ((Settings.get(bookSetting) & (effect.getSettingValue())) == effect.getSettingValue()) {
				quickPrayers.add(effect);
			}
		}
		return quickPrayers.toArray(new Effect[quickPrayers.size()]);
	}

	/**
	 * Gets the percentage of remaining prayer points out of the maximum.
	 *
	 * @return The percentage of prayer points remaining.
	 */
	public static int getPointPercentage() {
		return (100 * getRemainingPoints()) / Skills.getAbsoluteLevel(Skills.PRAYER);
	}

	/**
	 * Gets the remaining prayer points.
	 *
	 * @return The number of prayer points left.
	 */
	public static int getRemainingPoints() {
		return Integer.parseInt(Interfaces.getComponent(WIDGET_PRAYER_ORB, 4).getText());
	}

	/**
	 * Returns whether the specified effects are currently active.
	 *
	 * @return <tt>true</tt> if all of the specified effects are currently
	 *         active; otherwise <tt>false</tt>
	 */
	public static boolean isActive(final Effect... effects) {
		for (Effect effect : effects) {
			boolean active = false;
			for (Effect activeEffect : getActiveEffects()) {
				if (activeEffect.equals(effect)) {
					active = true;
				}
			}
			if (!active) {
				return false;
			}
		}
		return true;
	}

	private static boolean isQuickPrayerSet(Effect effect) {
		InterfaceComponent quickPrayerSelection = Interfaces.getComponent(WIDGET_PRAYER, WIDGET_QUICK_PRAYER_SELECTION);
		if (quickPrayerSelection != null && quickPrayerSelection.isValid()) {
			InterfaceComponent currentSelection = quickPrayerSelection.getComponent(effect.getComponentIndex());
			if (currentSelection != null && quickPrayerSelection.isValid()) {
				return currentSelection.getTextureID() == TEXTURE_QUICK_PRAYER_SELECTED;
			}
		}
		return false;
	}

	/**
	 * Returns wether you are using the modern prayer book.
	 *
	 * @return <tt>true</tt> if you are using the modern prayer book; otherwise
	 *         <tt>false</tt>.
	 */
	public static boolean isModernSetActive() {
		return Settings.get(SETTING_MODERN_SET_ACTIVE) % 2 == 0;
	}

	/**
	 * Returns whether quick prayers are currently active.
	 *
	 * @return <tt>true</tt> if quick prayers are currently active; otherwise
	 *         <tt>false</tt>
	 */
	public static boolean isQuickPrayersActive() {
		return Settings.get(SETTING_QUICK_PRAYER_ACTIVE) == 2;
	}

	private static boolean isQuickPrayerSet(Effect... effects) {
		for (Effect effect : effects) {
			if (!isQuickPrayerSet(effect)) {
				return false;
			}
		}
		return true;
	}

	private static boolean setActivated(final Effect effect, final boolean activate) {
		if (activate != isActive(effect) && (!activate || effect.getRequiredLevel() <= Skills.getAbsoluteLevel(Skills.PRAYER) && getRemainingPoints() > 0)) {
			if (Game.getCurrentTab() != Game.Tabs.PRAYER) {
				Game.openTab(Game.Tabs.PRAYER);
				Task.sleep(700);
			}
			if (Interfaces.getComponent(WIDGET_PRAYER, 8).getComponent(effect.getComponentIndex()).interact(activate ? "Activate" : "Deactivate")) {
				for (int i = 0; i < 30; i++) {
					if (isActive(effect)) {
						return true;
					}
					Task.sleep(100);
				}
				return true;
			}

		}
		return false;
	}

	/**
	 * Tries to activate or deactivate the specified effects.
	 *
	 * @param active  <tt>true</tt> to activate the effects; <tt>false</tt> to
	 *                deactivate the effects.
	 * @param effects The effects to activate or deactivate.
	 * @return <tt>true</tt> if all effects were set.
	 */
	public static boolean setActivated(final boolean active, final Effect... effects) {
		for (Effect effect : effects) {
			setActivated(effect, active);
		}
		return isActive(effects);
	}

	/**
	 * Sets the character's quick prayers to the given prayers.
	 *
	 * @param effects The Effects to set the quick prayers to.
	 * @return <tt>true</tt> if the quick prayers were set; otherwise
	 *         <tt>false</tt>.
	 */
	public static boolean setQuickPrayers(Effect... effects) {
		if (!isQuickPrayersActive()) {
			InterfaceComponent prayerOrb = Interfaces.getComponent(WIDGET_PRAYER_ORB, 1);
			if (prayerOrb != null && prayerOrb.isValid() && prayerOrb.interact("Select quick prayers")) {
				for (int i = 0; i < 10 && (Interfaces.getComponent(WIDGET_PRAYER, WIDGET_QUICK_PRAYER_SELECTION) == null ||
						Interfaces.getComponent(WIDGET_PRAYER, WIDGET_QUICK_PRAYER_SELECTION).getComponents() == null ||
						Interfaces.getComponent(WIDGET_PRAYER, WIDGET_QUICK_PRAYER_SELECTION).getComponents().length == 0); i++) {
					Task.sleep(Task.random(100, 200));
				}
			}
		}
		for (Effect effect : effects) {
			if (isQuickPrayerSet(effect)) {
				continue;
			}
			InterfaceComponent quickPrayerSelection = Interfaces.getComponent(WIDGET_PRAYER, WIDGET_QUICK_PRAYER_SELECTION);
			if (quickPrayerSelection != null && quickPrayerSelection.isValid()) {
				InterfaceComponent currentSelection = quickPrayerSelection.getComponent(effect.getComponentIndex());
				if (currentSelection != null && currentSelection.isValid()) {
					currentSelection.interact("Select");
					Task.sleep(Task.random(750, 1100));
				}
			}
		}
		if (isQuickPrayerSet(effects)) {
			InterfaceComponent confirmSelection = Interfaces.getComponent(WIDGET_PRAYER, WIDGET_QUICK_PRAYER_CONFIRM);
			if (confirmSelection != null && confirmSelection.isValid() && confirmSelection.interact("Confirm Selection")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param active <tt>true</tt> to turn quick prayers on; <tt>false</tt> to turn quick prayers off.
	 * @return <tt>true</tt> if quick prayers toggle was successful.
	 */
	public static boolean toggleQuickPrayers(boolean active) {
		return active == isQuickPrayersActive() ||
				Interfaces.getComponent(WIDGET_PRAYER_ORB, 1).interact(active ? (isModernSetActive() ? "Turn quick prayers on" : "Turn quick curses on") :
						(isModernSetActive() ? "Turn prayers off" : "Turn curses off"));
	}

}
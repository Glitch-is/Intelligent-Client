package org.rsbot.script.methods.tabs;

import org.rsbot.bot.Context;
import org.rsbot.script.methods.Game;
import org.rsbot.script.methods.Game.Tabs;
import org.rsbot.script.methods.Skills;
import org.rsbot.script.methods.ui.Interfaces;
import org.rsbot.script.wrappers.Entity;
import org.rsbot.script.wrappers.Interface;
import org.rsbot.script.wrappers.InterfaceComponent;
import org.rsbot.script.wrappers.Item;

import java.util.ArrayList;
import java.util.List;

public class Magic {
	enum Book {
		LUNAR(430, 6, Lunar.values()),
		NORMAL(192, 94, Normal.values()),
		ANCIENT(193, 0, Ancient.values());
		private final int interface_id;
		private final int scrollbar_id;
		private final Spell[] spells;

		private Book(int id, int scroll, Spell[] spells) {
			this.interface_id = id;
			this.scrollbar_id = scroll;
			this.spells = spells;
		}

		/**
		 * Gets this book's interface
		 *
		 * @return
		 */
		public Interface getInterface() {
			return Interfaces.get(interface_id);
		}

		/**
		 * Gets this book's scroll bar, or <tt>null</tt> if it doesn't exist
		 *
		 * @return
		 */
		public InterfaceComponent getScrollBar() {
			return getInterface() != null ? getInterface().getComponent(
					scrollbar_id) : null;
		}

		/**
		 * Gets the spells usable by this book
		 *
		 * @return
		 */
		public Spell[] getSpells() {
			return spells;
		}

		/**
		 * Gets the current book, or <tt>null</tt> if it can't be determined
		 *
		 * @return
		 */
		public static Book getCurrentBook() {
			for (Book b : Book.values()) {
				if (b.getInterface() != null) {
					return b;
				}
			}
			return null;
		}
	}

	enum Button {
		DEFENSIVE_CASTING(2191),
		NO_FILTER_COMBAT(2192),
		NO_FILTER_TELE(2195),
		NO_FILTER_MISC(2193),
		NO_FILTER_SKILL(2194),
		ORDER_LEVEL(1704),
		ORDER_COMBAT(1705),
		ORDER_TELE(1706);
		private final int texture_id;
		private static final int BUTTON_ON = 1703;

		private Button(int tex_id) {
			this.texture_id = tex_id;
		}

		/**
		 * Gets the interface component for this button
		 *
		 * @return
		 */
		public InterfaceComponent getComponent() {
			Interface i = getInterface();
			if (i != null) {
				for (InterfaceComponent c : i.getComponents()) {
					if (c != null && c.getTextureID() == texture_id) {
						return c;
					}
				}
			}
			return null;
		}

		/**
		 * Gets the background component for this button, the component that
		 * shows selected state
		 *
		 * @return
		 */
		private InterfaceComponent getBackground() {
			InterfaceComponent cO = getComponent();
			Interface i = getInterface();
			if (i != null && cO != null) {
				for (InterfaceComponent c : i.getComponents()) {
					if (c != null && c.getID() != cO.getID()
							&& c.getBoundingRect().equals(cO.getBoundingRect())) {
						return c;
					}
				}
			}
			return null;
		}

		/**
		 * Checks if this button is selected
		 *
		 * @return
		 */
		public boolean isSelected() {
			InterfaceComponent comp = getBackground();
			return comp != null && comp.getTextureID() == BUTTON_ON;
		}

		/**
		 * Sets the selected state of this button
		 *
		 * @param on
		 * @return
		 */
		public boolean setSelected(boolean on) {
			if (on != isSelected()) {
				InterfaceComponent comp = getComponent();
				return comp != null && comp.click(true);
			} else {
				return true;
			}
		}

		private static final Button[] filter = {Button.NO_FILTER_COMBAT,
				Button.NO_FILTER_TELE, Button.NO_FILTER_MISC};
		private static final Type[] filter_types = {Type.Combat,
				Type.Teleport, Type.Misc};
		private static final Button[] order = {Button.ORDER_LEVEL,
				Button.ORDER_COMBAT, Button.ORDER_TELE};

		/**
		 * Get the buttons that filter magic spells
		 *
		 * @return
		 */
		public static Button[] getFilters() {
			return filter;
		}

		/**
		 * Gets the spell type of each filter
		 *
		 * @return
		 */
		public static Type[] getFilterTypes() {
			return filter_types;
		}

		/**
		 * Get the buttons that set the order of magic spells
		 *
		 * @return
		 */
		public static Button[] getOrders() {
			return order;
		}
	}

	public static interface Spell {
		/**
		 * Gets the component texture id for this spell
		 *
		 * @return
		 */
		public int getTextureID();

		/**
		 * Gets this spell's type
		 *
		 * @return
		 */
		public Type getType();

		/**
		 * Checks if this spell is auto-castable
		 *
		 * @return
		 */
		public boolean isAutocastable();

		/**
		 * Gets the runes needed to cast this spell
		 *
		 * @return
		 */
		public Rune[] getRunes();

		/**
		 * Gets the rune counts needed to cast this spell
		 *
		 * @return
		 */
		public int[] getRuneCounts();

		/**
		 * Gets any other equipment needed to cast this spell
		 *
		 * @return
		 */
		public int[] getOtherEquipment();

		/**
		 * Gets the magic level needed to cast this spell
		 *
		 * @return
		 */
		public int getMagicLevel();

		/**
		 * Gets any other items needed to cast this spell
		 *
		 * @return
		 */
		public int[] getOtherItems();

		/**
		 * Gets this spell's spellbook
		 *
		 * @return
		 */
		public Book getBook();

		/**
		 * Checks if this spell is memebers
		 *
		 * @return
		 */
		public boolean isMembers();
	}

	enum Type {
		Combat, Teleport, Misc, Skill;
	}

	public enum Normal implements Spell {
		LUMBRIDGE_HOME_TELEPORT(356, Type.Teleport, new Rune[]{}, new int[]{}, 0),
		WIND_STRIKE(65, Type.Combat, new Rune[]{Rune.AIR, Rune.MIND}, new int[]{
				1, 1}, 1, true),
		CONFUSE(66, Type.Combat, new Rune[]{Rune.BODY, Rune.EARTH, Rune.WATER}, new int[]{
				1, 2, 3}, 3),
		ENCHANT_CROSSBOW_BOLT(358, Type.Misc, new Rune[]{}, new int[]{}, 4, false, true),
		WATER_STRIKE(67, Type.Combat, new Rune[]{Rune.AIR, Rune.WATER,
				Rune.MIND}, new int[]{1, 1, 1}, 5, true),
		LVL_1_ENCHANT(68, Type.Misc, new Rune[]{Rune.COSMIC, Rune.WATER}, new int[]{
				1, 1}, 7),
		EARTH_STRIKE(69, Type.Combat, new Rune[]{Rune.AIR, Rune.EARTH,
				Rune.MIND}, new int[]{1, 2, 1}, 9, true),
		WEAKEN(70, Type.Combat, new Rune[]{Rune.BODY, Rune.EARTH, Rune.WATER}, new int[]{
				1, 2, 3}, 11),
		FIRE_STRIKE(71, Type.Combat, new Rune[]{Rune.FIRE, Rune.AIR,
				Rune.MIND}, new int[]{3, 2, 1}, 13, true),
		BONES_TO_BANANAS(72, Type.Misc, new Rune[]{Rune.NATURE, Rune.EARTH,
				Rune.WATER}, new int[]{1, 2, 2}, 15),
		WIND_BOLT(73, Type.Combat, new Rune[]{Rune.AIR, Rune.CHAOS}, new int[]{
				2, 1}, 17, true),
		CURSE(74, Type.Combat, new Rune[]{Rune.BODY, Rune.EARTH, Rune.WATER}, new int[]{
				1, 3, 2}, 19),
		BIND(369, Type.Combat, new Rune[]{Rune.NATURE, Rune.EARTH, Rune.WATER}, new int[]{
				2, 3, 3}, 20),
		MOBILISING_ARMIES_TELEPORT(1570, Type.Teleport, new Rune[]{Rune.AIR,
				Rune.WATER, Rune.LAW}, new int[]{1, 1, 1}, 10, false, true),
		LOW_LEVEL_ALCHEMY(75, Type.Misc, new Rune[]{Rune.NATURE, Rune.FIRE}, new int[]{
				1, 3}, 21),
		WATER_BOLT(76, Type.Combat, new Rune[]{Rune.AIR, Rune.WATER,
				Rune.CHAOS}, new int[]{2, 2, 1}, 23, true),
		VARROCK_TELEPORT(77, Type.Teleport, new Rune[]{Rune.FIRE}, new int[]{1}, 25),
		LVL_2_ENCHANT(78, Type.Misc, new Rune[]{Rune.COSMIC, Rune.AIR}, new int[]{
				1, 3}, 27),
		EARTH_BOLT(79, Type.Combat, new Rune[]{Rune.EARTH}, new int[]{3}, 29, true),
		LUMBRIDGE_TELEPORT(80, Type.Teleport, new Rune[]{Rune.AIR,
				Rune.EARTH, Rune.LAW}, new int[]{3, 1, 1}, 31),
		TELEKINETIC_GRAB(81, Type.Misc, new Rune[]{Rune.AIR, Rune.LAW}, new int[]{
				1, 1}, 33),
		FIRE_BOLT(82, Type.Combat, new Rune[]{Rune.FIRE, Rune.AIR}, new int[]{
				4, 3}, 35, true),
		FALADOR_TELEPORT(83, Type.Teleport, new Rune[]{Rune.AIR, Rune.WATER,
				Rune.LAW}, new int[]{3, 1, 1}, 37),
		CRUMBLE_UNDEAD(84, Type.Combat, new Rune[]{Rune.AIR, Rune.EARTH,
				Rune.CHAOS}, new int[]{2, 2, 1}, 39, true),
		TELEPORT_TO_HOUSE(405, Type.Teleport, new Rune[]{Rune.EARTH,
				Rune.AIR, Rune.LAW}, new int[]{1, 1, 1}, 40, false, true),
		WIND_BLAST(85, Type.Combat, new Rune[]{}, new int[]{}, 41, true),
		SUPERHEAT_ITEM(86, Type.Misc, new Rune[]{Rune.NATURE, Rune.FIRE}, new int[]{
				1, 4}, 43),
		CAMELOT_TELEPORT(87, Type.Teleport, new Rune[]{Rune.AIR, Rune.LAW}, new int[]{
				5, 1}, 45, false, true),
		WATER_BLAST(88, Type.Combat, new Rune[]{Rune.AIR, Rune.WATER,
				Rune.DEATH}, new int[]{3, 3, 1}, 47, true),
		LVL_3_ENCHANT(89, Type.Misc, new Rune[]{Rune.COSMIC, Rune.FIRE}, new int[]{
				1, 5}, 49),
		IBAN_BLAST(103, Type.Combat, new Rune[]{Rune.FIRE, Rune.DEATH}, new int[]{
				5, 1}, 50, new int[0], Staff.IBAN.getIDs(), true, true),
		SNARE(370, Type.Combat, new Rune[]{Rune.EARTH}, new int[]{4}, 50, false, true),
		MAGIC_DART(374, Type.Combat, new Rune[]{Rune.DEATH, Rune.MIND}, new int[]{
				1, 4}, 50, new int[0], Staff.SLAYER.getIDs(), true, true),
		ARDOUGNE_TELEPORT(104, Type.Teleport, new Rune[]{Rune.WATER, Rune.LAW}, new int[]{
				2, 2}, 51, false, true),
		EARTH_BLAST(90, Type.Combat, new Rune[]{Rune.AIR, Rune.EARTH,
				Rune.DEATH}, new int[]{3, 4, 1}, 53, true),
		HIGH_LEVEL_ALCHEMY(91, Type.Misc, new Rune[]{Rune.NATURE, Rune.FIRE}, new int[]{
				1, 5}, 55),
		CHARGE_WATER_ORB(92, Type.Misc, new Rune[]{Rune.COSMIC, Rune.WATER}, new int[]{
				3, 30}, 56, false, true),
		LVL_4_ENCHANT(93, Type.Misc, new Rune[]{Rune.COSMIC, Rune.EARTH}, new int[]{
				1, 10}, 57),
		WATCHTOWER_TELEPORT(105, Type.Teleport, new Rune[]{Rune.EARTH,
				Rune.LAW}, new int[]{2, 2}, 58, false, true),
		FIRE_BLAST(94, Type.Combat, new Rune[]{Rune.FIRE, Rune.AIR,
				Rune.DEATH}, new int[]{5, 4, 1}, 59, true),
		CHARGE_EARTH_ORB(95, Type.Misc, new Rune[]{Rune.COSMIC, Rune.EARTH}, new int[]{
				3, 30}, 60, false, true),
		BONES_TO_PEACHES(404, Type.Misc, new Rune[]{Rune.NATURE, Rune.EARTH,
				Rune.WATER}, new int[]{2, 4, 4}, 60, false, true),
		SARADOMIN_STRIKE(111, Type.Combat, new Rune[]{Rune.FIRE, Rune.AIR,
				Rune.BLOOD}, new int[]{2, 4, 2}, 60, new int[0], Staff.SARADOMIN
				.getIDs(), true, true),
		CLAWS_OF_GUTHIX(110, Type.Combat, new Rune[]{Rune.FIRE, Rune.AIR,
				Rune.BLOOD}, new int[]{1, 4, 2}, 60, new int[0], Staff.GUTHIX
				.getIDs(), true, true),
		FLAMES_OF_ZAMORAK(109, Type.Combat, new Rune[]{Rune.FIRE, Rune.AIR,
				Rune.BLOOD}, new int[]{4, 1, 2}, 60, new int[0], Staff.ZAMAORAK
				.getIDs(), true, true),
		TROLLHEIM_TELEPORT(373, Type.Teleport, new Rune[]{Rune.FIRE, Rune.LAW}, new int[]{
				2, 2}, 61, false, true),
		WIND_WAVE(96, Type.Combat, new Rune[]{Rune.AIR, Rune.BLOOD}, new int[]{
				5, 1}, 62, true, true),
		CHARGE_FIRE_ORB(97, Type.Misc, new Rune[]{Rune.COSMIC, Rune.FIRE}, new int[]{
				3, 30}, 63, false, true),
		TELEPORT_TO_APE_ATOLL(407, Type.Teleport, new Rune[]{Rune.FIRE,
				Rune.WATER, Rune.LAW}, new int[]{2, 2, 2}, 64, new int[]{1963}, new int[]{}, false, true),
		WATER_WAVE(98, Type.Combat, new Rune[]{Rune.AIR, Rune.BLOOD,
				Rune.WATER}, new int[]{5, 1, 7}, 65, true, true),
		CHARGE_AIR_ORB(99, Type.Misc, new Rune[]{Rune.COSMIC, Rune.AIR}, new int[]{
				3, 30}, 66, false, true),
		VULNERABILITY(106, Type.Combat, new Rune[]{Rune.EARTH, Rune.WATER,
				Rune.SOUL}, new int[]{5, 5, 1}, 66, false, true),
		LVL_5_ENCHANT(100, Type.Misc, new Rune[]{Rune.COSMIC, Rune.EARTH,
				Rune.WATER}, new int[]{1, 15, 15}, 68, false, true),
		EARTH_WAVE(101, Type.Combat, new Rune[]{Rune.AIR, Rune.EARTH,
				Rune.BLOOD}, new int[]{5, 7, 1}, 70, true, true),
		ENFEEBLE(107, Type.Combat, new Rune[]{Rune.EARTH, Rune.WATER,
				Rune.SOUL}, new int[]{8, 8, 1}, 73, false, true),
		TELEOTHER_LUMBRIDGE(399, Type.Teleport, new Rune[]{Rune.EARTH,
				Rune.LAW, Rune.SOUL}, new int[]{1, 1, 1}, 74, false, true),
		FIRE_WAVE(102, Type.Combat, new Rune[]{Rune.FIRE, Rune.AIR,
				Rune.BLOOD}, new int[]{7, 5, 1}, 75, true, true),
		ENTANGLE(371, Type.Combat, new Rune[]{Rune.NATURE, Rune.EARTH,
				Rune.WATER}, new int[]{4, 5, 5}, 79, false, true),
		STUN(108, Type.Combat, new Rune[]{Rune.EARTH, Rune.WATER, Rune.SOUL}, new int[]{
				12, 12, 1}, 80, false, true),
		CHARGE(372, Type.Combat, new Rune[]{Rune.FIRE, Rune.AIR, Rune.BLOOD}, new int[]{
				3, 3, 3}, 80, false, true),
		WIND_SURGE(815, Type.Combat, new Rune[]{Rune.AIR, Rune.BLOOD}, new int[]{
				7, 1}, 81, false, true),
		TELEOTHER_FALADOR(400, Type.Teleport, new Rune[]{Rune.WATER,
				Rune.SOUL}, new int[]{1, 1}, 82, false, true),
		TELEPORT_BLOCK(1571, Type.Teleport, new Rune[]{Rune.DEATH, Rune.LAW,
				Rune.CHAOS}, new int[]{1, 1, 1}, 85),
		WATER_SURGE(816, Type.Combat, new Rune[]{Rune.AIR, Rune.BLOOD,
				Rune.WATER, Rune.DEATH}, new int[]{7, 1, 10, 1}, 85, true, true),
		LVL_6_ENCHANT(403, Type.Misc, new Rune[]{Rune.COSMIC, Rune.FIRE,
				Rune.EARTH}, new int[]{1, 20, 20}, 87, false, true),
		EARTH_SURGE(817, Type.Combat, new Rune[]{Rune.AIR, Rune.EARTH,
				Rune.BLOOD, Rune.DEATH}, new int[]{7, 10, 1, 1}, 90, true, true),
		TELEOTHER_CAMELOT(401, Type.Teleport, new Rune[]{Rune.LAW, Rune.SOUL}, new int[]{
				1, 2}, 90, false, true),
		FIRE_SURGE(818, Type.Combat, new Rune[]{Rune.FIRE, Rune.AIR,
				Rune.BLOOD, Rune.DEATH}, new int[]{10, 7, 1, 1}, 95, true, true),
		WIND_RUSH(3759, Type.Combat, new Rune[]{Rune.AIR}, new int[]{2}, 1, true),
		STORM_OF_ARMADYL(7702, Type.Combat, new Rune[]{Rune.ARMADYL}, new int[]{1}, 77, true, true);
		private final int textureID;
		private final Type spellType;
		private final int[] rCounts;
		private final Rune[] runes;
		private final int level;
		private final int[] extraItems;
		private final int[] extraEquip;
		private final boolean isAutoCastable;
		private final boolean isMembers;

		private Normal(int tex, Type spell, Rune[] runes, int[] counts,
		               int level, int[] extraItems, int[] extraEquip,
		               boolean isAutoCastable, boolean isMembers) {
			this.textureID = tex;
			this.spellType = spell;
			this.isAutoCastable = isAutoCastable;
			this.runes = runes;
			this.rCounts = counts;
			this.level = level;
			this.extraItems = extraItems;
			this.extraEquip = extraEquip;
			this.isMembers = isMembers;
		}

		private Normal(int tex, Type spell, Rune[] runes, int[] counts,
		               int level, boolean isAutoCastable) {
			this(tex, spell, runes, counts, level, new int[0], new int[0],
					isAutoCastable, false);
		}

		private Normal(int tex, Type spell, Rune[] runes, int[] counts,
		               int level, boolean isAutoCastable, boolean isMembers) {
			this(tex, spell, runes, counts, level, new int[0], new int[0],
					isAutoCastable, isMembers);
		}

		private Normal(int tex, Type spell, Rune[] runes, int[] counts,
		               int level) {
			this(tex, spell, runes, counts, level, false);
		}

		/**
		 * {@inheritDoc}
		 */
		public int getTextureID() {
			return textureID;
		}

		/**
		 * {@inheritDoc}
		 */
		public Type getType() {
			return spellType;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isAutocastable() {
			return isAutoCastable;
		}

		/**
		 * {@inheritDoc}
		 */
		public Rune[] getRunes() {
			return runes;
		}

		/**
		 * {@inheritDoc}
		 */
		public int[] getRuneCounts() {
			return rCounts;
		}

		/**
		 * {@inheritDoc}
		 */
		public int[] getOtherEquipment() {
			return extraEquip;
		}

		/**
		 * {@inheritDoc}
		 */
		public int getMagicLevel() {
			return level;
		}

		/**
		 * {@inheritDoc}
		 */
		public int[] getOtherItems() {
			return extraItems;
		}

		/**
		 * {@inheritDoc}
		 */
		public Book getBook() {
			return Book.NORMAL;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isMembers() {
			return isMembers;
		}

	}

	public enum Ancient implements Spell {
		ICE_RUSH(375, Type.Combat, new Rune[]{Rune.DEATH, Rune.WATER,
				Rune.CHAOS}, new int[]{2, 2, 2}, 58, true),
		ICE_BLITZ(377, Type.Combat, new Rune[]{Rune.BLOOD, Rune.DEATH,
				Rune.WATER}, new int[]{2, 2, 3}, 82, true),
		ICE_BURST(376, Type.Combat, new Rune[]{Rune.DEATH, Rune.WATER,
				Rune.CHAOS}, new int[]{2, 4, 4}, 70, true),
		ICE_BARRAGE(378, Type.Combat, new Rune[]{Rune.BLOOD, Rune.DEATH,
				Rune.WATER}, new int[]{2, 4, 6}, 94, true),
		BLOOD_RUSH(383, Type.Combat, new Rune[]{Rune.BLOOD, Rune.DEATH,
				Rune.CHAOS}, new int[]{1, 2, 2}, 56, true),
		BLOOD_BLITZ(385, Type.Combat, new Rune[]{Rune.BLOOD, Rune.DEATH}, new int[]{
				4, 2}, 80, true),
		BLOOD_BURST(384, Type.Combat, new Rune[]{Rune.BLOOD, Rune.DEATH,
				Rune.CHAOS}, new int[]{2, 2, 4}, 68, true),
		BLOOD_BARRAGE(386, Type.Combat, new Rune[]{Rune.SOUL, Rune.BLOOD,
				Rune.DEATH}, new int[]{1, 4, 4}, 92, true),
		SMOKE_RUSH(379, Type.Combat, new Rune[]{Rune.AIR, Rune.FIRE,
				Rune.DEATH, Rune.CHAOS}, new int[]{1, 1, 2, 2}, 50, true),
		SMOKE_BLITZ(381, Type.Combat, new Rune[]{Rune.AIR, Rune.FIRE,
				Rune.BLOOD, Rune.DEATH}, new int[]{2, 2, 2, 2}, 74, true),
		SMOKE_BURST(380, Type.Combat, new Rune[]{Rune.AIR, Rune.FIRE,
				Rune.DEATH, Rune.CHAOS}, new int[]{2, 2, 2, 4}, 62, true),
		SMOKE_BARRAGE(382, Type.Combat, new Rune[]{Rune.AIR, Rune.FIRE,
				Rune.BLOOD, Rune.DEATH}, new int[]{4, 4, 2, 4}, 86, true),
		SHADOW_RUSH(387, Type.Combat, new Rune[]{Rune.SOUL, Rune.AIR,
				Rune.DEATH, Rune.CHAOS}, new int[]{1, 1, 2, 2}, 52, true),
		SHADOW_BLITZ(389, Type.Combat, new Rune[]{Rune.SOUL, Rune.AIR,
				Rune.BLOOD, Rune.DEATH}, new int[]{2, 2, 2, 2}, 76, true),
		SHADOW_BURST(388, Type.Combat, new Rune[]{Rune.SOUL, Rune.AIR,
				Rune.DEATH, Rune.CHAOS}, new int[]{2, 1, 2, 4}, 64, true),
		SHADOW_BARRAGE(390, Type.Combat, new Rune[]{Rune.SOUL, Rune.AIR,
				Rune.BLOOD, Rune.DEATH}, new int[]{3, 4, 2, 4}, 88, true),
		MIASMIC_RUSH(1574, Type.Combat, new Rune[]{Rune.SOUL, Rune.EARTH,
				Rune.CHAOS}, new int[]{1, 1, 2}, 61, Staff.ZURIEL.getIDs(), true),
		MIASMIC_BLITZ(1573, Type.Combat, new Rune[]{Rune.SOUL, Rune.EARTH,
				Rune.BLOOD}, new int[]{3, 3, 2}, 85, Staff.ZURIEL.getIDs(), true),
		MIASMIC_BURST(1575, Type.Combat, new Rune[]{Rune.SOUL, Rune.EARTH,
				Rune.CHAOS}, new int[]{2, 2, 4}, 73, Staff.ZURIEL.getIDs(), true),
		MIASMIC_BARRAGE(1572, Type.Combat, new Rune[]{Rune.SOUL, Rune.EARTH,
				Rune.BLOOD}, new int[]{4, 4, 4}, 97, Staff.ZURIEL.getIDs(), true),
		PADDEWWA_TELEPORT(391, Type.Teleport, new Rune[]{Rune.LAW, Rune.AIR,
				Rune.FIRE}, new int[]{2, 1, 1}, 54),
		SENNTISTEN_TELEPORT(392, Type.Teleport, new Rune[]{Rune.LAW,
				Rune.SOUL}, new int[]{2, 1}, 60),
		KHARYRLL_TELEPORT(393, Type.Teleport, new Rune[]{Rune.LAW, Rune.BLOOD}, new int[]{
				2, 1}, 66),
		LASSAR_TELEPORT(394, Type.Teleport, new Rune[]{Rune.LAW, Rune.WATER}, new int[]{
				2, 4}, 72),
		DAREEYAK_TELEPORT(395, Type.Teleport, new Rune[]{Rune.LAW, Rune.AIR,
				Rune.FIRE}, new int[]{2, 2, 3}, 78),
		CARRALLANGAR_TELEPORT(396, Type.Teleport, new Rune[]{Rune.LAW,
				Rune.SOUL}, new int[]{2, 2}, 84),
		ANNAKARL_TELEPORT(397, Type.Teleport, new Rune[]{Rune.LAW, Rune.BLOOD}, new int[]{
				2, 2}, 90),
		GHORROCK_TELEPORT(398, Type.Teleport, new Rune[]{Rune.LAW, Rune.WATER}, new int[]{
				2, 8}, 96),
		EDGEVILLE_HOME_TELEPORT(356, Type.Teleport, new Rune[]{}, new int[]{}, 0);
		private final int textureID;
		private final Type spellType;
		private final int[] rCounts;
		private final Rune[] runes;
		private final int level;
		private final int[] extraEquip;
		private final boolean isAutoCastable;

		private Ancient(int tex, Type spell, Rune[] runes, int[] counts,
		                int level, int[] extraEquip, boolean isAutoCastable) {
			this.textureID = tex;
			this.spellType = spell;
			this.isAutoCastable = isAutoCastable;
			this.runes = runes;
			this.rCounts = counts;
			this.level = level;
			this.extraEquip = extraEquip;
		}

		private Ancient(int tex, Type spell, Rune[] runes, int[] counts,
		                int level, boolean isAutoCastable) {
			this(tex, spell, runes, counts, level, new int[0], isAutoCastable);
		}

		private Ancient(int tex, Type spell, Rune[] runes, int[] counts,
		                int level) {
			this(tex, spell, runes, counts, level, false);
		}

		/**
		 * {@inheritDoc}
		 */
		public int getTextureID() {
			return textureID;
		}

		/**
		 * {@inheritDoc}
		 */
		public Type getType() {
			return spellType;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isAutocastable() {
			return isAutoCastable;
		}

		/**
		 * {@inheritDoc}
		 */
		public Rune[] getRunes() {
			return runes;
		}

		/**
		 * {@inheritDoc}
		 */
		public int[] getRuneCounts() {
			return rCounts;
		}

		/**
		 * {@inheritDoc}
		 */
		public int[] getOtherEquipment() {
			return extraEquip;
		}

		/**
		 * {@inheritDoc}
		 */
		public int getMagicLevel() {
			return level;
		}

		/**
		 * {@inheritDoc}
		 */
		public int[] getOtherItems() {
			return new int[0];
		}

		/**
		 * {@inheritDoc}
		 */
		public Book getBook() {
			return Book.NORMAL;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isMembers() {
			return true;
		}
	}

	public enum Lunar implements Spell {
		BARBARIAN_TELE(547, Type.Teleport, new Rune[]{Rune.ASTRAL, Rune.LAW,
				Rune.FIRE}, new int[]{2, 2, 3}, 75),
		CURE_OTHER(559, Type.Combat, new Rune[]{Rune.ASTRAL, Rune.EARTH}, new int[]{
				1, 10}, 68),
		FERTILE_SOIL(553, Type.Misc, new Rune[]{Rune.ASTRAL, Rune.NATURE,
				Rune.EARTH}, new int[]{3, 2, 15}, 83),
		CURE_GROUP(565, Type.Combat, new Rune[]{Rune.ASTRAL, Rune.COSMIC}, new int[]{
				2, 2}, 74),
		NPC_CONTACT(568, Type.Misc, new Rune[]{Rune.ASTRAL, Rune.COSMIC,
				Rune.AIR}, new int[]{1, 1, 2}, 67),
		ENERGY_TRANSFER(558, Type.Combat, new Rune[]{Rune.ASTRAL, Rune.LAW,
				Rune.NATURE}, new int[]{3, 2, 1}, 91),
		MONSTER_EXAMINE(577, Type.Combat, new Rune[]{Rune.ASTRAL,
				Rune.COSMIC, Rune.MIND}, new int[]{1, 1, 1}, 66),
		HUMIDIFY(578, Type.Misc, new Rune[]{Rune.ASTRAL, Rune.WATER,
				Rune.FIRE}, new int[]{1, 3, 1}, 68),
		HUNTER_KIT(579, Type.Misc, new Rune[]{Rune.ASTRAL, Rune.EARTH}, new int[]{
				2, 2}, 71),
		STAT_SPY(626, Type.Combat, new Rune[]{Rune.ASTRAL, Rune.COSMIC,
				Rune.BODY}, new int[]{2, 2, 5}, 75),
		DREAM(630, Type.Misc, new Rune[]{Rune.ASTRAL, Rune.COSMIC, Rune.BODY}, new int[]{
				2, 1, 5}, 79),
		PLANK_MAKE(581, Type.Misc, new Rune[]{Rune.ASTRAL, Rune.EARTH,
				Rune.NATURE}, new int[]{2, 15, 1}, 86),
		SPELLBOOK_SWAP(632, Type.Misc, new Rune[]{Rune.ASTRAL, Rune.COSMIC,
				Rune.LAW}, new int[]{3, 2, 1}, 96),
		TUNE_BANE_ORE(7688, Type.Misc, new Rune[]{Rune.ASTRAL, Rune.EARTH}, new int[]{
				2, 4}, 87, new int[]{21778}, new int[0], false),
		MAGIC_IMBUE(552, Type.Misc, new Rune[]{Rune.ASTRAL, Rune.FIRE,
				Rune.WATER}, new int[]{2, 7, 7}, 82),
		VENGANCE(614, Type.Combat, new Rune[]{Rune.ASTRAL, Rune.DEATH,
				Rune.EARTH}, new int[]{4, 2, 10}, 94),
		BAKE_PIE(543, Type.Misc, new Rune[]{Rune.ASTRAL, Rune.FIRE,
				Rune.WATER}, new int[]{1, 5, 4}, 65),
		LUNAR_HOME_TELE(356, Type.Teleport, new Rune[]{}, new int[]{}, 0),
		FISHING_GUILD_TELE(555, Type.Teleport, new Rune[]{Rune.ASTRAL,
				Rune.LAW, Rune.WATER}, new int[]{3, 3, 8}, 85),
		KHAZARD_TELE(549, Type.Teleport, new Rune[]{Rune.ASTRAL, Rune.LAW,
				Rune.WATER}, new int[]{2, 2, 4}, 78),
		VENGANCE_OTHER(611, Type.Combat, new Rune[]{Rune.ASTRAL, Rune.DEATH,
				Rune.EARTH}, new int[]{3, 2, 10}, 93),
		MOONCLAN_TELE(544, Type.Teleport, new Rune[]{Rune.ASTRAL, Rune.LAW,
				Rune.EARTH}, new int[]{2, 1, 2}, 69),
		CATHERBY_TELEPORT(556, Type.Teleport, new Rune[]{Rune.ASTRAL,
				Rune.LAW, Rune.WATER}, new int[]{3, 3, 10}, 87),
		STRING_JEWELRY(550, Type.Misc, new Rune[]{Rune.ASTRAL, Rune.EARTH,
				Rune.WATER}, new int[]{2, 10, 5}, 80),
		CURE_ME(562, Type.Combat, new Rune[]{Rune.ASTRAL, Rune.COSMIC}, new int[]{
				2, 2}, 71),
		WATERBIRTH_TELE(545, Type.Teleport, new Rune[]{Rune.ASTRAL, Rune.LAW,
				Rune.WATER}, new int[]{2, 1, 1}, 72),
		SUPERGLASS_MAKE(548, Type.Misc, new Rune[]{Rune.ASTRAL, Rune.FIRE,
				Rune.AIR}, new int[]{2, 6, 10}, 77),
		BOOST_POTION_SHARE(551, Type.Combat, new Rune[]{Rune.ASTRAL,
				Rune.EARTH, Rune.WATER}, new int[]{3, 12, 10}, 84),
		STAT_RESTORE_POT_SHARE(554, Type.Combat, new Rune[]{Rune.ASTRAL,
				Rune.EARTH, Rune.WATER}, new int[]{2, 10, 10}, 81),
		ICE_PLATEAU_TELE(557, Type.Teleport, new Rune[]{Rune.ASTRAL,
				Rune.LAW, Rune.WATER}, new int[]{3, 3, 8}, 89),
		HEAL_OTHER(560, Type.Combat, new Rune[]{Rune.ASTRAL, Rune.LAW,
				Rune.BLOOD}, new int[]{3, 3, 1}, 92),
		HEAL_GROUP(566, Type.Combat, new Rune[]{Rune.ASTRAL, Rune.LAW,
				Rune.BLOOD}, new int[]{4, 3, 2}, 95),
		OURANIA_TELE(583, Type.Teleport, new Rune[]{Rune.ASTRAL, Rune.LAW,
				Rune.EARTH}, new int[]{2, 1, 6}, 71),
		CURE_PLANT(567, Type.Misc, new Rune[]{Rune.ASTRAL, Rune.EARTH}, new int[]{
				1, 8}, 66),
		TELE_GROUP_MOONCLAN(569, Type.Teleport, new Rune[]{Rune.ASTRAL,
				Rune.LAW, Rune.EARTH}, new int[]{2, 1, 4}, 70),
		TELE_GROUP_WATERBIRTH(570, Type.Teleport, new Rune[]{Rune.ASTRAL,
				Rune.LAW, Rune.WATER}, new int[]{2, 1, 5}, 73),
		TELE_GROUP_BARBARIAN(571, Type.Teleport, new Rune[]{Rune.ASTRAL,
				Rune.LAW, Rune.FIRE}, new int[]{2, 2, 6}, 66),
		TELE_GROUP_KHAZARD(572, Type.Teleport, new Rune[]{Rune.ASTRAL,
				Rune.LAW, Rune.WATER}, new int[]{2, 2, 8}, 79),
		TELE_GROUP_FISHING_GUILD(573, Type.Teleport, new Rune[]{Rune.ASTRAL,
				Rune.LAW, Rune.WATER}, new int[]{3, 3, 10}, 86),
		TELE_GROUP_CATHERBY(574, Type.Teleport, new Rune[]{Rune.ASTRAL,
				Rune.LAW, Rune.WATER}, new int[]{3, 3, 12}, 88),
		TELE_GROUP_ICE_PLATEAU(575, Type.Teleport, new Rune[]{Rune.ASTRAL,
				Rune.LAW, Rune.WATER}, new int[]{3, 3, 16}, 90),
		SOUTH_FALADOR_TELE(4585, Type.Teleport, new Rune[]{Rune.ASTRAL,
				Rune.LAW, Rune.AIR}, new int[]{2, 1, 2}, 72, true),
		REPAIR_RUNE_POUCH(4586, Type.Misc, new Rune[]{Rune.ASTRAL,
				Rune.COSMIC, Rune.LAW}, new int[]{2, 1, 1}, 75, true),
		NORTH_ARDOUGNE_TELE(4587, Type.Teleport, new Rune[]{Rune.ASTRAL,
				Rune.LAW, Rune.WATER}, new int[]{2, 1, 5}, 76, true),
		REMOTE_FARM(4588, Type.Misc, new Rune[]{Rune.ASTRAL, Rune.EARTH,
				Rune.NATURE}, new int[]{2, 2, 3}, 78, true),
		SPIRITUALISE_FOOD(5645, Type.Combat, new Rune[]{Rune.ASTRAL,
				Rune.COSMIC, Rune.BODY}, new int[]{2, 3, 5}, 80, true),
		MAKE_LEATHER(5644, Type.Misc, new Rune[]{Rune.ASTRAL, Rune.FIRE,
				Rune.BODY}, new int[]{2, 2, 2}, 83, true),
		DISRUPTION_SHIELD(5646, Type.Combat, new Rune[]{Rune.ASTRAL,
				Rune.BLOOD, Rune.BODY}, new int[]{3, 3, 10}, 90, true),
		VENGANCE_GROUP(5647, Type.Combat, new Rune[]{Rune.ASTRAL, Rune.DEATH,
				Rune.EARTH}, new int[]{4, 3, 11}, 95, true),
		TROLLHEIM_TELE(7685, Type.Teleport, new Rune[]{Rune.ASTRAL, Rune.LAW,
				Rune.WATER}, new int[]{3, 3, 10}, 92, true),
		TELE_GROUP_TROLLHEIM(7686, Type.Teleport, new Rune[]{Rune.ASTRAL,
				Rune.LAW, Rune.WATER}, new int[]{3, 3, 20}, 93, true),
		BORROWED_POWER(7691, Type.Misc, new Rune[]{Rune.ASTRAL}, new int[]{3}, 99, new int[0], new int[]{21514}, true);
		private final int textureID;
		private final Type spellType;
		private final boolean isLividFarm;
		private final int[] rCounts;
		private final Rune[] runes;
		private final int level;
		private final int[] extraItems;
		private final int[] extraEquip;

		private Lunar(int tex, Type spell, Rune[] runes, int[] counts,
		              int level, int[] extraItems, int[] extraEquip, boolean livid) {
			this.textureID = tex;
			this.spellType = spell;
			this.isLividFarm = livid;
			this.runes = runes;
			this.rCounts = counts;
			this.level = level;
			this.extraItems = extraItems;
			this.extraEquip = extraEquip;
		}

		private Lunar(int tex, Type spell, Rune[] runes, int[] counts,
		              int level, boolean livid) {
			this(tex, spell, runes, counts, level, new int[0], new int[0],
					livid);
		}

		private Lunar(int tex, Type spell, Rune[] runes, int[] counts, int level) {
			this(tex, spell, runes, counts, level, false);
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isLividFarm() {
			return isLividFarm;
		}

		/**
		 * {@inheritDoc}
		 */
		public int getTextureID() {
			return textureID;
		}

		/**
		 * {@inheritDoc}
		 */
		public Type getType() {
			return spellType;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isAutocastable() {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		public Rune[] getRunes() {
			return runes;
		}

		/**
		 * {@inheritDoc}
		 */
		public int[] getRuneCounts() {
			return rCounts;
		}

		/**
		 * {@inheritDoc}
		 */
		public int[] getOtherEquipment() {
			return extraEquip;
		}

		/**
		 * {@inheritDoc}
		 */
		public int getMagicLevel() {
			return level;
		}

		/**
		 * {@inheritDoc}
		 */
		public int[] getOtherItems() {
			return extraItems;
		}

		/**
		 * {@inheritDoc}
		 */
		public Book getBook() {
			return Book.LUNAR;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isMembers() {
			return true;
		}
	}

	enum Staff {
		// staff, battle-staff, mystic staff
		AIR(Rune.AIR, 1381, 1397, 1405),
		FIRE(Rune.FIRE, 1387, 1393, 1401),
		EARTH(Rune.EARTH, 1385, 1399, 1407),
		WATER(Rune.WATER, 1383, 1395, 1403),
		// battle-staff, mystic-staff
		MUD(new Rune[]{Rune.WATER, Rune.EARTH}, 6562, 6563),
		LAVA(new Rune[]{Rune.EARTH, Rune.FIRE}, 3053, 3054),
		STEAM(new Rune[]{Rune.WATER, Rune.FIRE}, 11736, 11738),
		// other staffs
		IBAN(1409),
		SARADOMIN(2415),
		GUTHIX(2416, 8841), // Both guthix staff and void knight mace
		ZAMAORAK(2417),
		TOME_OF_FROST(Rune.WATER, 18346),
		SLAYER(4170, 15486), // Slayer staff and Staff of light
		ZURIEL(13868);
		private final int[] ids;
		private final Rune[] runes;

		private Staff(Rune[] runes, int... ids) {
			this.runes = runes;
			this.ids = ids;
		}

		private Staff(Rune rune, int... ids) {
			this(new Rune[]{rune}, ids);
		}

		private Staff(int... ids) {
			this(new Rune[0], ids);
		}

		/**
		 * Get the ids corresponding to this staff
		 *
		 * @return
		 */
		public int[] getIDs() {
			return ids;
		}

		/**
		 * Get the runes corresponding to this staff
		 *
		 * @return
		 */
		public Rune[] getRunes() {
			return runes;
		}
	}

	enum Rune {
		AIR(556),
		FIRE(554),
		EARTH(557),
		WATER(555),
		MIND(558),
		CHAOS(562),
		DEATH(560),
		BLOOD(565),
		ARMADYL(21773),
		BODY(559),
		COSMIC(564),
		ASTRAL(9075),
		NATURE(561),
		LAW(563),
		SOUL(566);
		private final int[] ids;

		private Rune(int... ids) {
			this.ids = ids;
		}

		/**
		 * Gets the item ids for this rune
		 *
		 * @return
		 */
		public int[] getIDs() {
			return ids;
		}

		/**
		 * Gets the staves that work with this rune
		 *
		 * @return
		 */
		public Staff[] getStaves() {
			List<Staff> staves = new ArrayList<Staff>();
			for (Staff s : Staff.values()) {
				for (Rune r : s.getRunes()) {
					if (r.equals(this)) {
						staves.add(s);
						break;
					}
				}
			}
			return staves.toArray(new Staff[staves.size()]);
		}
	}

	enum ComboRune {
		DUST(new Rune[]{Rune.AIR, Rune.EARTH}, 4696),
		LAVA(new Rune[]{Rune.EARTH, Rune.FIRE}, 4699),
		MIST(new Rune[]{Rune.AIR, Rune.WATER}, 4695),
		MUD(new Rune[]{Rune.EARTH, Rune.WATER}, 4698),
		SMOKE(new Rune[]{Rune.FIRE, Rune.AIR}, 4697),
		STEAM(new Rune[]{Rune.FIRE, Rune.WATER}, 4694);
		private final int[] ids;
		private final Rune[] runes;

		private ComboRune(Rune[] runes, int... ids) {
			this.runes = runes;
			this.ids = ids;
		}

		/**
		 * Gets the ids of this combonation rune
		 *
		 * @return
		 */
		public int[] getIDs() {
			return ids;
		}

		/**
		 * Gets the runes this combination rune works for
		 *
		 * @return
		 */
		public Rune[] getRunes() {
			return runes;
		}

		/**
		 * Gets all the combonation runes that work with the provided rune
		 *
		 * @param r
		 * @return
		 */
		public static ComboRune[] matchRunes(Rune r) {
			List<ComboRune> runes = new ArrayList<ComboRune>();
			for (ComboRune cR : values()) {
				for (Rune cRR : cR.getRunes()) {
					if (cRR.equals(r)) {
						runes.add(cR);
						break;
					}
				}
			}
			return runes.toArray(new ComboRune[runes.size()]);
		}
	}

	/**
	 * Checks if the set of runes and rune counts is avaliable to the player
	 *
	 * @param runes   Runes to check
	 * @param lCounts the Counts for the runes
	 * @return
	 */
	public static boolean validateRuneSet(Rune[] runes, int[] lCounts) {
		int[] counts = new int[runes.length];
		// Cache equipment so we don't screw with tabs
		Item[] itms = Equipment.getItems();
		// Load combo rune counts
		int[] comboRuneCounts = new int[ComboRune.values().length];
		for (int i = 0; i < comboRuneCounts.length; i++) {
			ComboRune cR = ComboRune.values()[i];
			comboRuneCounts[i] = Inventory.getCount(true, cR.getIDs());
		}
		out:
		for (int i = 0; i < runes.length; i++) {
			Rune r = runes[i];
			counts[i] = i < lCounts.length ? lCounts[i] : 1;
			for (Staff s : r.getStaves()) {
				for (Item itm : itms) {
					if (itm != null) {
						for (int id : s.getIDs()) {
							if (itm.getID() == id) {
								counts[i] = 0;
								continue out;
							}
						}
					}
				}
			}
			counts[i] -= Inventory.getCount(true, r.getIDs());
			ComboRune[] cRunes = ComboRune.matchRunes(r);
			for (ComboRune cR : cRunes) {
				int take = Math.min(comboRuneCounts[cR.ordinal()], counts[i]);
				counts[i] -= take;
				comboRuneCounts[cR.ordinal()] -= take;
			}
		}
		for (int i : counts) {
			if (i > 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Gets the current spellbook interface, or <tt>null</tt> if not found
	 *
	 * @return
	 */
	public static Interface getInterface() {
		for (Book b : Book.values()) {
			Interface i = b.getInterface();
			if (i != null) {
				return i;
			}
		}
		return null;
	}

	/**
	 * Checks if its possible to cast the provided spell
	 *
	 * @param spell
	 * @return
	 */
	public static boolean canCast(Spell spell) {
		return (spell.getBook().equals(Book.getCurrentBook())
				&& Skills.getLevel(Skills.MAGIC) >= spell.getMagicLevel()
				&& Equipment.containsAll(spell.getOtherEquipment())
				&& Inventory.containsAll(spell.getOtherItems()) && validateRuneSet(
				spell.getRunes(), spell.getRuneCounts()));
	}

	/**
	 * Gets the component on the spellbook interface for the provided spell
	 *
	 * @param spell
	 * @return
	 */
	public static InterfaceComponent getSpellComponent(Spell spell) {
		if (!isTabOpen()) {
			openTab();
		}
		Interface inter = getInterface();
		if (inter != null) {
			// Check if the spell is filtered out
			for (int i = 0; i < Math.min(Button.getFilters().length,
					Button.getFilterTypes().length); i++) {
				if (!Button.getFilters()[i].isSelected()
						&& Button.getFilterTypes()[i].equals(spell.getType())) {
					Button.getFilters()[i].setSelected(true);
				}
			}
			for (InterfaceComponent iC : inter.getComponents()) {
				if (iC != null && iC.getTextureID() == spell.getTextureID()) {
					return iC;
				}
			}
		}
		return null;
	}

	/**
	 * Interacts with the specified spell component, scrolling as necessary
	 *
	 * @param spell
	 * @param action
	 * @param option
	 * @return did interact
	 */
	private static boolean spellInteract(Spell spell, String action, String option) {
		InterfaceComponent comp = getSpellComponent(spell);
		if (comp != null && comp.isValid()) {
			if (Interfaces.scrollTo(comp, Book.getCurrentBook().getScrollBar())) {
				return comp.interact(action, option);
			}
		}
		return false;
	}

	/**
	 * Opens the magic tab
	 *
	 * @return opened
	 */
	public static boolean openTab() {
		return Game.openTab(Tabs.MAGIC);
	}

	/**
	 * Checks if the Magic tab is open
	 *
	 * @return is open
	 */
	public static boolean isTabOpen() {
		return Game.getCurrentTab() != null
				&& Game.getCurrentTab().equals(Tabs.MAGIC);
	}

	/**
	 * Checks whether or not a spell is selected.
	 *
	 * @return <tt>true</tt> if a spell is selected; otherwise <tt>false</tt>.
	 */
	public static boolean isSpellSelected() {
		return Context.get().client.isSpellSelected();
	}

	/**
	 * Gets the selected spell.
	 *
	 * @return <tt>spell</tt> if a spell is selected; otherwise <tt>null</tt> .
	 */
	public Spell getSelectedSpell() {
		if (Book.getCurrentBook() != null) {
			for (Spell spell : Book.getCurrentBook().getSpells()) {
				InterfaceComponent c = getSpellComponent(spell);
				if (c.getBorderThickness() == 2) {
					return spell;
				}
			}
		}
		return null;
	}

	/**
	 * Clicks a specified spell, opens magic tab if not open and uses interface
	 * of the spell to click it, so it works if the spells are layout in any
	 * way.
	 *
	 * @param spell The spell to cast.
	 * @return <tt>true</tt> if the spell was clicked; otherwise <tt>false</tt>.
	 */
	public static boolean castSpell(final Spell spell) {
		return spellInteract(spell, "Cast", null);
	}

	/**
	 * Hovers a specified spell, opens magic tab if not open and uses interface
	 * of the spell to hover it, so it works if the spells are layout in any
	 * sway.
	 *
	 * @param spell The spell to hover.
	 * @return <tt>true</tt> if the spell was hovered; otherwise <tt>false</tt>.
	 */
	public static boolean hoverSpell(final Spell spell) {
		InterfaceComponent comp = getSpellComponent(spell);
		if (comp != null && comp.isValid()) {
			if (Interfaces.scrollTo(comp, Book.getCurrentBook().getScrollBar())) {
				return comp.hover();
			}
		}
		return false;
	}

	/**
	 * Auto-casts a spell via the magic tab.
	 *
	 * @param spell The spell to auto-cast.
	 * @return <tt>true</tt> if the "Auto-cast" interface option was clicked;
	 *         otherwise <tt>false</tt>.
	 */
	public static boolean autoCastSpell(final Spell spell) {
		return spell.isAutocastable() && spellInteract(spell, "Autocast", null);
	}

	/**
	 * Gets the current spell book.
	 *
	 * @return The Book enum of your current spell book.
	 */
	public static Book getCurrentSpellBook() {
		return Book.getCurrentBook();
	}

	/**
	 * Casts a spell on a Tile/Player/NPC/Object/Ground Item.
	 *
	 * @param entity An entity or tile
	 * @param spell  The spell to cast.
	 * @return <tt>true</tt> if casted; otherwise <tt>false</tt>.
	 */
	public static boolean castSpellOn(final Entity entity, final Spell spell) {
		return !(isSpellSelected() || entity == null) && castSpell(spell) && entity.interact("Cast");
	}
}
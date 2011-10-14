package org.rsbot.script.methods.tabs;

import org.rsbot.bot.concurrent.Task;
import org.rsbot.script.methods.Settings;
import org.rsbot.script.methods.ui.Interfaces;
import org.rsbot.script.wrappers.Interface;
import org.rsbot.script.wrappers.InterfaceComponent;

/**
 * Summoning tab related operations.
 */
public class Summoning {
	public static final int WIDGET_TAB = 747;
	public static final int WIDGET_DETAILS = 662;
	public static final int WIDGET_OPTIONS = 880;
	public static final int WIDGET_DISMISS = 228;

	public static final int COMPONENT_TAB = 2;
	public static final int COMPONENT_SUMMONED_CHECK = 3;
	public static final int COMPONENT_POINTS = 5;
	public static final int COMPONENT_FOLLOWER_DETAILS = 9;
	public static final int COMPONENT_SPECIAL_MOVE = 99;
	public static final int COMPONENT_ATTACK = 14;
	public static final int COMPONENT_CALL_FOLLOWER = 17;
	public static final int COMPONENT_DISMISS_FOLLOWER = 18;
	public static final int COMPONENT_TAKE_BOB = 12;
	public static final int COMPONENT_RENEW_FAMILIAR = 13;
	public static final int COMPONENT_DISMISS = 2;

	public static final int TEXTURE_ID_SUMMONED_FAMILIAR = 1802;
	public static final int SETTING_INDEX_TIME_LEFT = 1176;

	/**
	 * Summoning familiars.
	 */
	public static enum Familiar {
		SPIRIT_WOLF("Spirit Wolf", 1, 26, 150, 1, 6, 3, "Howl", 0),
		DREADFOWL("Dreadfowl", 1, 26, 160, 4, 4, 3, "Dreadfowl Strike", 0),
		SPIRIT_SPIDER("Spirit spider", 2, 25, 180, 10, 15, 6, "Egg Spawn", 0),
		THORNY_SNAIL("Thorny snail", 2, 26, 280, 13, 16, 3, "Slime Spray", 3),
		GRANITE_CRAB("Granite crab", 2, 26, 160, 16, 18, 12, "Stony Shell", 0),
		MOSQUITO("Spirit mosquito", 2, 32, 430, 17, 12, 3, "Pester", 0),
		DESERT_WYRM("Desert wyrm", 1, 31, 470, 18, 19, 6, "Electric Lash", 0),
		SPIRIT_SCORPION("Spirit scorpion", 2, 51, 670, 19, 17, 6, "Venom Shot", 0),
		SPIRIT_TZ_KIH("Spirit Tz-Kih", 3, 36, 630, 22, 18, 6, "Fireball Assault", 0),
		ALBINO_RAT("Albino rat", 3, 37, 680, 23, 22, 6, "Cheese Feast", 0),
		SPIRIT_KALPHITE("Spirit kalphite", 3, 39, 770, 25, 22, 6, "Sandstorm", 6),
		COMPOST_MOUND("Compost Mound", 6, 37, 930, 28, 24, 12, "Generate Compost", 0),
		GIANT_CHINCHOMPA("Giant chinchompa", 1, 42, 970, 29, 31, 3, "Explode", 0),
		VAMPIRE_BAT("Vampire bat", 4, 44, 1050, 31, 33, 4, "Vampire Touch", 0),
		HONEY_BADGER("Honey badger", 4, 45, 1100, 32, 25, 12, "Insane Ferocity", 0),
		BEAVER("Beaver", 4, 0, 0, 33, 27, 3, "Multichop", 0),
		VOID_RAVAGER("Void ravager", 4, 46, 1210, 34, 27, 3, "Call to Arms", 0),
		VOID_SPINNER("Void spinner", 4, 46, 1210, 34, 27, 3, "Call to Arms", 0),
		VOID_SHIFTER("Void shifter", 4, 40, 590, 34, 94, 3, "Call to Arms", 0),
		VOID_TORCHER("Void torcher", 4, 46, 1210, 34, 94, 3, "Call to Arms", 0),
		BRONZE_MINOTAUR("Bronze minotaur", 9, 50, 1330, 36, 30, 6, "Bronze Bull Rush", 0),
		BULL_ANT("Bull ant", 5, 58, 1540, 40, 30, 12, "Unburden", 9),
		MACAW("Macaw", 5, 0, 0, 41, 31, 12, "Herbcall", 0),
		EVIL_TURNIP("Evil turnip", 5, 62, 1670, 42, 30, 6, "Evil Flames", 0),
		SPIRIT_COCKATRICE("Spirit cockatrice", 6, 64, 1730, 43, 36, 3, "Petrifying Gaze", 0),
		SPIRIT_GUTHATRICE("Spirit guthatrice", 6, 64, 1730, 43, 36, 3, "Petrifying Gaze", 0),
		SPIRIT_SARATRICE("Spirit saratrice", 6, 64, 1730, 43, 36, 3, "Petrifying Gaze", 0),
		SPIRIT_ZAMATRICE("Spirit zamatrice", 6, 64, 1730, 43, 36, 3, "Petrifying Gaze", 0),
		SPIRIT_PENGATRICE("Spirit pengatrice", 6, 64, 1730, 43, 36, 3, "Petrifying Gaze", 0),
		SPIRIT_CORAXATRICE("Spirit coraxatrice", 6, 64, 1730, 43, 36, 3, "Petrifying Gaze", 0),
		SPIRIT_VULATRICE("Spirit vulatrice", 6, 64, 1730, 43, 36, 3, "Petrifying Gaze", 0),
		IRON_MINOTAUR("Iron minotaur", 9, 70, 1930, 46, 37, 6, "Iron Bull Rush", 0),
		PYRELORD("Pyrelord", 5, 70, 1930, 46, 32, 6, "Immense Heat", 0),
		MAGPIE("Magpie", 5, 0, 0, 47, 34, 12, "Thieving Fingers", 0),
		BLOATED_LEECH("Bloated leech", 5, 76, 2110, 49, 34, 6, "Blood Drain", 0),
		SPIRIT_TERRORBIRD("Spirit terrorbird", 6, 62, 2330, 52, 36, 8, "Tireless Run", 12),
		ABYSSAL_PARASITE("Abyssal parasite", 6, 86, 2340, 54, 30, 6, "Abyssal Drain", 7),
		SPIRIT_JELLY("Spirit jelly", 6, 88, 2550, 55, 43, 6, "Dissolve", 0),
		STEEL_MINOTAUR("Steel minotaur", 9, 90, 2600, 56, 46, 6, "Steel Bull Rush", 0),
		IBIS("Ibis", 6, 0, 0, 56, 38, 12, "Fish Rain", 0),
		SPIRIT_GRAAHK("Spirit graahk", 6, 93, 2680, 57, 49, 3, "Goad", 0),
		SPIRIT_KYATT("Spirit kyatt", 6, 93, 2680, 57, 49, 3, "Ambush", 0),
		SPIRIT_LARUPIA("Spirit larupia", 6, 93, 2680, 57, 49, 6, "Rending", 0),
		KARAMTHULHU_OVERLORD("Karamthulhu overlord", 6, 95, 2760, 58, 44, 3, "Doomspere Device", 0),
		SMOKE_DEVIL("Smoke devil", 7, 101, 3000, 61, 48, 6, "Dust Cloud", 0),
		ABYSSAL_LURKER("Abyssal lurker", 7, 93, 3080, 62, 41, 20, "Abyssal Stealth", 7),
		SPIRIT_COBRA("Spirit cobra", 7, 105, 3140, 63, 56, 3, "Ophidian Incubation", 0),
		STRANGER_PLANT("Stranger plant", 7, 107, 3220, 64, 49, 6, "Poisonous Blast", 0),
		MITHRIL_MINOTAUR("Mithril minotaur", 9, 112, 3400, 66, 55, 6, "Mithril Bull Rush", 0),
		BARKER_TOAD("Barker toad", 7, 112, 3400, 66, 8, 6, "Toad Bark", 0),
		WAR_TORTOISE("War tortoise", 7, 86, 3480, 67, 43, 20, "Testudo", 18),
		BUNYIP("Bunyip", 7, 70, 400, 68, 44, 3, "Swallow Whole", 0),
		FRUIT_BAT("Fruit bat", 7, 0, 1000, 69, 45, 6, "Fruitfall", 0),
		RAVENOUS_LOCUST("Ravenous locust", 4, 120, 3700, 70, 24, 12, "Famine", 0),
		ARCTIC_BEAR("Arctic bear", 8, 122, 3810, 71, 28, 6, "Arctic Blast", 0),
		PHOENIX("Phoenix", 8, 124, 1530, 72, 30, 12, "Rise from the Ashes", 0),
		OBSIDIAN_GOLEM("Obsidian golem", 8, 126, 4060, 73, 55, 12, "Volcanic Strength", 0),
		GRANITE_LOBSTER("Granite lobster", 8, 129, 4180, 74, 47, 6, "Crushing Claw", 0),
		PRAYING_MANTIS("Praying mantis", 8, 131, 4280, 75, 69, 6, "Mantis Strike", 0),
		ADAMANT_MINOTAUR("Adamant minotaur", 9, 133, 4410, 76, 66, 6, "Adamant Bull Rush", 0),
		FORGE_REGENT("Forge regent", 9, 133, 4410, 76, 45, 6, "Inferno", 0),
		TALON_BEAST("Talon beast", 9, 135, 4540, 77, 49, 6, "Deadly Claw", 0),
		GIANT_ENT("Giant ent", 8, 137, 4670, 78, 49, 6, "Acorn Missile", 0),
		FIRE_TITAN("Fire titan", 9, 139, 4760, 79, 62, 20, "Titan's Constitution", 0),
		ICE_TITAN("Ice titan", 9, 139, 4760, 79, 64, 20, "Titan's Constitution", 0),
		MOSS_TITAN("Moss titan", 9, 139, 4760, 79, 58, 20, "Titan's Constitution", 0),
		HYDRA("Hydra", 8, 141, 4900, 80, 49, 6, "Regrowth", 0),
		SPIRIT_DAGANNOTH("Spirit dagannoth", 9, 148, 5280, 83, 57, 6, "Spike Shot", 0),
		LAVA_TITAN("Lava titan", 9, 148, 5280, 83, 61, 4, "Ebon Thunder", 0),
		SWAMP_TITAN("Swamp titan", 9, 152, 5660, 85, 56, 6, "Swamp Plague", 0),
		RUNE_MINOTAUR("Rune minotaur", 9, 154, 5700, 86, 151, 6, "Rune Bull Rush", 0),
		UNICORN_STALLION("Unicorn stallion", 9, 70, 1000, 88, 54, 20, "Healing Aura", 0),
		GEYSER_TITAN("Geyser titan", 10, 200, 6100, 89, 69, 6, "Boil", 0),
		WOLPERTIGER("Wolpertiger", 10, 210, 6510, 92, 62, 20, "Magic Focus", 0),
		ABYSSAL_TITAN("Abyssal titan", 10, 215, 6670, 93, 32, 6, "Essence Shipment", 7),
		IRON_TITAN("Iron titan", 10, 220, 6940, 95, 60, 12, "Iron Within", 0),
		PACK_YAK("Pack yak", 10, 135, 7100, 96, 58, 12, "Winter Storage", 30),
		STEEL_TITAN("Steel titan", 10, 230, 7540, 99, 64, 12, "Steel of Legends", 0),
		CLAY_FAMILIAR_CLASS1("Clay familiar (class 1)", 0, 25, 100, 1, 30, 0, "Clay deposit scroll", 1),
		CLAY_FAMILIAR_CLASS2("Clay familiar (class 2)", 0, 37, 250, 20, 30, 0, "Clay deposit scroll", 6),
		CLAY_FAMILIAR_CLASS3("Clay familiar (class 3)", 0, 58, 500, 40, 30, 0, "Clay deposit scroll", 12),
		CLAY_FAMILIAR_CLASS4("Clay familiar (class 4)", 0, 93, 750, 60, 30, 0, "Clay deposit scroll", 18),
		CLAY_FAMILIAR_CLASS5("Clay familiar (class 5)", 0, 139, 1000, 80, 30, 0, "Clay deposit scroll", 24);

		private final String name;
		private final int summonPoints;
		private final int combatLevel;
		private final int lifePoints;
		private final int requiredLevel;
		private final int time;
		private final int specialMovePoints;
		private final String scrollName;
		private final int inventorySpace;

		Familiar(String name, int summonPoints, int combatLevel, int lifePoints, int requiredLevel, int time, int specialMovePoints, String scrollName, int inventorySpace) {
			this.name = name;
			this.summonPoints = summonPoints;
			this.combatLevel = combatLevel;
			this.lifePoints = lifePoints;
			this.requiredLevel = requiredLevel;
			this.time = time;
			this.specialMovePoints = specialMovePoints;
			this.scrollName = scrollName;
			this.inventorySpace = inventorySpace;
		}

		/**
		 * Gets the level required to summon the familiar
		 *
		 * @return the level required to summon the familiar
		 */
		public int getRequiredLevel() {
			return requiredLevel;
		}

		/**
		 * Gets the time that the familiar is summoned for
		 *
		 * @return the time that the familiar is summoned for
		 */
		public int getTime() {
			return time;
		}

		/**
		 * Gets the name of the familiar
		 *
		 * @return the name of the familiar
		 */
		public String getName() {
			return name;
		}

		/**
		 * Gets the name of the special move scroll for the familiar
		 *
		 * @return the name of the special move scroll for the familiar
		 */
		public String getScrollName() {
			return scrollName;
		}

		/**
		 * Gets the summoning points required to cast a special move
		 *
		 * @return the summoning points required to cast a special move
		 */
		public int getSpecialMovePoints() {
			return specialMovePoints;
		}

		/**
		 * Gets the summoning points required to summon the familiar
		 *
		 * @return the summoning points required to summon the familiar
		 */
		public int getSummonPoints() {
			return summonPoints;
		}

		/**
		 * Gets the combat level of the familiar
		 *
		 * @return Gets the combat level of the familiar
		 */
		public int getCombatLevel() {
			return combatLevel;
		}

		/**
		 * Returns whether or not the familiar is capable of fighting in combat
		 *
		 * @return <tt>true</tt> if the familair is capable of fighting in combat; otherwise <tt>false</tt>
		 */
		public boolean isCombat() {
			return combatLevel != 0;
		}

		/**
		 * Gets the maximum amount of life points of the familiar
		 *
		 * @return the maximum amount of life points of the familiar
		 */
		public int getLifePoints() {
			return lifePoints;
		}

		/**
		 * Gets the maximum amount of items the familiar can store
		 *
		 * @return the maximum amount of items the familiar can store
		 */
		public int getInventorySpace() {
			return inventorySpace;
		}

		/**
		 * Returns whether or not the familiar is capable of storing items
		 *
		 * @return <tt>true</tt> if the familair is capable of storing items; otherwise <tt>false</tt>
		 */
		public boolean canStore() {
			return inventorySpace != 0;
		}

		public String toString() {
			return name;
		}

	}

	/**
	 * Returns the number of summoning points left.
	 *
	 * @return The number of summoning points left.
	 */
	public static int getPoints() {
		InterfaceComponent pointsComp = Interfaces.getComponent(WIDGET_TAB, COMPONENT_POINTS);
		if (pointsComp != null && pointsComp.isValid()) {
			try {
				return Integer.parseInt(pointsComp.getText());
			} catch (NumberFormatException ignored) {
			}
		}
		return -1;
	}

	/**
	 * Checks whether you have a familiar.
	 *
	 * @return <tt>true</tt> if you have a familiar.
	 */
	public static boolean isFamiliarSummoned() {
		InterfaceComponent checkComp = Interfaces.getComponent(WIDGET_TAB, COMPONENT_SUMMONED_CHECK);
		return checkComp != null && checkComp.isValid() && checkComp.getTextureID() == TEXTURE_ID_SUMMONED_FAMILIAR;
	}

	/**
	 * Does a action in the summoning skill bubble.
	 *
	 * @param act The action to perform.
	 * @return <tt>true</tt> if action is performed.
	 */
	public static boolean interact(String act) {
		InterfaceComponent interactComp = Interfaces.getComponent(WIDGET_TAB, COMPONENT_TAB);
		return interactComp != null && interactComp.isValid() && interactComp.interact(act);
	}

	/**
	 * Presses attack in the summoning tab.
	 *
	 * @return <tt>true</tt> if the action was performed.
	 */
	public static boolean attack() {
		return isFamiliarSummoned() && interact("Attack");
	}

	/**
	 * Casts the familiar's attack.
	 *
	 * @return <tt>true</tt> if the action was performed.
	 */
	public static boolean cast() {
		return isFamiliarSummoned() && Inventory.getItemID(getSummonedFamiliar().getScrollName()) != -1 && interact("Cast");
	}

	/**
	 * Presses cancel in the summoning tab.
	 *
	 * @return <tt>true</tt> if the action was performed.
	 */
	public static boolean cancel() {
		return interact("Cancel");
	}

	/**
	 * Renews the familiar from the summoning tab.
	 *
	 * @return <tt>true</tt> if the action was performed.
	 */
	public static boolean renewFamiliar() {
		return isFamiliarSummoned() && Inventory.getItemID(getSummonedFamiliar().getName() + " pouch") != -1 && interact("Renew Familiar");
	}

	/**
	 * Takes the BoB of the familiar.
	 *
	 * @return <tt>true</tt> if the action was performed.
	 */
	public static boolean takeBob() {
		return isFamiliarSummoned() && getSummonedFamiliar().canStore() && interact("Take BoB");
	}

	/**
	 * Dismisses the familiar from the summoning tab.
	 *
	 * @return <tt>true</tt> if the action was performed.
	 */
	public static boolean dismiss() {
		if (interact("Dismiss")) {
			InterfaceComponent dismissComp = Interfaces.getComponent(WIDGET_DISMISS, COMPONENT_DISMISS);
			for (int i = 0; i < 200; i++) {
				if (dismissComp != null && dismissComp.isValid()) {
					return dismissComp.click(true);
				}
				Task.sleep(10);
				dismissComp = Interfaces.getComponent(WIDGET_DISMISS, COMPONENT_DISMISS);
			}
		}
		return false;
	}

	/**
	 * Calls the familiar from the summoning tab.
	 *
	 * @return <tt>true</tt> if the action was performed.
	 */
	public static boolean call() {
		return isFamiliarSummoned() && interact("Call Follower");
	}

	/**
	 * Shows follower details from the summoning tab.
	 *
	 * @return <tt>true</tt> if the action was performed.
	 */
	public static boolean showDetails() {
		return isFamiliarSummoned() && interact("Follower Details");
	}

	/**
	 * Returns the time left before the familiar vanishes in seconds.
	 *
	 * @return The time left before the familiar vanishes in seconds.
	 */

	public static float getSecondsLeft() {
		return Settings.get(SETTING_INDEX_TIME_LEFT) / 128.0f;
	}

	/**
	 * Sets the left-click option to the given action.
	 *
	 * @param action the action string
	 * @return <tt>true</tt> if action is performed.
	 */
	public static boolean setLeftClickOption(String action) {
		Interface optionInterface = Interfaces.get(WIDGET_OPTIONS);
		if (optionInterface != null && optionInterface.isValid()) {
			for (InterfaceComponent option : optionInterface.getComponents()) {
				if (option != null && option.getText() != null) {
					if (option.containsAction(action)
							|| option.containsText(action)) {
						return setLeftClickOption(option.getIndex());
					}
				}
			}
		}
		return false;
	}

	/**
	 * Sets the left click option to the given index.
	 *
	 * @param option the option index
	 * @return <tt>true</tt> if action is performed.
	 */
	public static boolean setLeftClickOption(int option) {
		if (interact("Select left-click option")) {
			InterfaceComponent optionComp = Interfaces.getComponent(WIDGET_OPTIONS, option);
			for (int i = 0; i < 200; i++) {
				if (optionComp != null && optionComp.isValid()) {
					return optionComp.click(true);
				}
				Task.sleep(10);
				optionComp = Interfaces.getComponent(WIDGET_OPTIONS, option);
			}
		}
		return false;
	}

	/**
	 * Finds your current summoned familiar.
	 *
	 * @return your current familiar
	 */
	public static Familiar getSummonedFamiliar() {
		if (!isFamiliarSummoned()) {
			return null;
		}
		InterfaceComponent followerComp = Interfaces.getComponent(WIDGET_TAB, COMPONENT_CALL_FOLLOWER);
		if (followerComp != null && followerComp.isValid()) {
			String special = followerComp.getComponent(0).getComponentName();
			for (Familiar f : Familiar.values()) {
				if (special.toLowerCase().endsWith(f.getScrollName().toLowerCase())) {
					return f;
				}
			}
		}
		return null;
	}
}
package org.rsbot.script.methods;

import org.rsbot.bot.Context;
import org.rsbot.bot.accessors.Client;
import org.rsbot.bot.concurrent.Task;
import org.rsbot.script.methods.ui.Interfaces;

/**
 * This class is for all the skill calculations.
 * <p/>
 * Example usage: skills.getRealLevel(Skills.ATTACK);
 *
 * @author Timer
 */
public class Skills {
	public static final String[] SKILL_NAMES = {"attack", "defence", "strength", "constitution", "range", "prayer",
			"magic", "cooking", "woodcutting", "fletching", "fishing", "firemaking", "crafting", "smithing", "mining",
			"herblore", "agility", "thieving", "slayer", "farming", "runecrafting", "hunter", "construction",
			"summoning", "dungeoneering", "-unused-"};

	/**
	 * A table containing the experiences that begin each level.
	 */
	public static final int[] XP_TABLE = {0, 0, 83, 174, 276, 388, 512, 650, 801, 969, 1154, 1358, 1584, 1833, 2107,
			2411, 2746, 3115, 3523, 3973, 4470, 5018, 5624, 6291, 7028, 7842, 8740, 9730, 10824, 12031, 13363, 14833,
			16456, 18247, 20224, 22406, 24815, 27473, 30408, 33648, 37224, 41171, 45529, 50339, 55649, 61512, 67983,
			75127, 83014, 91721, 101333, 111945, 123660, 136594, 150872, 166636, 184040, 203254, 224466, 247886, 273742,
			302288, 333804, 368599, 407015, 449428, 496254, 547953, 605032, 668051, 737627, 814445, 899257, 992895,
			1096278, 1210421, 1336443, 1475581, 1629200, 1798808, 1986068, 2192818, 2421087, 2673114, 2951373, 3258594,
			3597792, 3972294, 4385776, 4842295, 5346332, 5902831, 6517253, 7195629, 7944614, 8771558, 9684577, 10692629,
			11805606, 13034431, 14391160, 15889109, 17542976, 19368992, 21385073, 23611006, 26068632, 28782069,
			31777943, 35085654, 38737661, 42769801, 47221641, 52136869, 57563718, 63555443, 70170840, 77474828,
			85539082, 94442737, 104273167};

	public static final int ATTACK = 0;
	public static final int DEFENSE = 1;
	public static final int STRENGTH = 2;
	public static final int CONSTITUTION = 3;
	public static final int RANGE = 4;
	public static final int PRAYER = 5;
	public static final int MAGIC = 6;
	public static final int COOKING = 7;
	public static final int WOODCUTTING = 8;
	public static final int FLETCHING = 9;
	public static final int FISHING = 10;
	public static final int FIREMAKING = 11;
	public static final int CRAFTING = 12;
	public static final int SMITHING = 13;
	public static final int MINING = 14;
	public static final int HERBLORE = 15;
	public static final int AGILITY = 16;
	public static final int THIEVING = 17;
	public static final int SLAYER = 18;
	public static final int FARMING = 19;
	public static final int RUNECRAFTING = 20;
	public static final int HUNTER = 21;
	public static final int CONSTRUCTION = 22;
	public static final int SUMMONING = 23;
	public static final int DUNGEONEERING = 24;

	public interface Interface {
		public static final int TAB_STATS = 320;
		public static final int ATTACK = 1;
		public static final int DEFENSE = 22;
		public static final int STRENGTH = 4;
		public static final int CONSTITUTION = 2;
		public static final int RANGE = 46;
		public static final int PRAYER = 70;
		public static final int MAGIC = 87;
		public static final int COOKING = 62;
		public static final int WOODCUTTING = 102;
		public static final int FLETCHING = 95;
		public static final int FISHING = 38;
		public static final int FIREMAKING = 85;
		public static final int CRAFTING = 78;
		public static final int SMITHING = 20;
		public static final int MINING = 3;
		public static final int HERBLORE = 30;
		public static final int AGILITY = 12;
		public static final int THIEVING = 54;
		public static final int SLAYER = 112;
		public static final int FARMING = 120;
		public static final int RUNECRAFTING = 104;
		public static final int HUNTER = 136;
		public static final int CONSTRUCTION = 128;
		public static final int SUMMONING = 144;
		public static final int DUNGEONEERING = 152;
	}

	/**
	 * Gets the index of the skill with a given name. This is not case sensitive.
	 *
	 * @param statName The skill's name.
	 * @return The index of the specified skill; otherwise -1.
	 */
	public static int getIndex(final String statName) {
		for (int i = 0; i < Skills.SKILL_NAMES.length; i++) {
			if (Skills.SKILL_NAMES[i].equalsIgnoreCase(statName)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Gets the level at the given experience.
	 *
	 * @param exp The experience.
	 * @return The level based on the experience given.
	 * @see #XP_TABLE
	 */
	public static int getLevelAt(final int exp) {
		for (int i = Skills.XP_TABLE.length - 1; i > 0; i--) {
			if (exp > Skills.XP_TABLE[i]) {
				return i;
			}
		}
		return 1;
	}

	/**
	 * Gets the experience at the given level.
	 *
	 * @param lvl The level.
	 * @return The level based on the experience given.
	 */
	public static int getLevelExperience(final int lvl) {
		if (lvl > 120) {
			return -1;
		}
		return Skills.XP_TABLE[lvl];
	}

	/**
	 * Gets the skill name of an index.
	 *
	 * @param index The index.
	 * @return The name of the skill for that index.
	 */
	public static String getName(final int index) {
		if (index > Skills.SKILL_NAMES.length - 1) {
			return null;
		}
		return Skills.SKILL_NAMES[index];
	}

	/**
	 * Gets the current experience for the given skill.
	 *
	 * @param index The index of the skill.
	 * @return -1 if the skill is unavailable
	 */
	public static int getExperience(final int index) {
		if (!isSkill(index)) {
			return -1;
		}
		final Client client = Context.get().client;
		final int[] skills = client.getSkillExperiences();
		if (index > skills.length - 1) {
			return -1;
		}
		return client.getSkillExperiences()[index];
	}

	/**
	 * Gets the effective level of the given skill (accounting for temporary
	 * boosts and reductions).
	 *
	 * @param index The index of the skill.
	 * @return The current level of the given Skill.
	 */
	public static int getLevel(final int index) {
		if (!isSkill(index)) {
			return -1;
		}
		return Context.get().client.getSkillLevels()[index];
	}

	/**
	 * Gets the player's current level in a skill based on their experience in that skill.
	 *
	 * @param index The index of the skill.
	 * @return The real level of the skill.
	 * @see #getAbsoluteLevel(int)
	 */
	public static int getAbsoluteLevel(final int index) {
		if (!isSkill(index)) {
			return -1;
		}
		return Skills.getLevelAt(getExperience(index));
	}

	/**
	 * Gets the percentage to the next level in a given skill.
	 *
	 * @param index The index of the skill.
	 * @return The percent to the next level of the provided skill or 0 if level
	 *         of skill is 99.
	 */
	public static int getPercentToLevel(final int index) {
		if (!isSkill(index)) {
			return -1;
		}
		final int lvl = getAbsoluteLevel(index);
		return getPercentBetweenLevels(index, lvl + 1);
	}

	/**
	 * Gets the percentage to the a level in a given skill.
	 *
	 * @param index  The index of the skill.
	 * @param endLvl The level for the percent.
	 * @return The percent to the level provided of the provided skill or 0 if level
	 *         of skill is 99.
	 */
	public static int getPercentBetweenLevels(final int index, final int endLvl) {
		if (!isSkill(index)) {
			return -1;
		}
		final int lvl = getAbsoluteLevel(index);
		if (index == Skills.DUNGEONEERING && (lvl == 120 || endLvl > 120)) {
			return 0;
		} else if (lvl == 99 || endLvl > 99) {
			return 0;
		}
		final int xpTotal = Skills.XP_TABLE[endLvl] - Skills.XP_TABLE[lvl];
		if (xpTotal == 0) {
			return 0;
		}
		final int xpDone = getExperience(index) - Skills.XP_TABLE[lvl];
		return 100 * xpDone / xpTotal;
	}

	/**
	 * Gets the maximum level of a given skill.
	 *
	 * @param index The index of the skill.
	 * @return The max level of the skill.
	 */
	public static int getMaxLevel(final int index) {
		if (!isSkill(index)) {
			return -1;
		}
		return Context.get().client.getSkillLevelMaxes()[index];
	}

	/**
	 * Gets the maximum experience of a given skill.
	 *
	 * @param index The index of the skill.
	 * @return The max experience of the skill.
	 */
	public static int getMaxExperience(final int index) {
		if (!isSkill(index)) {
			return -1;
		}
		return Context.get().client.getSkillExperiencesMax()[index];
	}

	/**
	 * Gets the experience remaining until reaching the next level in a given skill.
	 *
	 * @param index The index of the skill.
	 * @return The experience to the next level of the skill.
	 */
	public static int getExperienceToLevel(final int index) {
		if (!isSkill(index)) {
			return -1;
		}
		final int lvl = getAbsoluteLevel(index);
		return getExperienceBetweenLevels(index, lvl + 1);
	}

	/**
	 * Gets the experience remaining until reaching the a level in a given
	 * skill.
	 *
	 * @param index  The index of the skill.
	 * @param endLvl The level for the experience remaining.
	 * @return The experience to the level provided of the skill.
	 */
	public static int getExperienceBetweenLevels(final int index, final int endLvl) {
		if (!isSkill(index)) {
			return -1;
		}
		final int lvl = getAbsoluteLevel(index);
		if (index == Skills.DUNGEONEERING && (lvl == 120 || endLvl > 120)) {
			return 0;
		} else if (lvl == 99 || endLvl > 99) {
			return 0;
		}
		return Skills.XP_TABLE[endLvl] - getExperience(index);
	}

	/**
	 * Gets the total/overall level.
	 *
	 * @return The total/overall level.
	 */
	public static int getTotal() {
		int total = 0;
		for (int i = 0; i < Skills.SKILL_NAMES.length - 1; i++) {
			total += getAbsoluteLevel(i);
		}
		return total;
	}

	/**
	 * Gets the total/overall experience.
	 *
	 * @return The total/overall experience.
	 */
	public static int getTotalExperience() {
		int total = 0;
		for (int i = 0; i < Skills.SKILL_NAMES.length - 1; i++) {
			total += getExperience(i);
		}
		return total;
	}

	/**
	 * Moves the mouse over a given component in the stats tab.
	 *
	 * @param component The component index.
	 * @return <tt>true</tt> if the mouse was moved over the given component
	 *         index.
	 */
	public static boolean hover(final int component) {
		Game.openTab(Game.Tabs.STATS);
		Task.sleep(Task.random(10, 100));
		return Interfaces.getComponent(Interface.TAB_STATS, component).hover();
	}

	/**
	 * Checks if one of the given skills is boosted.
	 *
	 * @param index The index of the skill.
	 * @return <tt>true</tt> if one the given skills is boosted.
	 */
	public static boolean isBoosted(final int... index) {
		if (!isSkill(index)) {
			return false;
		}
		for (int i : index) {
			int realLevel = getAbsoluteLevel(i);
			if (realLevel > getMaxLevel(i)) {
				switch (i) {
					case Skills.DUNGEONEERING:
						realLevel = 120;
						break;
					default:
						realLevel = 99;
						break;
				}
			}
			if (realLevel == getLevel(i)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if this index is not out of range.
	 *
	 * @param index The index of the skill.
	 * @return <tt>true</tt> if this index is not out of range.
	 */
	private static boolean isSkill(final int... index) {
		for (int i : index) {
			if (i > Skills.SKILL_NAMES.length - 1) {
				return false;
			}
		}
		return true;
	}
}
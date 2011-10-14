package org.rsbot.script.methods.tabs;

import org.rsbot.bot.concurrent.Task;
import org.rsbot.script.methods.Game;
import org.rsbot.script.methods.Game.Tabs;
import org.rsbot.script.methods.Skills;
import org.rsbot.script.methods.ui.Interfaces;
import org.rsbot.script.wrappers.Interface;
import org.rsbot.script.wrappers.InterfaceComponent;

public class SkillTab {
	public static final int SKILL_TAB = 320;
	public static final int[] SKILL_COMPONENTS = {200, 28, 11, 193, 52, 76,
			93, 68, 165, 101, 44, 172, 84, 179, 186, 36, 19, 60, 118, 126, 110,
			142, 134, 150, 158};

	/**
	 * Gets the skill interface
	 *
	 * @return
	 */
	public static Interface getInterface() {
		return Interfaces.get(SKILL_TAB);
	}

	/**
	 * Gets the interface component for a skill
	 *
	 * @param skill
	 * @return the interface component or null
	 */
	public static InterfaceComponent getSkillComponent(int skill) {
		Interface intr = null;
		if (skill >= 0 && skill < SKILL_COMPONENTS.length
				&& (intr = getInterface()) != null && intr.isValid()) {
			return intr.getComponent(SKILL_COMPONENTS[skill]);
		}
		return null;
	}

	/**
	 * Opens the skill guide for said skill
	 *
	 * @param skill
	 * @return opened
	 */
	public static boolean openSkill(int skill) {
		InterfaceComponent comp = getSkillComponent(skill);
		return comp != null && comp.isValid() && comp.click(true);
	}

	/**
	 * Hovers over the skill component
	 *
	 * @param skill
	 * @return hovered
	 */
	public static boolean hoverSkill(int skill) {
		InterfaceComponent comp = getSkillComponent(skill);
		return comp != null && comp.isValid() && comp.hover();
	}

	/**
	 * Opens the skill tab
	 *
	 * @return opened
	 */
	public static boolean openTab() {
		return Game.openTab(Tabs.STATS);
	}

	/**
	 * Checks if the Skill tab is open
	 *
	 * @return is open
	 */
	public static boolean isTabOpen() {
		return Game.getCurrentTab() != null
				&& Game.getCurrentTab().equals(Tabs.STATS);
	}

	/**
	 * Gets the goal exp of a skill
	 *
	 * @param skill
	 * @return the goal, or -1 if there is no goal
	 */
	public static int getGoalExp(int skill) {
		openTab();
		Task.sleep(500);
		if (isTabOpen()) {
			Interface stats = getInterface();
			if (stats != null && stats.isValid()) {
				if (hoverSkill(skill)) {
					Task.sleep(500);
					InterfaceComponent pop = stats.getComponent(202);
					if (pop != null && pop.isValid()) {
						Integer goal = null;
						InterfaceComponent[] children = pop.getComponents();
						for (int i = 0; i < children.length - 1; i++) {
							InterfaceComponent child = children[i];
							InterfaceComponent nChild = children[i + 1];
							if (child != null && child.isValid()
									&& child.getText() != null
									&& nChild != null && nChild.isValid()
									&& nChild.getText() != null) {
								if (child.getText().trim()
										.equalsIgnoreCase("target lvl:")
										&& (goal = getInteger(nChild.getText()
										.trim().replace(",", ""))) != null
										&& goal > Skills
										.getAbsoluteLevel(skill)
										&& goal < Skills.getMaxLevel(skill)) {
									return Skills.XP_TABLE[goal.intValue()];
								} else if (child.getText().equalsIgnoreCase(
										"target xp:")
										&& (goal = getInteger(nChild.getText()
										.trim().replace(",", ""))) != null
										&& goal > Skills.getExperience(skill)) {
									return goal.intValue();
								}
							}
						}
					}
				}
			}
		}
		return -1;
	}

	private static Integer getInteger(String s) {
		try {
			return Integer.valueOf(s);
		} catch (NumberFormatException e) {
			return null;
		}
	}
}

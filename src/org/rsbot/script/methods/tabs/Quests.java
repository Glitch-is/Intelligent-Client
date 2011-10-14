package org.rsbot.script.methods.tabs;

import org.rsbot.script.methods.Game;
import org.rsbot.script.methods.ui.Interfaces;
import org.rsbot.script.wrappers.InterfaceComponent;

/**
 * Quest tab related operations.
 *
 * @author Timer
 */
public class Quests {
	public enum Progress {
		NOT_STARTED, IN_PROGRESS, COMPLETED
	}

	public static final int WIDGET = 190;
	public static final int BUTTON_FILTER = 10;
	public static final int BUTTON_REVERSE = 12;
	public static final int COMPONENT_POINTS = 2;
	public static final int COMPONENT_QUESTS = 18;

	/**
	 * Gets the quest component of the specified quest.
	 *
	 * @param name the name of the quest
	 * @return the specified quest's <code>InterfaceComponent</code>; otherwise <code>null</code>
	 */
	public static InterfaceComponent getComponent(String name) {
		InterfaceComponent[] components = getComponents();
		if (components != null) {
			for (InterfaceComponent component : components) {
				if (component.getText().equalsIgnoreCase(name)) {
					return component;
				}
			}
		}
		return null;
	}

	/**
	 * Gets an array instance of all the individual quest components.
	 *
	 * @return an array instance of <code>InterfaceComponent</code>; otherwise <code>null</code>
	 */
	public static InterfaceComponent[] getComponents() {
		openTab();
		InterfaceComponent c = Interfaces.getComponent(WIDGET, COMPONENT_QUESTS);
		return c != null ? c.getComponents() : null;
	}

	/**
	 * Gets the maximum number of quest points possible.
	 *
	 * @return maximum number of quest points possible
	 */
	public static int getMaxPoints() {
		openTab();
		InterfaceComponent c = Interfaces.getComponent(WIDGET, COMPONENT_POINTS);
		if (c != null) {
			String text = c.getText().replace("Quest Points: ", "");
			try {
				return Integer.parseInt(text.split(" / ")[1]);
			} catch (NumberFormatException ignored) {
			}
		}
		return -1;
	}

	/**
	 * Gets the local player's current number of quest points.
	 *
	 * @return the local player's current number of quest points.
	 */
	public static int getPoints() {
		openTab();
		InterfaceComponent c = Interfaces.getComponent(WIDGET, COMPONENT_POINTS);
		if (c != null) {
			String text = c.getText().replace("Quest Points: ", "");
			try {
				return Integer.parseInt(text.split(" / ")[0]);
			} catch (NumberFormatException ignored) {
			}
		}
		return -1;
	}

	/**
	 * Gets the completion status of the specified quest. Only works if not hidden.
	 *
	 * @param name the name of the quest to look of.
	 * @return the quest's <code>Progress</code>; otherwise <code>null</code>
	 */
	public static Progress getProgress(String name) {
		if (!isFiltered(name)) {
			switch (getComponent(name).getTextColor()) {
				case 16711680:
					return Progress.NOT_STARTED;
				case 16776960:
					return Progress.IN_PROGRESS;
				case 65280:
					return Progress.COMPLETED;
			}
		}
		return null;
	}

	/**
	 * Check if the specified button is selected.
	 *
	 * @param buttonIndex component index of the button to check selection for
	 * @return <tt>true</tt> if the specified button is selected; otherwise <tt>false</tt>
	 */
	public static boolean isButtonSelected(int buttonIndex) {
		openTab();
		InterfaceComponent button = Interfaces.getComponent(WIDGET, buttonIndex);
		return button != null && button.getTextureID() == 699;
	}

	/**
	 * Checks whether the specified quest is finished. Only works if not hidden.
	 *
	 * @param name the name of the quest to look for
	 * @return <tt>true</tt> if the specified quest is completed; otherwise <tt>false</tt>
	 */
	public static boolean isCompleted(String name) {
		return Progress.COMPLETED.equals(getProgress(name));
	}

	/**
	 * Checks whether the specified quest is filtered out.
	 *
	 * @param name the name of the quest
	 * @return <tt>true</tt> if the quest is filtered out; otherwise <tt>false</tt>
	 */
	public static boolean isFiltered(String name) {
		InterfaceComponent c = getComponent(name);
		return c != null && c.getTextColor() == 2236962;
	}

	/**
	 * Opens the quest tab if not already opened.
	 */
	public static void openTab() {
		Game.Tabs tab = Game.Tabs.QUESTS;
		if (Game.getCurrentTab() != tab) {
			Game.openTab(tab);
		}
	}

	/**
	 * Sets the selection state of the specified button.
	 *
	 * @param buttonIndex component index of the button to select/deselect
	 * @param select      <tt>true</tt> to select; otherwise <tt>fase</tt>
	 */
	public static void selectButton(int buttonIndex, boolean select) {
		openTab();
		if (select != isButtonSelected(buttonIndex)) {
			InterfaceComponent button = Interfaces.getComponent(WIDGET, buttonIndex);
			if (button != null) {
				button.click(true);
			}
		}
	}
}
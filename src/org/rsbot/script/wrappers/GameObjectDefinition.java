package org.rsbot.script.wrappers;

/**
 * An object definition.
 *
 * @author Timer
 */
public class GameObjectDefinition {
	private final org.rsbot.bot.accessors.RSObjectDef od;

	public GameObjectDefinition(final org.rsbot.bot.accessors.RSObjectDef od) {
		this.od = od;
	}

	public String[] getActions() {
		return od.getActions();
	}

	public int[] getChildIDs() {
		return od.getChildrenIDs();
	}

	public String getName() {
		return od.getName();
	}
}

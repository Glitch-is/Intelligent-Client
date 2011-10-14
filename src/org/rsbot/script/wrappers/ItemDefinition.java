package org.rsbot.script.wrappers;

/**
 * An item definition.
 *
 * @author Timer
 */
public class ItemDefinition {
	private final org.rsbot.bot.accessors.RSItemDef id;

	public ItemDefinition(final org.rsbot.bot.accessors.RSItemDef id) {
		this.id = id;
	}

	public String[] getActions() {
		return id.getActions();
	}

	public String[] getGroundActions() {
		return id.getGroundActions();
	}

	public String getName() {
		return id.getName();
	}

	public boolean isMembers() {
		return id.isMembersObject();
	}
}

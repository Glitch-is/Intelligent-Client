package org.rsbot.bot.accessors;

public interface MenuItemNode extends NodeSub {
	String getAction();

	String getOption();

	int getType();
}

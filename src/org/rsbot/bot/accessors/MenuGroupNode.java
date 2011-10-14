package org.rsbot.bot.accessors;

public interface MenuGroupNode extends NodeSub {
	NodeSubQueue getItems();

	String getOption();

	int size();
}

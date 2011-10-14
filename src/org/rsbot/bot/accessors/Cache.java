package org.rsbot.bot.accessors;

public interface Cache {
	HashTable getTable();

	int getInitialCount();

	int getSpaceLeft();

	NodeSubQueue getList();
}

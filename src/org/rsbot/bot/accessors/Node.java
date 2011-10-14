package org.rsbot.bot.accessors;

public interface Node {
	long getID();

	Node getNext();

	Node getPrevious();
}

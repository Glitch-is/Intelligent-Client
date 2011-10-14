package org.rsbot.script.wrappers.internal;

public class StatusQueue {
	private final org.rsbot.bot.accessors.StatusNodeList nl;
	private org.rsbot.bot.accessors.StatusNode c_node;

	public StatusQueue(final org.rsbot.bot.accessors.StatusNodeList nl) {
		this.nl = nl;
	}

	public org.rsbot.bot.accessors.StatusNode getFirst() {
		final org.rsbot.bot.accessors.StatusNode node = nl.getHead().getNext();

		if (node == nl.getHead()) {
			c_node = null;
			return null;
		}
		c_node = node.getNext();

		return node;
	}

	public org.rsbot.bot.accessors.StatusNode getLast() {
		final org.rsbot.bot.accessors.StatusNode node = nl.getHead().getPrevious();

		if (node == nl.getHead()) {
			c_node = null;
			return null;
		}
		c_node = node.getPrevious();

		return node;
	}

	public org.rsbot.bot.accessors.StatusNode getNext() {
		final org.rsbot.bot.accessors.StatusNode node = c_node;

		if (node == nl.getHead() || node == null) {
			c_node = null;
			return null;
		}
		c_node = node.getNext();

		return node;
	}

	public org.rsbot.bot.accessors.StatusNode getPrevious() {
		final org.rsbot.bot.accessors.StatusNode node = c_node;

		if (node == nl.getHead() || node == null) {
			c_node = null;
			return null;
		}
		c_node = node.getNext();

		return node;
	}

	public int size() {
		int size = 0;
		org.rsbot.bot.accessors.StatusNode node = nl.getHead().getPrevious();

		while (node != nl.getHead()) {
			node = node.getPrevious();
			size++;
		}

		return size;
	}
}

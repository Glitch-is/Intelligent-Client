package org.rsbot.script.wrappers;

import org.rsbot.bot.accessors.NodeDeque;

public class Deque<N> {
	private final NodeDeque nl;
	private org.rsbot.bot.accessors.Node current;

	public Deque(NodeDeque nl) {
		this.nl = nl;
	}

	public int size() {
		int size = 0;
		org.rsbot.bot.accessors.Node node = nl.getTail().getPrevious();

		while (node != nl.getTail()) {
			node = node.getPrevious();
			size++;
		}

		return size;
	}

	public N getHead() {
		org.rsbot.bot.accessors.Node node = nl.getTail().getNext();

		if (node == nl.getTail()) {
			current = null;
			return null;
		}
		current = node.getNext();

		return (N) node;
	}

	public N getTail() {
		org.rsbot.bot.accessors.Node node = nl.getTail().getPrevious();

		if (node == nl.getTail()) {
			current = null;
			return null;
		}
		current = node.getPrevious();

		return (N) node;
	}

	public N getNext() {
		org.rsbot.bot.accessors.Node node = current;

		if (node == nl.getTail()) {
			current = null;
			return null;
		}
		current = node.getNext();

		return (N) node;
	}
}
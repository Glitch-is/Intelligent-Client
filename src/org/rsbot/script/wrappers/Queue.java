package org.rsbot.script.wrappers;

import org.rsbot.bot.accessors.NodeSubQueue;

public class Queue<N extends org.rsbot.bot.accessors.NodeSub> {
	private final NodeSubQueue nl;
	private org.rsbot.bot.accessors.NodeSub current;

	public Queue(NodeSubQueue nl) {
		this.nl = nl;
	}

	public int size() {
		int size = 0;
		org.rsbot.bot.accessors.NodeSub node = nl.getTail().getPrevSub();

		while (node != nl.getTail()) {
			node = node.getPrevSub();
			size++;
		}

		return size;
	}

	public N getHead() {
		org.rsbot.bot.accessors.NodeSub node = nl.getTail().getNextSub();

		if (node == nl.getTail()) {
			current = null;
			return null;
		}
		current = node.getNextSub();

		return (N) node;
	}

	public N getNext() {
		org.rsbot.bot.accessors.NodeSub node = current;

		if (node == nl.getTail()) {
			current = null;
			return null;
		}
		current = node.getNextSub();

		return (N) node;
	}
}

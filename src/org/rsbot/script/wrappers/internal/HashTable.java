package org.rsbot.script.wrappers.internal;

public class HashTable {
	private org.rsbot.bot.accessors.HashTable nc;
	private org.rsbot.bot.accessors.Node current;
	private int c_index = 0;

	public HashTable(org.rsbot.bot.accessors.HashTable hashTable) {
		nc = hashTable;
	}

	public org.rsbot.bot.accessors.Node getFirst() {
		c_index = 0;
		return getNext();
	}

	public org.rsbot.bot.accessors.Node getNext() {
		if (c_index > 0 && nc.getBuckets()[c_index - 1] != current) {
			org.rsbot.bot.accessors.Node node = current;
			current = node.getPrevious();
			return node;
		}
		while (c_index < nc.getBuckets().length) {
			org.rsbot.bot.accessors.Node node = nc.getBuckets()[c_index++].getPrevious();
			if (nc.getBuckets()[c_index - 1] != node) {
				current = node.getPrevious();
				return node;
			}
		}
		return null;
	}
}

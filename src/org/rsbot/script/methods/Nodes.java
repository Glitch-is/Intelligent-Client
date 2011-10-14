package org.rsbot.script.methods;

import org.rsbot.bot.accessors.DefLoader;
import org.rsbot.bot.accessors.HashTable;
import org.rsbot.bot.accessors.Node;

/**
 * For internal use to reference data stored in the engine's Node-based structures.
 */
public class Nodes {
	/**
	 * @param nc The node cache to check
	 * @param id The id of the node
	 * @return A <tt>Node</tt> object corresponding to the ID in the nodecache.
	 */
	public static Node lookup(final HashTable nc, final long id) {
		try {
			if (nc == null || nc.getBuckets() == null || id < 0) {
				return null;
			}

			final Node n = nc.getBuckets()[(int) (id & nc.getBuckets().length - 1)];
			for (Node node = n.getPrevious(); node != n; node = node.getPrevious()) {
				if (node.getID() == id) {
					return node;
				}
			}
		} catch (final Exception ignored) {
		}
		return null;
	}

	/**
	 * @param loader The node's loader.
	 * @param id     The id of the node
	 * @return A <tt>Node</tt> object corresponding to the ID in the loader.
	 */
	public static Node lookup(final DefLoader loader, final long id) {
		if (loader == null || loader.getCache() == null) {
			return null;
		}
		return lookup(loader.getCache().getTable(), id);
	}
}

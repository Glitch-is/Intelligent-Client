package org.rsbot.script.methods;

import org.rsbot.bot.Context;
import org.rsbot.bot.accessors.Client;
import org.rsbot.bot.accessors.Node;
import org.rsbot.bot.accessors.RSNPCNode;
import org.rsbot.script.util.Filter;
import org.rsbot.script.wrappers.NPC;

import java.util.HashSet;
import java.util.Set;

/**
 * Provides access to non-player characters.
 */
public class NPCs {
	/**
	 * A filter that accepts all matches.
	 */
	public static final Filter<NPC> ALL_FILTER = new Filter<NPC>() {
		public boolean accept(final NPC npc) {
			return true;
		}
	};

	/**
	 * Returns an array of all loaded RSNPCs.
	 *
	 * @return An array of the loaded RSNPCs.
	 */
	public static NPC[] getLoaded() {
		return getLoaded(NPCs.ALL_FILTER);
	}

	/**
	 * Returns an array of all loaded RSNPCs that are accepted by the provided
	 * Filter
	 *
	 * @param filter Filters out unwanted matches.
	 * @return An array of the loaded RSNPCs.
	 */
	public static NPC[] getLoaded(final Filter<NPC> filter) {
		final Client client = Context.get().client;
		final int[] indices = client.getRSNPCIndexArray();
		final Set<NPC> npcs = new HashSet<NPC>();
		for (final int index : indices) {
			final Node node = Nodes.lookup(client.getRSNPCNC(), index);
			if (node instanceof RSNPCNode) {
				final NPC npc = new NPC(((RSNPCNode) node).getRSNPC());
				if (npc != null && filter.accept(npc)) {
					npcs.add(npc);
				}
			}
		}
		return npcs.toArray(new NPC[npcs.size()]);
	}

	/**
	 * Returns an array of all loaded RSNPCs with the provided ID(s).
	 *
	 * @param ids Allowed NPC IDs.
	 * @return An array of the loaded RSNPCs matching the provided ID(s).
	 */
	public static NPC[] getLoaded(final int... ids) {
		return getLoaded(new Filter<NPC>() {
			public boolean accept(final NPC npc) {
				if (npc != null) {
					for (final int id : ids) {
						if (npc.getID() == id) {
							return true;
						}
					}
				}
				return false;
			}
		});
	}

	/**
	 * Returns an array of all loaded RSNPCs with the provided name(s).
	 *
	 * @param names Allowed NPC names.
	 * @return An array of the loaded RSNPCs matching the provided name(s).
	 */
	public static NPC[] getLoaded(final String... names) {
		return getLoaded(new Filter<NPC>() {
			public boolean accept(final NPC npc) {
				final String name = npc != null ? npc.getName() : null;
				if (name != null) {
					for (final String n : names) {
						if (n != null && n.equalsIgnoreCase(name)) {
							return true;
						}
					}
				}
				return false;
			}
		});
	}

	/**
	 * Returns the NPC that is nearest out of all of loaded RSNPCs accepted by
	 * the provided Filter.
	 *
	 * @param filter Filters out unwanted matches.
	 * @return An NPC object representing the nearest NPC accepted by the
	 *         provided Filter; or null if there are no matching NPCs in the
	 *         current region.
	 */
	public static NPC getNearest(final Filter<NPC> filter) {
		int min = Integer.MAX_VALUE;
		NPC closest = null;
		final Client client = Context.get().client;
		final int[] indices = client.getRSNPCIndexArray();
		for (final int index : indices) {
			final Node node = Nodes.lookup(client.getRSNPCNC(), index);
			if (node instanceof RSNPCNode) {
				final NPC npc = new NPC(((RSNPCNode) node).getRSNPC());
				final int distance = Calculations.distanceTo(npc);
				if (distance < min) {
					if (filter.accept(npc)) {
						min = distance;
						closest = npc;
					}
				}
			}
		}
		return closest;
	}

	/**
	 * Returns the NPC that is nearest out of all of the RSNPCs with the
	 * provided ID(s). Can return null.
	 *
	 * @param ids Allowed NPC IDs.
	 * @return An NPC object representing the nearest NPC with one of the
	 *         provided IDs; or null if there are no matching NPCs in the
	 *         current region.
	 */
	public static NPC getNearest(final int... ids) {
		return getNearest(new Filter<NPC>() {
			public boolean accept(final NPC npc) {
				if (npc != null) {
					for (final int id : ids) {
						if (npc.getID() == id) {
							return true;
						}
					}
				}
				return false;
			}
		});
	}

	/**
	 * Returns the NPC that is nearest out of all of the RSNPCs with the
	 * provided name(s). Can return null.
	 *
	 * @param names Allowed NPC names.
	 * @return An NPC object representing the nearest NPC with one of the
	 *         provided names; or null if there are no matching NPCs in the
	 *         current region.
	 */
	public static NPC getNearest(final String... names) {
		return getNearest(new Filter<NPC>() {
			public boolean accept(final NPC npc) {
				final String name = npc != null ? npc.getName() : null;
				if (name != null) {
					for (final String n : names) {
						if (n != null && n.equalsIgnoreCase(name)) {
							return true;
						}
					}
				}
				return false;
			}
		});
	}
}
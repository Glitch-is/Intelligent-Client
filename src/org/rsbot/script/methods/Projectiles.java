package org.rsbot.script.methods;

import org.rsbot.bot.Context;
import org.rsbot.bot.accessors.Node;
import org.rsbot.bot.accessors.ProjectileNode;
import org.rsbot.script.wrappers.Projectile;
import org.rsbot.script.wrappers.internal.Deque;

import java.util.HashSet;

public class Projectiles {
	public static Projectile[] getLoaded() {
		HashSet<Projectile> hashSet = new HashSet<Projectile>();
		final Deque<Node> projectileDeque = new Deque<Node>(Context.get().client.getProjectiles());
		for (Node localNode = projectileDeque.getTail(); localNode != null; localNode = projectileDeque.getNext()) {
			hashSet.add(new Projectile(((ProjectileNode) localNode).getProjectile()));
		}
		return hashSet.toArray(new Projectile[hashSet.size()]);
	}
}

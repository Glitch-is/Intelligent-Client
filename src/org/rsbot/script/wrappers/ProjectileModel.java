package org.rsbot.script.wrappers;

import org.rsbot.bot.accessors.Projectile;

public class ProjectileModel extends GameModel {
	public org.rsbot.bot.accessors.Projectile c;

	ProjectileModel(org.rsbot.bot.accessors.Model paramModel, Projectile paramProjectile) {
		super(paramModel);
		this.c = paramProjectile;
	}

	protected void update() {
	}

	public int getLocalX() {
		return (int) this.c.getLocalX();
	}

	public int getLocalY() {
		return (int) this.c.getLocalY();
	}

	protected int getHeight() {
		return (int) this.c.getLocalZ();
	}
}
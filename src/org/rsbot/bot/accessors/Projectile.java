package org.rsbot.bot.accessors;

public interface Projectile extends RSAnimable {
	Model getModel();

	double getLocalX();

	double getLocalY();

	double getLocalZ();
}

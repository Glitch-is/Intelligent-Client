package org.rsbot.bot.event.impl;

import org.rsbot.bot.event.listener.PaintListener;
import org.rsbot.script.methods.Projectiles;
import org.rsbot.script.wrappers.Projectile;
import org.rsbot.script.wrappers.Tile;

import java.awt.*;

public class DrawProjectiles implements PaintListener {
	public void onRepaint(final Graphics render) {
		final Projectile[] projectiles = Projectiles.getLoaded();
		render.setColor(Color.RED);
		for (final Projectile projectile : projectiles) {
			final Tile location = projectile.getLocation();
			final int height = (int) Math.round(projectile.getHeight() / 2);
			final Point p = location.getCentralPoint(height);
			render.fillRect((int) p.getX() - 1, (int) p.getY() - 1, 2, 2);
			/*final GameModel model = projectile.getModel();
			try {
				model.drawWireFrame(render);
			} catch (final Throwable e) {
				e.printStackTrace();
			}*/
		}
	}
}

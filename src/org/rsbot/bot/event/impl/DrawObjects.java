package org.rsbot.bot.event.impl;

import org.rsbot.bot.event.listener.PaintListener;
import org.rsbot.script.methods.Calculations;
import org.rsbot.script.methods.Game;
import org.rsbot.script.methods.Objects;
import org.rsbot.script.methods.Players;
import org.rsbot.script.wrappers.GameObject;
import org.rsbot.script.wrappers.Player;
import org.rsbot.script.wrappers.Tile;

import java.awt.*;
import java.util.HashMap;

public class DrawObjects implements PaintListener {
	private static final HashMap<GameObject.Type, Color> color_map = new HashMap<GameObject.Type, Color>();

	static {
		color_map.put(GameObject.Type.BOUNDARY, Color.BLACK);
		color_map.put(GameObject.Type.FLOOR_DECORATION, Color.YELLOW);
		color_map.put(GameObject.Type.INTERACTABLE, Color.WHITE);
		color_map.put(GameObject.Type.WALL_DECORATION, Color.GRAY);
	}

	public void onRepaint(final Graphics render) {
		if (!Game.isLoggedIn()) {
			return;
		}
		final Player player = Players.getLocal();
		if (player == null) {
			return;
		}
		final FontMetrics metrics = render.getFontMetrics();
		final Tile location = player.getLocation();
		final int locX = location.getX();
		final int locY = location.getY();
		final int tHeight = metrics.getHeight();
		for (int x = locX - 25; x < locX + 25; x++) {
			for (int y = locY - 25; y < locY + 25; y++) {
				final Tile tile = new Tile(x, y);
				final Point screen = tile.getCentralPoint();
				if (!Calculations.isPointOnScreen(screen)) {
					continue;
				}
				final GameObject[] objects = Objects.getLoadedAt(tile);
				int i = 0;
				for (final GameObject object : objects) {
					final Point real = object.getLocation().getCentralPoint();
					if (!Calculations.isPointOnScreen(real)) {
						continue;
					}
					if (screen.x > -1) {
						render.setColor(Color.GREEN);
						render.fillRect(screen.x - 1, screen.y - 1, 2, 2);
						render.setColor(Color.RED);
						render.drawLine(screen.x, screen.y, real.x, real.y);
					}
					final String s = "" + object.getID();
					final int ty = real.y - tHeight / 2 - i++ * 15;
					final int tx = real.x - metrics.stringWidth(s) / 2;
					render.setColor(color_map.get(object.getType()));
					render.drawString(s, tx, ty);
				}
			}
		}
	}
}
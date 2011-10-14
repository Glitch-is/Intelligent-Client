package org.rsbot.bot.event.impl;

import org.rsbot.bot.event.listener.PaintListener;
import org.rsbot.script.methods.GroundItems;
import org.rsbot.script.methods.NPCs;
import org.rsbot.script.methods.Objects;
import org.rsbot.script.methods.Players;
import org.rsbot.script.wrappers.GameModel;
import org.rsbot.script.wrappers.GameObject;
import org.rsbot.script.wrappers.GroundItem;

import java.awt.*;
import java.util.HashMap;

/**
 * @author Timer
 */
public class DrawModels implements PaintListener {
	private static final HashMap<GameObject.Type, Color> color_map = new HashMap<GameObject.Type, Color>();

	static {
		color_map.put(GameObject.Type.BOUNDARY, Color.BLACK);
		color_map.put(GameObject.Type.FLOOR_DECORATION, Color.YELLOW);
		color_map.put(GameObject.Type.INTERACTABLE, Color.WHITE);
		color_map.put(GameObject.Type.WALL_DECORATION, Color.GRAY);
	}

	public void onRepaint(final Graphics render) {
		for (final org.rsbot.script.wrappers.GameObject o : Objects.getLoaded()) {
			final GameModel model = o.getModel();
			if (model != null && o.isOnScreen()) {
				render.setColor(color_map.get(o.getType()));
				model.draw(render);
				render.setColor(Color.green);
				final Point p = model.getCentralPoint();
				render.fillOval(p.x - 3, p.y - 3, 6, 6);
				render.setColor(Color.pink);
				final Point nP = model.getNextViewportPoint();
				render.fillOval(nP.x - 2, nP.y - 2, 4, 4);
			}
		}
		for (final org.rsbot.script.wrappers.Character c : Players.getLoaded()) {
			final GameModel model = c.getModel();
			if (model != null && c.isOnScreen()) {
				render.setColor(Color.red);
				model.draw(render);
				render.setColor(Color.green);
				final Point p = model.getCentralPoint();
				render.fillOval(p.x - 3, p.y - 3, 6, 6);
				render.setColor(Color.pink);
				final Point nP = model.getNextViewportPoint();
				render.fillOval(nP.x - 2, nP.y - 2, 4, 4);
			}
		}
		for (final org.rsbot.script.wrappers.Character c : NPCs.getLoaded()) {
			final GameModel model = c.getModel();
			if (model != null && c.isOnScreen()) {
				render.setColor(Color.magenta);
				model.draw(render);
				render.setColor(Color.green);
				final Point p = model.getCentralPoint();
				render.fillOval(p.x - 3, p.y - 3, 6, 6);
				render.setColor(Color.pink);
				final Point nP = model.getNextViewportPoint();
				render.fillOval(nP.x - 2, nP.y - 2, 4, 4);
			}
		}
		for (final GroundItem item : GroundItems.getLoaded()) {
			final GameModel model = item.getModel();
			if (model != null && item.isOnScreen()) {
				render.setColor(Color.cyan);
				model.draw(render);
				render.setColor(Color.green);
				final Point p = model.getCentralPoint();
				render.fillOval(p.x - 3, p.y - 3, 6, 6);
				render.setColor(Color.pink);
				final Point nP = model.getNextViewportPoint();
				render.fillOval(nP.x - 2, nP.y - 2, 4, 4);
			}
		}
	}
}
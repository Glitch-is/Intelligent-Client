package org.rsbot.bot.event.impl;

import org.rsbot.bot.Context;
import org.rsbot.bot.accessors.Client;
import org.rsbot.bot.accessors.Node;
import org.rsbot.bot.accessors.RSNPCNode;
import org.rsbot.bot.event.listener.PaintListener;
import org.rsbot.script.methods.Calculations;
import org.rsbot.script.methods.Game;
import org.rsbot.script.methods.Nodes;
import org.rsbot.script.wrappers.NPC;

import java.awt.*;

public class DrawNPCs implements PaintListener {
	public void onRepaint(final Graphics render) {
		if (!Game.isLoggedIn()) {
			return;
		}
		final Client client = Context.get().client;
		final FontMetrics metrics = render.getFontMetrics();
		for (final int element : client.getRSNPCIndexArray()) {
			final Node node = Nodes.lookup(client.getRSNPCNC(), element);
			if (node == null || !(node instanceof RSNPCNode)) {
				continue;
			}
			final NPC npc = new NPC(((RSNPCNode) node).getRSNPC());
			final Point location = npc.getCentralPoint();
			if (!Calculations.isPointOnScreen(location)) {
				continue;
			}
			render.setColor(Color.RED);
			render.fillRect((int) location.getX() - 1, (int) location.getY() - 1, 2, 2);
			String s = npc.getID() + (npc.getLevel() > 0 ? " (" + npc.getLevel() + ")" : "");
			render.setColor(npc.isInCombat() ? Color.red : npc.isMoving() ? Color.green : Color.WHITE);
			render.drawString(s, location.x - metrics.stringWidth(s) / 2, location.y - metrics.getHeight() / 2);
			if (npc.getAnimation() != -1 || npc.getGraphic() > 0 || npc.getPrayerIconIndex() != -1) {
				s = "(";
				if (npc.getPrayerIconIndex() != -1) {
					s += "P: " + npc.getPrayerIconIndex() + " | ";
				}
				if (npc.getAnimation() != -1 || npc.getGraphic() > 0) {
					s += "A: " + npc.getAnimation() + " | S: " + npc.getStance() + " | G: " + npc.getGraphic() + " | ";
				}
				s = s.substring(0, s.lastIndexOf(" | "));
				s += ")";
				render.drawString(s, location.x - metrics.stringWidth(s) / 2, location.y - metrics.getHeight() * 3 / 2);
			}
		}
	}
}
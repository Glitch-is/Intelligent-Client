package org.rsbot.bot.event.impl;

import org.rsbot.bot.Context;
import org.rsbot.bot.accessors.Client;
import org.rsbot.bot.event.listener.PaintListener;
import org.rsbot.script.methods.Calculations;
import org.rsbot.script.methods.Game;
import org.rsbot.script.wrappers.Player;

import java.awt.*;

public class DrawPlayers implements PaintListener {
	public void onRepaint(final Graphics render) {
		if (!Game.isLoggedIn()) {
			return;
		}
		final Client client = Context.get().client;
		final org.rsbot.bot.accessors.RSPlayer[] players = client.getRSPlayerArray();
		if (players == null) {
			return;
		}
		final FontMetrics metrics = render.getFontMetrics();
		for (final org.rsbot.bot.accessors.RSPlayer element : players) {
			if (element == null) {
				continue;
			}
			final Player player = new Player(element);
			final Point location = player.getCentralPoint();
			if (!Calculations.isPointOnScreen(location)) {
				continue;
			}
			render.setColor(Color.RED);
			render.fillRect((int) location.getX() - 1, (int) location.getY() - 1, 2, 2);
			String s = player.getName() + " (" + player.getLevel() + ")";
			render.setColor(player.isInCombat() ? Color.RED : player.isMoving() ? Color.GREEN : Color.WHITE);
			render.drawString(s, location.x - metrics.stringWidth(s) / 2, location.y - metrics.getHeight() / 2);
			final String msg = player.getMessage();
			boolean raised = false;
			if (player.getAnimation() != -1 || player.getGraphic() > 0 || player.getNPCID() != -1 || player.getPrayerIconIndex() != -1 || player.getSkullIconIndex() != -1) {
				s = "";
				s += "(";
				if (player.getNPCID() != -1) {
					s += "NPC: " + player.getNPCID() + " | ";
				}
				if (player.getPrayerIconIndex() != -1) {
					s += "P: " + player.getPrayerIconIndex() + " | ";
				}
				if (player.getSkullIconIndex() != -1) {
					s += "SK: " + player.getSkullIconIndex() + " | ";
				}
				if (player.getAnimation() != -1 || player.getGraphic() > 0 || player.getNPCID() != -1) {
					s += "A: " + player.getAnimation() + " | ST: " + player.getStance() + " | G: " + player.getGraphic() + " | ";
				}

			s=	s.substring(0, s.lastIndexOf(" | "));
				s += ")";

				render.drawString(s, location.x - metrics.stringWidth(s) / 2, location.y - metrics.getHeight() * 3 / 2);
				raised = true;
			}
			if (msg != null) {
				render.setColor(Color.ORANGE);
				render.drawString(msg, location.x - metrics.stringWidth(msg) / 2, location.y - metrics.getHeight() * (raised ? 5 : 3) / 2);
			}
		}
	}
}
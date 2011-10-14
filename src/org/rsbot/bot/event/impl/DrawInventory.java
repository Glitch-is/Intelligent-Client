package org.rsbot.bot.event.impl;

import org.rsbot.bot.event.listener.PaintListener;
import org.rsbot.script.methods.Game;
import org.rsbot.script.methods.tabs.Inventory;
import org.rsbot.script.wrappers.Item;

import java.awt.*;

public class DrawInventory implements PaintListener {
	public void onRepaint(final Graphics render) {
		if (!Game.isLoggedIn()) {
			return;
		}

		if (Game.getCurrentTab() != Game.Tabs.INVENTORY) {
			return;
		}

		render.setColor(Color.WHITE);
		final Item[] inventoryItems = Inventory.getItems();
		for (final Item inventoryItem : inventoryItems) {
			if (inventoryItem.getID() != -1) {
				final Point location = inventoryItem.getComponent().getCentralPoint();
				render.drawString("" + inventoryItem.getID(), location.x, location.y);
			}
		}
	}
}
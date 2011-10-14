package org.rsbot.bot.event.impl;

import org.rsbot.bot.Bot;
import org.rsbot.bot.accessors.Client;
import org.rsbot.bot.accessors.input.Mouse;
import org.rsbot.bot.event.listener.PaintListener;

import java.awt.*;

public class DrawMouse implements PaintListener {
	private final Client client;

	public DrawMouse(Bot bot) {
		client = bot.composite.client;
	}

	public void onRepaint(final Graphics render) {
		Mouse mouse = client.getMouse();
		if (mouse != null) {
			int mouse_x = mouse.getX();
			int mouse_y = mouse.getY();
			int mouse_press_x = mouse.getPressX();
			int mouse_press_y = mouse.getPressY();
			long mouse_press_time = mouse.getPressTime();
			render.setColor(Color.YELLOW.darker());
			render.drawLine(mouse_x - 5, mouse_y - 5, mouse_x + 5, mouse_y + 5);
			render.drawLine(mouse_x + 5, mouse_y - 5, mouse_x - 5, mouse_y + 5);
			if (System.currentTimeMillis() - mouse_press_time < 1000) {
				render.setColor(Color.RED);
				render.drawLine(mouse_press_x - 5, mouse_press_y - 5, mouse_press_x + 5, mouse_press_y + 5);
				render.drawLine(mouse_press_x + 5, mouse_press_y - 5, mouse_press_x - 5, mouse_press_y + 5);
			}
		}
	}
}
package org.rsbot.bot.event.impl;

import org.rsbot.bot.Bot;
import org.rsbot.bot.accessors.Client;
import org.rsbot.bot.accessors.input.Mouse;
import org.rsbot.bot.event.listener.TextPaintListener;
import org.rsbot.util.StringUtil;

import java.awt.*;

public class TMousePosition implements TextPaintListener {
	private final Client client;

	public TMousePosition(final Bot bot) {
		client = bot.composite.client;
	}

	public int drawLine(final Graphics render, int idx) {
		final Mouse mouse = client.getMouse();
		if (mouse != null) {
			final int mouse_x = mouse.getX();
			final int mouse_y = mouse.getY();
			final String off = mouse.isPresent() ? "" : " (off)";
			StringUtil.drawLine(render, idx++, "Mouse Position: (" + mouse_x + "," + mouse_y + ")" + off);
		}
		return idx;
	}
}
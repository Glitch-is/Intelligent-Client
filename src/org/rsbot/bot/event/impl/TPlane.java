package org.rsbot.bot.event.impl;

import org.rsbot.bot.event.listener.TextPaintListener;
import org.rsbot.script.methods.Game;
import org.rsbot.util.StringUtil;

import java.awt.*;

public class TPlane implements TextPaintListener {
	public int drawLine(final Graphics render, int idx) {
		final int floor = Game.getPlane();
		StringUtil.drawLine(render, idx++, "Plane " + floor);
		return idx;
	}
}
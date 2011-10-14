package org.rsbot.bot.event.impl;

import org.rsbot.bot.event.listener.TextPaintListener;
import org.rsbot.script.methods.Players;
import org.rsbot.script.wrappers.Tile;
import org.rsbot.util.StringUtil;

import java.awt.*;

public class TPlayerLocation implements TextPaintListener {
	public int drawLine(final Graphics render, int idx) {
		final Tile position = Players.getLocal().getLocation();
		StringUtil.drawLine(render, idx++, "Position: " + position);
		return idx;
	}
}
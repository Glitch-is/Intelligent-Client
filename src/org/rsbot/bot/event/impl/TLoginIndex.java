package org.rsbot.bot.event.impl;

import org.rsbot.bot.event.listener.TextPaintListener;
import org.rsbot.script.methods.Game;
import org.rsbot.util.StringUtil;

import java.awt.*;

public class TLoginIndex implements TextPaintListener {
	public int drawLine(final Graphics render, int idx) {
		StringUtil.drawLine(render, idx++, "Client State: " + Game.getClientState());
		return idx;
	}
}

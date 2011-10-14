package org.rsbot.bot.event.impl;

import org.rsbot.bot.event.listener.TextPaintListener;
import org.rsbot.script.methods.Game;
import org.rsbot.script.methods.Players;
import org.rsbot.util.StringUtil;

import java.awt.*;

public class TAnimation implements TextPaintListener {
	public int drawLine(final Graphics render, int idx) {
		final int[] animation = new int[2];
		animation[0] = -1;
		animation[1] = -1;
		if (Game.isLoggedIn()) {
			animation[0] = Players.getLocal().getAnimation();
			animation[1] = Players.getLocal().getStance();
		}
		StringUtil.drawLine(render, idx++, "Animator " + animation[0]);
		StringUtil.drawLine(render, idx++, "Passive Animator " + animation[1]);
		return idx;
	}
}
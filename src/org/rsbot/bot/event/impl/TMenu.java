package org.rsbot.bot.event.impl;

import org.rsbot.bot.event.listener.TextPaintListener;
import org.rsbot.util.StringUtil;

import java.awt.*;

public class TMenu implements TextPaintListener {
	public int drawLine(final Graphics render, int idx) {
		StringUtil.drawLine(render, idx++, "Interaction List");
		final String[] menu = org.rsbot.script.methods.Menu.getItems();
		for (final String menuItem : menu) {
			StringUtil.drawLine(render, idx++, menuItem);
		}
		return idx;
	}
}

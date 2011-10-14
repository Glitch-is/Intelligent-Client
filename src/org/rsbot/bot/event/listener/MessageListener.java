package org.rsbot.bot.event.listener;

import org.rsbot.bot.event.MessageEvent;

import java.util.EventListener;

public interface MessageListener extends EventListener {
	public void messageReceived(final MessageEvent e);
}

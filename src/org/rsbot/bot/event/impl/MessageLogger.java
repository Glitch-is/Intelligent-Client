package org.rsbot.bot.event.impl;

import org.rsbot.bot.event.MessageEvent;
import org.rsbot.bot.event.listener.MessageListener;

import java.util.logging.Logger;

public class MessageLogger implements MessageListener {
	private final Logger log = Logger.getLogger("Messages");

	public void messageReceived(final MessageEvent e) {
		if (e.getSender().equals("")) {
			log.info("[" + e.getID() + "] " + e.getMessage());
		} else {
			log.info("[" + e.getID() + "] " + e.getSender() + ": " + e.getMessage());
		}
	}
}
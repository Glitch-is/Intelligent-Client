package org.rsbot.bot.event.listener;

import org.rsbot.bot.event.CharacterMovedEvent;

import java.util.EventListener;

/**
 * @author Qauters
 */
public interface CharacterMovedListener extends EventListener {
	public void characterMoved(CharacterMovedEvent e);
}

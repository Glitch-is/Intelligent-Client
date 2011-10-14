package org.rsbot.bot.event.handler;

import org.rsbot.script.event.GameEvent;

public interface ScriptDispatcher {
	public void submit(final GameEvent event);

	public boolean remove(final GameEvent event);

	public void clear();

	public int getEventDelay();

	public void setEventDelay(final int delay);
}

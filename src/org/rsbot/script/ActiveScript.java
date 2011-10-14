package org.rsbot.script;

import org.rsbot.bot.event.handler.ScriptDispatcher;
import org.rsbot.script.event.Action;
import org.rsbot.script.event.GameEvent;
import org.rsbot.script.event.handler.EventContainer;

/**
 * A script that is re-active to event based operations.
 *
 * @author Timer
 */
public abstract class ActiveScript extends Action implements ScriptDispatcher {
	private EventContainer container = null;

	public void init(final EventContainer container) {
		if (this.container == null) {
			this.container = container;
		}
	}

	public final void submit(final GameEvent event) {
		container.submit(event);
	}

	public final boolean remove(final GameEvent event) {
		return container.remove(event);
	}

	public final void clear() {
		container.clear();
	}

	public final int getEventDelay() {
		return container.getEventDelay();
	}

	public final void setEventDelay(final int delay) {
		container.setEventDelay(delay);
	}
}

package org.rsbot.script.event;

import org.rsbot.bot.Context;
import org.rsbot.bot.event.RSEvent;

import java.util.EventListener;
import java.util.concurrent.atomic.AtomicLong;

public abstract class GameEvent extends RSEvent {
	private long mask = -1;
	private static AtomicLong curMask = new AtomicLong(0x8000);

	public GameEvent() {
	}

	public GameEvent(final Action... actions) {
		for (final Action action : actions) {
			addAction(action);
		}
	}

	public void addAction(final Action action) {
		Context.get().composite.eventManager.addListener(action, getMask());
	}

	public void removeAction(final Action action) {
		Context.get().composite.eventManager.removeListener(action);
	}

	@Override
	public void dispatch(final EventListener el) {
		((Action) el).dispatch();
	}

	@Override
	public long getMask() {
		if (mask == -1) {
			mask = curMask.getAndSet(curMask.get() * 2);
		}
		return mask;
	}

	public abstract boolean call();
}

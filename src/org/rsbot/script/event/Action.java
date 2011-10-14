package org.rsbot.script.event;

import java.security.InvalidParameterException;
import java.util.EventListener;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Action implements EventListener, Runnable {
	public long mask = -1;
	private final int id;
	private static AtomicInteger ind = new AtomicInteger(1);

	public Action(final long mask) {
		this();
		this.mask = mask;
	}

	public Action() {
		id = ind.getAndIncrement();
	}

	public boolean validate() {
		return true;
	}

	public void dispatch() {
		if (validate()) {
			run();
		}
	}

	@Override
	public boolean equals(final Object o) {
		if (o instanceof Action) {
			if (mask == -1) {
				throw new InvalidParameterException("action mask never initialized");
			}
			final Action action = (Action) o;
			return action.id == this.id && action.mask == this.mask;
		}
		return false;
	}
}

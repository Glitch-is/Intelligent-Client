package org.rsbot.bot.event;

import org.rsbot.bot.concurrent.LoopTask;

import java.util.EventListener;
import java.util.EventObject;

public abstract class RSEvent extends EventObject {
	public static enum Type {
		LINEAR, CONCURRENT
	}

	private static final long serialVersionUID = 6977096569226837605L;
	public Type eventType = Type.LINEAR;

	private static final Object SOURCE = new Object();

	public RSEvent() {
		super(RSEvent.SOURCE);
	}

	public abstract void dispatch(final EventListener el);

	public abstract long getMask();

	public LoopTask task(final EventListener el) {
		return new LoopTask() {
			@Override
			protected int loop() {
				dispatch(el);
				return -1;
			}
		};
	}
}

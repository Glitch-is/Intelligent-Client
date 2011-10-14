package org.rsbot.script.event.handler;

import org.rsbot.bot.Bot;
import org.rsbot.bot.concurrent.LoopTask;
import org.rsbot.bot.event.handler.ScriptDispatcher;
import org.rsbot.script.ActiveScript;
import org.rsbot.script.event.GameEvent;

import java.util.ConcurrentModificationException;
import java.util.LinkedList;

/**
 * A class to organise, manage, and dispatch events.
 *
 * @author Timer
 */
public class EventContainer extends LoopTask implements ScriptDispatcher {
	protected final Bot bot;
	private final LinkedList<GameEvent> events = new LinkedList<GameEvent>();
	private int delay = 1000;
	private ActiveScript script = null;

	public EventContainer(final Bot bot) {
		super(bot);
		this.bot = bot;
	}

	public void setScript(final ActiveScript script) {
		if (this.script == null) {
			this.script = script;
			final GameEvent scriptRefresh = new GameEvent() {
				@Override
				public boolean call() {
					return true;
				}
			};
			script.mask = scriptRefresh.getMask();
			scriptRefresh.addAction(script);
		}
	}

	@Override
	protected boolean onRun() {
		return bot != null;
	}

	@Override
	protected int loop() {
		try {
			for (final GameEvent event : events) {
				if (event.call()) {
					container.bot.composite.eventManager.dispatchEvent(event);
				}
			}
		} catch (final ConcurrentModificationException ignored) {
			return 0;
		}
		return delay;
	}

	@Override
	protected void onStop() {
		clear();
		delay = 1000;
		script = null;
	}

	public void submit(final GameEvent event) {
		if (!events.contains(event)) {
			events.offer(event);
		}
	}

	public boolean remove(final GameEvent event) {
		return events.remove(event);
	}

	public void clear() {
		events.clear();
	}

	public int getEventDelay() {
		return delay;
	}

	public void setEventDelay(final int delay) {
		this.delay = delay;
	}
}

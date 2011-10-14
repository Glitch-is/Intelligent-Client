package org.rsbot.bot.event.listener;

import org.rsbot.bot.Bot;
import org.rsbot.bot.concurrent.handler.TaskContainer;

/**
 * @author Timer
 */
public interface ScriptListener {
	public void scriptStarted(final TaskContainer handler);

	public void scriptStopped(final TaskContainer handler);

	public void scriptResumed(final TaskContainer handler);

	public void scriptPaused(final TaskContainer handler);

	public void inputChanged(final Bot bot, final int mask);
}

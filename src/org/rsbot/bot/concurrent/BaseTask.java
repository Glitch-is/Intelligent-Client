package org.rsbot.bot.concurrent;

import java.util.concurrent.Future;

/**
 * An abstract task to perform an action.
 *
 * @author Koskai
 */
public interface BaseTask extends Runnable {
	/**
	 * Checks if the current task is still running.
	 *
	 * @return <tt>true</tt> if done; otherwise <tt>false</tt>.
	 */
	boolean isDone();

	/**
	 * Stops the Task
	 */
	void stop();

	/**
	 * Waits for the task to finish
	 */
	void join();

	/**
	 * Provides the future for this Task
	 *
	 * @param f The future
	 */
	void init(Future<?> f);
}

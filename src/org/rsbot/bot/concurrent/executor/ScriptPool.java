package org.rsbot.bot.concurrent.executor;

import org.rsbot.bot.concurrent.handler.TaskContainer;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A thread factory to maintain scripts within their bot (for context) and security.
 *
 * @author Timer
 */
public class ScriptPool implements ThreadFactory {
	private static final AtomicInteger threadNumber = new AtomicInteger(1);
	private static int PRIORITY = Thread.MIN_PRIORITY;

	public Thread newThread(final Runnable r) {
		Thread thread = new Thread(Thread.currentThread().getThreadGroup(), r, TaskContainer.TASK_GROUP.getName() + "-" + threadNumber.getAndIncrement());
		if (thread.isDaemon()) {
			thread.setDaemon(false);
		}
		if (thread.getPriority() != PRIORITY) {
			thread.setPriority(PRIORITY);
		}
		return thread;
	}
}
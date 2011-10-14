package org.rsbot.bot;

import org.rsbot.bot.accessors.Client;

import java.util.HashMap;
import java.util.Map;

public class Context {
	private static final Map<ThreadGroup, Context> context = new HashMap<ThreadGroup, Context>();
	public final Bot bot;
	public final BotComposite composite;
	public final Client client;

	public Context(final Bot bot) {
		this.bot = bot;
		this.composite = bot.composite;
		this.client = bot.composite.client;
	}

	public static Context get() {
		final Context context = Context.context.get(Thread.currentThread().getThreadGroup());
		if (context == null) {
			throw new RuntimeException("Client does not exist: " + Thread.currentThread() + "@" + Thread.currentThread().getThreadGroup());
		}
		return context;
	}

	/**
	 * Adds a bot to static context by threads.
	 *
	 * @param threadGroup The thread group of the bot.
	 * @param context     The context to add into context.
	 */
	public static void add(final ThreadGroup threadGroup, final Context context) {
		Context.context.put(threadGroup, context);
	}


	/**
	 * Adds a bot to static context by threads.
	 *
	 * @param threadGroup The thread group of the bot.
	 * @param bot         The bot to add into context.
	 */
	public static void put(final ThreadGroup threadGroup, final Bot bot) {
		Context.context.put(threadGroup, new Context(bot));
	}

	/**
	 * Removes a bot from static context-resolution by threads.
	 *
	 * @param threadGroup The thread-group to remove from context.
	 */
	public static void remove(final ThreadGroup threadGroup) {
		context.remove(threadGroup);
	}
}

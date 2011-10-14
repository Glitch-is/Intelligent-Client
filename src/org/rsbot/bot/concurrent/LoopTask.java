package org.rsbot.bot.concurrent;

import org.rsbot.bot.Bot;
import org.rsbot.bot.Context;
import org.rsbot.bot.concurrent.handler.TaskContainer;
import org.rsbot.bot.event.handler.EventManager;
import org.rsbot.bot.event.handler.EventMulticaster;
import org.rsbot.bot.event.listener.PaintListener;

import java.util.EventListener;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A task that is looped until completion.
 *
 * @author Timer
 */
public abstract class LoopTask extends Task implements EventListener {
	protected int id = -1;
	private boolean running = false, paused = false;
	public TaskContainer container;
	public static final Logger log = Logger.getLogger(Task.class.getName());
	private final EventManager eventManager;

	public LoopTask(final Bot bot) {
		this.eventManager = bot.composite.eventManager;
	}

	public LoopTask() {
		this.eventManager = Context.get().composite.eventManager;
	}

	/**
	 * Is executed upon task invoking.
	 *
	 * @return Determines if the task should start or not.
	 */
	protected boolean onRun() {
		return true;
	}

	/**
	 * Is executed upon task completion.
	 */
	protected void onStop() {
	}

	/**
	 * The main loop. Initially invoked if onRun() returns true, then continuously until
	 * a negative integer is returned or the task stopped externally. When this task
	 * is paused this method will not be called until the script is resumed. Avoid causing
	 * execution to pause using sleep() within this method in favor of returning the number
	 * of milliseconds to sleep. This ensures that pausing and anti-randoms perform normally.
	 *
	 * @return The number of milliseconds that the manager should sleep before
	 *         calling it again. Returning a negative number will deactivate the script.
	 */
	protected abstract int loop();

	public void run() {
		boolean start = false;
		running = true;
		try {
			start = onRun();
		} catch (Throwable throwable) {
			log.log(Level.SEVERE, "Unable to start task: ", throwable);
		}
		unblockEvents();
		if (start) {
			paused = false;
			while (running) {
				try {
					if (paused) {
						Task.sleep(1000);
						continue;
					}
					int timeOut = -1;
					try {
						timeOut = loop();
					} catch (final ThreadDeath ignored) {
					} catch (final Exception ex) {
						log.log(Level.WARNING, "Uncaught exception from task: ", ex);
					}
					if (timeOut < 0) {
						break;
					}
					try {
						Task.sleep(timeOut);
					} catch (final ThreadDeath ignored) {
						break;
					}
				} catch (Throwable ignored) {
				}
			}
		} else {
			log.log(Level.SEVERE, "The script is blocking start.");
		}
		running = false;
		try {
			onStop();
		} catch (final Throwable ignored) {
		}
		blockEvents(false);
		if (getId() == container.get().getId()) {
			Context.get().composite.concurrentDispatch.stop();
		}
	}

	/**
	 * Sets if the task is running or not.
	 *
	 * @param running Running or not.
	 */
	public void setRunning(final boolean running) {
		this.running = running;
	}

	/**
	 * Sets this task paused or not.
	 *
	 * @param paused If the task is paused.
	 */
	public void setPaused(final boolean paused) {
		if (running) {
			this.paused = paused;
			if (paused) {
				blockEvents(true);
			} else {
				unblockEvents();
			}
		}
	}

	/**
	 * Returns whether or not this script has started and not stopped.
	 *
	 * @return <tt>true</tt> if running; otherwise <tt>false</tt>.
	 */
	public final boolean isRunning() {
		return running;
	}

	/**
	 * Returns whether or not this script is paused.
	 *
	 * @return <tt>true</tt> if paused; otherwise <tt>false</tt>.
	 */
	public final boolean isPaused() {
		return paused;
	}

	public void blockEvents(final boolean paint) {
		eventManager.removeListener(LoopTask.this);
		if (paint && LoopTask.this instanceof PaintListener) {
			eventManager.addListener(LoopTask.this, EventMulticaster.PAINT_EVENT);
		}
	}

	public void unblockEvents() {
		eventManager.removeListener(LoopTask.this);
		eventManager.addListener(LoopTask.this);
	}


	/**
	 * Sets the id of this task.
	 *
	 * @param id The id of the task.
	 */
	public void setID(final int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}

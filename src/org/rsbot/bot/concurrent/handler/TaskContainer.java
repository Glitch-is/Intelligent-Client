package org.rsbot.bot.concurrent.handler;

import org.rsbot.bot.Bot;
import org.rsbot.bot.concurrent.LoopTask;
import org.rsbot.bot.concurrent.executor.ScriptPool;
import org.rsbot.bot.event.listener.ScriptListener;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * A flexible container to handle tasks.
 *
 * @author Timer
 */
public class TaskContainer {
	private final Map<Integer, LoopTask> tasks = new HashMap<Integer, LoopTask>();
	public static final ExecutorService service = new ThreadPoolExecutor(Integer.MAX_VALUE, Integer.MAX_VALUE, 0L, TimeUnit.NANOSECONDS,
			new SynchronousQueue<Runnable>(), new ScriptPool(), new ThreadPoolExecutor.AbortPolicy());
	public static final ThreadGroup TASK_GROUP = new ThreadGroup("Container");
	private int parent = -1;
	public final Bot bot;
	private boolean paused = false;
	private final Map<Integer, Boolean> state_memory = new HashMap<Integer, Boolean>();
	private final List<Integer> event_memory = new ArrayList<Integer>();
	private final RandomHandler randomHandler;
	private Set<ScriptListener> listeners = Collections.synchronizedSet(new HashSet<ScriptListener>());

	public TaskContainer(final Bot bot) {
		this.bot = bot;
		randomHandler = new RandomHandler(this);
	}

	public void addScriptListener(final ScriptListener l) {
		listeners.add(l);
	}

	public void removeScriptListener(final ScriptListener l) {
		listeners.remove(l);
	}

	public void init(final LoopTask parent) {
		if (this.parent != -1) {
			clean();
		}
		this.parent = pool(parent);
	}

	public void submit(final LoopTask t) {
		invoke(pool(t));
	}

	protected void clean() {
		parent = -1;
		for (final Map.Entry<Integer, LoopTask> task : tasks.entrySet()) {
			if (task.getValue().isRunning()) {
				task.getValue().setRunning(false);
			}
		}
		tasks.clear();
	}

	/**
	 * Adds a new task to the containers' pool.
	 *
	 * @param loopTask The task to add.
	 * @return The ID if the new task.
	 */
	public int pool(final LoopTask loopTask) {
		for (int off = 0; off < tasks.size(); ++off) {
			if (!tasks.containsKey(off)) {
				tasks.put(off, loopTask);
				loopTask.setID(off);
				return off;
			}
		}
		loopTask.container = this;
		loopTask.setID(tasks.size());
		tasks.put(tasks.size(), loopTask);
		return tasks.size() - 1;
	}

	/**
	 * Dispatches the current task onto the executor service for future concurrency.
	 *
	 * @param loopTaskID The task id to start.
	 * @return <tt>true</tt> if started.
	 */
	private boolean start(final int loopTaskID) {
		final LoopTask task = tasks.get(loopTaskID);
		if (task != null && (loopTaskID != parent || !task.isRunning())) {
			if (loopTaskID == parent) {
				task.log("Script started.");
			}
			task.setRunning(true);
			task.init(service.submit(task, task));
			return true;
		}
		return false;
	}

	/**
	 * Invokes the start of a task.
	 *
	 * @param loopTaskID The task.
	 * @return <tt>true</tt> if the task was started; otherwise false.
	 */
	public boolean invoke(final int loopTaskID) {
		return start(loopTaskID);
	}

	/**
	 * Attempts to invoke the creation of the parent on the executor.
	 */
	public void invoke() {
		start(parent);
		for (final ScriptListener l : listeners) {
			l.scriptStarted(this);
		}
		final Thread randomThread = new Thread(getThreadGroup(), randomHandler);
		randomThread.setPriority(Thread.MIN_PRIORITY);
		randomThread.start();
	}

	/**
	 * Stops a desired task.  Remains in the pool.
	 *
	 * @param loopTaskID The task to stop.
	 */
	public void stop(final int loopTaskID) {
		final LoopTask task = tasks.get(loopTaskID);
		if (task != null) {
			if (task.getId() == parent) {
				stop();
				return;
			}
			task.setRunning(false);
		}
	}

	/**
	 * Stops all tasks; remains in the pool.
	 */
	public void stop() {
		for (final LoopTask task : tasks.values()) {
			task.setRunning(false);
			if (task.getId() == parent) {
				task.log("Script stopped.");
			}
		}
		for (final ScriptListener l : listeners) {
			l.scriptStopped(this);
		}
	}

	/**
	 * Pauses all tasks.
	 */
	public void pause() {
		pause(true);
	}

	/**
	 * Pauses all tasks.
	 *
	 * @param events Leave events added.
	 */
	public void pause(final boolean events) {
		if (paused) {
			return;
		}
		state_memory.clear();
		event_memory.clear();
		paused = true;
		for (final LoopTask task : tasks.values()) {
			state_memory.put(task.getId(), task.isPaused());
			task.setPaused(true);
			if (!events) {
				event_memory.add(task.getId());
				task.blockEvents(false);
			}
		}
		for (final ScriptListener l : listeners) {
			l.scriptPaused(this);
		}
	}

	/**
	 * Resumes all tasks.
	 */
	public void resume() {
		if (!paused) {
			return;
		}
		paused = false;
		for (final LoopTask task : tasks.values()) {
			final boolean paused = state_memory.containsKey(task.getId()) ? state_memory.get(task.getId()) : false;
			task.setPaused(paused);
			if (!paused) {
				if (event_memory.contains(task.getId())) {
					task.unblockEvents();
				}
			}
		}
		for (final ScriptListener l : listeners) {
			l.scriptResumed(this);
		}
	}

	/**
	 * Removes a task from the pool.
	 *
	 * @param loopTaskID The id of the task.
	 */
	public void remove(final int loopTaskID) {
		final LoopTask task = tasks.get(loopTaskID);
		if (task != null && task.isRunning()) {
			task.setRunning(false);
		}
		tasks.remove(loopTaskID);
	}

	/**
	 * Gets all the tasks in the pool.
	 *
	 * @return All the pooled executor tasks.
	 */
	public Map<Integer, LoopTask> getTasks() {
		return Collections.unmodifiableMap(tasks);
	}

	/**
	 * Returns if the parent task is running or not.
	 *
	 * @return <tt>true</tt> if running; otherwise <tt>false</tt>.
	 */
	public boolean isRunning() {
		final LoopTask task = tasks.get(parent);
		return task != null && task.isRunning();
	}

	/**
	 * Returns if the parent task is paused or not.
	 *
	 * @return <tt>true</tt> if paused; otherwise <tt>false</tt>.
	 */
	public boolean isPaused() {
		final LoopTask task = tasks.get(parent);
		return task != null && task.isPaused();
	}

	public LoopTask get() {
		return parent != -1 ? tasks.get(parent) : null;
	}

	public LoopTask get(final int i) {
		return tasks.get(i);
	}

	public ThreadGroup getThreadGroup() {
		return bot.getThreadGroup();
	}

	public void updateInput(final Bot bot, final int mask) {
		for (final ScriptListener l : listeners) {
			l.inputChanged(bot, mask);
		}
	}
}

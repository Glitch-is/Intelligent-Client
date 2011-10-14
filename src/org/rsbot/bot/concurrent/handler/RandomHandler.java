package org.rsbot.bot.concurrent.handler;

import org.rsbot.bot.Context;
import org.rsbot.bot.concurrent.LoopTask;
import org.rsbot.bot.concurrent.Task;
import org.rsbot.bot.event.listener.PaintListener;
import org.rsbot.script.Detector;
import org.rsbot.script.methods.Game;
import org.rsbot.script.methods.input.Mouse;
import org.rsbot.script.randoms.*;

import java.awt.*;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 * Handles random events.
 *
 * @author Timer
 */
public class RandomHandler implements Runnable, PaintListener {
	private final TaskContainer taskContainer;
	private int currentRandom = -1;
	private static final int maxTime = Task.random(240, 300);
	private LinkedList<Detector> detectors = new LinkedList<Detector>();
	private static final Logger log = Logger.getLogger(RandomHandler.class.getName());

	public RandomHandler(final TaskContainer taskContainer) {
		this.taskContainer = taskContainer;
	}

	private void init() {
		detectors.clear();
		detectors.addFirst(new Login());
		detectors.addLast(new Frog());
		detectors.addLast(new Maze());
		detectors.addLast(new Mime());
		detectors.addLast(new Pinball());
	}

	private Detector getRandom() {
		for (final Detector detector : detectors) {
			if (detector.isDetected()) {
				return detector;
			}
		}
		return null;
	}

	private void pauseTasks() {
		taskContainer.pause(false);
	}

	private void notifyTasks() {
		taskContainer.resume();
	}

	private void activate(final LoopTask loopTask) {
		Context.get().composite.eventManager.addListener(this);
		pauseTasks();
		currentRandom = taskContainer.pool(loopTask);
		taskContainer.invoke(currentRandom);
		log.info("Random activated: " + loopTask.getClass().getSimpleName());
	}

	private void deactivate() {
		Context.get().composite.eventManager.removeListener(this);
		notifyTasks();
		taskContainer.stop(currentRandom);
		try {
			log.info("Random completed: " + taskContainer.get(currentRandom).getClass().getSimpleName());
		} catch (final NullPointerException ignored) {
		}
		taskContainer.remove(currentRandom);
		currentRandom = -1;
	}

	public final void onRepaint(Graphics g) {
		Point p = Mouse.getLocation();
		int w = Game.getWidth(), h = Game.getHeight();
		g.setColor(new Color(51, 153, 255, 50));
		g.fillRect(0, 0, p.x - 1, p.y - 1);
		g.fillRect(p.x + 1, 0, w - (p.x + 1), p.y - 1);
		g.fillRect(0, p.y + 1, p.x - 1, h - (p.y - 1));
		g.fillRect(p.x + 1, p.y + 1, w - (p.x + 1), h - (p.y - 1));
	}

	public void run() {
		currentRandom = -1;
		long timeOut = -1;
		init();
		while (taskContainer.isRunning()) {
			if (currentRandom != -1) {
				if (timeOut == -1) {
					timeOut = System.currentTimeMillis() + (maxTime * 1000);
				} else if (System.currentTimeMillis() > timeOut) {
					deactivate();
					taskContainer.get().setRunning(false);
					break;
				}
				if (!taskContainer.getTasks().get(currentRandom).isRunning()) {
					deactivate();
					timeOut = -1;
					continue;
				}
				Task.sleep(Task.random(1000, 2500));
			} else {
				final Detector detection = getRandom();
				if (detection != null) {
					activate(detection);
					continue;
				}
				Task.sleep(Task.random(1000, 2500));
			}
		}
		if (currentRandom != -1) {
			deactivate();
		}
	}
}

package org.rsbot.script;

import org.rsbot.bot.concurrent.LoopTask;

/**
 * A random event detector.
 *
 * @author Timer
 */
public abstract class Detector extends LoopTask {
	public abstract boolean isDetected();
}

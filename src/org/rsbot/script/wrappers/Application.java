package org.rsbot.script.wrappers;

/**
 * A conditional interface.
 *
 * @author Timer
 */
public abstract class Application {
	public int timeOut = 5000;

	public abstract boolean apply();
}

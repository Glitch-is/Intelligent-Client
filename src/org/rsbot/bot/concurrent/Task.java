package org.rsbot.bot.concurrent;

import org.rsbot.script.ScriptManifest;

import java.awt.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A task with no providers.
 *
 * @author Timer
 */
public abstract class Task implements BaseTask {
	/**
	 * The logger instance
	 */
	private final Logger log = Logger.getLogger(getClass().getName());
	/**
	 * The instance of {@link java.util.Random} for random number generation.
	 */
	protected static final java.util.Random random = new java.util.Random();

	private Future<?> f;

	/**
	 * {@inheritDoc}
	 */
	public boolean isDone() {
		return f.isDone();
	}

	/**
	 * {@inheritDoc}
	 */
	public void join() {
		try {
			f.get();
		} catch (InterruptedException ignored) {
		} catch (ExecutionException ignored) {
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void init(final Future<?> f) {
		this.f = f;
	}

	/**
	 * {@inheritDoc}
	 */
	public void stop() {
		this.f.cancel(true);
	}

	/**
	 * Pauses execution for a given number of milliseconds.
	 *
	 * @param toSleep The time to sleep in milliseconds.
	 */
	public static void sleep(final int toSleep) {
		try {
			long start = System.currentTimeMillis();
			Thread.sleep(toSleep);

			// Guarantee minimum sleep
			long now;
			while (start + toSleep > (now = System.currentTimeMillis())) {
				Thread.sleep(start + toSleep - now);
			}
		} catch (final InterruptedException ignored) {
		}
	}

	/**
	 * Returns a random integer with min as the inclusive lower bound and max as
	 * the exclusive upper bound.
	 *
	 * @param min The inclusive lower bound.
	 * @param max The exclusive upper bound.
	 * @return Random integer min <= n < max.
	 */
	public static int random(final int min, final int max) {
		final int n = Math.abs(max - min);
		return Math.min(min, max) + (n == 0 ? 0 : random.nextInt(n));
	}

	/**
	 * Returns a normally distributed pseudorandom integer about a mean centered
	 * between min and max with a provided standard deviation.
	 *
	 * @param min The inclusive lower bound.
	 * @param max The exclusive upper bound.
	 * @param sd  The standard deviation. A higher value will increase the
	 *            probability of numbers further from the mean being returned.
	 * @return Random integer min <= n < max from the normal distribution
	 *         described by the parameters.
	 */
	public static int random(final int min, final int max, final int sd) {
		return random(min, max, min + (max - min) / 2, sd);
	}

	/**
	 * Returns a normally distributed pseudorandom integer with a provided
	 * standard deviation about a provided mean.
	 *
	 * @param min  The inclusive lower bound.
	 * @param max  The exclusive upper bound.
	 * @param mean The mean (>= min and < max).
	 * @param sd   The standard deviation. A higher value will increase the
	 *             probability of numbers further from the mean being returned.
	 * @return Random integer min <= n < max from the normal distribution
	 *         described by the parameters.
	 */
	public static int random(final int min, final int max, final int mean, final int sd) {
		if (min == max) {
			return min;
		}
		int rand;
		do {
			rand = (int) (random.nextGaussian() * sd + mean);
		} while (rand < min || rand >= max);
		return rand;
	}

	/**
	 * Returns a random double with min as the inclusive lower bound and max as
	 * the exclusive upper bound.
	 *
	 * @param min The inclusive lower bound.
	 * @param max The exclusive upper bound.
	 * @return Random double min <= n < max.
	 */
	public static double random(final double min, final double max) {
		return Math.min(min, max) + random.nextDouble() * Math.abs(max - min);
	}

	/**
	 * Prints to the RSBot log.
	 *
	 * @param message Object to log.
	 */
	public void log(final Object message) {
		log.info(message.toString());
	}

	/**
	 * Prints to the RSBot log with a font color
	 *
	 * @param color   The color of the font
	 * @param message Object to log
	 */
	public void log(final Color color, final Object message) {
		final Object[] parameters = {color};
		log.log(Level.INFO, message.toString(), parameters);
	}

	@Override
	public String toString() {
		final ScriptManifest scriptManifest = getClass().getAnnotation(ScriptManifest.class);
		return scriptManifest.name() + " by " + scriptManifest.authors()[0];
	}
}
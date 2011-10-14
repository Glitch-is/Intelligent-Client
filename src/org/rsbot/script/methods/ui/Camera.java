package org.rsbot.script.methods.ui;

import org.rsbot.bot.Context;
import org.rsbot.bot.concurrent.Task;
import org.rsbot.bot.input.InputManager;
import org.rsbot.script.methods.Calculations;
import org.rsbot.script.util.Timer;
import org.rsbot.script.wrappers.Character;
import org.rsbot.script.wrappers.GameObject;
import org.rsbot.script.wrappers.Tile;

import java.awt.event.KeyEvent;

/**
 * Camera related operations.
 */
public class Camera {
	/**
	 * Turns to a Character (NPC or Player).
	 *
	 * @param c The Character to turn to.
	 */
	public static void turnTo(final Character c) {
		final int angle = getCharacterAngle(c);
		setAngle(angle);
	}

	/**
	 * Turns to within a few degrees of an Character (NPC or Player).
	 *
	 * @param c   The Character to turn to.
	 * @param dev The maximum difference in the angle.
	 */
	public static void turnTo(final org.rsbot.script.wrappers.Character c, final int dev) {
		int angle = getCharacterAngle(c);
		angle = Task.random(angle - dev, angle + dev + 1);
		setAngle(angle);
	}

	/**
	 * Turns to an GameObject.
	 *
	 * @param o The GameObject to turn to.
	 */
	public static void turnTo(final GameObject o) {
		final int angle = getObjectAngle(o);
		setAngle(angle);
	}

	/**
	 * Turns to within a few degrees of an GameObject.
	 *
	 * @param o   The GameObject to turn to.
	 * @param dev The maximum difference in the turn angle.
	 */
	public static void turnTo(final GameObject o, final int dev) {
		int angle = getObjectAngle(o);
		angle = Task.random(angle - dev, angle + dev + 1);
		setAngle(angle);
	}

	/**
	 * Turns to a specific Tile.
	 *
	 * @param tile Tile to turn to.
	 */
	public static void turnTo(final Tile tile) {
		final int angle = getTileAngle(tile);
		setAngle(angle);
	}

	/**
	 * Turns within a few degrees to a specific Tile.
	 *
	 * @param tile Tile to turn to.
	 * @param dev  Maximum deviation from the angle to the tile.
	 */
	public static void turnTo(final Tile tile, final int dev) {
		int angle = getTileAngle(tile);
		angle = Task.random(angle - dev, angle + dev + 1);
		setAngle(angle);
	}

	/**
	 * Sets the altitude to max or minimum.
	 *
	 * @param up True to go up. False to go down.
	 * @return <tt>true</tt> if the altitude was changed.
	 */
	public static boolean setPitch(final boolean up) {
		if (up) {
			return setPitch(100);
		} else {
			return setPitch(0);
		}
	}

	/**
	 * Set the camera to a certain percentage of the maximum pitch. Don't rely
	 * on the return value too much - it should return whether the camera was
	 * successfully set, but it isn't very accurate near the very extremes of
	 * the height.
	 * <p/>
	 * <p/>
	 * This also depends on the maximum camera angle in a region, as it changes
	 * depending on situation and surroundings. So in some areas, 68% might be
	 * the maximum altitude. This method will do the best it can to switch the
	 * camera altitude to what you want, but if it hits the maximum or stops
	 * moving for any reason, it will return.
	 * <p/>
	 * <p/>
	 * <p/>
	 * Mess around a little to find the altitude percentage you like. In later
	 * versions, there will be easier-to-work-with methods regarding altitude.
	 *
	 * @param percent The percentage of the maximum pitch to set the camera to.
	 * @return true if the camera was successfully moved; otherwise false.
	 */
	public static boolean setPitch(final int percent) {
		final InputManager inputManager = Context.get().composite.inputManager;
		int curAlt = getPitch();
		int lastAlt = 0;
		if (curAlt == percent) {
			return true;
		} else if (curAlt < percent) {
			inputManager.pressKey((char) KeyEvent.VK_UP);
			long start = System.currentTimeMillis();
			while (curAlt < percent && System.currentTimeMillis() - start < Task.random(50, 100)) {
				if (lastAlt != curAlt) {
					start = System.currentTimeMillis();
				}
				lastAlt = curAlt;
				Task.sleep(Task.random(5, 10));
				curAlt = getPitch();
			}
			inputManager.releaseKey((char) KeyEvent.VK_UP);
			return true;
		} else {
			inputManager.pressKey((char) KeyEvent.VK_DOWN);
			long start = System.currentTimeMillis();
			while (curAlt > percent && System.currentTimeMillis() - start < Task.random(50, 100)) {
				if (lastAlt != curAlt) {
					start = System.currentTimeMillis();
				}
				lastAlt = curAlt;
				Task.sleep(Task.random(5, 10));
				curAlt = getPitch();
			}
			inputManager.releaseKey((char) KeyEvent.VK_DOWN);
			return true;
		}
	}

	/**
	 * Moves the camera in a random direction for a given time.
	 *
	 * @param timeOut The maximum time in milliseconds to move the camera for.
	 */
	public static void moveRandomly(final int timeOut) {
		final InputManager inputManager = Context.get().composite.inputManager;
		final Timer timeToHold = new Timer(timeOut);
		final int lowestCamAltPossible = Task.random(75, 100);
		final int vertical = Task.random(0, 20) < 15 ? KeyEvent.VK_UP : KeyEvent.VK_DOWN;
		final int horizontal = Task.random(0, 20) < 5 ? KeyEvent.VK_LEFT : KeyEvent.VK_RIGHT;
		if (Task.random(0, 10) < 8) {
			inputManager.pressKey((char) vertical);
		}
		if (Task.random(0, 10) < 8) {
			inputManager.pressKey((char) horizontal);
		}
		while (timeToHold.isRunning() && Context.get().client.getCamPosZ() >= lowestCamAltPossible) {
			Task.sleep(10);
		}
		inputManager.releaseKey((char) vertical);
		inputManager.releaseKey((char) horizontal);
	}

	/**
	 * Rotates the camera to a specific angle in the closest direction.
	 *
	 * @param degrees The angle to rotate to.
	 */
	public static void setAngle(final int degrees) {
		final InputManager inputManager = Context.get().composite.inputManager;
		if (getAngleTo(degrees) > 5) {
			inputManager.pressKey((char) KeyEvent.VK_LEFT);
			while (getAngleTo(degrees) > 5) {
				Task.sleep(10);
			}
			inputManager.releaseKey((char) KeyEvent.VK_LEFT);
		} else if (getAngleTo(degrees) < -5) {
			inputManager.pressKey((char) KeyEvent.VK_RIGHT);
			while (getAngleTo(degrees) < -5) {
				Task.sleep(10);
			}
			inputManager.releaseKey((char) KeyEvent.VK_RIGHT);
		}
	}

	/**
	 * Rotates the camera to the specified cardinal direction.
	 *
	 * @param direction The char direction to turn the map. char options are w,s,e,n
	 *                  and defaults to north if character is unrecognized.
	 */
	public static void setCompass(final char direction) {
		switch (direction) {
			case 'n':
				setAngle(359);
				break;
			case 'w':
				setAngle(89);
				break;
			case 's':
				setAngle(179);
				break;
			case 'e':
				setAngle(269);
				break;
			default:
				setAngle(359);
				break;
		}
	}

	/**
	 * Uses the compass component to set the camera to face north.
	 */
	public static void setNorth() {
		Interfaces.getComponent(Context.get().composite.getGameGUI().getCompass().getID()).click(true);
	}

	/**
	 * Returns the camera angle at which the camera would be facing a certain
	 * character.
	 *
	 * @param n the Character
	 * @return The angle
	 */
	public static int getCharacterAngle(final Character n) {
		return getTileAngle(n.getLocation());
	}

	/**
	 * Returns the camera angle at which the camera would be facing a certain
	 * object.
	 *
	 * @param o The GameObject
	 * @return The angle
	 */
	public static int getObjectAngle(final GameObject o) {
		return getTileAngle(o.getLocation());
	}

	/**
	 * Returns the camera angle at which the camera would be facing a certain
	 * tile.
	 *
	 * @param t The target tile
	 * @return The angle in degrees
	 */
	public static int getTileAngle(final Tile t) {
		int ang = Calculations.angleToTile(t) - 90;
		if (ang < 0) {
			ang = 360 + ang;
		}
		return ang % 360;
	}

	/**
	 * Returns the angle between the current camera angle and the given angle in
	 * degrees.
	 *
	 * @param degrees The target angle.
	 * @return The angle between the who angles in degrees.
	 */
	public static int getAngleTo(final int degrees) {
		int ca = getAngle();
		if (ca < degrees) {
			ca += 360;
		}
		int da = ca - degrees;
		if (da > 180) {
			da -= 360;
		}
		return da;
	}

	/**
	 * Returns the current compass orientation in degrees, with North at 0,
	 * increasing counter-clockwise to 360.
	 *
	 * @return The current camera angle in degrees.
	 */
	public static int getAngle() {
		// the client uses fixed point radians 0 - 2^14
		// degrees = yaw * 360 / 2^14 = yaw / 45.5111...
		return (int) (Context.get().client.getCameraYaw() / 45.51);
	}

	/**
	 * Returns the current percentage of the maximum pitch of the camera in an
	 * open area.
	 *
	 * @return The current camera altitude percentage.
	 */
	public static int getPitch() {
		return (int) ((Context.get().client.getCameraPitch() - 1024) / 20.48);
	}

	/**
	 * Returns the current x position of the camera.
	 *
	 * @return The x position.
	 */
	public static int getX() {
		return Context.get().client.getCamPosX();
	}

	/**
	 * Returns the current y position of the camera.
	 *
	 * @return The y position.
	 */
	public static int getY() {
		return Context.get().client.getCamPosY();
	}

	/**
	 * Returns the current z position of the camera.
	 *
	 * @return The z position.
	 */
	public static int getZ() {
		return Context.get().client.getCamPosZ();
	}
}

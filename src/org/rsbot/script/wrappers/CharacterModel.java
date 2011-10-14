package org.rsbot.script.wrappers;

import org.rsbot.bot.accessors.Model;
import org.rsbot.script.methods.Calculations;

/**
 * @author Timer
 */
public class CharacterModel extends GameModel {
	public final org.rsbot.bot.accessors.RSCharacter c;

	private final int[] x_base, z_base;

	CharacterModel(final Model model, final org.rsbot.bot.accessors.RSCharacter c) {
		super(model);
		this.c = c;
		x_base = xPoints;
		z_base = zPoints;
		xPoints = new int[numVertices];
		zPoints = new int[numVertices];
	}

	/**
	 * Performs a y rotation camera transform, where
	 * the character's orientation is the rotation around
	 * the y axis in fixed point radians.
	 * <p/>
	 * [cos(t), 0, sin(t)
	 * 0, 1, 0
	 * -sin(t), 0, cos(t)]
	 */
	@Override
	protected void update() {
		final int theta = c.getOrientation() & 0x3fff;
		final int sin = Calculations.SIN_TABLE[theta];
		final int cos = Calculations.COS_TABLE[theta];
		for (int i = 0; i < numVertices; ++i) {
			// Note that the second row of the matrix would result
			// in no change, as the y coordinates are always unchanged
			// by rotation about the y axis.
			xPoints[i] = x_base[i] * cos + z_base[i] * sin >> 15;
			zPoints[i] = z_base[i] * cos - x_base[i] * sin >> 15;
		}
	}

	@Override
	public int getLocalX() {
		return c.getX();
	}

	@Override
	public int getLocalY() {
		return c.getY();
	}
}

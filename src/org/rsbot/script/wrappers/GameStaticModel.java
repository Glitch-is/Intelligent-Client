package org.rsbot.script.wrappers;

import org.rsbot.bot.accessors.LDModel;

/**
 * @author Timer
 */
public class GameStaticModel extends GameModel {
	public final int x, y;

	GameStaticModel(final LDModel model, final int x, final int y) {
		super(model);
		this.x = x;
		this.y = y;
	}

	@Override
	protected void update() {
	}

	@Override
	public int getLocalX() {
		return x;
	}

	@Override
	public int getLocalY() {
		return y;
	}
}

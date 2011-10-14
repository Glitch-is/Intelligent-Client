package org.rsbot.script.wrappers;

import org.rsbot.bot.accessors.Model;
import org.rsbot.bot.accessors.RSAnimable;

/**
 * @author Timer
 */
public class AnimableModel extends GameModel {
	private final RSAnimable animable;

	AnimableModel(final Model model, final RSAnimable animable) {
		super(model);
		this.animable = animable;
	}

	@Override
	protected void update() {
	}

	@Override
	public int getLocalX() {
		return animable.getX();
	}

	@Override
	public int getLocalY() {
		return animable.getY();
	}
}

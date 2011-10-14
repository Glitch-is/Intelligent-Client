package org.rsbot.script.wrappers;

import org.rsbot.bot.accessors.Model;
import org.rsbot.bot.accessors.RSObject;

/**
 * @author Timer
 */
public class GameObjectModel extends GameModel {
	public final RSObject object;

	GameObjectModel(final Model model, final RSObject object) {
		super(model);
		this.object = object;
	}

	@Override
	protected void update() {
	}

	@Override
	public int getLocalX() {
		return object.getX();
	}

	@Override
	public int getLocalY() {
		return object.getY();
	}
}

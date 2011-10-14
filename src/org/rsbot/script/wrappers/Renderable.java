package org.rsbot.script.wrappers;

import java.awt.*;

public interface Renderable {
	/**
	 * Draws the entity in detail.
	 *
	 * @param render The render to paint onto.
	 */
	public void draw(final Graphics render);
}

package org.rsbot.ui.component;

import org.rsbot.Configuration;
import org.rsbot.Loader;
import org.rsbot.ui.Chrome;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * @author Timer
 */
public class BotHome {
	private static final Font FONT = new Font("Helvetica", 1, 13);
	private int width;
	private int height;

	public void setSize(final int width, final int height) {
		this.width = width;
		this.height = height;
	}

	public void paint(final Graphics render) {
		render.setColor(Color.black);
		render.fillRect(0, 0, width, height);
		if (Loader.loading) {
			final Image icon = Configuration.getImage(Configuration.Paths.Resources.ICON);
			final int w = icon.getWidth(null), h = icon.getHeight(null);
			final int paintXPos = width / 2 - w / 2;
			final int paintYPos = height / 2 - h / 2;
			render.drawImage(icon, paintXPos, paintYPos, null);
			render.setColor(Color.white);
			final Loader.Objective objective = Loader.currentObjective;
			String action = "Loading";
			if (objective != null) {
				final String oAction = objective.getAction();
				if (oAction != null) {
					action = oAction;
				}
			}
			action += " ...";
			final Rectangle2D rectangle2D = render.getFontMetrics().getStringBounds(action, render);
			final int textXPos = width / 2 - (int) rectangle2D.getMaxX() / 2;
			render.drawString(action, textXPos, paintYPos + h + 15);
			return;
		}
		int len = Math.min(Chrome.bots.size(), 6);
		if (len == 1) {
			draw(render, 0, 0, 0, width, height);
		} else if (len == 2) {
			draw(render, 0, 0, 0, width, height / 2);
			draw(render, 1, 0, height / 2, width, height / 2);
		} else if (len == 3) {
			draw(render, 0, 0, 0, width / 2, height / 2);
			draw(render, 1, width / 2, 0, width / 2, height / 2);
			draw(render, 2, 0, height / 2, width, height / 2);
		} else if (len == 4) {
			draw(render, 0, 0, 0, width / 2, height / 2);
			draw(render, 1, width / 2, 0, width / 2, height / 2);
			draw(render, 2, 0, height / 2, width / 2, height / 2);
			draw(render, 3, width / 2, height / 2, width / 2, height / 2);
		} else if (len == 5) {
			draw(render, 0, 0, 0, width / 3, height / 2);
			draw(render, 1, width / 3, 0, width / 3, height / 2);
			draw(render, 2, (width * 2) / 3, 0, width / 3, height / 2);
			draw(render, 3, 0, height / 2, width / 2, height / 2);
			draw(render, 4, width / 2, height / 2, width / 2, height / 2);
		} else if (len == 6) {
			draw(render, 0, 0, 0, width / 3, height / 2);
			draw(render, 1, width / 3, 0, width / 3, height / 2);
			draw(render, 2, (width * 2) / 3, 0, width / 3, height / 2);
			draw(render, 3, 0, height / 2, width / 3, height / 2);
			draw(render, 4, width / 3, height / 2, width / 3, height / 2);
			draw(render, 5, (width * 2) / 3, height / 2, width / 3, height / 2);
		} else {
			return;
		}
		FontMetrics metrics = render.getFontMetrics(FONT);
		render.setColor(new Color(0, 0, 0, 170));
		render.fillRect(0, height - 30, width, 30);
		render.setColor(Color.white);
		render.drawString("Spectating " + (Chrome.bots.size() == 1 ? "1 bot." : Chrome.bots.size() + " bots."), 5, height + metrics.getDescent() - 14);
	}

	public void draw(final Graphics g, final int idx, final int x, final int y, final int width, final int height) {
		final BufferedImage img = Chrome.bots.get(idx).getImage();
		if (img != null && img.getWidth() > 0) {
			final int w_img = img.getWidth(), h_img = img.getHeight();
			final float img_ratio = (float) w_img / (float) h_img;
			final float bound_ratio = (float) width / (float) height;
			int w, h;
			if (img_ratio < bound_ratio) {
				h = height;
				w = (int) (((float) w_img / (float) h_img) * h);
			} else {
				w = width;
				h = (int) (((float) h_img / (float) w_img) * w);
			}
			g.drawImage(img.getScaledInstance(w, h, Image.SCALE_FAST), x + width / 2 - w / 2, y + height / 2 - h / 2, null);
			g.setColor(Color.gray);
			g.drawRect(x, y, width - 1, height - 1);
		}
	}
}

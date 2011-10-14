package org.rsbot.ui.component;

import org.rsbot.bot.Bot;
import org.rsbot.bot.event.handler.EventManager;
import org.rsbot.ui.Chrome;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import static org.rsbot.script.methods.Environment.INPUT_KEYBOARD;
import static org.rsbot.script.methods.Environment.INPUT_MOUSE;

/**
 * @author Timer
 */
public class BotPanel extends JPanel {
	private class HomeUpdater implements Runnable {
		private boolean running;

		public void run() {
			synchronized (this) {
				if (running) {
					throw new IllegalStateException("Already running!");
				}
				running = true;
			}
			while (true) {
				synchronized (this) {
					if (!running) {
						break;
					}
				}
				repaint();
				try {
					Thread.sleep(70);
				} catch (Exception ex) {
					break;
				}
			}
			synchronized (this) {
				running = false;
			}
		}

		public void stop() {
			synchronized (this) {
				running = false;
			}
		}
	}

	private Bot bot;
	private BotHome home;
	private HomeUpdater updater;
	private int offX;
	private boolean present;

	public BotPanel(BotHome home) {
		this.home = home;
		this.updater = new HomeUpdater();
		setSize(new Dimension(Chrome.PANEL_WIDTH, Chrome.PANEL_HEIGHT));
		setMinimumSize(new Dimension(Chrome.PANEL_WIDTH, Chrome.PANEL_HEIGHT));
		setPreferredSize(new Dimension(Chrome.PANEL_WIDTH, Chrome.PANEL_HEIGHT));
		setBackground(Color.black);
		home.setSize(getWidth(), getHeight());
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent evt) {
				BotPanel.this.home.setSize(getWidth(), getHeight());
				if (bot != null) {
					bot.resize(getWidth(), getHeight());
					offset();
				}
				requestFocus();
			}
		});
		addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				redispatch(e);
				if (!hasFocus()) {
					requestFocus();
				}
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
				redispatch(e);
			}

			public void mousePressed(MouseEvent e) {
				redispatch(e);
			}

			public void mouseReleased(MouseEvent e) {
				redispatch(e);
			}
		});
		addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
				redispatch(e);
			}

			public void mouseMoved(MouseEvent e) {
				redispatch(e);
			}
		});
		addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				redispatch(e);
			}

		});
		addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				redispatch(e);
			}

			public void keyReleased(KeyEvent e) {
				redispatch(e);
			}

			public void keyTyped(KeyEvent e) {
				redispatch(e);
			}
		});
	}

	public void offset() {
		if (bot.getCanvas() != null) {
			// center canvas horizontally if not filling container
			offX = (getWidth() - bot.getCanvas().getWidth()) / 2;
		}
	}

	public void setBot(Bot bot) {
		if (this.bot != null) {
			this.bot.setPanel(null);
		} else {
			updater.stop();
		}
		this.bot = bot;
		if (bot != null) {
			bot.setPanel(this);
			if (bot.getCanvas() != null) {
				offset();
			}
		} else {
			new Thread(updater).start();
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (bot == null) {
			home.paint(g);
		} else {
			g.drawImage(bot.getImage(), offX, 0, null);
		}
	}

	private void redispatch(final MouseEvent e) {
		if (bot != null && bot.getLoader().getComponentCount() > 0) {
			if (bot.composite.client.getMouse() == null) {
				return;
			}
			boolean present = bot.composite.client.getMouse().isPresent();
			Component c = bot.getLoader().getComponent(0);
			// account for horizontal offset
			e.translatePoint(-offX, 0);
			// fire human mouse event for scripts
			dispatchHuman(c, e);
			if (!bot.composite.overrideInput && (bot.composite.inputFlags & INPUT_MOUSE) == 0) {
				return;
			}
			if (e.getX() > 0 && e.getX() < c.getWidth() && e.getY() < c.getHeight() && e.getID() != MouseEvent.MOUSE_EXITED) {
				if (present) {
					if (e instanceof MouseWheelEvent) {
						MouseWheelEvent mwe = (MouseWheelEvent) e;
						c.dispatchEvent(new MouseWheelEvent(c, e.getID(), System.currentTimeMillis(), 0, e.getX(), e.getY(), 0, e.isPopupTrigger(), mwe.getScrollType(), mwe.getScrollAmount(), mwe.getWheelRotation()));
					} else {
						c.dispatchEvent(new MouseEvent(c, e.getID(), System.currentTimeMillis(), 0, e.getX(), e.getY(), 0, e.isPopupTrigger(), e.getButton()));
					}
				} else {
					c.dispatchEvent(new MouseEvent(c, MouseEvent.MOUSE_ENTERED, System.currentTimeMillis(), 0, e.getX(), e.getY(), 0, false));
				}
			} else if (present) {
				c.dispatchEvent(new MouseEvent(c, MouseEvent.MOUSE_EXITED, System.currentTimeMillis(), 0, e.getX(), e.getY(), 0, false));
			}
		}
	}

	private void redispatch(KeyEvent e) {
		if (bot != null) {
			final EventManager m = bot.composite.eventManager;
			if (m != null) {
				m.dispatchEvent(e);
			}
			if ((bot.composite.overrideInput || (bot.composite.inputFlags & INPUT_KEYBOARD) != 0) && bot.getLoader().getComponentCount() > 0) {
				Component c = bot.getLoader().getComponent(0);
				c.dispatchEvent(e);
			}
		}
	}

	private void dispatchHuman(Component c, MouseEvent e) {
		if (e.getX() > 0 && e.getX() < c.getWidth() && e.getY() < c.getHeight() && e.getID() != MouseEvent.MOUSE_EXITED) {
			if (present) {
				bot.composite.eventManager.dispatchEvent(e);
			} else {
				present = true;
				bot.composite.eventManager.dispatchEvent(new MouseEvent(c, MouseEvent.MOUSE_ENTERED, System.currentTimeMillis(), 0, e.getX(), e.getY(), 0, false));
			}
		} else if (present) {
			present = false;
			bot.composite.eventManager.dispatchEvent(new MouseEvent(c, MouseEvent.MOUSE_EXITED, System.currentTimeMillis(), 0, e.getX(), e.getY(), 0, false));
		}
	}
}

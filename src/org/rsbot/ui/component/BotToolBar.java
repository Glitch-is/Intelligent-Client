package org.rsbot.ui.component;

import org.rsbot.Configuration;
import org.rsbot.bot.Bot;
import org.rsbot.bot.concurrent.handler.TaskContainer;
import org.rsbot.script.methods.Environment;
import org.rsbot.ui.locale.Messages;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class BotToolBar extends JToolBar {
	private final JButton RUN, PAUSE, RESUME, STOP, INPUT;
	public static final ImageIcon ICON_HOME;
	public static final ImageIcon ICON_BOT;
	public static final Image IMAGE_CLOSE;
	public static final Image IMAGE_CLOSE_OVER;
	private final ActionListener listener;
	private Bot bot;
	private static final int OFFSET = 6;
	private int idx;

	static {
		ICON_HOME = new ImageIcon(Configuration.getImage(Configuration.Paths.Resources.ICON_HOME));
		ICON_BOT = new ImageIcon(Configuration.getImage(Configuration.Paths.Resources.ICON_BOT));
		IMAGE_CLOSE = Configuration.getImage(Configuration.Paths.Resources.ICON_CLOSE);
		IMAGE_CLOSE_OVER = Configuration.getImage(Configuration.Paths.Resources.ICON_CLOSE_OVER);
	}

	public BotToolBar(final ActionListener listener) {
		this.listener = listener;

		/* Initialize buttons */
		RUN = new JButton(Messages.RUN, new ImageIcon(Configuration.getImage(Configuration.Paths.Resources.ICON_PLAY)));
		RUN.addActionListener(listener);
		RUN.setFocusable(false);

		PAUSE = new JButton(Messages.PAUSE, new ImageIcon(Configuration.getImage(Configuration.Paths.Resources.ICON_PAUSE)));
		PAUSE.addActionListener(listener);
		PAUSE.setFocusable(false);

		RESUME = new JButton(Messages.RESUME, new ImageIcon(Configuration.getImage(Configuration.Paths.Resources.ICON_PLAY)));
		RESUME.addActionListener(listener);
		RESUME.setFocusable(false);

		STOP = new JButton(Messages.STOP, new ImageIcon(Configuration.getImage(Configuration.Paths.Resources.ICON_DELETE)));
		STOP.addActionListener(listener);
		STOP.setFocusable(false);

		INPUT = new JButton(Messages.INPUT, new ImageIcon(getInputImage()));
		INPUT.addActionListener(listener);
		INPUT.setFocusable(false);

		/* Create home button */
		final JButton home = new JButton("", ICON_HOME);
		home.setFocusable(false);
		home.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent arg0) {
				setSelection(getComponentIndex(home));
			}
		});

		/* Layout bar */
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		setFloatable(false);

		/* Add components */
		add(home);
		add(new AddButton(listener));
		add(Box.createHorizontalGlue());
		add(RUN);
		add(PAUSE);
		add(RESUME);
		add(STOP);
		add(INPUT);
	}

	public void setBot(final Bot bot) {
		this.bot = bot;
	}

	public void addTab() {
		int idx = getComponentCount() - OFFSET - 1;
		add(new BotButton(Messages.BOT_GUI_TAB_TEXT, ICON_BOT), idx);
		validate();
		setSelection(idx);
	}

	public void removeTab(final int idx) {
		remove(idx);
		revalidate();
		repaint();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setSelection(0);
			}
		});
	}

	private void setSelection(final int idx) {
		updateSelection(true);
		this.idx = idx;
		updateSelection(false);
		listener.actionPerformed(new ActionEvent(this, 0, Messages.TAB));
	}

	private void updateSelection(final boolean enabled) {
		int idx = getCurrentTab();
		if (idx >= 0) {
			getComponent(idx).setEnabled(enabled);
			getComponent(idx).repaint();
		}
	}

	public void setTabLabel(final int idx, final String label) {
		((BotButton) getComponentAtIndex(idx)).setText(label);
	}

	public int getCurrentTab() {
		if (idx > -1 && idx < getComponentCount() - OFFSET) {
			return idx;
		} else {
			return -1;
		}
	}

	public void update() {
		if (bot != null) {
			INPUT.setEnabled(true);
			INPUT.setIcon(new ImageIcon(getInputImage()));
			RUN.setEnabled(true);
			final TaskContainer concurrentDispatch = bot.composite.concurrentDispatch;
			if (concurrentDispatch != null && concurrentDispatch.isRunning()) {
				RUN.setVisible(false);
				STOP.setVisible(true);
				if (concurrentDispatch.isPaused()) {
					PAUSE.setVisible(false);
					RESUME.setVisible(true);
					return;
				}
				PAUSE.setVisible(true);
				RESUME.setVisible(false);
				return;
			}
			RUN.setVisible(true);
			RUN.setEnabled(true);
			STOP.setVisible(false);
			PAUSE.setVisible(false);
			RESUME.setVisible(false);
			return;
		}
		RUN.setVisible(true);
		RUN.setEnabled(false);
		STOP.setVisible(false);
		PAUSE.setVisible(false);
		RESUME.setVisible(false);
		INPUT.setEnabled(false);
	}

	private Image getInputImage() {
		if (bot == null) {
			return Configuration.getImage(Configuration.Paths.Resources.ICON_TICK);
		}
		final boolean override = bot.composite.overrideInput;
		final int state = bot.composite.inputFlags;
		if (override || state == (Environment.INPUT_KEYBOARD | Environment.INPUT_MOUSE)) {
			return Configuration.getImage(Configuration.Paths.Resources.ICON_TICK);
		} else if (state == Environment.INPUT_KEYBOARD) {
			return Configuration.getImage(Configuration.Paths.Resources.ICON_KEYBOARD);
		} else if (state == Environment.INPUT_MOUSE) {
			return Configuration.getImage(Configuration.Paths.Resources.ICON_MOUSE);
		} else {
			return Configuration.getImage(Configuration.Paths.Resources.ICON_DELETE);
		}
	}

	private class BotButton extends JPanel {
		private JLabel nameLabel;
		private boolean hovered;
		private boolean close;

		public BotButton(final String text, final Icon icon) {
			super(new BorderLayout());
			setBorder(new EmptyBorder(3, 6, 2, 3));
			nameLabel = new JLabel(text);
			nameLabel.setIcon(icon);
			nameLabel.setPreferredSize(new Dimension(85, 22));
			nameLabel.setMaximumSize(new Dimension(85, 22));
			add(nameLabel, BorderLayout.WEST);

			setPreferredSize(new Dimension(110, 22));
			setMaximumSize(new Dimension(110, 22));
			setFocusable(false);
			addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					if (hovered && close) {
						int idx = BotToolBar.this.getComponentIndex(BotButton.this);
						listener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, Messages.CLOSE_TAB + "." + idx));
					} else {
						setSelection(getComponentIndex(BotButton.this));
					}
				}

				public void mouseEntered(MouseEvent e) {
					hovered = true;
					repaint();
				}

				public void mouseExited(MouseEvent e) {
					hovered = false;
					repaint();
				}
			});
			addMouseMotionListener(new MouseMotionAdapter() {
				public void mouseMoved(MouseEvent e) {
					close = e.getX() > 95;
					repaint();
				}
			});
		}

		public void setText(final String label) {
			nameLabel.setText(label);
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			if (getComponentIndex(this) == idx) {
				g.setColor(new Color(255, 255, 255, 200));
				g.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 1, 4, 4);
				g.setColor(new Color(180, 180, 180, 200));
				g.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 1, 4, 4);
			} else if (hovered) {
				g.setColor(new Color(255, 255, 255, 150));
				g.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 1, 4, 4);
				g.setColor(new Color(180, 180, 180, 150));
				g.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 1, 4, 4);
			} else {
				g.setColor(new Color(180, 180, 180, 150));
				g.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 1, 4, 4);
			}
			if (hovered && close) {
				g.drawImage(IMAGE_CLOSE_OVER, 90, 3, null);
			} else {
				g.drawImage(IMAGE_CLOSE, 90, 3, null);
			}
		}
	}

	private static class AddButton extends JComponent {
		private static final Image ICON;
		private static final Image ICON_OVER;
		private static final Image ICON_DOWN;
		private boolean hovered = false;
		private boolean pressed = false;

		static {
			ICON = Configuration.getImage(Configuration.Paths.Resources.ICON_ADD);
			ICON_OVER = Configuration.getImage(Configuration.Paths.Resources.ICON_ADD_OVER);
			ICON_DOWN = Configuration.getImage(Configuration.Paths.Resources.ICON_ADD_DOWN);
		}

		public AddButton(final ActionListener listener) {
			setPreferredSize(new Dimension(20, 20));
			setMaximumSize(new Dimension(20, 20));
			setFocusable(false);
			addMouseListener(new MouseAdapter() {
				public void mouseEntered(final MouseEvent e) {
					hovered = true;
					repaint();
				}

				public void mouseExited(final MouseEvent e) {
					hovered = false;
					repaint();
				}

				public void mousePressed(final MouseEvent e) {
					pressed = true;
					repaint();
				}

				public void mouseReleased(final MouseEvent e) {
					pressed = false;
					repaint();
					listener.actionPerformed(new ActionEvent(this, e.getID(), Messages.FILE + "." + Messages.NEW_BOT));
				}
			});
		}

		@Override
		public void paintComponent(final Graphics g) {
			super.paintComponent(g);
			if (pressed) {
				g.drawImage(ICON_DOWN, 2, 2, null);
			} else if (hovered) {
				g.drawImage(ICON_OVER, 2, 2, null);
			} else {
				g.drawImage(ICON, 2, 2, null);
			}
		}
	}
}
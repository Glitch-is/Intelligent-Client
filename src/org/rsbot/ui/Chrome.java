package org.rsbot.ui;

import org.rsbot.Configuration;
import org.rsbot.bot.Bot;
import org.rsbot.bot.concurrent.LoopTask;
import org.rsbot.bot.concurrent.handler.TaskContainer;
import org.rsbot.bot.event.listener.ScriptListener;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.methods.Environment;
import org.rsbot.script.methods.Game;
import org.rsbot.script.util.WindowUtil;
import org.rsbot.ui.component.BotHome;
import org.rsbot.ui.component.BotMenuBar;
import org.rsbot.ui.component.BotPanel;
import org.rsbot.ui.component.BotToolBar;
import org.rsbot.ui.locale.Messages;
import org.rsbot.ui.component.log.TextAreaLogHandler;
import org.rsbot.util.ScreenshotUtil;
import org.rsbot.util.UpdateChecker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * @author Timer
 */
public class Chrome extends JFrame implements ActionListener, ScriptListener {
	public static final int PANEL_WIDTH = 765, PANEL_HEIGHT = 503, LOG_HEIGHT = 120;
	private static final long serialVersionUID = -5411033752001988794L;
	public BotPanel panel;
	private BotToolBar toolBar;
	private BotMenuBar menuBar;
	private JScrollPane textScroll;
	private JLabel textLabel;
	private BotHome home;
	public static final List<Bot> bots = new ArrayList<Bot>();
	private static final Logger log = Logger.getLogger(Configuration.NAME);

	public Chrome() {
		init();
		pack();
		setLocationRelativeTo(getOwner());
		setMinimumSize(getSize());
		setResizable(true);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame.setDefaultLookAndFeelDecorated(true);
				JPopupMenu.setDefaultLightWeightPopupEnabled(false);
				ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
			}
		});
	}

	@Override
	public void setTitle(final String title) {
		if (title != null && title.equals("<init>")) {
			super.setTitle(Configuration.NAME);
			return;
		}
		String t = Configuration.NAME + " v" + Configuration.getVersionFormatted();
		final int v = Configuration.getVersion(), l = UpdateChecker.getLatestVersion();
		if (v > l) {
			t += " beta";
		}
		if (title != null) {
			t = title + " - " + t;
		}
		super.setTitle(t);
	}

	public void actionPerformed(ActionEvent evt) {
		String action = evt.getActionCommand();
		String menu, option;
		int z = action.indexOf('.');
		if (z == -1) {
			menu = action;
			option = "";
		} else {
			menu = action.substring(0, z);
			option = action.substring(z + 1);
		}
		if (menu.equals(Messages.CLOSE_TAB)) {
			int idx = Integer.parseInt(option);
			removeBot(bots.get(idx - 1));
		} else if (menu.equals(Messages.FILE)) {
			final Bot current = getCurrentBot();
			if (option.equals(Messages.NEW_BOT)) {
				addBot();
			} else if (option.equals(Messages.CLOSE_BOT)) {
				removeBot(current);
			} else if (option.equals(Messages.RUN_SCRIPT)) {
				if (current != null) {
					showScriptSelector(current);
				}
			} else if (option.equals(Messages.STOP_SCRIPT)) {
				if (current != null) {
					showStopScript(current);
				}
			} else if (option.equals(Messages.PAUSE_SCRIPT)) {
				if (current != null) {
					pauseScript(current);
				}
			} else if (option.equals(Messages.RESUME_SCRIPT)) {
				if (current != null) {
					resumeScript(current);
				}
			} else if (option.equals(Messages.SAVE_SCREENSHOT)) {
				if (current != null) {
					ScreenshotUtil.saveScreenshot(current, Game.isLoggedIn());
				}
			} else if (option.equals(Messages.EXIT)) {
				System.exit(0);
			}
		} else if (menu.equals(Messages.EDIT)) {
			if (option.equals(Messages.ACCOUNTS)) {
				AccountManager.getInstance().showGUI();
			} else {
				final Bot current = getCurrentBot();
				if (current != null) {
					if (option.equals(Messages.FORCE_INPUT)) {
						current.composite.overrideInput = ((JCheckBoxMenuItem) evt.getSource()).isSelected();
						toolBar.update();
					} else if (option.equals(Messages.LESS_CPU)) {
						current.composite.disableRendering = ((JCheckBoxMenuItem) evt.getSource()).isSelected();
					} else if (option.equals(Messages.DISABLE_ANTI_RANDOMS)) {
						current.composite.disableRandoms = ((JCheckBoxMenuItem) evt.getSource()).isSelected();
					} else if (option.equals(Messages.DISABLE_LOGIN)) {
						current.composite.disableAutoLogin = ((JCheckBoxMenuItem) evt.getSource()).isSelected();
					}
				}
			}
		} else if (menu.equals(Messages.VIEW)) {
			final Bot current = getCurrentBot();
			boolean selected = ((JCheckBoxMenuItem) evt.getSource()).isSelected();
			if (option.equals(Messages.EXPAND_LOG)) {
				setLogState(selected);
			} else if (current != null) {
				final Class<?> el = BotMenuBar.DEBUG_MAP.get(option);
				menuBar.getCheckBox(option).setSelected(selected);
				if (selected) {
					current.addListener(el);
				} else {
					current.removeListener(el);
				}
			}
		} else if (menu.equals(Messages.HELP)) {
			if (option.equals(Messages.SITE)) {
				openURL(Configuration.Paths.URLs.SITE);
			} else if (option.equals(Messages.PROJECT)) {
				openURL(Configuration.Paths.URLs.PROJECT);
			} else if (option.equals(Messages.ABOUT)) {
				JOptionPane.showMessageDialog(this,
						new String[]{"An open source bot developed by the community.",
								"Visit " + Configuration.Paths.URLs.SITE + " for more information."},
						Messages.ABOUT,
						JOptionPane.INFORMATION_MESSAGE);
			}

		} else if (menu.equals(Messages.TAB)) {
			final Bot curr = getCurrentBot();
			menuBar.setBot(curr);
			panel.setBot(curr);
			panel.repaint();
			toolBar.setBot(curr);
			setTitle(curr == null ? null : curr.composite.concurrentDispatch.isRunning() ? curr.composite.account : null);
			toolBar.update();
		} else if (menu.equals(Messages.RUN)) {
			final Bot current = getCurrentBot();
			if (current != null) {
				showScriptSelector(current);
			}
		} else if (menu.equals(Messages.STOP)) {
			final Bot current = getCurrentBot();
			if (current != null) {
				showStopScript(current);
			}
		} else if (menu.equals(Messages.PAUSE)) {
			final Bot current = getCurrentBot();
			if (current != null) {
				pauseScript(current);
			}
		} else if (menu.equals(Messages.RESUME)) {
			final Bot current = getCurrentBot();
			if (current != null) {
				resumeScript(current);
			}
		} else if (menu.equals(Messages.INPUT)) {
			final Bot current = getCurrentBot();
			if (current != null) {
				current.composite.overrideInput = !current.composite.overrideInput;
				menuBar.update();
				toolBar.update();
			}
		}
	}

	public BotPanel getPanel() {
		return panel;
	}

	public Bot getBot(final Object o) {
		final ClassLoader cl = o.getClass().getClassLoader();
		for (final Bot bot : bots) {
			if (cl == bot.getLoader().getClient().getClass().getClassLoader()) {
				panel.offset();
				return bot;
			}
		}
		return null;
	}

	public void addBot() {
		final Bot bot = new Bot();
		bots.add(bot);
		toolBar.addTab();
		bot.composite.concurrentDispatch.addScriptListener(this);
		final Thread startBot = new Thread(new Runnable() {
			public void run() {
				bot.start();
			}
		});
		startBot.setDaemon(true);
		startBot.setName("Bot_initialization");
		startBot.setPriority(Thread.MAX_PRIORITY);
		startBot.start();
	}

	public void removeBot(final Bot bot) {
		try {
			bot.composite.concurrentDispatch.get().setRunning(false);
		} catch (final NullPointerException ignored) {
		}
		bot.composite.concurrentDispatch.removeScriptListener(this);
		int idx = bots.indexOf(bot);
		if (idx >= 0) {
			toolBar.removeTab(idx + 1);
		}
		bots.remove(idx);
		new Thread(new Runnable() {
			public void run() {
				bot.stop();
				System.gc();
			}
		}).start();
	}

	void pauseScript(final Bot bot) {
		bot.composite.concurrentDispatch.pause();
	}

	void resumeScript(final Bot bot) {
		bot.composite.concurrentDispatch.resume();
	}

	private Bot getCurrentBot() {
		int idx = toolBar.getCurrentTab() - 1;
		if (idx >= 0) {
			return bots.get(idx);
		}
		return null;
	}

	private void showScriptSelector(Bot bot) {
		if (AccountManager.getAccountNames().length == 0) {
			JOptionPane.showMessageDialog(this, Messages.BOT_GUI_NO_ACCOUNTS);
			AccountManager.getInstance().showGUI();
		} else if (bot.composite.client == null) {
			JOptionPane.showMessageDialog(this, Messages.BOT_GUI_NOT_LOADED);
		} else {
			new ScriptSelector(this, bot).showGUI();
		}
	}

	private void showStopScript(Bot bot) {
		final TaskContainer sh = bot.composite.concurrentDispatch;
		if (sh.isRunning()) {
			final LoopTask s = sh.get();
			ScriptManifest prop = s.getClass().getAnnotation(ScriptManifest.class);
			int result = JOptionPane.showConfirmDialog(this,
					"Would you like to stop the script " + prop.name() + "?",
					"Script", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (result == JOptionPane.OK_OPTION) {
				s.setRunning(false);
			}
		}
	}

	private void setLogState(boolean expanded) {
		Dimension size = getSize();
		size.height += (expanded ? textScroll.getSize() : textLabel.getSize()).height;
		size.height -= (expanded ? textLabel.getSize() : textScroll.getSize()).height;
		remove(expanded ? textLabel : textScroll);
		add(expanded ? textScroll : textLabel, BorderLayout.SOUTH);
		setMinimumSize(size);
		if ((getExtendedState() & Frame.MAXIMIZED_BOTH) != Frame.MAXIMIZED_BOTH) {
			pack();
		}
	}

	private void init() {
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				dispose();
				System.exit(0);
			}
		});
		setIconImage(Configuration.getImage(Configuration.Paths.Resources.ICON));
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (final Exception ignored) {
		}
		WindowUtil.setFrame(this);

		home = new BotHome();
		panel = new BotPanel(home);
		toolBar = new BotToolBar(this);
		menuBar = new BotMenuBar(this);
		panel.setFocusTraversalKeys(0, new HashSet<AWTKeyStroke>());
		toolBar.update();
		menuBar.setBot(null);
		setJMenuBar(menuBar);

		textScroll = new JScrollPane(
				TextAreaLogHandler.TEXT_AREA,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		textScroll.setBorder(null);
		textScroll.setPreferredSize(new Dimension(PANEL_WIDTH, LOG_HEIGHT));
		textScroll.setVisible(true);

		textLabel = new JLabel(" ");
		textLabel.setFont(new Font(textLabel.getFont().getName(), Font.BOLD, textLabel.getFont().getSize()));
		textLabel.setSize(PANEL_WIDTH, textLabel.getHeight());

		setLogState(false);

		add(toolBar, BorderLayout.NORTH);
		add(panel, BorderLayout.CENTER);
	}

	public void setupLabel() {
		final Color statusDefaultColor = textLabel.getForeground();
		Logger.getLogger("").addHandler(new Handler() {
			public void close() throws SecurityException {
			}

			public void flush() {
			}

			@Override
			public void publish(final LogRecord record) {
				final StringBuilder txt = new StringBuilder(46);
				txt.append(" ");
				txt.append(record.getMessage());
				final boolean error = record.getLevel() == Level.WARNING || record.getLevel() == Level.SEVERE;
				textLabel.setForeground(error ? Color.RED : statusDefaultColor);
				textLabel.setText(txt.toString());
			}
		});
	}

	public static void openURL(final String url) {
		Configuration.OperatingSystem os = Configuration.getCurrentOperatingSystem();
		try {
			if (os == Configuration.OperatingSystem.MAC) {
				Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
				Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[]{String.class});
				openURL.invoke(null, url);
			} else if (os == Configuration.OperatingSystem.WINDOWS) {
				Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
			} else { // assume Unix or Linux
				String[] browsers = {"firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape"};
				String browser = null;
				for (int count = 0; (count < browsers.length) && (browser == null); count++) {
					if (Runtime.getRuntime().exec(new String[]{"which", browsers[count]}).waitFor() == 0) {
						browser = browsers[count];
					}
				}
				if (browser == null) {
					throw new Exception("Could not find web browser");
				} else {
					Runtime.getRuntime().exec(new String[]{browser, url});
				}
			}
		} catch (Exception e) {
			log.severe("Error opening browser");
		}
	}

	public void scriptStarted(final TaskContainer handler) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				final Bot bot = handler.bot;
				bot.composite.inputFlags = Environment.INPUT_KEYBOARD;
				bot.composite.overrideInput = false;
				final String acct = bot.composite.account;
				toolBar.setTabLabel(bots.indexOf(bot) + 1, acct == null ? Messages.BOT_GUI_TAB_TEXT : acct);
				if (bot.equals(getCurrentBot())) {
					menuBar.update();
					toolBar.update();
					setTitle(acct);
				}
			}
		});
	}

	public void scriptStopped(final TaskContainer handler) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				final Bot bot = handler.bot;
				bot.composite.inputFlags = Environment.INPUT_KEYBOARD | Environment.INPUT_MOUSE;
				bot.composite.overrideInput = false;
				toolBar.setTabLabel(bots.indexOf(bot) + 1, Messages.BOT_GUI_TAB_TEXT);
				if (bot.equals(getCurrentBot())) {
					menuBar.update();
					toolBar.update();
					setTitle(null);
				}
			}
		});
	}

	public void scriptResumed(final TaskContainer handler) {
		if (handler.bot.equals(getCurrentBot())) {
			toolBar.update();
			menuBar.update();
		}
	}

	public void scriptPaused(final TaskContainer handler) {
		if (handler.bot.equals(getCurrentBot())) {
			toolBar.update();
			menuBar.update();
		}
	}

	public void inputChanged(final Bot bot, final int mask) {
		bot.composite.inputFlags = mask;
		if (bot.equals(getCurrentBot())) {
			toolBar.update();
		}
	}
}

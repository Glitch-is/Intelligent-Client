package org.rsbot.ui;

import org.rsbot.Configuration;
import org.rsbot.bot.Bot;
import org.rsbot.bot.concurrent.handler.TaskContainer;
import org.rsbot.bot.event.listener.ScriptListener;
import org.rsbot.service.FileScriptSource;
import org.rsbot.service.ScriptDefinition;
import org.rsbot.service.ScriptSource;
import org.rsbot.service.ServiceException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Timer
 */
public class ScriptSelector extends JDialog implements ScriptListener {
	public static void main(String[] args) {
		new ScriptSelector(null, null).setVisible(true);
	}

	private static final String[] COLUMN_NAMES = new String[]{"", "Name", "Version", "Author", "Description"};
	private static final ScriptSource SRC_SOURCES;
	private static final ScriptSource SRC_PRECOMPILED;

	static {
		SRC_SOURCES = new FileScriptSource(new File(Configuration.Paths.getScriptsSourcesDirectory()));
		SRC_PRECOMPILED = new FileScriptSource(new File(Configuration.Paths.getScriptsPrecompiledDirectory()));
	}

	private Bot bot;
	private JTable table;
	private JTextField search;
	private JComboBox accounts;
	private ScriptTableModel model;
	private List<ScriptDefinition> scripts;
	private JButton submit;

	public ScriptSelector(Frame frame, Bot bot) {
		super(frame, "Script Selector");
		this.bot = bot;
		this.scripts = new ArrayList<ScriptDefinition>();
		this.model = new ScriptTableModel(this.scripts);
	}

	public void showGUI() {
		init();
		update();
		setVisible(true);
		load();
	}

	public void update() {
		boolean available = !bot.composite.concurrentDispatch.isRunning();
		submit.setEnabled(available && table.getSelectedRow() != -1);
		table.setEnabled(available);
		search.setEnabled(available);
		accounts.setEnabled(available);
		table.clearSelection();
	}

	private void load() {
		scripts.clear();
		scripts.addAll(SRC_PRECOMPILED.list());
		scripts.addAll(SRC_SOURCES.list());
		model.search(search.getText());
	}

	private void init() {
		setLayout(new BorderLayout());
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		bot.composite.concurrentDispatch.addScriptListener(ScriptSelector.this);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(final WindowEvent e) {
				bot.composite.concurrentDispatch.removeScriptListener(ScriptSelector.this);
				dispose();
			}
		});

		table = new JTable(model) {
			public String getToolTipText(MouseEvent e) {
				int row = rowAtPoint(e.getPoint());
				ScriptDefinition def = model.getDefinition(row);
				if (def != null) {
					StringBuilder b = new StringBuilder();
					if (def.authors.length > 1) {
						b.append("Authors: ");
					} else {
						b.append("Author: ");
					}
					boolean prefix = false;
					for (String author : def.authors) {
						if (prefix) {
							b.append(", ");
						} else {
							prefix = true;
						}
						b.append(author);
					}
					return b.toString();
				}
				return super.getToolTipText(e);
			}
		};
		table.setRowHeight(20);
		table.setIntercellSpacing(new Dimension(1, 1));
		table.setShowGrid(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(new TableSelectionListener());
		setColumnWidths(table, 30, 175, 50, 100);

		JToolBar toolBar = new JToolBar();
		toolBar.setMargin(new Insets(1, 1, 1, 1));
		toolBar.setFloatable(false);

		search = new JTextField();
		search.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				table.clearSelection();
			}
		});
		search.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				model.search(search.getText());
				table.revalidate();
			}
		});
		submit = new JButton("Start Script", new ImageIcon(Configuration.getImage(Configuration.Paths.Resources.ICON_START)));
		JButton connect = new JButton(new ImageIcon(Configuration.getImage(Configuration.Paths.Resources.ICON_DISCONNECT)));
		submit.setEnabled(false);
		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				final ScriptDefinition def = model.getDefinition(table.getSelectedRow());
				new Thread(bot.getThreadGroup(), new Runnable() {
					public void run() {
						try {
							bot.setAccount((String) accounts.getSelectedItem());
							bot.composite.concurrentDispatch.init(def.source.load(def));
							bot.composite.concurrentDispatch.invoke();
							bot.composite.concurrentDispatch.removeScriptListener(ScriptSelector.this);
						} catch (final ServiceException e) {
							e.printStackTrace();
						}
					}
				}).start();
				dispose();
			}
		});

		connect.setEnabled(false);
		connect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
			}
		});

		accounts = new JComboBox(AccountManager.getAccountNames());
		accounts.setMinimumSize(new Dimension(200, 20));
		accounts.setPreferredSize(new Dimension(200, 20));

		toolBar.add(search);
		toolBar.add(Box.createHorizontalStrut(5));
		toolBar.add(accounts);
		toolBar.add(Box.createHorizontalStrut(5));
		toolBar.add(connect);
		toolBar.add(Box.createHorizontalStrut(5));
		toolBar.add(submit);

		JPanel center = new JPanel();
		center.setLayout(new BorderLayout());
		JScrollPane pane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		center.add(pane, BorderLayout.CENTER);

		add(center, BorderLayout.CENTER);
		add(toolBar, BorderLayout.SOUTH);

		setSize(750, 400);
		setMinimumSize(getSize());
		setLocationRelativeTo(getParent());
		search.requestFocus();
	}

	private void setColumnWidths(JTable table, int... widths) {
		for (int i = 0; i < widths.length; ++i) {
			table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
			table.getColumnModel().getColumn(i).setMinWidth(widths[i]);
			table.getColumnModel().getColumn(i).setMaxWidth(widths[i]);
		}
	}

	public void scriptStarted(final TaskContainer handler) {
		update();
	}

	public void scriptStopped(final TaskContainer handler) {
		update();
	}

	public void scriptResumed(TaskContainer handler) {
	}

	public void scriptPaused(TaskContainer handler) {
	}

	public void inputChanged(Bot bot, int mask) {
	}

	private class TableSelectionListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent evt) {
			if (!evt.getValueIsAdjusting()) {
				submit.setEnabled(table.getSelectedRow() != -1);
			}
		}
	}

	private static class ScriptTableModel extends AbstractTableModel {
		public static final ImageIcon ICON_SCRIPT_SRC = new ImageIcon(Configuration.getImage(Configuration.Paths.Resources.ICON_SCRIPT_SRC));
		public static final ImageIcon ICON_SCRIPT_PRE = new ImageIcon(Configuration.getImage(Configuration.Paths.Resources.ICON_SCRIPT_PRE));
		public static final ImageIcon ICON_SCRIPT_DRM = new ImageIcon(Configuration.getImage(Configuration.Paths.Resources.ICON_SCRIPT_DRM));

		private List<ScriptDefinition> scripts;
		private List<ScriptDefinition> matches;

		public ScriptTableModel(List<ScriptDefinition> scripts) {
			this.scripts = scripts;
			this.matches = new ArrayList<ScriptDefinition>();
		}

		public void search(String substr) {
			matches.clear();
			if (substr.length() == 0) {
				matches.addAll(scripts);
			} else {
				substr = substr.toLowerCase();
				for (ScriptDefinition def : scripts) {
					if (def.name.toLowerCase().contains(substr)) {
						matches.add(def);
					} else {
						for (String keyword : def.keywords) {
							if (keyword.toLowerCase().contains(substr)) {
								matches.add(def);
								break;
							}
						}
					}
				}
			}
			fireTableDataChanged();
		}

		public ScriptDefinition getDefinition(int rowIndex) {
			return matches.get(rowIndex);
		}

		public int getRowCount() {
			return matches.size();
		}

		public int getColumnCount() {
			return COLUMN_NAMES.length;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			if (rowIndex >= 0 && rowIndex < matches.size()) {
				ScriptDefinition def = matches.get(rowIndex);
				if (columnIndex == 0) {
					if (def.source == SRC_SOURCES) {
						return ICON_SCRIPT_SRC;
					}
					if (def.source == SRC_PRECOMPILED) {
						return ICON_SCRIPT_PRE;
					}
					return ICON_SCRIPT_DRM;
				}
				if (columnIndex == 1) {
					return def.name;
				}
				if (columnIndex == 2) {
					return def.version;
				}
				if (columnIndex == 3) {
					StringBuilder b = new StringBuilder();
					for (String author : def.authors) {
						b.append(author).append(", ");
					}
					return b.replace(b.length() - 2, b.length(), "");
				}
				if (columnIndex == 4) {
					return def.description;
				}
			}
			return null;
		}

		@Override
		public Class<?> getColumnClass(int col) {
			if (col == 0) {
				return ImageIcon.class;
			}
			return String.class;
		}

		@Override
		public String getColumnName(int col) {
			return COLUMN_NAMES[col];
		}
	}
}
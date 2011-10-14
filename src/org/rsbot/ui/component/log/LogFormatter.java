package org.rsbot.ui.component.log;

import org.rsbot.util.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	public boolean appendNewLine;

	public LogFormatter() {
		this(true);
	}

	public LogFormatter(final boolean appendNewLine) {
		this.appendNewLine = appendNewLine;
	}

	@Override
	public String format(LogRecord record) {
		final StringBuilder result = new StringBuilder().append("[").append(record.getLevel().getName()).append("] ").
				append(new Date(record.getMillis())).append(": ").append(record.getLoggerName()).append(": ").
				append(record.getMessage()).append(StringUtil.throwableToString(record.getThrown()));
		if (appendNewLine) {
			result.append(LogFormatter.LINE_SEPARATOR);
		}
		return result.toString();
	}

	@Override
	public String formatMessage(final LogRecord record) {
		return String.format(record.getMessage());
	}

	public String formatTimestamp(final LogRecord record) {
		final SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
		return "[" + dateFormat.format(record.getMillis()) + "]";
	}

	public String formatClass(final LogRecord record) {
		final String append = "...";
		final String[] className = record.getLoggerName().split("\\.");
		final String name = className[className.length - 1];
		final int maxLen = 16;

		return String.format(name.length() > maxLen ? name.substring(0, maxLen - append.length()) + append : name);
	}

	public String formatError(final LogRecord record) {
		return StringUtil.throwableToString(record.getThrown());
	}
}
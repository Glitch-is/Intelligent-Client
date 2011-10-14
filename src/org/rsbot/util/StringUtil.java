package org.rsbot.util;

import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * A string utility.
 *
 * @author Jacmob
 */
public class StringUtil {
	private static final String[] COLOURS_STR = new String[]{"red", "green", "cyan", "purple", "white"};
	private static final Map<String, Color> COLOR_MAP = new HashMap<String, Color>();

	public static String stripHtml(final String s) {
		return s.replaceAll("\\<.*?\\>", "");
	}

	public static String formatVersion(final int version) {
		final float v = (float) version / 100;
		String s = Float.toString(v);
		final int z = s.indexOf('.');
		if (z == -1) {
			s += ".00";
		} else {
			final String exp = s.substring(z + 1);
			if (exp.length() == 1) {
				s += "0";
			}
		}
		return s;
	}

	/**
	 * Draws a line on the screen at the specified index. Default is green.
	 * <p/>
	 * Available colours: red, green, cyan, purple, white.
	 *
	 * @param render The Graphics object to be used.
	 * @param row    The index where you want the text.
	 * @param text   The text you want to render. Colours can be set like [red].
	 */
	public static void drawLine(final Graphics render, final int row, final String text) {
		final FontMetrics metrics = render.getFontMetrics();
		final int height = metrics.getHeight() + 4; // height + gap
		final int y = row * height + 15 + 19;
		final String[] texts = text.split("\\[");
		int xIdx = 7;
		Color cur = Color.GREEN;
		for (String t : texts) {
			for (@SuppressWarnings("unused") final String element : COLOURS_STR) {
				// String element = COLOURS_STR[i];
				// Don't search for a starting '[' cause it they don't exists.
				// we split on that.
				final int endIdx = t.indexOf(']');
				if (endIdx != -1) {
					final String colorName = t.substring(0, endIdx);
					if (COLOR_MAP.containsKey(colorName)) {
						cur = COLOR_MAP.get(colorName);
					} else {
						try {
							final Field f = Color.class.getField(colorName);
							final int mods = f.getModifiers();
							if (Modifier.isPublic(mods) && Modifier.isStatic(mods) && Modifier.isFinal(mods)) {
								cur = (Color) f.get(null);
								COLOR_MAP.put(colorName, cur);
							}
						} catch (final Exception ignored) {
						}
					}
					t = t.replace(colorName + "]", "");
				}
			}
			render.setColor(Color.BLACK);
			render.drawString(t, xIdx, y + 1);
			render.setColor(cur);
			render.drawString(t, xIdx, y);
			xIdx += metrics.stringWidth(t);
		}
	}

	public static String throwableToString(final Throwable t) {
		if (t != null) {
			final Writer exception = new StringWriter();
			final PrintWriter printWriter = new PrintWriter(exception);
			t.printStackTrace(printWriter);
			return exception.toString();
		}
		return "";
	}

	public static byte[] getBytesUtf8(final String string) {
		try {
			return string.getBytes("UTF-8");
		} catch (final UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

	public static String newStringUtf8(final byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		try {
			return new String(bytes, "UTF-8");
		} catch (final UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}

	public static String sha1sum(final String data) {
		final MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (final NoSuchAlgorithmException ignored) {
			return data;
		}
		return byteArrayToHexString(md.digest(getBytesUtf8(data)));
	}

	public static String byteArrayToHexString(byte[] b) {
		final StringBuilder s = new StringBuilder(b.length * 2);
		for (byte aB : b) {
			s.append(Integer.toString((aB & 0xff) + 0x100, 16).substring(1));
		}
		return s.toString();
	}
}
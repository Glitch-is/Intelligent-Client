package org.rsbot.loader.script;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Paris
 */
public class Scanner {
	private final static int EOL = 0xA;
	private InputStream in;

	public Scanner(final byte[] data) {
		this(new ByteArrayInputStream(data));
	}

	public Scanner(final InputStream in) {
		this.in = in;
	}

	public int readByte() {
		try {
			return in.read();
		} catch (final IOException ignored) {
			return -1;
		}
	}

	public int readShort() {
		return (readByte() << 8) | readByte();
	}

	public int readInt() {
		return (readShort() << 16) | readShort();
	}

	public long readLong() {
		return (readInt() << 32) | readInt();
	}

	public String readString() {
		return new String(readSegment());
	}

	public byte[] readSegment() {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		int b;
		while ((b = readByte()) != EOL && b != -1) {
			out.write(b);
		}
		return out.toByteArray();
	}

	public void readSegment(final byte[] data, final int len, final int off) {
		for (int i = off; i < off + len; i++) {
			data[i] = (byte) readByte();
		}
	}
}

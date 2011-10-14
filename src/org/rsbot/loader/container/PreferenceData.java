package org.rsbot.loader.container;

import org.rsbot.Configuration;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class PreferenceData {
	private File file = null;

	public PreferenceData(final int type) {
		file = new File(Configuration.Paths.getSettingsDirectory(), "preferences" + type + ".dat");
		try {
			if (!file.exists()) {
				if (!file.createNewFile()) {
					file = null;
				}
			}
		} catch (final IOException ignored) {
		}
	}

	public byte[] get() {
		if (file == null) {
			return new byte[0];
		}
		try {
			final RandomAccessFile raf = new RandomAccessFile(file, "rw");
			final byte[] b = new byte[(int) raf.length()];
			raf.readFully(b);
			return b;
		} catch (final IOException ioe) {
			return new byte[0];
		}
	}

	public void set(final byte[] data) {
		if (file == null) {
			return;
		}
		try {
			final RandomAccessFile raf = new RandomAccessFile(file, "rw");
			raf.write(data);
		} catch (final IOException ignored) {
		}
	}
}
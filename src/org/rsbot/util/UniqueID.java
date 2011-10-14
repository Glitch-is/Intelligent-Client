package org.rsbot.util;

import org.rsbot.Configuration;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Generates a key unique to a computer.
 *
 * @author Paris
 */
public class UniqueID {
	private static final int LENGTH = 64;
	private static File store = new File(Configuration.Paths.getSettingsDirectory(), "random.dat");

	public static String getID() {
		if (!store.exists()) {
			final byte[] d = new byte[LENGTH];
			new SecureRandom().nextBytes(d);
			IOHelper.write(new ByteArrayInputStream(d), store);
		}
		final MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (final NoSuchAlgorithmException ignored) {
			return store.getAbsolutePath();
		}
		md.update(IOHelper.read(store));
		md.update(StringUtil.getBytesUtf8(Configuration.NAME));
		md.update(StringUtil.getBytesUtf8(Configuration.Paths.URLs.CLIENT_PATCH));
		return StringUtil.byteArrayToHexString(md.digest());
	}
}
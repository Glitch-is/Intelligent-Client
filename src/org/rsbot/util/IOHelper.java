package org.rsbot.util;

import java.io.*;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.GZIPInputStream;

/**
 * @author Paris
 */
public class IOHelper {
	public static byte[] read(final InputStream is) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			final byte[] temp = new byte[4096];
			int read;
			while ((read = is.read(temp)) != -1) {
				buffer.write(temp, 0, read);
			}
		} catch (final IOException ignored) {
			try {
				buffer.close();
			} catch (final IOException ignored2) {
			}
			buffer = null;
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (final IOException ignored) {
			}
		}
		return buffer == null ? null : buffer.toByteArray();
	}

	public static byte[] read(final URL in) {
		try {
			return read(in.openStream());
		} catch (final IOException ignored) {
			return null;
		}
	}

	public static byte[] read(final File in) {
		try {
			return read(new FileInputStream(in));
		} catch (final FileNotFoundException ignored) {
			return null;
		}
	}

	public static String readString(final URL in) {
		return StringUtil.newStringUtf8(read(in));
	}

	public static String readString(final File in) {
		return StringUtil.newStringUtf8(read(in));
	}

	public static void write(final InputStream in, final OutputStream out) {
		try {
			final byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
		} catch (final IOException ignored) {
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (final IOException ignored) {
			}
		}
	}

	public static void write(final InputStream in, final File out) {
		try {
			write(in, new FileOutputStream(out));
		} catch (final FileNotFoundException ignored) {
		}
	}

	public static void write(final String s, final File out) {
		final ByteArrayInputStream in = new ByteArrayInputStream(StringUtil.getBytesUtf8(s));
		write(in, out);
	}

	public static byte[] ungzip(final byte[] data) {
		if (data.length < 2) {
			return data;
		}

		final int header = (data[0] | data[1] << 8) ^ 0xffff0000;
		if (header != GZIPInputStream.GZIP_MAGIC) {
			return data;
		}

		try {
			final ByteArrayInputStream b = new ByteArrayInputStream(data);
			final GZIPInputStream gzin = new GZIPInputStream(b);
			final ByteArrayOutputStream out = new ByteArrayOutputStream(data.length);
			for (int c = gzin.read(); c != -1; c = gzin.read()) {
				out.write(c);
			}
			return out.toByteArray();
		} catch (final IOException e) {
			e.printStackTrace();
			return data;
		}
	}

	public static boolean isZip(final File file) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			final byte[] m = new byte[4];
			fis.read(m);
			fis.close();
			return (m[0] << 24 | m[1] << 16 | m[2] << 8 | m[3]) == 0x504b0304;
		} catch (final IOException ignored) {
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (final IOException ignored) {
				}
			}
		}
		return false;
	}

	public static long crc32(final InputStream in) throws IOException {
		final CheckedInputStream cis = new CheckedInputStream(in, new CRC32());
		final byte[] buf = new byte[128];
		while (cis.read(buf) > -1) {
		}
		return cis.getChecksum().getValue();
	}

	public static long crc32(final byte[] data) throws IOException {
		return crc32(new ByteArrayInputStream(data));
	}

	public static long crc32(final File path) throws IOException {
		return crc32(new FileInputStream(path));
	}

	public static String md5(final String data) {
		return md5(StringUtil.getBytesUtf8(data));
	}

	public static String md5(final byte[] data) {
		final MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (final NoSuchAlgorithmException ignored) {
			return StringUtil.newStringUtf8(data);
		}
		return StringUtil.byteArrayToHexString(md.digest(data));
	}

	public static String sha1(final String data) {
		return sha1(StringUtil.getBytesUtf8(data));
	}

	public static String sha1(final byte[] data) {
		final MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (final NoSuchAlgorithmException ignored) {
			return StringUtil.newStringUtf8(data);
		}
		return StringUtil.byteArrayToHexString(md.digest(data));
	}

	public static long adler32(final InputStream in) throws IOException {
		final CheckedInputStream cis = new CheckedInputStream(in, new Adler32());
		final byte[] buf = new byte[128];
		while (cis.read(buf) > -1) {
		}
		return cis.getChecksum().getValue();
	}

	public static long adler32(final byte[] data) throws IOException {
		return adler32(new ByteArrayInputStream(data));
	}

	public static long adler32(final File path) throws IOException {
		return adler32(new FileInputStream(path));
	}
}
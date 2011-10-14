package org.rsbot.loader;

import java.awt.*;
import java.io.*;
import java.net.SocketPermission;
import java.net.URL;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.util.Calendar;
import java.util.Map;
import java.util.PropertyPermission;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Alex
 */
public final class RSClassLoader extends ClassLoader {
	private final Logger log = Logger.getLogger(RSClassLoader.class.getName());
	private Map<String, byte[]> classes;
	private ProtectionDomain domain;

	public RSClassLoader(final Map<String, byte[]> classes, final URL source) {
		try {
			final CodeSource codeSource = new CodeSource(source, (CodeSigner[]) null);
			domain = new ProtectionDomain(codeSource, getPermissions());
			this.classes = classes;

			//Get path of org/rsbot/bot/accessors/RandomAccessFile
			String s = getClass().getResource("RSClassLoader.class").toString();
			s = s.replace("loader/RSClassLoader.class", "bot/accessors/RandomAccessFile.class");
			final URL url = new URL(s);

			//Read org/rsbot/bot/accessors/RandomAccessFile
			InputStream is = null;
			try {
				final ByteArrayOutputStream bos = new ByteArrayOutputStream(5000);
				is = new BufferedInputStream(url.openStream());

				final byte[] buff = new byte[1024];
				int len;
				while ((len = is.read(buff)) != -1) {
					bos.write(buff, 0, len);
				}

				final byte[] data = bos.toByteArray();

				//Store it so we can load it
				this.classes.put("org.rsbot.bot.accessors.RandomAccessFile", data);
			} catch (final IOException e) {
				e.printStackTrace();
			} finally {
				if (is != null) {
					is.close();
				}
			}
		} catch (final Exception ignored) {
		}
	}

	private Permissions getPermissions() {
		final Permissions ps = new Permissions();
		ps.add(new AWTPermission("accessEventQueue"));
		ps.add(new PropertyPermission("user.home", "read"));
		ps.add(new PropertyPermission("java.vendor", "read"));
		ps.add(new PropertyPermission("java.version", "read"));
		ps.add(new PropertyPermission("os.name", "read"));
		ps.add(new PropertyPermission("os.arch", "read"));
		ps.add(new PropertyPermission("os.version", "read"));
		ps.add(new SocketPermission("*", "connect,resolve"));
		String uDir = System.getProperty("user.home");
		if (uDir != null) {
			uDir += "/";
		} else {
			uDir = "~/";
		}
		final String[] dirs = {"c:/rscache/", "/rscache/", "c:/windows/", "c:/winnt/", "c:/", uDir, "/tmp/", "."};
		final String[] rsDirs = {".jagex_cache_32", ".file_store_32"};
		for (String dir : dirs) {
			final File f = new File(dir);
			ps.add(new FilePermission(dir, "read"));
			if (!f.exists()) {
				continue;
			}
			dir = f.getPath();
			for (final String rsDir : rsDirs) {
				ps.add(new FilePermission(dir + File.separator + rsDir + File.separator + "-", "read"));
				ps.add(new FilePermission(dir + File.separator + rsDir + File.separator + "-", "write"));
			}
		}
		Calendar.getInstance();
		//TimeZone.getDefault();//Now the default is set they don't need permission
		//ps.add(new FilePermission())
		ps.setReadOnly();
		return ps;
	}

	@Override
	public final Class<?> loadClass(final String name) throws ClassNotFoundException {
		if (classes.containsKey(name)) {
			final byte buffer[] = classes.remove(name);
			try {
				return defineClass(name, buffer, 0, buffer.length, domain);
			} catch (Throwable throwable) {
				log.log(Level.SEVERE, "Error occurred while loading the game client", throwable);
			}
		}
		return super.loadClass(name);
	}
}
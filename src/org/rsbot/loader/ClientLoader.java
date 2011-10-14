package org.rsbot.loader;

import org.rsbot.Configuration;
import org.rsbot.loader.script.ModScript;
import org.rsbot.loader.script.ParseException;
import org.rsbot.util.HttpClient;
import org.rsbot.util.IOHelper;
import org.rsbot.util.IniParser;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Loads an instance of Runescape.
 *
 * @author Timer
 */
public class ClientLoader {
	private static final Logger log = Logger.getLogger(ClientLoader.class.getName());

	private static final String id = ClientLoader.class.getName();
	private static final String id_patch = "patch";
	private static final String id_version = "client_version";

	private static ModScript MOD_SCRIPT = null;
	private static Map<String, byte[]> classes = new HashMap<String, byte[]>();
	private static long loadedClassHash = -1L;

	public final static int PORT_CLIENT = 43594;

	private static final File manifest = new File(Configuration.Paths.getCacheDirectory(), "client.ini");
	private static final File patch = new File(Configuration.Paths.getClientPatchCacheFile());
	private static final File client = new File(Configuration.Paths.getCacheDirectory(), "client.jar");

	public synchronized static boolean update() {
		if (!manifest.exists()) {
			try {
				manifest.createNewFile();
			} catch (final IOException ignored) {
			}
		}
		try {
			HttpClient.download(new URL(Configuration.Paths.URLs.CLIENT_PATCH), patch);
		} catch (final IOException ioe) {
			log.severe("Failed to download client patch");
			if (!patch.exists()) {
				return false;
			}
			log.info("Attempting to use a cached copy");
		}
		final byte[] patchBytes = IOHelper.read(patch);
		long hash;
		try {
			hash = IOHelper.crc32(patchBytes);
		} catch (final IOException ignored) {
			hash = -1;
		}
		try {
			long oldHash;
			try {
				oldHash = Long.parseLong(getManifest(id_patch));
			} catch (final NullPointerException ignored) {
				oldHash = -1;
			} catch (final NumberFormatException ignored) {
				oldHash = -1;
			}
			if (oldHash == -1 || MOD_SCRIPT == null || hash != oldHash) {
				MOD_SCRIPT = new ModScript(patchBytes);
				putManifest(id_patch, Long.toString(hash));
			}
		} catch (final IOException ioe) {
			log.severe("Failed to serialize manifest");
			return false;
		} catch (final ParseException pe) {
			log.severe("Bad patch magic!");
			patch.deleteOnExit();
			return false;
		}
		return true;
	}

	public synchronized static boolean isOutdated() {
		if (!patch.exists()) {
			return true;
		}
		final String rawVersion = getManifest(id_version);
		final int version = rawVersion == null ? -1 : Integer.parseInt(rawVersion);
		int currentVersion = getRemoteVersion(version == -1 ? 660 : version);
		return currentVersion != MOD_SCRIPT.getVersion();
	}

	public synchronized static void load() throws IOException, ParseException {
		if (!patch.exists()) {
			return;
		}
		final String rawVersion = getManifest(id_version);
		final int version = rawVersion == null ? -1 : Integer.parseInt(rawVersion);
		final int currentVersion = getRemoteVersion(version == -1 ? 660 : version);
		if (currentVersion != MOD_SCRIPT.getVersion()) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(
							null,
							"The bot is currently outdated, please wait patiently for a new version.",
							"Outdated (" + MOD_SCRIPT.getName() + ")",
							JOptionPane.INFORMATION_MESSAGE);

				}
			});
			throw new ParseException(MOD_SCRIPT.getName() + " is outdated (" + currentVersion + " != " + MOD_SCRIPT.getVersion() + ")");
		}

		if (!ClientLoader.client.exists() || version == -1 || version != currentVersion) {
			classes.clear();
			log.info("Downloading " + getTargetName());
			final JarFile loader = getJar(true), client = getJar(false);
			final List<String> replace = Arrays.asList(MOD_SCRIPT.getAttribute("replace").split(" "));
			for (final JarFile jar : new JarFile[]{loader, client}) {
				final Enumeration<JarEntry> entries = jar.entries();
				while (entries.hasMoreElements()) {
					final JarEntry entry = entries.nextElement();
					String name = entry.getName();
					if (name.endsWith(".class")) {
						name = name.substring(0, name.length() - 6).replace('/', '.');
						if (jar == client || replace.contains(name)) {
							classes.put(name, IOHelper.read(jar.getInputStream(entry)));
						}
					}
				}
				jar.close();
			}
			writeClient(ClientLoader.client, classes);
			putManifest(id_version, Integer.toString(currentVersion));
		}

		final long classHash = IOHelper.crc32(IOHelper.read(ClientLoader.client));
		if (loadedClassHash == -1L || loadedClassHash != classHash) {
			log.info("Processing " + getTargetName());
			readClient(ClientLoader.client, classes);
			for (final Map.Entry<String, byte[]> entry : classes.entrySet()) {
				entry.setValue(MOD_SCRIPT.process(entry.getKey(), entry.getValue()));
			}
			loadedClassHash = classHash;
			BotStub.crawler = new Crawler("http://www." + getTargetName() + ".com/");
		}
	}

	public static Map<String, byte[]> getClasses() {
		if (classes == null) {
			return null;
		}
		final Map<String, byte[]> copy = new HashMap<String, byte[]>(classes.size());
		for (final Map.Entry<String, byte[]> item : classes.entrySet()) {
			copy.put(item.getKey(), item.getValue().clone());
		}
		return copy;
	}

	private synchronized static void putManifest(final String key, final String value) throws IOException {
		final Map<String, Map<String, String>> data = new HashMap<String, Map<String, String>>(1);
		final Map<String, String> info = new HashMap<String, String>();
		final Map<String, String> oldMap = IniParser.deserialise(manifest).get(id);
		if (oldMap != null) {
			info.putAll(oldMap);
		}
		info.put(key, value);
		data.put(id, info);
		IniParser.serialise(data, manifest);
	}

	private synchronized static String getManifest(final String key) {
		try {
			final Map<String, String> info = IniParser.deserialise(manifest).get(id);
			return info.get(key);
		} catch (final Throwable ignored) {
			return null;
		}
	}

	private static int getRemoteVersion(final int start) {
		String host = getTargetHost();
		Socket sock = null;
		for (int i = start; i < start + 50; i++) {
			try {
				sock = new Socket(host, PORT_CLIENT);
				final byte[] payload = new byte[]{15, 0, 0, (byte) (i >> 8), (byte) i};
				sock.getOutputStream().write(payload, 0, payload.length);
				if (sock.getInputStream().read() == 0) {
					return i;
				}
			} catch (final IOException ignored) {
				host = getTargetHost();
			} finally {
				if (sock != null) {
					try {
						sock.close();
					} catch (final IOException ignored) {
					}
				}
			}
		}
		return -1;
	}

	private static JarFile getJar(final boolean loader) {
		while (true) {
			try {
				final StringBuilder sb = new StringBuilder(50);
				sb.append("jar:http://").append(getTargetHost()).append("/").append(loader ? "loader" : getTargetName()).append(".jar!/");
				final JarURLConnection juc = (JarURLConnection) new URL(sb.toString()).openConnection();
				juc.setConnectTimeout(5000);
				return juc.getJarFile();
			} catch (final Exception ignored) {
			}
		}
	}

	private static String getTargetHost() {
		return "world" + (1 + new Random().nextInt(169)) + "." + getTargetName() + ".com";
	}

	public static String getTargetName() {
		return MOD_SCRIPT.getAttribute("target");
	}

	private static void readClient(final File file, final Map<String, byte[]> map) throws IOException {
		map.clear();
		final JarFile jar = new JarFile(file);
		final Enumeration<JarEntry> entries = jar.entries();
		while (entries.hasMoreElements()) {
			final JarEntry entry = entries.nextElement();
			String name = entry.getName();
			if (name.endsWith(".class")) {
				name = name.substring(0, name.length() - 6).replace('/', '.');
				map.put(name, IOHelper.read(jar.getInputStream(entry)));
			}
		}
		jar.close();
	}

	private static void writeClient(final File file, final Map<String, byte[]> map) throws IOException {
		final ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(file));
		zip.setMethod(ZipOutputStream.STORED);
		zip.setLevel(0);
		for (final Map.Entry<String, byte[]> item : map.entrySet()) {
			final ZipEntry entry = new ZipEntry(item.getKey() + ".class");
			entry.setMethod(ZipEntry.STORED);
			final byte[] data = item.getValue();
			entry.setSize(data.length);
			entry.setCompressedSize(data.length);
			entry.setCrc(IOHelper.crc32(data));
			zip.putNextEntry(entry);
			zip.write(item.getValue());
			zip.closeEntry();
		}
		zip.close();
		map.clear();
	}
}

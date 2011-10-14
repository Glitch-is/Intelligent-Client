package org.rsbot.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * A simple class to encrypt account data.
 *
 * @author Jacmob
 */
public class AccountStore {
	public static class Account {
		private final String username;
		private String password;
		private final Map<String, String> attributes = new TreeMap<String, String>();

		public Account(final String username) {
			this.username = username;
		}

		public String getUsername() {
			return username;
		}

		public String getPassword() {
			return password;
		}

		public String getAttribute(final String key) {
			return attributes.get(key);
		}

		public void setAttribute(final String key, final String value) {
			attributes.put(key, value);
		}

		public void setPassword(final String password) {
			this.password = password;
		}

		@Override
		public String toString() {
			return "Account[" + username + "]";
		}
	}

	private static final String KEY_ALGORITHM = "DESede";
	private static final String CIPHER_TRANSFORMATION = "DESede/CBC/PKCS5Padding";
	private static final int FORMAT_VERSION = 2;

	private final File file;
	private byte[] digest;
	private final String[] protectedAttributes = {"pin"};

	private final Map<String, Account> accounts = new TreeMap<String, Account>();

	public AccountStore(final File file) {
		this.file = file;
	}

	public Account get(final String username) {
		return accounts.get(username);
	}

	public void remove(final String username) {
		accounts.remove(username);
	}

	public void add(final Account account) {
		accounts.put(account.username, account);
	}

	public Collection<Account> list() {
		return accounts.values();
	}

	public void load() throws IOException {
		if (!file.exists()) {
			file.createNewFile();
		}
		if (!file.canRead() || !file.canWrite()) {
			file.setReadable(true);
			file.setWritable(true);
		}
		final BufferedReader br = new BufferedReader(new FileReader(file));
		try {
			final int v = Integer.parseInt(br.readLine());
			if (v != FORMAT_VERSION) {
				throw new IOException("unsupported format version: " + v);
			}
		} catch (final NumberFormatException ex) {
			throw new IOException("bad format");
		}
		accounts.clear();
		Account current = null;
		for (; ; ) {
			final String line = br.readLine();
			if (line == null) {
				break;
			}
			if (line.startsWith("[") && line.endsWith("]")) {
				if (current != null) {
					accounts.put(current.username, current);
				}
				final String name = AccountStore.fixName(line.trim().substring(1).substring(0, line.length() - 2));
				current = new Account(name);
				continue;
			}
			if (current != null && line.matches("^\\w+=.+$")) {
				final String[] split = line.trim().split("=");
				if (split[0].equals("password")) {
					current.password = decrypt(split[1]);
				} else {
					if (Arrays.asList(protectedAttributes).contains(split[0])) {
						split[1] = decrypt(split[1]);
					}
					current.setAttribute(split[0], split[1]);
				}
			}
		}
		if (current != null) {
			accounts.put(current.username, current);
		}
		br.close();
	}

	public void save() throws IOException {
		final BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		bw.write(Integer.toString(FORMAT_VERSION));
		bw.newLine();
		for (final String name : accounts.keySet()) {
			bw.append("[").append(AccountStore.fixName(name.trim())).append("]");
			bw.newLine();
			final String password = accounts.get(name).password;
			if (password != null) {
				bw.append("password=");
				bw.append(encrypt(password));
			}
			bw.newLine();
			for (final Map.Entry<String, String> entry : accounts.get(name).attributes.entrySet()) {
				final String key = entry.getKey();
				String value = entry.getValue();
				if (Arrays.asList(protectedAttributes).contains(key)) {
					value = encrypt(value);
				}
				bw.append(key).append("=").append(value);
				bw.newLine();
			}
		}
		bw.close();
	}

	public void setPassword(final String password) {
		if (password == null) {
			digest = null;
			return;
		}
		try {
			final MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(password.getBytes("iso-8859-1"), 0, password.length());
			digest = md.digest();
		} catch (final Exception e) {
			throw new RuntimeException("Unable to digest password!");
		}
		digest = Arrays.copyOf(digest, 24);
		for (int i = 0, off = 20; i < 4; ++i) {
			digest[off++] = digest[i];
		}
	}

	private String encrypt(final String data) {
		if (digest == null) {
			final byte[] enc = Base64.encodeBase64(StringUtil.getBytesUtf8(data));
			return StringUtil.newStringUtf8(enc);
		}
		final SecretKey key = new SecretKeySpec(digest, KEY_ALGORITHM);
		final IvParameterSpec iv = new IvParameterSpec(new byte[8]);

		byte[] enc;
		try {
			final Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
			cipher.init(Cipher.ENCRYPT_MODE, key, iv);
			enc = cipher.doFinal(StringUtil.getBytesUtf8(data));
		} catch (final Exception e) {
			throw new RuntimeException("Unable to encrypt data!");
		}
		return StringUtil.newStringUtf8(Base64.encodeBase64(enc));
	}

	private String decrypt(final String data) throws IOException {
		if (digest == null) {
			final byte[] enc = Base64.decodeBase64(StringUtil.getBytesUtf8(data));
			return StringUtil.newStringUtf8(enc);
		}
		final SecretKey key = new SecretKeySpec(digest, KEY_ALGORITHM);
		final IvParameterSpec iv = new IvParameterSpec(new byte[8]);

		byte[] dec;
		try {
			final Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
			cipher.init(Cipher.DECRYPT_MODE, key, iv);
			dec = cipher.doFinal(Base64.decodeBase64(data));
		} catch (final Exception e) {
			throw new IOException("Unable to decrypt data!");
		}
		return StringUtil.newStringUtf8(dec);
	}

	/**
	 * Capitalizes the first character and replaces spaces with underscores.
	 * Purely aesthetic.
	 *
	 * @param name The name of the account
	 * @return Fixed name
	 */
	private static String fixName(String name) {
		if (name.contains("@")) {
			name = name.toLowerCase().trim();
		} else {
			if (name.charAt(0) > 91) {
				name = (char) (name.charAt(0) - 32) + name.substring(1);
			}
			name = name.replaceAll("\\s", "_");
		}
		return name;
	}
}


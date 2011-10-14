package org.rsbot.service;

import org.rsbot.util.IOHelper;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class ScriptClassLoader extends ClassLoader {
	private final URL base;

	public ScriptClassLoader(final URL url) {
		this.base = url;
	}

	public Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
		Class<?> clazz = findLoadedClass(name);

		if (clazz == null) {
			try {
				final InputStream in = getResourceAsStream(name.replace('.', '/') + ".class");
				final byte[] bytes = IOHelper.read(in);
				clazz = defineClass(name, bytes, 0, bytes.length);
				if (resolve) {
					resolveClass(clazz);
				}
			} catch (final Exception e) {
				clazz = super.loadClass(name, resolve);
			}
		}

		return clazz;
	}

	public URL getResource(final String name) {
		try {
			return new URL(base, name);
		} catch (MalformedURLException e) {
			return null;
		}
	}

	public InputStream getResourceAsStream(final String name) {
		try {
			return new URL(base, name).openStream();
		} catch (IOException e) {
			return null;
		}
	}
}

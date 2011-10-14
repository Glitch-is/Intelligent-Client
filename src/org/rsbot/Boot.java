package org.rsbot;

import java.io.IOException;
import java.net.URLDecoder;


/**
 * A java class to execute the bot.
 *
 * @author Paris
 */
public class Boot {
	public static void main(final String[] args) throws IOException {
		String location = Boot.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		location = URLDecoder.decode(location, "UTF-8").replaceAll("\\\\", "/");
		final String app = Application.class.getCanonicalName();
		final String flags = "-Xmx512m -Dsun.java2d.d3d=false";
		boolean sh = true;
		final char q = '"', s = ' ';
		final StringBuilder param = new StringBuilder(64);

		Configuration.bootstrap();

		switch (Configuration.getCurrentOperatingSystem()) {
			case WINDOWS:
				sh = false;
				param.append("javaw");
				param.append(s);
				param.append(flags);
				break;
			case MAC:
				param.append("java");
				param.append(s);
				param.append(flags);
				param.append(s);
				param.append("-Xdock:name=");
				param.append(q);
				param.append(Configuration.NAME);
				param.append(q);
				param.append(s);
				param.append("-Xdock:icon=");
				param.append(q);
				param.append(Configuration.Paths.Resources.ICON);
				param.append(q);
				param.append(s);
				param.append("-Dapple.laf.useScreenMenuBar=true");
				break;
			default:
				param.append("java");
				param.append(s);
				param.append(flags);
				break;
		}

		param.append(s);
		param.append("-classpath");
		param.append(s);
		param.append(q);
		param.append(location);
		param.append(q);
		param.append(s);
		param.append(app);

		for (final String arg : args) {
			param.append(s);
			param.append(arg);
		}

		final Runtime run = Runtime.getRuntime();

		if (sh) {
			run.exec(new String[]{"/bin/sh", "-c", param.toString()});
		} else {
			run.exec(param.toString());
		}
	}
}
package org.rsbot.loader;

import org.rsbot.util.HttpClient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Crawler {
	private static final Logger log = Logger.getLogger(Crawler.class.getName());

	private static HashMap<String, String> parameters = new HashMap<String, String>();
	private final String world_prefix;

	public Crawler(final String root) {
		final String index = firstMatch("<a id=\"continue\" class=\"barItem\" href=\"([^\"]+)\"\\s+onclick=\"[^\"]+\">Continue to Full Site for News and Game Help", downloadPage(root, null));
		final String frame = root + "game.ws";
		final String game = firstMatch("<frame id=\"[^\"]+\" style=\"[^\"]+\" src=\"([^\"]+)\"", downloadPage(frame, index));

		world_prefix = game.substring(12, game.indexOf(".runescape"));

		final Pattern pattern = Pattern.compile("<param name=\"?([^\\s]+)\"?\\s+value=\"?([^>]*)\"?>", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		final Matcher matcher = pattern.matcher(downloadPage(game, frame));
		while (matcher.find()) {
			final String key = removeTrailingChar(matcher.group(1), '"');
			final String value = removeTrailingChar(matcher.group(2), '"');
			parameters.put(key, value);
		}
		if (parameters.containsKey("haveie6")) {
			parameters.put("haveie6", "0");
		}

		log.fine("Parameters: " + parameters);
	}

	private String downloadPage(final String url, final String referer) {
		try {
			final HttpURLConnection con = HttpClient.getHttpConnection(new URL(url));
			if (referer != null && !referer.isEmpty()) {
				con.addRequestProperty("Referer", referer);
			}
			return HttpClient.downloadAsString(con);
		} catch (final IOException e) {
			e.printStackTrace();
			return "";
		}
	}

	private String firstMatch(final String regex, final String str) {
		final Pattern pattern = Pattern.compile(regex);
		final Matcher matcher = pattern.matcher(str);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

	public HashMap<String, String> getParameters() {
		return parameters;
	}

	public String getWorldPrefix() {
		return world_prefix;
	}

	private String removeTrailingChar(final String str, final char ch) {
		if (str == null || str.isEmpty()) {
			return str;
		} else if (str.length() == 1) {
			return str.charAt(0) == ch ? "" : str;
		}
		try {
			final int l = str.length() - 1;
			if (str.charAt(l) == ch) {
				return str.substring(0, l);
			}
			return str;
		} catch (final Exception e) {
			return str;
		}
	}
}
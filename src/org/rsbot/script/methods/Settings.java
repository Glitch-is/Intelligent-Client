package org.rsbot.script.methods;

import org.rsbot.bot.Context;

/**
 * Provides access to game settings.
 *
 * @author Timer
 */
public class Settings {
	public static final int COMBAT_STYLE = 43;
	public static final int AUTO_RETALIATE = 172;
	public static final int SPECIAL_ATTACK_ENERGY = 300;
	public static final int SPECIAL_ATTACK_ENABLED = 301;

	public static final int LOOP_MUSIC = 19;
	public static final int PRAYER_THICK_SKIN = 83;
	public static final int BANK_WITHDRAW_MODE = 115;
	public static final int TYPE_SHOP = 118;
	public static final int SCREEN_BRIGHTNESS = 166;
	public static final int MUSIC_VOLUME = 168;
	public static final int SOUND_EFFECT_VOLUME = 169;
	public static final int MOUSE_BUTTONS = 170;
	public static final int CHAT_EFFECTS = 171;
	public static final int RUN = 173;
	public static final int SPLIT_PRIVATE_CHAT = 287;
	public static final int SPECIAL_ATTACK = 301;
	public static final int BANK_REARRANGE_MODE = 304;
	public static final int ACCEPT_AID = 427;
	public static final int AREA_SOUND_VOLUME = 872;
	public static final int SWAP_QUEST_DIARY = 1002;

	/**
	 * Gets the settings array.
	 *
	 * @return An <tt>int</tt> array representing all of the settings values; otherwise <tt>new int[0]</tt>.
	 */
	public static int[] list() {
		final org.rsbot.bot.accessors.Settings settingArray = Context.get().client.getSettingArray();
		if (settingArray == null || settingArray.getData() == null) {
			return new int[0];
		}
		return settingArray.getData().clone();
	}

	/**
	 * Gets the setting at a given index.
	 *
	 * @param setting The setting index to return the value of.
	 * @return <tt>int</tt> representing the setting of the given setting id; otherwise <tt>-1</tt>.
	 */
	public static int get(final int setting) {
		final int[] settings = list();
		if (setting < settings.length) {
			return settings[setting];
		}
		return -1;
	}
}

package org.rsbot.bot.accessors;

public interface RSPlayer extends RSCharacter {
	int getLevel();

	String getName();

	int getTeam();

	RSPlayerComposite getComposite();

	int getPrayerIcon();

	int getSkullIcon();
}

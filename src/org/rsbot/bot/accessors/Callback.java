package org.rsbot.bot.accessors;

import org.rsbot.bot.Bot;
import org.rsbot.bot.BotComposite;

public interface Callback {
	public Bot getBot();

	public BotComposite getBotComposite();

	public void notifyRegion(int id, int[] keys);

	public void notifyMessage(int id, String sender, String msg);

	public void rsCharacterMoved(RSCharacter c, int i);

	public void updateRenderInfo(Render r, RenderData rd);
}

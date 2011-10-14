package org.rsbot.bot;

import org.rsbot.bot.accessors.Callback;
import org.rsbot.bot.accessors.Render;
import org.rsbot.bot.accessors.RenderData;
import org.rsbot.bot.event.CharacterMovedEvent;
import org.rsbot.bot.event.MessageEvent;

import java.util.Arrays;

public class CallbackImpl implements Callback {
	private final Bot bot;

	public CallbackImpl(final Bot bot) {
		this.bot = bot;
	}

	public Bot getBot() {
		return bot;
	}

	public BotComposite getBotComposite() {
		return bot.composite;
	}

	public void notifyRegion(int id, int[] keys) {
		System.out.println(id + " " + Arrays.toString(keys));
	}

	public void notifyMessage(final int id, final String sender, final String msg) {
		final MessageEvent m = new MessageEvent(sender, id, msg);
		bot.composite.eventManager.dispatchEvent(m);
	}

	public void rsCharacterMoved(final org.rsbot.bot.accessors.RSCharacter c, final int i) {
		final CharacterMovedEvent e = new CharacterMovedEvent(c, i);
		bot.composite.eventManager.dispatchEvent(e);
	}

	public void updateRenderInfo(final Render r, final RenderData rd) {
		bot.composite.updateRenderInfo(r, rd);
	}
}
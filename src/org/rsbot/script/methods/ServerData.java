package org.rsbot.script.methods;

import org.rsbot.bot.Context;

public class ServerData {
	public int getWorldID() {
		return Context.get().client.getWorldData().getWorldID();
	}
}

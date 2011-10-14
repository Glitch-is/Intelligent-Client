package org.rsbot.service;

import org.rsbot.bot.concurrent.LoopTask;

import java.util.List;

public interface ScriptSource {
	List<ScriptDefinition> list();

	LoopTask load(ScriptDefinition def) throws ServiceException;
}

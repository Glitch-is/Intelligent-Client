package org.rsbot.bot.event;

import org.rsbot.bot.event.handler.EventMulticaster;
import org.rsbot.bot.event.listener.MouseTargetListener;
import org.rsbot.script.wrappers.Entity;

import java.util.EventListener;

public class MouseTargetEvent extends RSEvent {
	private final Entity entity;

	public MouseTargetEvent(final Entity entity) {
		eventType = Type.CONCURRENT;
		this.entity = entity;
	}

	@Override
	public void dispatch(final EventListener el) {
		((MouseTargetListener) el).mouseTargeted(entity);
	}

	@Override
	public long getMask() {
		return EventMulticaster.MOUSE_TARGET_EVENT;
	}
}

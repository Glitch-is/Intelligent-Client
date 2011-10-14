package org.rsbot.bot.accessors;

public interface RSGround {
	RSAnimableNode getRSAnimableList();

	RSInteractable getFloorDecoration();

	RSInteractable getBoundary1();

	RSInteractable getBoundary2();

	RSInteractable getWallDecoration1();

	RSInteractable getWallDecoration2();

	RSGroundEntity getGroundObject();
}

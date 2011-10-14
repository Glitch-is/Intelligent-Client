package org.rsbot.bot.accessors;

public interface RSCharacter extends RSAnimable {
	int[] getAnimationQueue();

	Graphic[] getGraphicsData();

	int getHeight();

	int getHPRatio();

	int getInteracting();

	int[] getLocationX();

	int[] getLocationY();

	int getOrientation();

	int getLoopCycleStatus();

	RSMessageData getMessageData();

	int isMoving();

	Model getModel();

	Animator getAnimator();

	Animator getPassiveAnimator();
}

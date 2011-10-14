package org.rsbot.bot.accessors;

import org.rsbot.bot.accessors.input.Keyboard;
import org.rsbot.bot.accessors.input.Mouse;

import java.awt.*;

public interface Client {
	RSPlayer getMyRSPlayer();

	RSPlayer[] getRSPlayerArray();

	Canvas getCanvas();

	Mouse getMouse();

	Keyboard getKeyboard();

	int getBaseX();

	int getBaseY();

	int getLoopCycle();

	RSInterface[][] getRSInterfaceCache();

	Render getGameRender();

	int getScreenFactor();

	int getCamPosX();

	int getCamPosY();

	int getCamPosZ();

	int getCameraPitch();

	int getCameraYaw();

	boolean[] getValidRSInterfaceArray();

	int getLoginIndex();

	int getDestX();

	int getDestY();

	boolean isDestinationSet();

	TileData[] getTileData();

	byte[][][] getGroundByteArray();

	RSGroundData[] getRSGroundDataArray();

	RSGround[][][] getRSGroundArray();

	RSItemDefLoader getRSItemDefLoader();

	RSObjectDefLoader getRSObjectDefLoader();

	ChatLine[] getChatLines();

	NodeDeque getMenuItems();

	NodeSubQueue getCollapsedMenuItems();

	MenuGroupNode getCurrentMenuGroupNode();

	int getSubMenuX();

	int getSubMenuY();

	int getSubMenuWidth();

	int getSubMenuHeight();

	int getMenuX();

	int getMenuY();

	int getMenuWidth();

	int getMenuHeight();

	boolean isMenuCollapsed();

	boolean isMenuOpen();

	float getMinimapAngle();

	int getMinimapOffset();

	int getMinimapScale();

	int getMinimapSetting();

	String getCurrentUsername();

	String getCurrentPassword();

	DetailInfoNode getDetailInfoNode();

	Rectangle[] getRSInterfaceBoundsArray();

	HashTable getRSInterfaceNC();

	HashTable getRSItemHashTable();

	HashTable getRSNPCNC();

	int getRSNPCCount();

	int[] getRSNPCIndexArray();

	int[] getRSPlayerIndexArray();

	int getRSPlayerCount();

	Settings getSettingArray();

	int[] getSkillExperiences();

	int[] getSkillExperiencesMax();

	int[] getSkillLevelMaxes();

	int[] getSkillLevels();

	boolean isSpellSelected();

	boolean isItemSelected();

	ServerData getWorldData();

	Callback getCallback();

	int getPlane();

	RenderData getRenderData();

	int getPublicChatMode();

	int getSelfInteracting();

	int[] getCURVECOS();

	int[] getCURVESIN();

	String getSelectedItemName();

	int getMenuOptionsCount();

	int getMenuOptionsCountCollapsed();

	int getGUIRSInterfaceIndex();

	StatusNodeListLoader getRSInteractableDefListLoader();

	void setCallback(Callback cb);

	NodeDeque getProjectiles();

	int getCrosshairColor();

	int getPlayerRights();

	void setPlayerRights(int level);

	boolean isShiftClickEnabled();

	void setShiftClick(boolean enabled);
}

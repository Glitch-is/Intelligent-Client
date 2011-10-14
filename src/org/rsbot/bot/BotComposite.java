package org.rsbot.bot;

import org.rsbot.bot.accessors.Client;
import org.rsbot.bot.concurrent.handler.TaskContainer;
import org.rsbot.bot.event.handler.EventManager;
import org.rsbot.bot.input.InputManager;
import org.rsbot.script.event.handler.EventContainer;
import org.rsbot.script.methods.Calculations;
import org.rsbot.script.methods.Environment;

public class BotComposite {
	public String account;
	public Client client;
	public final InputManager inputManager;
	public final TaskContainer concurrentDispatch;
	public final EventManager eventManager;
	public EventContainer scriptEventContainer;

	public final Calculations.Render render = new Calculations.Render();
	public final Calculations.RenderData renderData = new Calculations.RenderData();

	public GameGUI gameGUI;

	public volatile boolean overrideInput = false;
	public volatile boolean disableRandoms = false;
	public volatile boolean disableAutoLogin = false;
	public volatile boolean disableRendering = false;

	/**
	 * Defines what types of input are enabled when overrideInput is false.
	 * Defaults to 'keyboard only' whenever a script is started.
	 */
	public volatile int inputFlags = Environment.INPUT_KEYBOARD | Environment.INPUT_MOUSE;

	public boolean paintMouseApplication = true;
	public double mouseMultiplier = 1D;

	public BotComposite(final Bot bot) {
		inputManager = new InputManager(bot);
		concurrentDispatch = new TaskContainer(bot);
		eventManager = new EventManager(bot.getThreadGroup());
	}

	/**
	 * Updates the rendering data. For internal use only.
	 *
	 * @param r  The client graphics toolkit.
	 * @param rd The client viewport.
	 */
	public void updateRenderInfo(final org.rsbot.bot.accessors.Render r, final org.rsbot.bot.accessors.RenderData rd) {
		if (r == null || rd == null) {
			return;
		}
		render.absoluteX1 = r.getAbsoluteX1();
		render.absoluteX2 = r.getAbsoluteX2();
		render.absoluteY1 = r.getAbsoluteY1();
		render.absoluteY2 = r.getAbsoluteY2();
		render.xMultiplier = r.getXMultiplier();
		render.yMultiplier = r.getYMultiplier();
		render.zNear = r.getZNear();
		render.zFar = r.getZFar();
		renderData.xOff = rd.getXOff();
		renderData.xX = rd.getXX();
		renderData.xY = rd.getXY();
		renderData.xZ = rd.getXZ();
		renderData.yOff = rd.getYOff();
		renderData.yX = rd.getYX();
		renderData.yY = rd.getYY();
		renderData.yZ = rd.getYZ();
		renderData.zOff = rd.getZOff();
		renderData.zX = rd.getZX();
		renderData.zY = rd.getZY();
		renderData.zZ = rd.getZZ();
	}

	public GameGUI getGameGUI() {
		return gameGUI;
	}

	public Calculations.Render getRender() {
		return render;
	}

	public Calculations.RenderData getRenderData() {
		return renderData;
	}
}

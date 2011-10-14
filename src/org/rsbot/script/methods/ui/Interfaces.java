package org.rsbot.script.methods.ui;

import org.rsbot.bot.Context;
import org.rsbot.bot.accessors.Client;
import org.rsbot.bot.accessors.RSInterface;
import org.rsbot.bot.concurrent.Task;
import org.rsbot.script.methods.input.Mouse;
import org.rsbot.script.wrappers.Interface;
import org.rsbot.script.wrappers.InterfaceComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides access to interfaces.
 *
 * @author Timer
 */
public class Interfaces {
	private static final Map<Client, Interface[]> caches = new HashMap<Client, Interface[]>();

	/**
	 * @return <code>Interface</code> array containing all valid interfaces.
	 */
	public static Interface[] getLoaded() {
		Client client = Context.get().client;
		ensureCapacity(client);
		RSInterface[][] clientCache = client.getRSInterfaceCache();
		if (clientCache == null) {
			return new Interface[0];
		}
		ArrayList<Interface> validInterfaces = new ArrayList<Interface>();
		for (int i = 0; i < clientCache.length; i++) {
			if (clientCache[i] != null) {
				Interface anInterface = get(i);
				if (anInterface.isValid()) {
					validInterfaces.add(anInterface);
				}
			}
		}
		return validInterfaces.toArray(new Interface[validInterfaces.size()]);
	}

	/**
	 * @param index The index of the interface.
	 * @return The <tt>Interface</tt> for the given index.
	 */
	public static Interface get(int index) {
		final Client client = Context.get().client;
		Interface[] cachedInterfaces = caches.get(client);
		if (cachedInterfaces == null) {
			cachedInterfaces = new Interface[100];
			caches.put(client, cachedInterfaces);
		}
		Interface anInterface;
		if (index < cachedInterfaces.length) {
			anInterface = cachedInterfaces[index];
			if (anInterface == null) {
				anInterface = new Interface(index);
				cachedInterfaces[index] = anInterface;
			}
		} else {
			anInterface = new Interface(index);
			ensureCapacity(client);
			cachedInterfaces = caches.get(client);
			if (index < cachedInterfaces.length) {
				cachedInterfaces[index] = anInterface;
			}
		}
		return anInterface;
	}

	/**
	 * @param index      The parent interface index
	 * @param childIndex The component index
	 * @return <tt>InterfaceComponent</tt> for the given index and child index.
	 */
	public static InterfaceComponent getComponent(final int index, final int childIndex) {
		return get(index).getComponent(childIndex);
	}

	/**
	 * @param id The packed interface index ((x << 16) | (y & 0xFFFF)).
	 * @return <tt>InterfaceComponent</tt> for the given interface id.
	 */
	public static InterfaceComponent getComponent(final int id) {
		final int x = id >> 16;
		final int y = id & 0xFFFF;
		return get(x).getComponent(y);
	}

	/**
	 * @return <tt>true</tt> if continue component is valid; otherwise
	 *         <tt>false</tt>.
	 */
	public static boolean canContinue() {
		return getContinueComponent() != null;
	}

	/**
	 * @return <tt>true</tt> if continue component was clicked; otherwise
	 *         <tt>false</tt>.
	 */
	public static boolean clickContinue() {
		final InterfaceComponent cont = getContinueComponent();
		return cont != null && cont.isValid() && cont.click(true);
	}

	/**
	 * @return <tt>InterfaceComponent</tt> containing "Click here to continue";
	 *         otherwise null.
	 */
	public static InterfaceComponent getContinueComponent() {
		if (Context.get().client.getRSInterfaceCache() == null) {
			return null;
		}
		final Interface[] valid = getLoaded();
		for (final Interface iface : valid) {
			if (iface.getIndex() != 137) {
				final int len = iface.getChildCount();
				for (int i = 0; i < len; i++) {
					final InterfaceComponent child = iface.getComponent(i);
					if (child.containsText("Click here to continue") && child.isValid() && child.getAbsLocation().x > 10 && child.getAbsLocation().y > 300) {
						return child;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Scrolls to the component
	 *
	 * @param component   component to scroll to
	 * @param scrollBarID scrollbar to scroll with
	 * @return true when scrolled successfully
	 */
	public static boolean scrollTo(final InterfaceComponent component, final int scrollBarID) {
		final InterfaceComponent scrollBar = getComponent(scrollBarID);
		return scrollTo(component, scrollBar);
	}

	/**
	 * Scrolls to the component
	 *
	 * @param component component to scroll to
	 * @param scrollBar scrollbar to scroll with
	 * @return true when scrolled successfully
	 */
	public static boolean scrollTo(final InterfaceComponent component, final InterfaceComponent scrollBar) {
		// Check arguments
		if (component == null || scrollBar == null || !component.isValid()) {
			return false;
		}

		if (scrollBar.getComponents().length != 6) {
			return true; // no scrollbar, so probably not scrollable
		}

		// Find scrollable area
		InterfaceComponent scrollableArea = component;
		while (scrollableArea.getScrollableContentHeight() == 0
				&& scrollableArea.getParentID() != -1) {
			scrollableArea = getComponent(scrollableArea.getParentID());
		}

		// Check scrollable area
		if (scrollableArea.getScrollableContentHeight() == 0) {
			return false;
		}

		// Get scrollable area height
		final int areaY = scrollableArea.getAbsLocation().y;
		final int areaHeight = scrollableArea.getRealHeight();

		// Check if the component is already visible
		if (component.getAbsLocation().y >= areaY && component.getAbsLocation().y <= areaY + areaHeight - component.getRealHeight()) {
			return true;
		}

		// Calculate scroll bar position to click
		final InterfaceComponent scrollBarArea = scrollBar.getComponent(0);
		final int contentHeight = scrollableArea.getScrollableContentHeight();

		int pos = (int) ((float) scrollBarArea.getRealHeight() / contentHeight * (component.getRelativeY() + Task.random(-areaHeight / 2, areaHeight / 2 - component.getRealHeight())));
		if (pos < 0) // inner
		{
			pos = 0;
		} else if (pos >= scrollBarArea.getRealHeight()) {
			pos = scrollBarArea.getRealHeight() - 1; // outer
		}

		// Click on the scrollbar
		Mouse.click(scrollBarArea.getAbsLocation().x + Task.random(0, scrollBarArea.getRealWidth()), scrollBarArea.getAbsLocation().y + pos, true);

		// Wait a bit
		Task.sleep(Task.random(200, 400));

		// Scroll to it if we missed it
		while (component.getAbsLocation().y < areaY || component.getAbsLocation().y > areaY + areaHeight - component.getRealHeight()) {
			final boolean scrollUp = component.getAbsLocation().y < areaY;
			scrollBar.getComponent(scrollUp ? 4 : 5).interact("");

			Task.sleep(Task.random(100, 200));
		}

		// Return whether or not the component is visible now.
		return component.getAbsLocation().y >= areaY && component.getAbsLocation().y <= areaY + areaHeight - component.getRealHeight();
	}

	private static void ensureCapacity(Client paramClient) {
		RSInterface[][] arrayOfWidget = paramClient.getRSInterfaceCache();
		Interface[] arrayOfWidget1 = caches.get(paramClient);
		if (arrayOfWidget1 == null) {
			arrayOfWidget1 = new Interface[100];
			caches.put(paramClient, arrayOfWidget1);
		}
		if ((arrayOfWidget != null) && (arrayOfWidget1.length < arrayOfWidget.length)) {
			caches.put(paramClient, Arrays.copyOf(arrayOfWidget1, arrayOfWidget.length));
		}
	}
}

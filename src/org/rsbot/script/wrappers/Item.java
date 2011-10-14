package org.rsbot.script.wrappers;

import org.rsbot.bot.Context;
import org.rsbot.bot.accessors.HardReference;
import org.rsbot.bot.accessors.SoftReference;
import org.rsbot.script.methods.Nodes;
import org.rsbot.script.methods.input.Mouse;
import org.rsbot.script.util.Filter;

import java.awt.*;

/**
 * Represents an item (with an id and stack size). May or may not wrap a component.
 *
 * @author Timer
 */
public class Item implements Entity {
	private static final Point M1_POINT = new Point(-1, -1);

	private final int id;
	private final int stack;
	private InterfaceComponent component;

	public Item(final int id, final int stack) {
		this.id = id;
		this.stack = stack;
	}

	public Item(final org.rsbot.bot.accessors.RSItem item) {
		id = item.getID();
		stack = item.getStackSize();
	}

	public Item(final InterfaceComponent item) {
		id = item.getComponentID();
		stack = item.getComponentStackSize();
		component = item;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean verify() {
		return component != null && getID() != -1;
	}

	/**
	 * {@inheritDoc}
	 */
	public Point getCentralPoint() {
		return component != null ? component.getCentralPoint() : M1_POINT;
	}

	/**
	 * {@inheritDoc}
	 */
	public Point getNextViewportPoint() {
		return component != null ? component.getNextViewportPoint() : M1_POINT;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean contains(final Point point) {
		return component != null && component.contains(point);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isOnScreen() {
		return component != null && component.isVisible();
	}

	/**
	 * {@inheritDoc}
	 */
	public Polygon[] getBounds() {
		return component != null ? component.getBounds() : new Polygon[0];
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hover() {
		return component != null && Mouse.moveAndApply(this, new Filter<Point>() {
			public boolean accept(final Point point) {
				return true;
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean click(final boolean left) {
		return component != null && Mouse.moveAndApply(this, new Filter<Point>() {
			public boolean accept(final Point point) {
				Mouse.click(true);
				return true;
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean interact(final String action) {
		return interact(action, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean interact(final String action, final String option) {
		return component != null && component.interact(action, option);
	}

	/**
	 * {@inheritDoc}
	 */
	public void draw(final Graphics render) {
		if (component != null) {
			component.draw(render);
		}
	}

	/**
	 * Gets the component wrapped by this Item.
	 *
	 * @return The wrapped component or <code>null</code>.
	 */
	public InterfaceComponent getComponent() {
		return component;
	}

	/**
	 * Gets this item's definition if available.
	 *
	 * @return The ItemDefinition; or <code>null</code> if unavailable.
	 */
	public ItemDefinition getDefinition() {
		try {
			final org.rsbot.bot.accessors.Node ref = Nodes.lookup(Context.get().client.getRSItemDefLoader(), id);
			if (ref != null) {
				if (ref instanceof HardReference) {
					return new ItemDefinition((org.rsbot.bot.accessors.RSItemDef) ((HardReference) ref).get());
				} else if (ref instanceof SoftReference) {
					final Object def = ((SoftReference) ref).getReference().get();

					if (def != null) {
						return new ItemDefinition((org.rsbot.bot.accessors.RSItemDef) def);
					}
				}
			}
			return null;
		} catch (final ClassCastException e) {
			return null;
		}
	}

	/**
	 * Gets this item's id.
	 *
	 * @return The id.
	 */
	public int getID() {
		return id;
	}

	/**
	 * Gets the name of this item using the wrapped component's name
	 * if available, otherwise the definition if available.
	 *
	 * @return The item's name or <code>null</code> if not found.
	 */
	public String getName() {
		if (component != null) {
			return component.getComponentName().replaceAll("\\<.*?>", "");
		} else {
			final ItemDefinition definition = getDefinition();
			if (definition != null) {
				return definition.getName().replaceAll("\\<.*?>", "");
			}
		}
		return null;
	}

	/**
	 * Gets this item's stack size.
	 *
	 * @return The stack size.
	 */
	public int getStackSize() {
		return stack;
	}

	/**
	 * Determines if this item contains the desired action
	 *
	 * @param action The item menu action to check.
	 * @return <tt>true</tt> if the item has the action; otherwise
	 *         <tt>false</tt>.
	 */
	public boolean hasAction(final String action) {
		final ItemDefinition itemDefinition = getDefinition();
		if (itemDefinition != null) {
			for (final String a : itemDefinition.getActions()) {
				if (a != null && a.equalsIgnoreCase(action)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns whether or not this item has an available definition.
	 *
	 * @return <tt>true</tt> if an item definition is available;
	 *         otherwise <tt>false</tt>.
	 */
	public boolean hasDefinition() {
		return getDefinition() != null;
	}

	/**
	 * Checks whether or not a valid component is being wrapped.
	 *
	 * @return <tt>true</tt> if there is a visible wrapped component.
	 */
	public boolean isComponentValid() {
		return component != null && component.isValid();
	}
}
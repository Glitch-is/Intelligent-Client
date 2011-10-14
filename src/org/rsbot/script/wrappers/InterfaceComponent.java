package org.rsbot.script.wrappers;

import org.rsbot.bot.Context;
import org.rsbot.bot.accessors.RSInterface;
import org.rsbot.bot.accessors.RSInterfaceNode;
import org.rsbot.bot.concurrent.Task;
import org.rsbot.script.methods.Menu;
import org.rsbot.script.methods.input.Mouse;
import org.rsbot.script.methods.ui.Interfaces;
import org.rsbot.script.util.Filter;
import org.rsbot.script.wrappers.internal.HashTable;

import java.awt.*;


/**
 * Represents an interface component. An InterfaceComponent may or
 * may not have a parent component, and will always have a
 * parent Interface.
 *
 * @author Qauters
 */
public class InterfaceComponent implements Entity {
	private static final Color TARGET_FILL_COLOR = new Color(0, 0, 0, 50);
	private static final Color TARGET_STROKE_COLOR = new Color(0, 255, 0, 150);

	/**
	 * The index of this interface in the parent. If this
	 * component does not have a parent component, this
	 * represents the index in the parent interface;
	 * otherwise this represents the component index in
	 * the parent component.
	 */
	private final int index;

	/**
	 * The parent interface containing this component.
	 */
	private final Interface parInterface;

	/**
	 * The parent component
	 */
	private final InterfaceComponent parent;

	/**
	 * Initializes the component.
	 *
	 * @param parent The parent interface.
	 * @param index  The child index of this child.
	 */
	InterfaceComponent(final Interface parent, final int index) {
		parInterface = parent;
		this.index = index;
		this.parent = null;
	}

	/**
	 * Initializes the component.
	 *
	 * @param parInterface The parent interface.
	 * @param parent       The parent component.
	 * @param index        The child index of this child.
	 */
	InterfaceComponent(final Interface parInterface, final InterfaceComponent parent, final int index) {
		this.parInterface = parInterface;
		this.parent = parent;
		this.index = index;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean verify() {
		return parInterface.isValid() && getBoundsArrayIndex() != -1;
	}

	/**
	 * {@inheritDoc}
	 */
	public Point getCentralPoint() {
		final Point p = getAbsLocation();
		final int w = getWidth();
		final int h = getHeight();
		return isValid() ? new Point((p.x * 2 + w) / 2, (p.y * 2 + h) / 2) : new Point(-1, -1);
	}

	/**
	 * {@inheritDoc}
	 */
	public Point getNextViewportPoint() {
		final Rectangle rect = getBoundingRect();
		if (rect.x == -1 || rect.y == -1 || rect.width == -1 || rect.height == -1) {
			return new Point(-1, -1);
		}
		final int min_x = rect.x + 1, min_y = rect.y + 1;
		final int max_x = min_x + rect.width - 2, max_y = min_y + rect.height - 2;

		return new Point(Task.random(min_x, max_x, rect.width / 3), Task.random(min_y, max_y, rect.height / 3));
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean contains(final Point point) {
		final Rectangle rect = getBoundingRect();
		if (rect.x == -1 || rect.y == -1 || rect.width == -1 || rect.height == -1) {
			return false;
		}
		final int min_x = rect.x + 1, min_y = rect.y + 1;
		final int max_x = min_x + rect.width - 2, max_y = min_y + rect.height - 2;
		return (point.x >= min_x) && (point.x <= max_x) && (point.y >= min_y) && (point.y <= max_y);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isOnScreen() {
		return isValid() && isVisible();
	}

	/**
	 * {@inheritDoc}
	 */
	public Polygon[] getBounds() {
		if (isValid()) {
			final Point p = getAbsLocation();
			final int w = getWidth();
			final int h = getHeight();
			final Polygon poly = new Polygon();
			poly.addPoint(p.x, p.y);
			poly.addPoint(p.x + w, p.y);
			poly.addPoint(p.x + w, p.y + h);
			poly.addPoint(p.x, p.y + h);
			return new Polygon[]{poly};
		}
		return new Polygon[0];
	}

	/**
	 * {@inheritDoc}
	 */
	public Rectangle getBoundingRect() {
		final Polygon[] polygons = getBounds();
		return polygons.length != 0 ? polygons[0].getBounds() : new Rectangle(-1, -1);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hover() {
		return isValid() && Mouse.moveAndApply(this, new Filter<Point>() {
			public boolean accept(final Point point) {
				return true;
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean click(final boolean left) {
		return isValid() && Mouse.moveAndApply(this, new Filter<Point>() {
			public boolean accept(Point point) {
				Mouse.click(left);
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
		return isValid() && Mouse.moveAndApply(this, new Filter<Point>() {
			public boolean accept(Point point) {
				return Menu.click(action, option);
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	public void draw(final Graphics render) {
		final Rectangle bounds = getBoundingRect();
		if (bounds != null) {
			render.setColor(TARGET_FILL_COLOR);
			render.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
			render.setColor(TARGET_STROKE_COLOR);
			render.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
		}
	}

	/**
	 * Checks the actions of the child for a given substring
	 *
	 * @param phrase The phrase to check for
	 * @return <tt>true</tt> if found
	 */
	public boolean containsAction(final String phrase) {
		for (final String action : getActions()) {
			if (action.toLowerCase().contains(phrase.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks the text of this component for a given substring
	 *
	 * @param phrase The phrase to check for
	 * @return <tt>true</tt> if the text contained the phrase
	 * @see #getText()
	 */
	public boolean containsText(final String phrase) {
		return getText().contains(phrase);
	}

	public Point getAbsLocation() {
		RSInterface localWidget = getInterfaceInternal();
		if (localWidget == null) {
			return new Point(-1, -1);
		}
		int k = getParentID();
		Object localObject;
		int i;
		int j;
		if (k != -1) {
			localObject = Interfaces.getComponent(k >> 16, k & 0xFFFF).getAbsLocation();
			i = ((Point) localObject).x;
			j = ((Point) localObject).y;
		} else {
			localObject = Context.get().client.getRSInterfaceBoundsArray();
			int m = localWidget.getBoundsArrayIndex();
			if ((m >= 0) && (localObject != null) && (m < ((Rectangle[]) localObject).length) && (((Rectangle[]) localObject)[m] != null)) {
				return new Point(((Rectangle[]) localObject)[m].x, ((Rectangle[]) localObject)[m].y);
			}
			i = localWidget.getMasterX();
			j = localWidget.getMasterY();
		}
		if (localWidget.getParentID() != -1) {
			localObject = Interfaces.getComponent(k >> 16, k & 0xFFFF).getInterfaceInternal();
			if ((((RSInterface) localObject).getHorizontalScrollBarSize() > 0) || (((RSInterface) localObject).getVerticalScrollBarSize() > 0)) {
				i -= ((RSInterface) localObject).getHorizontalScrollBarThumbPosition();
				j -= ((RSInterface) localObject).getVerticalScrollBarPosition();
			}
		}
		i += localWidget.getX();
		j += localWidget.getY();
		return new Point(i, j);
	}

	/**
	 * Gets the actions of this component.
	 *
	 * @return the actions or an empty array if null
	 */
	public String[] getActions() {
		final RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getActions();
		}
		return new String[0];
	}

	/**
	 * Gets the texture of this component
	 *
	 * @return the background color or -1 if null
	 */
	public int getTextureID() {
		final RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getTextureID();
		}
		return -1;
	}

	/**
	 * Gets the border thickness of this component
	 *
	 * @return the border thickness or -1 if null
	 */
	public int getBorderThickness() {
		final RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getBorderThickness();
		}
		return -1;
	}

	/**
	 * Gets the bounds array index of this component
	 *
	 * @return the bounds array index or -1 if null
	 */
	public int getBoundsArrayIndex() {
		final RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getBoundsArrayIndex();
		}

		return -1;
	}

	/**
	 * The child components (bank items etc) of this component.
	 *
	 * @return The components or RSInterfaceComponent[0] if null
	 */
	public InterfaceComponent[] getComponents() {
		final RSInterface inter = getInterfaceInternal();
		if (inter != null && inter.getComponents() != null) {
			final InterfaceComponent[] components = new InterfaceComponent[inter.getComponents().length];
			for (int i = 0; i < components.length; i++) {
				components[i] = new InterfaceComponent(parInterface, this, i);
			}
			return components;
		}
		return new InterfaceComponent[0];
	}

	/**
	 * Gets the child component at a given index
	 *
	 * @param idx The child index
	 * @return The child component, or null
	 */
	public InterfaceComponent getComponent(final int idx) {
		final InterfaceComponent[] components = getComponents();
		if (idx >= 0 && idx < components.length) {
			return components[idx];
		}
		return null;
	}

	/**
	 * Gets the id of this component
	 *
	 * @return The id of this component, or -1 if component == null
	 */
	public int getComponentID() {
		final RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getComponentID();
		}
		return -1;
	}

	/**
	 * Gets the index of this component
	 *
	 * @return The index of this component, or -1 if component == null
	 */
	public int getComponentIndex() {
		final RSInterface component = getInterfaceInternal();
		if (component != null) {
			return component.getComponentIndex();
		}

		return -1;
	}

	/**
	 * Gets the stack size of this component
	 *
	 * @return The stack size of this component, or -1 if component == null
	 */
	public int getComponentStackSize() {
		final RSInterface component = getInterfaceInternal();
		if (component != null) {
			return component.getComponentStackSize();
		}

		return -1;
	}

	/**
	 * Gets the name of this component
	 *
	 * @return The name of this component, or "" if component == null
	 */
	public String getComponentName() {
		final RSInterface component = getInterfaceInternal();
		if (component != null) {
			return component.getComponentName();
		}

		return "";
	}

	/**
	 * Gets the height of this component
	 *
	 * @return the height of this component or -1 if null
	 */
	public int getHeight() {
		if (!isInScrollableArea()) {
			return getRealHeight();
		}

		final RSInterface childInterface = getInterfaceInternal();
		if (childInterface != null) {
			return childInterface.getHeight() - 4;
		}
		return -1;
	}

	public int getID() {
		final RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getID();
		}
		return -1;

	}

	/**
	 * Returns the index of this interface in the parent.
	 * If this component does not have a parent component,
	 * this represents the index in the parent interface;
	 * otherwise this represents the component index in
	 * the parent component.
	 *
	 * @return The index of this interface.
	 * @see #getInterface()
	 * @see #getParent()
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Gets the model ID of this component
	 *
	 * @return the model ID or -1 if null
	 */
	public int getModelID() {
		final RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getModelID();
		}

		return -1;
	}

	/**
	 * Gets the model type of this component
	 *
	 * @return the model type or -1 if null
	 */
	public int getModelType() {
		final RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getModelType();
		}

		return -1;
	}

	public int getModelZoom() {
		final RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getModelZoom();
		}
		return -1;

	}

	/**
	 * Gets the parent id of this component. It will first look at the internal
	 * parentID, if that's -1 then it will search the RSInterfaceNC to find its
	 * parent.
	 *
	 * @return the parentID or -1 if none
	 */
	public int getParentID() {
		final RSInterface inter = getInterfaceInternal();
		if (inter == null) {
			return -1;
		}

		if (inter.getParentID() != -1) {
			return inter.getParentID();
		}

		final int mainID = getID() >>> 16;
		final HashTable ncI = new HashTable(Context.get().client.getRSInterfaceNC());

		for (RSInterfaceNode node = (RSInterfaceNode) ncI.getFirst(); node != null;
		     node = (RSInterfaceNode) ncI.getNext()) {
			if (mainID == node.getMainID()) {
				return (int) node.getID();
			}
		}

		return -1;
	}

	/**
	 * Gets the parent interface of this component.
	 * This component may be nested from its parent
	 * interface in parent components.
	 *
	 * @return The parent interface.
	 */
	public Interface getInterface() {
		return parInterface;
	}

	/**
	 * Gets the parent component of this component,
	 * or null if this is a top-level component.
	 *
	 * @return The parent component, or null.
	 */
	public InterfaceComponent getParent() {
		return parent;
	}

	/**
	 * Gets the relative x position of the child, calculated from the beginning
	 * of the interface
	 *
	 * @return the relative x position or -1 if null
	 */
	public int getRelativeX() {
		final RSInterface childInterface = getInterfaceInternal();
		if (childInterface != null) {
			return childInterface.getX();
		}
		return -1;
	}

	/**
	 * Gets the relative y position of the child, calculated from the beginning
	 * of the interface
	 *
	 * @return the relative y position -1 if null
	 */
	public int getRelativeY() {
		final RSInterface childInterface = getInterfaceInternal();
		if (childInterface != null) {
			return childInterface.getY();
		}
		return -1;
	}

	public int getVerticalScrollPosition() {
		final RSInterface childInterface = getInterfaceInternal();
		if (childInterface != null) {
			return childInterface.getVerticalScrollBarPosition();
		}
		return -1;
	}

	public int getHorizontalScrollPosition() {
		final RSInterface childInterface = getInterfaceInternal();
		if (childInterface != null) {
			return childInterface.getHorizontalScrollBarThumbPosition();
		}
		return -1;
	}

	public int getScrollableContentHeight() {
		final RSInterface childInterface = getInterfaceInternal();
		if (childInterface != null) {
			return childInterface.getVerticalScrollBarSize();
		}
		return -1;
	}

	public int getScrollableContentWidth() {
		final RSInterface childInterface = getInterfaceInternal();
		if (childInterface != null) {
			return childInterface.getHorizontalScrollBarSize();
		}
		return -1;
	}

	public int getRealHeight() {
		final RSInterface childInterface = getInterfaceInternal();
		if (childInterface != null) {
			return childInterface.getVerticalScrollBarThumbSize();
		}
		return -1;
	}

	public int getRealWidth() {
		final RSInterface childInterface = getInterfaceInternal();
		if (childInterface != null) {
			return childInterface.getHorizontalScrollBarThumbSize();
		}
		return -1;
	}

	public boolean isInScrollableArea() {
		//Check if we have a parent
		if (getParentID() == -1) {
			return false;
		}

		//Find scrollable area
		InterfaceComponent scrollableArea = Interfaces.getComponent(getParentID());
		while (scrollableArea.getScrollableContentHeight() == 0 && scrollableArea.getParentID() != -1) {
			scrollableArea = Interfaces.getComponent(scrollableArea.getParentID());
		}

		//Return if we are in a scrollable area
		return scrollableArea.getScrollableContentHeight() != 0;
	}

	/**
	 * Gets the selected action name of this component
	 *
	 * @return the selected action name or "" if null
	 */
	public String getSelectedActionName() {
		final RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getSelectedActionName();
		}
		return "";
	}

	public int getShadowColor() {
		final RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getShadowColor();
		}
		return -1;

	}

	public int getSpecialType() {
		final RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getSpecialType();
		}

		return -1;
	}

	/**
	 * Gets the spell name of this component
	 *
	 * @return the spell name or "" if null
	 */
	public String getSpellName() {
		final RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getSpellName();
		}
		return "";
	}

	/**
	 * Gets the text of this component
	 *
	 * @return the text or "" if null
	 */
	public String getText() {
		final RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getText();
		}
		return "";
	}

	/**
	 * Gets the text color of this component
	 *
	 * @return the text color or -1 if null
	 */
	public int getTextColor() {
		final RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getTextColor();
		}
		return -1;
	}

	/**
	 * Gets the tooltip of this component
	 *
	 * @return the tooltip or "" if null
	 */
	public String getTooltip() {
		final RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getToolTip();
		}
		return "";
	}

	/**
	 * Gets the type of this component
	 *
	 * @return the type or -1 if null
	 */
	public int getType() {
		final RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getType();
		}
		return -1;
	}

	/**
	 * Gets the value index array of this component
	 *
	 * @return the value index array or new int[0][0] if null
	 */
	public int[][] getValueIndexArray() {
		final RSInterface childInterface = getInterfaceInternal();
		if (childInterface != null) {
			final int[][] vindex = childInterface.getValueIndexArray();
			if (vindex != null) { // clone does NOT deep copy
				final int[][] out = new int[vindex.length][0];
				for (int i = 0; i < vindex.length; i++) {
					final int[] cur = vindex[i];
					if (cur != null) {
						// clone, otherwise you have a pointer
						out[i] = cur.clone();
					}
				}
				return out;
			}
		}
		return new int[0][0];
	}

	/**
	 * Gets the width of this component
	 *
	 * @return the width of the component or -1 if null
	 */
	public int getWidth() {
		if (!isInScrollableArea()) {
			return getRealWidth();
		}

		final RSInterface childInterface = getInterfaceInternal();
		if (childInterface != null) {
			return childInterface.getWidth() - 4;
		}
		return -1;
	}

	/**
	 * Gets the xRotation of this component
	 *
	 * @return xRotation of this component
	 */
	public int getXRotation() {
		final RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getXRotation();
		}
		return -1;

	}

	/**
	 * Gets the yRotation of this component
	 *
	 * @return yRotation of this component
	 */
	public int getYRotation() {
		final RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getYRotation();
		}
		return -1;

	}

	/**
	 * Gets the zRotation of this component
	 *
	 * @return zRotation of this component
	 */
	public int getZRotation() {
		final RSInterface inter = getInterfaceInternal();
		if (inter != null) {
			return inter.getZRotation();
		}
		return -1;
	}

	/**
	 * Determines whether or not this component is
	 * vertically flipped.
	 *
	 * @return <tt>true</tt> if this component is vertically flipped.
	 */
	public boolean isVerticallyFlipped() {
		final RSInterface inter = getInterfaceInternal();
		return inter != null && inter.isVerticallyFlipped();
	}

	/**
	 * Determines whether or not this component is
	 * horizontally flipped.
	 *
	 * @return <tt>true</tt> if this component is horizontally flipped.
	 */
	public boolean isHorizontallyFlipped() {
		final RSInterface inter = getInterfaceInternal();
		return inter != null && inter.isHorizontallyFlipped();
	}

	/**
	 * Whether or not this child is an inventory interface
	 *
	 * @return <tt>true</tt> if it's an inventory interface
	 */
	public boolean isInventory() {
		final RSInterface inter = getInterfaceInternal();
		return inter != null && inter.isInventoryRSInterface();
	}

	/**
	 * Determines if this interface is hidden.
	 *
	 * @return <tt>true</tt> if this interface is visible; otherwise <tt>false</tt>.
	 */
	public boolean isVisible() {
		final RSInterface inter = getInterfaceInternal();
		return inter != null && !inter.isHidden();
	}

	/**
	 * Determines whether or not this component is loaded for display.
	 *
	 * @return whether or not the component is valid
	 */
	public boolean isValid() {
		return verify();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof InterfaceComponent) {
			final InterfaceComponent child = (InterfaceComponent) obj;
			return index == child.index && child.parInterface.equals(parInterface);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return parInterface.getIndex() * 31 + index;
	}

	/**
	 * @return The interface represented by this object.
	 */
	RSInterface getInterfaceInternal() {
		if (parent != null) {
			final RSInterface p = parent.getInterfaceInternal();
			if (p != null) {
				final RSInterface[] components = p.getComponents();
				if (components != null && index >= 0 && index < components.length) {
					return components[index];
				}
			}
		} else {
			final RSInterface[] children = parInterface.getChildrenInternal();
			if (children != null && index < children.length) {
				return children[index];
			}
		}
		return null;
	}
}

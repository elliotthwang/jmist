/**
 *
 */
package org.jmist.packages.light;

import java.util.ArrayList;
import java.util.List;

import org.jmist.framework.Light;

/**
 * @author bkimmel
 *
 */
public abstract class CompositeLight implements Light {

	/**
	 * Adds a child <code>Light</code> to this <code>CompositeLight</code>.
	 * @param child The child <code>Light</code> to add.
	 * @return A reference to this <code>CompositeLight</code> so that calls to
	 * 		this method may be chained.
	 */
	public CompositeLight addChild(Light child) {
		this.children.add(child);
		return this;
	}

	/**
	 * Gets the list of child lights.
	 * @return The <code>List</code> of child lights.
	 */
	protected final List<Light> children() {
		return this.children;
	}

	/** The child lights. */
	private final List<Light> children = new ArrayList<Light>();

}

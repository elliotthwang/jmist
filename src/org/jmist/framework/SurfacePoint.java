/**
 *
 */
package org.jmist.framework;

import org.jmist.toolkit.*;

/**
 * @author bkimmel
 *
 */
public interface SurfacePoint {

	Point3 location();
	Vector3 normal();
	Vector3 microfacetNormal();

	Vector3 tangent();

	Material material();

	Medium mediumAbove();
	Medium mediumBelow();

	Point2 textureCoordinates();

}
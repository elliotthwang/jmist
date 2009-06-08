/**
 *
 */
package ca.eandb.jmist.framework;

import ca.eandb.jmist.math.Ray3;

/**
 * @author Brad Kimmel
 *
 */
public interface Geometry extends Bounded3, VisibilityFunction3, PartialBoundable3 {

	void intersect(Ray3 ray, IntersectionRecorder recorder);

//	WeightedSurfacePoint generateRandomSurfacePoint();
//
	boolean isClosed();

}
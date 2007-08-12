package org.jmist.framework;

/**
 * Accepts a series of intersections.
 * @author bkimmel
 */
public interface IIntersectionRecorder {

	/**
	 * Record an intersection.
	 * @param intersection The intersection to record.
	 */
	void record(IIntersection intersection);
	
	/**
	 * Indicates whether all intersections are required or
	 * just the nearest one.
	 * @return A value indicating whether all intersections
	 * 		should be recorded.
	 */
	boolean needAllIntersections();
	
}

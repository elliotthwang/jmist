/**
 *
 */
package ca.eandb.jmist.framework.lens;

import ca.eandb.jmist.framework.ScatteredRay;
import ca.eandb.jmist.framework.color.Color;
import ca.eandb.jmist.framework.path.EyeNode;
import ca.eandb.jmist.framework.path.EyeTerminalNode;
import ca.eandb.jmist.framework.path.PathInfo;
import ca.eandb.jmist.math.Basis3;
import ca.eandb.jmist.math.HPoint3;
import ca.eandb.jmist.math.Point2;
import ca.eandb.jmist.math.Point3;
import ca.eandb.jmist.math.Ray3;
import ca.eandb.jmist.math.SphericalCoordinates;
import ca.eandb.jmist.math.Vector3;

/**
 * A <code>Lens</code> that applies a
 * <a href="http://en.wikipedia.org/wiki/Lambert_cylindrical_equal-area_projection">Lambert
 * cylindrical projection</a> to incident radiance.
 *  
 * @author Brad Kimmel
 */
public final class LambertCylindricalLens extends AbstractLens {

	/** Serialization version ID. */
	private static final long serialVersionUID = -4366154660419656383L;

	/** The <code>Basis3</code> to use for conversion from spherical coordinates. */
	private static final Basis3 BASIS = Basis3.fromUV(Vector3.K,
			Vector3.NEGATIVE_I, Basis3.Orientation.LEFT_HANDED);

	/* (non-Javadoc)
	 * @see ca.eandb.jmist.framework.Lens#sample(ca.eandb.jmist.math.Point2, ca.eandb.jmist.framework.path.PathInfo, double, double, double)
	 */
	@Override
	public EyeNode sample(Point2 p, PathInfo pathInfo, double ru, double rv, double rj) {
		return new Node(p, pathInfo, ru, rv, rj);
	}

	/**
	 * An <code>EyeNode</code> generated by a <code>PinholeCamera</code>.
	 */
	private final class Node extends EyeTerminalNode {

		/** Projected point on the image plane. */
		private final Point2 pointOnImagePlane;

		/**
		 * Creates a <code>Node</code>.
		 * @param pointOnImagePlane The <code>Point2</code> on the image plane.
		 * @param pathInfo The <code>PathInfo</code> describing the context for
		 * 		this node.
		 */
		public Node(Point2 pointOnImagePlane, PathInfo pathInfo, double ru, double rv, double rj) {
			super(pathInfo, ru, rv, rj);
			this.pointOnImagePlane = pointOnImagePlane;
		}

		/* (non-Javadoc)
		 * @see ca.eandb.jmist.framework.path.EyeNode#project(ca.eandb.jmist.math.HPoint3)
		 */
		@Override
		public Point2 project(HPoint3 q) {
			Vector3 v = q.isPoint() ? q.toPoint3().unitVectorFromOrigin()
					                : q.toVector3().unit();
			SphericalCoordinates sc = SphericalCoordinates.fromCartesian(v,
					BASIS);
			double x = sc.azimuthal() / (2.0 * Math.PI);
			double y = 0.5 * (1.0 - Math.cos(sc.polar()));
			if (x < 0.0) {
				x += 1.0;
			}
			return new Point2(x, y);
		}

		/* (non-Javadoc)
		 * @see ca.eandb.jmist.framework.path.PathNode#getCosine(ca.eandb.jmist.math.Vector3)
		 */
		@Override
		public double getCosine(Vector3 v) {
			return 1.0;
		}

		/* (non-Javadoc)
		 * @see ca.eandb.jmist.framework.path.PathNode#getPDF()
		 */
		@Override
		public double getPDF() {
			return 1.0;
		}

		/* (non-Javadoc)
		 * @see ca.eandb.jmist.framework.path.PathNode#getPDF(ca.eandb.jmist.math.Vector3)
		 */
		@Override
		public double getPDF(Vector3 v) {
			return 1.0 / (4.0 * Math.PI);
		}

		/* (non-Javadoc)
		 * @see ca.eandb.jmist.framework.path.PathNode#getPosition()
		 */
		@Override
		public HPoint3 getPosition() {
			return Point3.ORIGIN;
		}

		/* (non-Javadoc)
		 * @see ca.eandb.jmist.framework.path.PathNode#isSpecular()
		 */
		@Override
		public boolean isSpecular() {
			return true;
		}

		/* (non-Javadoc)
		 * @see ca.eandb.jmist.framework.path.PathNode#sample(double, double, double)
		 */
		@Override
		public ScatteredRay sample(double ru, double rv, double rj) {
			Point2 p = pointOnImagePlane;
			SphericalCoordinates v = new SphericalCoordinates(
					Math.acos(1.0 - 2.0 * p.y()),
					2.0 * Math.PI * p.x());
			Ray3 ray = new Ray3(Point3.ORIGIN, v.toCartesian(BASIS));
			Color color = getWhite();
			double pdf = 1.0 / (4.0 * Math.PI);

			return ScatteredRay.diffuse(ray, color, pdf);
		}

		/* (non-Javadoc)
		 * @see ca.eandb.jmist.framework.path.PathNode#scatter(ca.eandb.jmist.math.Vector3)
		 */
		@Override
		public Color scatter(Vector3 v) {
			return getGray(getPDF(v));
		}

	}

}

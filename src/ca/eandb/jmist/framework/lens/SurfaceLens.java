/**
 *
 */
package ca.eandb.jmist.framework.lens;

import ca.eandb.jmist.framework.Random;
import ca.eandb.jmist.framework.ScatteredRay;
import ca.eandb.jmist.framework.SceneElement;
import ca.eandb.jmist.framework.ShadingContext;
import ca.eandb.jmist.framework.color.Color;
import ca.eandb.jmist.framework.path.EyeNode;
import ca.eandb.jmist.framework.path.EyeTerminalNode;
import ca.eandb.jmist.framework.path.PathInfo;
import ca.eandb.jmist.framework.random.RandomUtil;
import ca.eandb.jmist.framework.shader.MinimalShadingContext;
import ca.eandb.jmist.math.Basis3;
import ca.eandb.jmist.math.HPoint3;
import ca.eandb.jmist.math.Point2;
import ca.eandb.jmist.math.Point3;
import ca.eandb.jmist.math.Ray3;
import ca.eandb.jmist.math.Vector3;

/**
 * A camera <code>Lens</code> that captures all light incident on the geometry
 * associated with a given <code>SceneElement</code>.
 * @author Brad Kimmel
 */
public final class SurfaceLens extends AbstractLens {

	/** Serialization version ID. */
	private static final long serialVersionUID = -7507678606010224670L;
	
	/** The <code>SceneElement</code> from which rays are emitted. */
	private final SceneElement e;

	/**
	 * Creates a new <code>SurfaceLens</code>.
	 * @param e The <code>SceneElement</code> to capture light.
	 */
	public SurfaceLens(SceneElement e) {
		this.e = e;
	}

	/* (non-Javadoc)
	 * @see ca.eandb.jmist.framework.Lens#sample(ca.eandb.jmist.math.Point2, ca.eandb.jmist.framework.path.PathInfo, ca.eandb.jmist.framework.Random)
	 */
	public EyeNode sample(Point2 p, PathInfo pathInfo, double ru, double rv, double rj) {
		return new Node(p, pathInfo, ru, rv, rj);
	}

	/**
	 * An <code>EyeNode</code> generated by a <code>SurfaceLens</code>.
	 */
	private final class Node extends EyeTerminalNode {

		/** Projected point on the image plane. */
		private final Point2 pointOnImagePlane;
		
		/** The <code>ShadingContext</code> for this node. */
		private final ShadingContext context = new MinimalShadingContext(Random.DEFAULT);

		/**
		 * Creates a <code>Node</code>.
		 * @param pointOnImagePlane The <code>Point2</code> on the image plane.
		 * @param pathInfo The <code>PathInfo</code> describing the context for
		 * 		this node.
		 */
		public Node(Point2 pointOnImagePlane, PathInfo pathInfo, double ru, double rv, double rj) {
			super(pathInfo, ru, rv, rj);
			this.pointOnImagePlane = pointOnImagePlane;
			e.generateRandomSurfacePoint(context, pointOnImagePlane.x(), pointOnImagePlane.y(), 0.0);
		}

		/* (non-Javadoc)
		 * @see ca.eandb.jmist.framework.path.EyeNode#sample(ca.eandb.jmist.math.Point2, ca.eandb.jmist.framework.Random)
		 */
		public ScatteredRay sample(double ru, double rv, double rj) {
			Point3 o = context.getPosition();
			Vector3 n = context.getNormal();
			Vector3 v = RandomUtil.diffuse(ru, rv).toCartesian(Basis3.fromW(n));
			Ray3 ray = new Ray3(o, v);
			Color color = getWhite();
			return ScatteredRay.diffuse(ray, color, 1.0 / Math.PI);
		}

		/* (non-Javadoc)
		 * @see ca.eandb.jmist.framework.path.PathNode#scatterTo(ca.eandb.jmist.framework.path.PathNode)
		 */
		public Color scatter(Vector3 v) {
			return getGray(getPDF(v));
		}

		/* (non-Javadoc)
		 * @see ca.eandb.jmist.framework.path.EyeNode#project(ca.eandb.jmist.math.HPoint3)
		 */
		public Point2 project(HPoint3 x) {
			return pointOnImagePlane;
		}

		/* (non-Javadoc)
		 * @see ca.eandb.jmist.framework.path.PathNode#getCosine(ca.eandb.jmist.math.Vector3)
		 */
		public double getCosine(Vector3 v) {
			Vector3 n = context.getNormal();
			return n.dot(v);
		}

		/* (non-Javadoc)
		 * @see ca.eandb.jmist.framework.path.PathNode#getPosition()
		 */
		public HPoint3 getPosition() {
			return context.getPosition();
		}

		/* (non-Javadoc)
		 * @see ca.eandb.jmist.framework.path.PathNode#getPDF()
		 */
		public double getPDF() {
			return 1.0 / e.getSurfaceArea();
		}

		/* (non-Javadoc)
		 * @see ca.eandb.jmist.framework.path.PathNode#isSpecular()
		 */
		public boolean isSpecular() {
			return false;
		}

		/* (non-Javadoc)
		 * @see ca.eandb.jmist.framework.path.PathNode#getPDF(ca.eandb.jmist.math.Vector3)
		 */
		public double getPDF(Vector3 v) {
			Vector3 n = context.getNormal();
			return (n.dot(v) > 0.0) ? 1.0 / Math.PI : 0.0;
		}

	}

}

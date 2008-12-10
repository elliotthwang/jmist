/**
 *
 */
package ca.eandb.jmist.packages.geometry.primitive;

import java.util.Arrays;

import ca.eandb.jmist.framework.IntersectionRecorder;
import ca.eandb.jmist.framework.Material;
import ca.eandb.jmist.framework.SingleMaterialGeometry;
import ca.eandb.jmist.toolkit.Basis3;
import ca.eandb.jmist.toolkit.Box3;
import ca.eandb.jmist.toolkit.Point2;
import ca.eandb.jmist.toolkit.Point3;
import ca.eandb.jmist.toolkit.Polynomial;
import ca.eandb.jmist.toolkit.Ray3;
import ca.eandb.jmist.toolkit.Sphere;
import ca.eandb.jmist.toolkit.Vector3;

/**
 * A torus primitive <code>Geometry</code>.
 * @author Brad Kimmel
 */
public final class TorusGeometry extends SingleMaterialGeometry {

	/**
	 * Creates a new <code>TorusGeometry</code>.
	 * @param major The major radius of the torus (i.e., the distance from the
	 * 		center of the torus to a point in the center of the tube.
	 * @param minor The minor radius of the torus (i.e., the radius of the
	 * 		tube).
	 * @param material The <code>Material</code> to apply to this
	 * 		<code>TorusGeometry</code>.
	 */
	public TorusGeometry(double major, double minor, Material material) {
		super(material);
		this.major = major;
		this.minor = minor;
	}

	/* (non-Javadoc)
	 * @see ca.eandb.jmist.framework.Geometry#intersect(ca.eandb.jmist.toolkit.Ray3, ca.eandb.jmist.framework.IntersectionRecorder)
	 */
	public void intersect(Ray3 ray, IntersectionRecorder recorder) {

		Vector3		orig			= ray.origin().vectorFrom(Point3.ORIGIN);
		Vector3		dir				= ray.direction().unit();
		double		sqRadius1		= major * major;
		double		sqRadius2		= minor * minor;
		double		s2NormOfDir		= dir.squaredLength();
		double		s2NormOfOrig	= orig.squaredLength();
		double		dirDotOrig		= dir.dot(orig);
		double		K				= s2NormOfOrig - (sqRadius1 + sqRadius2);

		Polynomial	f = new Polynomial(
							K * K - 4.0 * sqRadius1 * (sqRadius2 - orig.y() * orig.y()),
							4.0 * dirDotOrig * (s2NormOfOrig - (sqRadius1 + sqRadius2)) + 8.0 * sqRadius1 * dir.y() * orig.y(),
							2.0 * s2NormOfDir * (s2NormOfOrig - (sqRadius1 + sqRadius2)) + 4.0 * ((dirDotOrig * dirDotOrig) + sqRadius1 * dir.y() * dir.y()),
							4.0 * dirDotOrig * s2NormOfDir,
							s2NormOfDir * s2NormOfDir
					);

		double[]	x = f.roots();

		if (x.length > 1)
		{
			Arrays.sort(x);
			for (int i = 0; i < x.length; i++)
				recorder.record(super.newIntersection(ray, x[i], i % 2 == 0));
		}

	}

	/* (non-Javadoc)
	 * @see ca.eandb.jmist.framework.AbstractGeometry#getNormal(ca.eandb.jmist.framework.AbstractGeometry.GeometryIntersection)
	 */
	@Override
	protected Vector3 getNormal(GeometryIntersection x) {

		Point3	p = x.location();
		Vector3	rel = new Vector3(p.x(), 0.0, p.z());

		double	length = rel.length();

		if (length > 0.0)
		{
			rel = rel.times(major / length);
			return p.vectorFrom(Point3.ORIGIN.plus(rel));
		}
		else
			return Vector3.K;

	}

	/* (non-Javadoc)
	 * @see ca.eandb.jmist.framework.AbstractGeometry#getBasis(ca.eandb.jmist.framework.AbstractGeometry.GeometryIntersection)
	 */
	@Override
	protected Basis3 getBasis(GeometryIntersection x) {

		Point3	p	= x.location();
		Vector3	u	= new Vector3(-p.z(), 0.0, p.x()).unit();

		return Basis3.fromWU(x.normal(), u, Basis3.Orientation.RIGHT_HANDED);

	}

	/* (non-Javadoc)
	 * @see ca.eandb.jmist.framework.AbstractGeometry#getTextureCoordinates(ca.eandb.jmist.framework.AbstractGeometry.GeometryIntersection)
	 */
	@Override
	protected Point2 getTextureCoordinates(GeometryIntersection x) {

		Vector3	cp	= x.location().vectorFrom(Point3.ORIGIN);
		Vector3	R	= new Vector3(cp.x(), 0.0, cp.z()).unit();
		Vector3	r	= cp.minus(R.times(major)).unit();

		return new Point2(
			(Math.PI + Math.atan2(-cp.z(), cp.x())) / (2.0 * Math.PI),
			(Math.PI + Math.atan2(cp.y(), R.dot(r))) / (2.0 * Math.PI)
		);

	}

	/* (non-Javadoc)
	 * @see ca.eandb.jmist.framework.Geometry#isClosed()
	 */
	public boolean isClosed() {
		return true;
	}

	/* (non-Javadoc)
	 * @see ca.eandb.jmist.framework.Bounded3#boundingBox()
	 */
	public Box3 boundingBox() {
		return new Box3(
				-(major + minor), -minor, -(major + minor),
				  major + minor ,  minor,   major + minor
		);
	}

	/* (non-Javadoc)
	 * @see ca.eandb.jmist.framework.Bounded3#boundingSphere()
	 */
	public Sphere boundingSphere() {
		return new Sphere(Point3.ORIGIN, major + minor);
	}

	/**
	 * The major radius of the torus (i.e., the distance from the center of the
	 * torus to a point in the center of the tube.
	 */
	private final double major;

	/**
	 * The minor radius of the torus (i.e., the radius of the tube).
	 */
	private final double minor;

}

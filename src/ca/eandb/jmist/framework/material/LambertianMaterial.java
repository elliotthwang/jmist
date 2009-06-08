/**
 *
 */
package ca.eandb.jmist.framework.material;

import java.io.Serializable;

import ca.eandb.jmist.framework.Intersection;
import ca.eandb.jmist.framework.Painter;
import ca.eandb.jmist.framework.ScatterRecorder;
import ca.eandb.jmist.framework.ScatterResult;
import ca.eandb.jmist.framework.SurfacePoint;
import ca.eandb.jmist.framework.color.Color;
import ca.eandb.jmist.framework.color.ColorModel;
import ca.eandb.jmist.math.RandomUtil;
import ca.eandb.jmist.math.Ray3;
import ca.eandb.jmist.math.SphericalCoordinates;
import ca.eandb.jmist.math.Vector3;

/**
 * A <code>Material</code> that reflects light equally in all directions in
 * the upper hemisphere.
 * @author Brad Kimmel
 */
public final class LambertianMaterial extends OpaqueMaterial implements
		Serializable {

	/**
	 * Creates a new <code>LambertianMaterial</code> that does not emit light.
	 * @param reflectance The reflectance <code>Painter</code>.
	 */
	public LambertianMaterial(Painter reflectance) {
		this(reflectance, null);
	}

	/**
	 * Creates a new <code>LambertianMaterial</code> that emits light.
	 * @param reflectance The reflectance <code>Painter</code>.
	 * @param emittance The emission <code>Painter</code>.
	 */
	public LambertianMaterial(Painter reflectance, Painter emittance) {
		this.reflectance = reflectance;
		this.emittance = emittance;
	}

	/* (non-Javadoc)
	 * @see ca.eandb.jmist.framework.AbstractMaterial#isEmissive()
	 */
	@Override
	public boolean isEmissive() {
		return (this.emittance != null);
	}

	/* (non-Javadoc)
	 * @see ca.eandb.jmist.framework.material.AbstractMaterial#emission(ca.eandb.jmist.framework.SurfacePoint, ca.eandb.jmist.math.Vector3)
	 */
	@Override
	public Color emission(SurfacePoint x, Vector3 out) {

		if (this.emittance == null || x.normal().dot(out) < 0.0) {
			return ColorModel.getInstance().getBlack();
		}

		double ndotv = x.microfacetNormal().dot(out);
		return ndotv > 0.0 ? emittance.getColor(x).times(ndotv) : ColorModel.getInstance().getBlack();

	}

	/* (non-Javadoc)
	 * @see ca.eandb.jmist.framework.material.AbstractMaterial#emit(ca.eandb.jmist.framework.SurfacePoint, ca.eandb.jmist.framework.ScatterRecorder)
	 */
	@Override
	public void emit(SurfacePoint x, ScatterRecorder recorder) {

		if (this.emittance != null) {

			SphericalCoordinates out = RandomUtil.uniformOnUpperHemisphere();
			Ray3 ray = new Ray3(x.location(), out.toCartesian(x.microfacetBasis()));

			if (x.normal().dot(ray.direction()) > 0.0) {
				recorder.record(ScatterResult.diffuse(ray, emittance.getColor(x)));
			}

		}

	}

	/* (non-Javadoc)
	 * @see ca.eandb.jmist.framework.material.AbstractMaterial#scatter(ca.eandb.jmist.framework.Intersection, ca.eandb.jmist.framework.ScatterRecorder)
	 */
	@Override
	public void scatter(Intersection x, ScatterRecorder recorder) {

		if (this.reflectance != null) {

			SphericalCoordinates out = RandomUtil.diffuse();
			Ray3 ray = new Ray3(x.location(), out.toCartesian(x.microfacetBasis()));

			if (ray.direction().dot(x.normal()) > 0.0) {
				recorder.record(ScatterResult.diffuse(ray, reflectance.getColor(x)));
			}

		}

	}

	/* (non-Javadoc)
	 * @see ca.eandb.jmist.framework.material.AbstractMaterial#scattering(ca.eandb.jmist.framework.Intersection, ca.eandb.jmist.math.Vector3)
	 */
	@Override
	public Color scattering(Intersection x, Vector3 out) {

		boolean toFront = (x.normal().dot(out) > 0.0);

		if (this.reflectance != null && x.front() == toFront) {
			return reflectance.getColor(x);
		} else {
			return ColorModel.getInstance().getBlack();
		}

	}

	/** The reflectance <code>Painter</code> of this <code>Material</code>. */
	private final Painter reflectance;

	/** The emittance <code>Painter</code> of this <code>Material</code>. */
	private final Painter emittance;

	/**
	 * Serialization version ID.
	 */
	private static final long serialVersionUID = 485410070543495668L;

}
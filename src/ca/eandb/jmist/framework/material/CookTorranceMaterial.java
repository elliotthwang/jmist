/**
 *
 */
package ca.eandb.jmist.framework.material;

import ca.eandb.jmist.framework.Medium;
import ca.eandb.jmist.framework.ScatteredRayRecorder;
import ca.eandb.jmist.framework.SurfacePoint;
import ca.eandb.jmist.framework.color.Color;
import ca.eandb.jmist.framework.color.ColorModel;
import ca.eandb.jmist.math.Optics;
import ca.eandb.jmist.math.Point3;
import ca.eandb.jmist.math.Ray3;
import ca.eandb.jmist.math.Vector3;

/**
 * @author Brad
 *
 */
public final class CookTorranceMaterial extends AbstractMaterial {

	private final double mSquared;

	private final Color n;

	private final Color k;

	public CookTorranceMaterial(double m, Color n, Color k) {
		this.mSquared = m * m;
		this.n = n;
		this.k = k;
	}

	/* (non-Javadoc)
	 * @see ca.eandb.jmist.framework.material.AbstractMaterial#scatter(ca.eandb.jmist.framework.SurfacePoint, ca.eandb.jmist.math.Vector3, ca.eandb.jmist.framework.ScatteredRayRecorder)
	 */
	@Override
	public void scatter(SurfacePoint x, Vector3 v, ScatteredRayRecorder recorder) {
		// TODO Auto-generated method stub
		super.scatter(x, v, recorder);
	}

	/* (non-Javadoc)
	 * @see ca.eandb.jmist.framework.material.AbstractMaterial#scattering(ca.eandb.jmist.framework.SurfacePoint, ca.eandb.jmist.math.Vector3, ca.eandb.jmist.math.Vector3)
	 */
	@Override
	public Color scattering(SurfacePoint x, Vector3 in, Vector3 out) {
		Vector3		E = in.opposite();
		Vector3		L = in;
		Vector3		H = E.plus(E).times(0.5).unit();
		Vector3		N = x.getShadingNormal();
		double		HdotN = H.dot(N);
		double		EdotH = E.dot(H);
		double		EdotN = E.dot(N);
		double		LdotN = L.dot(N);
		double		tanAlpha = Math.tan(Math.acos(HdotN));
		double		cos4Alpha = HdotN * HdotN * HdotN * HdotN;

		Medium		medium = x.getAmbientMedium();
		Color		n1 = medium.refractiveIndex(x.getPosition());
		Color		k1 = medium.extinctionIndex(x.getPosition());
		Color		F = Optics.reflectance(E, N, n1, k1, n, k);
		double		D = Math.exp(-(tanAlpha * tanAlpha / mSquared)) / (4.0 * mSquared * cos4Alpha);
		double		G = Math.min(1.0, Math.min(2.0 * HdotN * EdotN / EdotH, 2.0 * HdotN * LdotN / EdotH));

		return F.times(D * G / EdotN);
	}

	/* (non-Javadoc)
	 * @see ca.eandb.jmist.framework.Medium#extinctionIndex(ca.eandb.jmist.math.Point3)
	 */
	@Override
	public Color extinctionIndex(Point3 p) {
		return k;
	}

	/* (non-Javadoc)
	 * @see ca.eandb.jmist.framework.Medium#refractiveIndex(ca.eandb.jmist.math.Point3)
	 */
	@Override
	public Color refractiveIndex(Point3 p) {
		return n;
	}

	/* (non-Javadoc)
	 * @see ca.eandb.jmist.framework.Medium#transmittance(ca.eandb.jmist.math.Ray3, double)
	 */
	@Override
	public Color transmittance(Ray3 ray, double distance) {
		return ColorModel.getInstance().getBlack();
	}

}

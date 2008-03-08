/**
 *
 */
package org.jmist.framework;

import org.jmist.toolkit.Point3;
import org.jmist.toolkit.Ray3;

/**
 * @author bkimmel
 *
 */
public interface Medium {

	Spectrum transmittance(Ray3 ray, double distance);
	Spectrum refractiveIndex(Point3 p);
	Spectrum extinctionIndex(Point3 p);

	/**
	 * A vacuum <code>Medium</code>.
	 */
	public static final Medium VACUUM = new Medium() {

		/* (non-Javadoc)
		 * @see org.jmist.framework.Medium#extinctionIndex(org.jmist.toolkit.Point3)
		 */
		public Spectrum extinctionIndex(Point3 p) {
			return Spectrum.ZERO;
		}

		/* (non-Javadoc)
		 * @see org.jmist.framework.Medium#refractiveIndex(org.jmist.toolkit.Point3)
		 */
		public Spectrum refractiveIndex(Point3 p) {
			return Spectrum.ONE;
		}

		/* (non-Javadoc)
		 * @see org.jmist.framework.Medium#transmittance(org.jmist.toolkit.Ray3, double)
		 */
		public Spectrum transmittance(Ray3 ray, double distance) {
			return Spectrum.ONE;
		}

	};

}

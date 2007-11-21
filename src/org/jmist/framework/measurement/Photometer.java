/**
 *
 */
package org.jmist.framework.measurement;

import org.jmist.framework.Intersection;
import org.jmist.framework.Material;
import org.jmist.framework.Medium;
import org.jmist.framework.ScatterResult;
import org.jmist.framework.Spectrum;
import org.jmist.framework.reporting.DummyProgressMonitor;
import org.jmist.framework.reporting.ProgressMonitor;
import org.jmist.packages.RandomScatterRecorder;
import org.jmist.toolkit.Basis3;
import org.jmist.toolkit.Point2;
import org.jmist.toolkit.Point3;
import org.jmist.toolkit.SphericalCoordinates;
import org.jmist.toolkit.Tuple;
import org.jmist.toolkit.Vector3;
import org.jmist.util.MathUtil;

/**
 * @author bkimmel
 *
 */
public final class Photometer {

	public Photometer(CollectorSphere collectorSphere) {
		this.collectorSphere = collectorSphere;
	}

	public void reset() {
		this.collectorSphere.reset();
	}

	public void setSpecimen(Material specimen) {
		this.specimen = specimen;
	}

	public void setIncidentAngle(SphericalCoordinates incident) {
		this.incident = incident;
		this.in = incident.unit().opposite().toCartesian();
		this.front = (in.z() < 0.0);
	}

	public void setWavelength(double wavelength) {
		this.wavelengths = new Tuple(wavelength);
	}

	public Material getSpecimen() {
		return this.specimen;
	}

	public SphericalCoordinates getIncidentAngle() {
		return this.incident;
	}

	public double getWavelength() {
		return this.wavelengths.at(0);
	}

	public CollectorSphere getCollectorSphere() {
		return this.collectorSphere;
	}

	public void castPhoton() {
		this.castPhotons(1);
	}

	public void castPhotons(long n) {
		this.castPhotons(n, DummyProgressMonitor.getInstance());
	}

	public void castPhotons(long n, ProgressMonitor monitor) {
		this.castPhotons(n, monitor, DEFAULT_PROGRESS_INTERVAL);
	}

	public void castPhotons(long n, ProgressMonitor monitor, long progressInterval) {

		long untilCallback = progressInterval;

		if (!monitor.notifyProgress(0.0)) {
			monitor.notifyCancelled();
			return;
		}

		for (int i = 0; i < n; i++) {

			if (--untilCallback <= 0) {

				double progress = (double) i / (double) n;

				if (!monitor.notifyProgress(progress)) {
					monitor.notifyCancelled();
					return;
				}

				untilCallback = progressInterval;

			}

			RandomScatterRecorder scattering = new RandomScatterRecorder();

			this.specimen.scatter(this.x, this.wavelengths, scattering);

			ScatterResult sr = scattering.getScatterResult();

			if (sr != null) {
				assert(MathUtil.equal(sr.weightAt(0), 1.0));
				this.collectorSphere.record(sr.scatteredRay().direction());
			}

		}

		monitor.notifyComplete();

	}

	private final CollectorSphere collectorSphere;
	private Material specimen;
	private SphericalCoordinates incident;
	private Vector3 in;
	private Tuple wavelengths;
	private boolean front;

	private final Intersection x = new Intersection() {

		@Override
		public Point3 location() {
			return Point3.ORIGIN;
		}

		@Override
		public Material material() {
			return specimen;
		}

		@Override
		public Medium ambientMedium() {
			return Medium.VACUUM;
		}

		@Override
		public Vector3 microfacetNormal() {
			return Vector3.K;
		}

		@Override
		public Vector3 normal() {
			return Vector3.K;
		}

		@Override
		public Vector3 tangent() {
			return Vector3.I;
		}

		@Override
		public Point2 textureCoordinates() {
			return Point2.ORIGIN;
		}

		@Override
		public double distance() {
			return 1.0;
		}

		@Override
		public boolean front() {
			return front;
		}

		@Override
		public Vector3 incident() {
			return in;
		}

		@Override
		public Basis3 basis() {
			return Basis3.STANDARD;
		}

		@Override
		public Basis3 microfacetBasis() {
			return Basis3.STANDARD;
		}

		@Override
		public void illuminate(Vector3 from, Spectrum irradiance) {
			/* nothing to do. */
		}

		@Override
		public boolean closed() {
			return false;
		}

	};

	private static final long DEFAULT_PROGRESS_INTERVAL = 1000;

}

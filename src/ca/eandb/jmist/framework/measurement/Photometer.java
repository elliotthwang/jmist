/**
 *
 */
package ca.eandb.jmist.framework.measurement;

import ca.eandb.jmist.framework.Intersection;
import ca.eandb.jmist.framework.Material;
import ca.eandb.jmist.framework.Medium;
import ca.eandb.jmist.framework.RandomScatterRecorder;
import ca.eandb.jmist.framework.ScatterResult;
import ca.eandb.jmist.math.Basis3;
import ca.eandb.jmist.math.MathUtil;
import ca.eandb.jmist.math.Point2;
import ca.eandb.jmist.math.Point3;
import ca.eandb.jmist.math.SphericalCoordinates;
import ca.eandb.jmist.math.Tuple;
import ca.eandb.jmist.math.Vector3;

import ca.eandb.util.progress.DummyProgressMonitor;
import ca.eandb.util.progress.ProgressMonitor;

/**
 * @author Brad Kimmel
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

		RandomScatterRecorder scattering = new RandomScatterRecorder();

		for (int i = 0; i < n; i++) {

			if (--untilCallback <= 0) {

				double progress = (double) i / (double) n;

				if (!monitor.notifyProgress(progress)) {
					monitor.notifyCancelled();
					return;
				}

				untilCallback = progressInterval;

			}

			scattering.reset();
			this.specimen.scatter(this.x, scattering);

			ScatterResult sr = scattering.getScatterResult();

			if (sr != null) {
				//assert(MathUtil.equal(sr.getWeight(), 1.0));
				// FIXME: Account for color.
				this.collectorSphere.record(sr.getScatteredRay().direction());
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

		public Point3 location() {
			return Point3.ORIGIN;
		}

		public Material material() {
			return specimen;
		}

		public Medium ambientMedium() {
			return Medium.VACUUM;
		}

		public Vector3 microfacetNormal() {
			return Vector3.K;
		}

		public Vector3 normal() {
			return Vector3.K;
		}

		public Vector3 tangent() {
			return Vector3.I;
		}

		public Point2 textureCoordinates() {
			return Point2.ORIGIN;
		}

		public double distance() {
			return 1.0;
		}

		public boolean front() {
			return front;
		}

		public Vector3 incident() {
			return in;
		}

		public Basis3 basis() {
			return Basis3.STANDARD;
		}

		public Basis3 microfacetBasis() {
			return Basis3.STANDARD;
		}

		public boolean closed() {
			return false;
		}

	};

	private static final long DEFAULT_PROGRESS_INTERVAL = 1000;

}
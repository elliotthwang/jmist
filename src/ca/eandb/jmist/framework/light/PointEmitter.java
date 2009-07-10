/*
 * Copyright (c) 2008 Bradley W. Kimmel
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package ca.eandb.jmist.framework.light;

import ca.eandb.jmist.framework.Emitter;
import ca.eandb.jmist.framework.Photon;
import ca.eandb.jmist.framework.Random;
import ca.eandb.jmist.framework.color.Color;
import ca.eandb.jmist.framework.color.Spectrum;
import ca.eandb.jmist.framework.color.WavelengthPacket;
import ca.eandb.jmist.framework.random.RandomUtil;
import ca.eandb.jmist.math.Point3;
import ca.eandb.jmist.math.Ray3;
import ca.eandb.jmist.math.Sphere;
import ca.eandb.jmist.math.Vector3;

/**
 * @author brad
 *
 */
public final class PointEmitter implements Emitter {

	private final Point3 position;

	private final Spectrum radiance;

	public PointEmitter(Point3 position, Spectrum radiance) {
		this.position = position;
		this.radiance = radiance;
	}

	/* (non-Javadoc)
	 * @see ca.eandb.jmist.framework.Emitter#emit(ca.eandb.jmist.math.Sphere, ca.eandb.jmist.framework.color.WavelengthPacket, ca.eandb.jmist.framework.Random)
	 */
	@Override
	public Photon emit(Sphere target, WavelengthPacket lambda, Random rng) {
		Vector3 direction = RandomUtil.uniformOnSphere(rng).toCartesian();
		Ray3 ray = new Ray3(position, direction);
		Color color = radiance.sample(lambda);
		return new Photon(ray, color);
	}

	/* (non-Javadoc)
	 * @see ca.eandb.jmist.framework.Emitter#getEmittedRadiance(ca.eandb.jmist.math.Vector3, ca.eandb.jmist.framework.color.WavelengthPacket)
	 */
	@Override
	public Color getEmittedRadiance(Vector3 v, WavelengthPacket lambda) {
		return radiance.sample(lambda);
	}

	/* (non-Javadoc)
	 * @see ca.eandb.jmist.framework.Emitter#getRadiantExitance(ca.eandb.jmist.framework.color.WavelengthPacket)
	 */
	@Override
	public Color getRadiantExitance(WavelengthPacket lambda) {
		return radiance.sample(lambda).times(4.0 * Math.PI);
	}

	/* (non-Javadoc)
	 * @see ca.eandb.jmist.framework.Emitter#isAtInfinity()
	 */
	@Override
	public boolean isAtInfinity() {
		return false;
	}

}
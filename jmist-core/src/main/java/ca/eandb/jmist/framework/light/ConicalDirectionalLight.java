/**
 * Java Modular Image Synthesis Toolkit (JMIST)
 * Copyright (C) 2018 Bradley W. Kimmel
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

import ca.eandb.jmist.framework.Illuminable;
import ca.eandb.jmist.framework.Random;
import ca.eandb.jmist.framework.SurfacePoint;
import ca.eandb.jmist.framework.color.Spectrum;
import ca.eandb.jmist.framework.color.WavelengthPacket;
import ca.eandb.jmist.math.Basis3;
import ca.eandb.jmist.math.SphericalCoordinates;
import ca.eandb.jmist.math.Vector3;

/**
 * A <code>Light</code> that illuminates from a specified direction.
 * Equivalent to a point light at an infinite distance.
 * @author Brad Kimmel
 */
public final class ConicalDirectionalLight extends AbstractLight {

  /** Serialization version ID. */
  private static final long serialVersionUID = -1437021256512696489L;

  /**
   * The <code>Vector3</code> indicating the direction from which the light
   * originates.
   */
  private final Basis3 basis;

  private final double angle;

  /** The irradiance <code>Spectrum</code>. */
  private final Spectrum irradiance;

  /** A value indicating whether shadows should be applied. */
  private final boolean shadows;

  /**
   * Creates a new <code>DirectionalLight</code>.
   * @param from The <code>Vector3</code> indicating the axis of the cone from
   *     which the light originates.
   * @param angle The maximum angle between <code>from</code> and the generated
   *     light directions.
   * @param irradiance The irradiance <code>Spectrum</code>.
   * @param shadows A value indicating whether shadows should be applied.
   */
  public ConicalDirectionalLight(Vector3 from, double angle, Spectrum irradiance, boolean shadows) {
    this.basis = Basis3.fromW(from);
    this.angle = angle;
    this.irradiance = irradiance;
    this.shadows = shadows;
  }

  @Override
  public void illuminate(SurfacePoint x, WavelengthPacket lambda, Random rng, Illuminable target) {
    SphericalCoordinates sc = new SphericalCoordinates(
        angle * rng.next(),
        2.0 * Math.PI * rng.next());
    Vector3 dir = sc.toCartesian(basis);
    double dot = x.getShadingNormal().dot(dir);
    target.addLightSample(new DirectionalLightSample(x, dir, irradiance.sample(lambda).times(dot), shadows));
  }

}

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
package ca.eandb.jmist.math;

import org.apache.commons.math3.util.FastMath;

/**
 * The difference between two points in three dimensional space.
 * This class is immutable.
 * @author Brad Kimmel
 */
public final class Vector3 extends HPoint3 {

  /** Serialization version ID. */
  private static final long serialVersionUID = 6028310806663933497L;

  /** The zero vector (represents the vector between two identical points). */
  public static final Vector3 ZERO = new Vector3(0.0, 0.0, 0.0);

  /** The unit vector along the x-axis. */
  public static final Vector3 I = new Vector3(1.0, 0.0, 0.0);

  /** The unit vector along the y-axis. */
  public static final Vector3 J = new Vector3(0.0, 1.0, 0.0);

  /** The unit vector along the z-axis. */
  public static final Vector3 K = new Vector3(0.0, 0.0, 1.0);

  /** The unit vector along the negative x-axis. */
  public static final Vector3 NEGATIVE_I = new Vector3(-1.0, 0.0, 0.0);

  /** The unit vector along the negative y-axis. */
  public static final Vector3 NEGATIVE_J = new Vector3(0.0, -1.0, 0.0);

  /** The unit vector along the negative z-axis. */
  public static final Vector3 NEGATIVE_K = new Vector3(0.0, 0.0, -1.0);

  /**
   * Initializes the components for the vector.
   * @param x The length of the vector along the x axis.
   * @param y The length of the vector along the y axis.
   * @param z The length of the vector along the z axis.
   */
  public Vector3(double x, double y, double z) {
    super(x, y, z);
  }

  public double w() {
    return 0.0;
  }

  /**
   * Computes the magnitude of the vector.
   * @return The magnitude of the vector.
   */
  public double length() {
    return Math.sqrt(dot(this));
  }

  /**
   * Computes the square of the magnitude of this vector.
   * @return The square of the magnitude of this vector.
   */
  public double squaredLength() {
    return dot(this);
  }

  /**
   * Returns the opposite of this vector.
   * @return The opposite of this vector.
   */
  public Vector3 opposite() {
    return new Vector3(-x, -y, -z);
  }

  /**
   * Computes the sum of two vectors.
   * @param v The vector to add to this vector.
   * @return The sum of this vector and v.
   */
  public Vector3 plus(Vector3 v) {
    return new Vector3(x + v.x, y + v.y, z + v.z);
  }

  public Point3 plus(Point3 p) {
    return new Point3(x + p.x, y + p.y, z + p.z);
  }

  public HPoint3 plus(HPoint3 p) {
    return p.isPoint() ? plus(p.toPoint3()) : plus(p.toVector3());
  }

  /**
   * Computes the difference between two vectors.
   * @param v The vector to subtract from this vector.
   * @return The difference between this vector and v.
   */
  public Vector3 minus(Vector3 v) {
    return new Vector3(x - v.x, y - v.y, z - v.z);
  }

  /**
   * Computes this vector scaled by a constant factor.
   * @param c The factor to scale this vector by.
   * @return This vector scaled by c.
   */
  public Vector3 times(double c) {
    return new Vector3(c * x, c * y, c * z);
  }

  /**
   * Computes this vector scaled by the reciprocal of
   * a constant factor.
   * Equivalent to {@code this.times(1.0 / c).}
   * @param c The factor to divide this vector by.
   * @return The vector scaled by 1.0 / c.
   * @see #times(double)
   */
  public Vector3 divide(double c) {
    return new Vector3(x / c, y / c, z / c);
  }

  /**
   * Computes the dot product between two vectors.
   * @param v The vector to compute the dot product of with this vector.
   * @return The dot product of this vector and v.
   */
  public double dot(Vector3 v) {
    return (x * v.x) + (y * v.y) + (z * v.z);
  }

  /**
   * Computes the cross product of two vectors.
   * @param v The vector to compute the cross product of with this vector.
   * @return The cross product of this vector and v.
   */
  public Vector3 cross(Vector3 v) {
    return new Vector3(
        (y * v.z) - (z * v.y),
        (z * v.x) - (x * v.z),
        (x * v.y) - (y * v.x)
    );
  }

  /**
   * Computes the unit vector in the same direction as this vector.
   * @return The unit vector in the same direction as this vector.
   */
  public Vector3 unit() {
    return this.times(1.0 / length());
  }

  /**
   * Returns a new unit vector in the same direction as
   * the vector with the specified components.
   * Equivalent to {@code new Vector3(x, y, z).unit()}.
   * @param x The magnitude of the vector along the x-axis.
   * @param y The magnitude of the vector along the y-axis.
   * @param z The magnitude of the vector along the z-axis.
   * @return A unit vector in the same direction as the
   *     vector with the indicated components.
   * @see #unit()
   */
  public static Vector3 unit(double x, double y, double z) {
    double r = Math.sqrt(x * x + y * y + z * z);
    return new Vector3(x / r, y / r, z / r);
  }

  /**
   * Returns an arbitrary <code>Vector3</code> that is perpendicular to this
   * <code>Vector3</code> (i.e., it is guaranteed that
   * {@code this.dot(this.perp()) == 0.0}.
   * @return An arbitrary <code>Vector3</code> perpendicular to this one.
   */
  public Vector3 perp() {
    if (Math.abs(x) < Math.abs(y) && Math.abs(x) < Math.abs(z)) {
      return new Vector3(0, -z, y);
    } else if (Math.abs(y) < Math.abs(z)) {
      return new Vector3(z, 0, -x);
    } else {
      return new Vector3(-y, x, 0);
    }
  }

  /**
   * Converts this <code>Vector3</code> to a compact, two byte representation
   * of its direction.  The first 8 bits represent the angle between this
   * vector and the positive z-axis.  The second (low order) 8 bits represent
   * the counter clockwise angle about positive z-axis, with zero radians being
   * the positive x-axis.
   * @return The two byte representation of the direction.
   */
  public short toCompactDirection() {
    int theta = (int) Math.floor(FastMath.acos(z) * (256.0 / Math.PI));
    if (theta > 255) {
      theta = 255;
    }

    int phi = (int) Math.floor(Math.atan2(y, x) * 256.0 / (2.0 * Math.PI));
    if (phi > 255) {
      phi = 255;
    } else if (phi < 255) {
      phi += 256;
    }

    return (short) ((theta << 8) | phi);
  }

  /**
   * Creates a unit length <code>Vector3</code> corresponding to the
   * provided two byte direction representation.
   * @param dir The two byte direction.
   * @return The corresponding <code>Vector3</code>.
   * @see #toCompactDirection()
   */
  public static Vector3 fromCompactDirection(short dir) {
    int phi = dir & 0xff;
    int theta = (dir >> 8) & 0xff;

    return new Vector3(
        Trig.SIN_THETA[theta] * Trig.COS_PHI[phi],
        Trig.SIN_THETA[theta] * Trig.SIN_PHI[phi],
        Trig.COS_THETA[theta]);
  }

}

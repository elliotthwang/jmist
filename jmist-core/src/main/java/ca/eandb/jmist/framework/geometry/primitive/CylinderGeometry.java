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
package ca.eandb.jmist.framework.geometry.primitive;

import ca.eandb.jmist.framework.Intersection;
import ca.eandb.jmist.framework.IntersectionRecorder;
import ca.eandb.jmist.framework.geometry.PrimitiveGeometry;
import ca.eandb.jmist.math.Basis3;
import ca.eandb.jmist.math.Box3;
import ca.eandb.jmist.math.Interval;
import ca.eandb.jmist.math.MathUtil;
import ca.eandb.jmist.math.Point2;
import ca.eandb.jmist.math.Point3;
import ca.eandb.jmist.math.Polynomial;
import ca.eandb.jmist.math.Ray3;
import ca.eandb.jmist.math.Sphere;
import ca.eandb.jmist.math.Vector3;

/**
 * A cylinder aligned along the y-axis.
 *
 * @author Brad Kimmel
 */
public final class CylinderGeometry extends PrimitiveGeometry {

  /** Serialization version ID. */
  private static final long serialVersionUID = 1128440316229322913L;

  /** The point at the base of the cylinder */
  private final Point3 base;

  /** The radius of the cylinder */
  private final double radius;

  /** The height of the cylinder */
  private final double height;

  /** The surface ID for the base of the cylinder. */
  private static final int CYLINDER_SURFACE_BASE = 0;

  /** The surface ID for the top of the cylinder. */
  private static final int CYLINDER_SURFACE_TOP = 1;

  /** The surface ID for the body of the cylinder. */
  private static final int CYLINDER_SURFACE_BODY = 2;

  /**
   * Initializes the dimensions of this cylinder.
   * @param base    the center of the base of the cylinder
   * @param radius  the radius of the cylinder
   * @param height  the height of the cylinder
   */
  public CylinderGeometry(Point3 base, double radius, double height) {
    this.base = base;
    this.radius = radius;
    this.height = height;
  }

  @Override
  public void intersect(Ray3 ray, IntersectionRecorder recorder) {
    Interval I = recorder.interval();
    Point3 p;
    double t;

    // first check for intersection of ray with the caps on the ends of the cylinder

    // check bottom cap
    t = (this.base.y() - ray.origin().y()) / ray.direction().y();
    if (I.contains(t)) {
      p = ray.pointAt(t);
      if (this.base.squaredDistanceTo(p) < this.radius * this.radius) {
        Intersection x = super.newIntersection(ray, t, (ray.direction().y() > 0.0), CYLINDER_SURFACE_BASE)
            .setLocation(p);
        recorder.record(x);
      }
    }

    // check top cap
    t = (this.base.y() + this.height - ray.origin().y()) / ray.direction().y();
    if (I.contains(t)) {
      p = ray.pointAt(t);
      double r = (p.x() - this.base.x()) * (p.x() - this.base.x()) + (p.z() - this.base.z()) * (p.z() - this.base.z());
      if (r < this.radius * this.radius) {
        Intersection x = super.newIntersection(ray, t, (ray.direction().y() < 0.0), CYLINDER_SURFACE_TOP)
            .setLocation(p);
        recorder.record(x);
      }
    }

    // now check for intersection of ray with the body
    Vector3 orig = this.base.vectorTo(ray.origin());
    Vector3 dir = ray.direction();

    Polynomial f = new Polynomial(
        orig.x() * orig.x() + orig.z() * orig.z() - this.radius * this.radius,
        2.0 * (orig.x() * dir.x() + orig.z() * dir.z()),
        dir.x() * dir.x() + dir.z() * dir.z()
    );
    double[] x = f.roots();

    if (x.length == 2) {
      // for each solution, make sure the point lies between the base and the apex
      p = ray.pointAt(x[0]);
      if (MathUtil.inRangeOO(p.y(), this.base.y(), this.base.y() + this.height)) {
        Intersection isect = super.newIntersection(ray, x[0], (x[0] < x[1]), CYLINDER_SURFACE_BODY)
            .setLocation(p);
        recorder.record(isect);
      }

      p = ray.pointAt(x[1]);
      if (MathUtil.inRangeOO(p.y(), this.base.y(), this.base.y() + this.height)) {
        Intersection isect = super.newIntersection(ray, x[1], (x[0] > x[1]), CYLINDER_SURFACE_BODY)
            .setLocation(p);
        recorder.record(isect);
      }
    }
  }

  @Override
  public Box3 boundingBox() {
    return new Box3(
        this.base.x() - this.radius,
        this.base.y(),
        this.base.z() - this.radius,
        this.base.x() + this.radius,
        this.base.y() + this.height,
        this.base.z() + this.radius
    );
  }

  @Override
  public Sphere boundingSphere() {
    double h = this.height / 2.0;
    double r = Math.sqrt(this.radius * this.radius + h * h);
    Point3 c = new Point3(this.base.x(), this.base.y() + h, this.base.z());
    return new Sphere(c, r);
  }

  @Override
  protected Basis3 getBasis(GeometryIntersection x) {
    Vector3 n = this.getNormal(x);
    Vector3 r = this.base.vectorTo(x.getPosition());
    Vector3 u = new Vector3(-r.z(), 0.0, r.x());
    return Basis3.fromWU(n, u, Basis3.Orientation.RIGHT_HANDED);
  }

  @Override
  protected Vector3 getNormal(GeometryIntersection x) {
    switch (x.getTag()) {
    case CYLINDER_SURFACE_BASE:
      return Vector3.J.opposite();

    case CYLINDER_SURFACE_TOP:
      return Vector3.J;

    case CYLINDER_SURFACE_BODY:
      Point3 p = x.getPosition();
      return new Vector3(p.x() - this.base.x(), 0.0, p.z()
          - this.base.z()).unit();

    default:
      throw new IllegalArgumentException("Invalid surface ID.");
    }
  }

  @Override
  protected Point2 getTextureCoordinates(GeometryIntersection x) {
    Vector3 r = this.base.vectorTo(x.getPosition());
    double tx = (Math.PI + Math.atan2(r.z(), r.x())) / (2.0 * Math.PI);
    double ty;

    switch (x.getTag()) {
    case CYLINDER_SURFACE_BASE:
      ty = Math.sqrt(r.x() * r.x() + r.z() * r.z()) / (4.0 * this.radius);
      break;

    case CYLINDER_SURFACE_TOP:
      ty = 1.0 - Math.sqrt(r.x() * r.x() + r.z() * r.z())
          / (4.0 * this.radius);
      break;

    case CYLINDER_SURFACE_BODY:
      ty = 0.25 + (r.y() / (2.0 * this.height));
      break;

    default:
      throw new IllegalArgumentException("Invalid surface ID.");
    }

    return new Point2(tx, ty);
  }

}

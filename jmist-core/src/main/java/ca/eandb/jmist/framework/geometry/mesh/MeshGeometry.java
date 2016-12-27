/**
 * Java Modular Image Synthesis Toolkit (JMIST)
 * Copyright (C) 2008-2013 Bradley W. Kimmel
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
package ca.eandb.jmist.framework.geometry.mesh;

import ca.eandb.jmist.framework.Intersection;
import ca.eandb.jmist.framework.IntersectionRecorder;
import ca.eandb.jmist.framework.ShadingContext;
import ca.eandb.jmist.framework.geometry.AbstractGeometry;
import ca.eandb.jmist.math.Basis3;
import ca.eandb.jmist.math.Box3;
import ca.eandb.jmist.math.GeometryUtil;
import ca.eandb.jmist.math.MathUtil;
import ca.eandb.jmist.math.Plane3;
import ca.eandb.jmist.math.Point2;
import ca.eandb.jmist.math.Point3;
import ca.eandb.jmist.math.Ray3;
import ca.eandb.jmist.math.Sphere;
import ca.eandb.jmist.math.Vector2;
import ca.eandb.jmist.math.Vector3;

/**
 * A polyhedron <code>SceneElement</code>.
 * @author Brad Kimmel
 */
public final class MeshGeometry extends AbstractGeometry {

  /** Serialization version ID. */
  private static final long serialVersionUID = 262374288661771750L;

  /**
   * Creates a new <code>MeshGeometry</code>.
   * @param mesh The <code>Mesh</code> to create the geometry from.
   */
  public MeshGeometry(Mesh mesh) {
    this.mesh = MeshUtil.triangulate(mesh);
  }

  @Override
  public void intersect(final int index, final Ray3 ray,
                        IntersectionRecorder recorder) {
    Mesh.Face face = mesh.getFace(index);
    Mesh.Vertex va = face.getVertex(0);
    Mesh.Vertex vb = face.getVertex(1);
    Mesh.Vertex vc = face.getVertex(2);
    Point3 a = va.getPosition();
    Point3 b = vb.getPosition();
    Point3 c = vc.getPosition();
    final Plane3 plane = Plane3.throughPoints(a, b, c);
    final double t = plane.intersect(ray);

    if (recorder.interval().contains(t)) {
      Point3 p = ray.pointAt(t);
      Point2 uv = GeometryUtil.barycentric(p, a, b, c);
      final double u = uv.x();
      final double v = uv.y();

      if (u > 0.0 && v > 0.0 && (u + v) < 1.0) {
        recorder.record(new Intersection() {
          @Override
          public double getDistance() {
            return t;
          }

          @Override
          public double getTolerance() {
            return MathUtil.EPSILON;
          }

          @Override
          public boolean isFront() {
            return ray.direction().dot(plane.normal()) < 0.0;
          }

          @Override
          public void prepareShadingContext(ShadingContext context) {
            MeshGeometry.this.prepareShadingContext(context, index, u, v);
          }
        });
      }
    }
  }

  @Override
  public Box3 getBoundingBox(int index) {
    return MeshUtil.getBoundingBox(mesh.getFace(index));
  }

  @Override
  public Sphere getBoundingSphere(int index) {
    return MeshUtil.getBoundingSphere(mesh.getFace(index));
  }

  @Override
  public int getNumPrimitives() {
    return mesh.getFaceCount();
  }

  @Override
  public Box3 boundingBox() {
    return MeshUtil.getBoundingBox(mesh);
  }

  @Override
  public Sphere boundingSphere() {
    return MeshUtil.getBoundingSphere(mesh);
  }

  @Override
  public void generateRandomSurfacePoint(
      int index, ShadingContext context, double ru, double rv, double rj) {
    if (ru + rv > 1.0) {
      ru = 1.0 - ru;
      rv = 1.0 - rv;
    }
    prepareShadingContext(context, index, ru, rv);
  }

  @Override
  public void generateRandomSurfacePoint(ShadingContext context, double ru,
      double rv, double rj) {
    double base = 0.0;
    double x = ru * getSurfaceArea();
    int index = 0;
    for (Mesh.Face face : mesh.getFaces()) {
      double area = getSurfaceArea(face);
      if (x < base + area) {
        generateRandomSurfacePoint(index, context, (x - base) / area, rv, rj);
        return;
      }
      base += area;
      index++;
    }
    super.generateRandomSurfacePoint(context, ru, rv, rj);
  }

  @Override
  public double getSurfaceArea(int index) {
    return getSurfaceArea(mesh.getFace(index));
  }

  private double getSurfaceArea(Mesh.Face face) {
    Point3 a = face.getVertex(0).getPosition();
    Point3 b = face.getVertex(1).getPosition();
    Point3 c = face.getVertex(2).getPosition();
    return GeometryUtil.areaOfTriangle(a, b, c);
  }

  @Override
  public double getSurfaceArea() {
    if (surfaceArea < 0.0) {
      synchronized (this) {
        double area = 0.0;
        for (Mesh.Face face : mesh.getFaces()) {
          area += getSurfaceArea(face);
        }
        surfaceArea = area;
      }
    }
    return surfaceArea;
  }

  private void prepareShadingContext(
      ShadingContext context, int index, double u, double v) {
    Mesh.Face face = mesh.getFace(index);
    Mesh.Vertex va = face.getVertex(0);
    Mesh.Vertex vb = face.getVertex(1);
    Mesh.Vertex vc = face.getVertex(2);
    Point3 a = va.getPosition();
    Point3 b = vb.getPosition();
    Point3 c = vc.getPosition();
    Plane3 plane = Plane3.throughPoints(a, b, c);
    Vector3 ab = a.vectorTo(b);
    Vector3 ac = a.vectorTo(c);
    Point3 p = a.plus(ab.times(u)).plus(ac.times(v));

    context.setPosition(p);
    context.setPrimitiveIndex(index);

    Vector3 n = plane.normal();
    Vector3 tu = null;
    Vector3 tv = null;
    if (mesh.hasUVs()) {
      Point2 ta = va.getUV();
      Point2 tb = vb.getUV();
      Point2 tc = vc.getUV();
      Vector2 tab = ta.vectorTo(tb);
      Vector2 tac = ta.vectorTo(tc);
      context.setUV(ta.plus(tab.times(u))
                      .plus(tac.times(v)));

      // See http://www.terathon.com/code/tangent.html
      double r = 1.0 / (tab.x() * tac.y() - tab.y() * tac.x());

      tu = new Vector3(
          r * (tac.y() * ab.x() - tab.y() * ac.x()),
          r * (tac.y() * ab.y() - tab.y() * ac.y()),
          r * (tac.y() * ab.z() - tab.y() * ac.z()));
      tv = new Vector3(
          r * (tab.x() * ac.x() - tac.x() * ab.x()),
          r * (tab.x() * ac.y() - tac.x() * ab.y()),
          r * (tab.x() * ac.z() - tac.x() * ab.z()));

      context.setBasis(Basis3.fromWUV(n, tu, tv));
    } else {  // !mesh.hasUVs()
      context.setNormal(n);
    }

    if (mesh.hasVertexNormals()) {
      Vector3 na = va.getNormal();
      Vector3 nb = vb.getNormal();
      Vector3 nc = vc.getNormal();
      Vector3 shadingNormal = na.times(1.0 - u - v)
          .plus(nb.times(u)).plus(nc.times(v)).unit();
      if (mesh.hasUVs()) {
        context.setShadingBasis(Basis3.fromWUV(shadingNormal, tu, tv));
      } else {
        context.setShadingNormal(shadingNormal);
      }
    }
  }

  private final Mesh mesh;

  /** The surface area of this polyhedron. */
  private double surfaceArea = -1.0;

}
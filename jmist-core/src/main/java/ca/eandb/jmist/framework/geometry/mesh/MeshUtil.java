/**
 * 
 */
package ca.eandb.jmist.framework.geometry.mesh;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import ca.eandb.jmist.framework.BoundingBoxBuilder3;
import ca.eandb.jmist.math.Box3;
import ca.eandb.jmist.math.Point3;
import ca.eandb.jmist.math.Sphere;

/**
 * @author bwkimmel
 *
 */
public final class MeshUtil {
  
  private MeshUtil() {}
  
  public static Mesh triangulate(Mesh mesh) {
    return mesh.getMaxFaceVertexCount() == 3
        ? mesh : new TriangulatedMesh(mesh);
  }
  
  public static Sphere getBoundingSphere(Mesh mesh) {
    return getBoundingSphere(mesh.getVertices());
  }
  
  public static Sphere getBoundingSphere(Mesh.Face face) {
    return getBoundingSphere(face.getVertices());
  }
  
  private static Sphere getBoundingSphere(Iterable<Mesh.Vertex> vertices) {
    return Sphere.smallestContaining(() ->
        StreamSupport.stream(vertices.spliterator(), false)
            .map(v -> v.getPosition())
            .iterator());
  }

  public static Box3 getBoundingBox(Mesh mesh) {
    return getBoundingBox(mesh.getVertices());
  }
  
  public static Box3 getBoundingBox(Mesh.Face face) {
    return getBoundingBox(face.getVertices());
  }

  private static Box3 getBoundingBox(Iterable<Mesh.Vertex> vertices) {
    BoundingBoxBuilder3 builder = new BoundingBoxBuilder3();
    for (Mesh.Vertex vertex : vertices) {
      builder.add(vertex.getPosition());
    }
    return builder.getBoundingBox();
  }

}
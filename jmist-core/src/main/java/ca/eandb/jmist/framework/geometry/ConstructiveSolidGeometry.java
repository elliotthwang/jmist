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
package ca.eandb.jmist.framework.geometry;

import java.util.BitSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import ca.eandb.jmist.framework.Intersection;
import ca.eandb.jmist.framework.IntersectionDecorator;
import ca.eandb.jmist.framework.IntersectionRecorder;
import ca.eandb.jmist.framework.SceneElement;
import ca.eandb.jmist.framework.ShadingContext;
import ca.eandb.jmist.math.Interval;
import ca.eandb.jmist.math.Ray3;

/**
 * An abstract <code>SceneElement</code> that combines other geometries using a
 * boolean expression.
 * @author Brad Kimmel
 */
public abstract class ConstructiveSolidGeometry extends CompositeGeometry {

  /** Serialization version ID. */
  private static final long serialVersionUID = 2972169138865609527L;

  @Override
  public void intersect(Ray3 ray, IntersectionRecorder recorder) {
    /* Use a special intersection recorder that knows how to combine the
     * results of multiple sets of intersections.
     */
    CsgIntersectionRecorder csg = new CsgIntersectionRecorder();

    /* Compute the ray-geometry intersections for each child geometry in
     * turn.
     */
    for (SceneElement geometry : this.children()) {
      geometry.intersect(ray, csg);

      /* Advance to next argument. */
      csg.advance();
    }

    /* combine the results and transfer the resulting intesections to the
     * provided intersection recorder.
     */
    csg.transfer(recorder);
  }

  /**
   * Evaluates the boolean expression for this class of
   * <code>ConstructiveSolidGeometry</code>.
   * @param nArgs The number of child geometries.
   * @param args A <code>BitSet</code> of values indicating whether the ray
   *     is inside each of the child geometries.  This argument should not
   *     be modified by the method.
   * @return A value indicating whether the ray is inside the combined
   *     geometry.  The result should be deterministic.  That is, two calls
   *     to this method with the same value for <code>nArgs</code> and for
   *     <code>args</code> should yield the same result.
   */
  protected abstract boolean isInside(int nArgs, BitSet args);

  /**
   * An <code>IntersectionRecorder</code> that combines the results of
   * multiple sets of intersections using a boolean expression.
   * @author Brad Kimmel
   */
  private final class CsgIntersectionRecorder implements IntersectionRecorder {

    /**
     * The index of the component geometry whose intersections are
     * currently being recorded.
     */
    private int argumentIndex = 0;

    /**
     * The set of all the intersections recorded by each of the component
     * geometries, sorted by distance (least to greatest).
     */
    private final SortedSet<CsgIntersection> intersectionSet = new TreeSet<>(
        Comparator.comparingDouble(IntersectionDecorator::getDistance));

    @Override
    public boolean needAllIntersections() {
      return true;
    }

    @Override
    public boolean isEmpty() {
      return this.intersectionSet.isEmpty();
    }

    @Override
    public Interval interval() {
      return Interval.UNIVERSE;
    }

    @Override
    public void record(Intersection intersection) {
      this.intersectionSet.add(new CsgIntersection(this.argumentIndex, intersection));
    }

    /**
     * Combines the results of each set of intersections and transfers the
     * resulting intersections to the specified
     * <code>IntersectionRecorder</code>.
     * @param recorder The <code>IntersectionRecorder</code> to transfer
     *     the resulting intersections to.
     */
    public void transfer(IntersectionRecorder recorder) {
      int nArgs = this.argumentIndex;
      BitSet args = new BitSet(nArgs);
      Iterator<CsgIntersection> i = this.intersectionSet.iterator();
      boolean fromInside = false;
      boolean toInside;

      /* Loop through each intersection. */
      while (i.hasNext()) {
        CsgIntersection x = i.next();
        Intersection inner = x.getInnerIntersection();

        assert(x.getArgumentIndex() < nArgs);

        args.set(x.getArgumentIndex(), inner.isFront());
        toInside = isInside(nArgs, args);

        /* If the intersection represents the traversal from
         * outside the geometry to inside, or vice versa, then the
         * intersection is at the boundary of the combined geometry.
         * Only consider this intersection if it is within the
         * range expected by the recorder.
         */
        if (fromInside != toInside
            && recorder.interval().contains(inner.getDistance(), inner.getTolerance())) {
          /* The intersection is a front intersection if the ray
           * is passing into the geometry.
           */
          x.setFront(toInside);
          recorder.record(x);

          /* If we don't need all intersections, then we're
           * done.
           */
          if (!recorder.needAllIntersections()) {
            break;
          }
        }
        fromInside = toInside;
      } /* i.hasNext() */
    }

    /**
     * Completes a set of intersections and advances to the set of
     * intersections for the next geometry.
     */
    public void advance() {
      this.argumentIndex++;
    }

    /**
     * An <code>Intersection</code> decorator that adds functionality
     * needed to support constructive solid geometry.  This decorator
     * keeps track of which child geometry the intersection is for, and it
     * can be flipped, which toggles the {@link Intersection#isFront()}
     * property and negates the basis and normal.
     * @author Brad Kimmel
     */
    private final class CsgIntersection extends IntersectionDecorator {

      /**
       * A value indicating if this <code>CsgIntersection</code> was
       * flipped from the underlying <code>Intersection</code>.
       */
      private boolean flipped = false;

      /**
       * A value indicating which component geometry the inner
       * <code>Intersection</code> came from.
       */
      private final int argumentIndex;

      /**
       * Creates a new <code>CsgIntersection</code>.
       * @param argumentIndex The
       * @param inner
       */
      public CsgIntersection(int argumentIndex, Intersection inner) {
        super(inner);
        this.argumentIndex = argumentIndex;
      }

      @Override
      public boolean isFront() {
        return this.flipped ? !this.inner.isFront() : this.inner.isFront();
      }

      @Override
      protected void transformShadingContext(ShadingContext context) {
        context.setPrimitiveIndex(0);
        if (flipped) {
          context.setBasis(context.getBasis().opposite());
          context.setShadingBasis(context.getShadingBasis().opposite());
        }
      }

      /**
       * Gets the value identifying which of the component geometries
       * this <code>Intersection</code> came from.
       * @return The value identifying which of the component geometries
       *     this <code>Intersection</code> came from.
       */
      public int getArgumentIndex() {
        return this.argumentIndex;
      }

      /**
       * Gets the <code>Intersection</code> that was recorded by the
       * component geometry.
       * @return The <code>Intersection</code> that was recorded by the
       *     component geometry.
       */
      public Intersection getInnerIntersection() {
        return this.inner;
      }

      /**
       * Sets whether this <code>CsgIntersection</code> is an entry point
       * or an exit point from the geometry.
       * @param front A value indicating if this
       *     <code>CsgIntersection</code> represents an intersection
       *     with the outside of the geometry.
       */
      public void setFront(boolean front) {
        this.flipped = (front != inner.isFront());
      }

    }

  }

}

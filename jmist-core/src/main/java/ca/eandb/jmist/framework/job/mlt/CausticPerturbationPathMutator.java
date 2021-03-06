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
package ca.eandb.jmist.framework.job.mlt;

import ca.eandb.jmist.framework.Random;
import ca.eandb.jmist.framework.path.Path;
import ca.eandb.jmist.framework.path.PathNode;

public final class CausticPerturbationPathMutator implements PathMutator {

  @Override
  public double getTransitionPDF(Path from, Path to) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Path mutate(Path path, Random rnd) {
    int s = path.getLightPathLength();
    int t = path.getEyePathLength();

    path = path.slice(0, s + t - 1);
    if (path == null) {
      return null;
    }

    PathNode newEyeTail = path.getEyeTail();
    PathNode newLightTail = path.getLightTail();

    newLightTail = newLightTail.expand(rnd.next(), rnd.next(), rnd.next()); // FIXME

    return new Path(newLightTail, newEyeTail);
  }

}

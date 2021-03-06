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
package ca.eandb.jmist.framework.scene;

import ca.eandb.jmist.framework.Function1;
import ca.eandb.jmist.framework.Lens;
import ca.eandb.jmist.framework.Light;
import ca.eandb.jmist.framework.Material;
import ca.eandb.jmist.framework.SceneElement;
import ca.eandb.jmist.framework.color.ColorModel;
import ca.eandb.jmist.framework.function.PiecewiseLinearFunction1;
import ca.eandb.jmist.framework.geometry.primitive.PolyhedronGeometry;
import ca.eandb.jmist.framework.lens.PinholeLens;
import ca.eandb.jmist.framework.lens.TransformableLens;
import ca.eandb.jmist.framework.material.LambertianMaterial;
import ca.eandb.jmist.math.Point3;
import ca.eandb.jmist.math.Vector3;
import ca.eandb.jmist.util.ArrayUtil;

/**
 * A <code>Model</code> of the Cornell Box without the blocks.
 * @author Brad Kimmel
 */
public final class EmptyCornellBoxScene extends AbstractScene {

  /** Serialization version ID. */
  private static final long serialVersionUID = 1838010848508780354L;

  /** The wavelengths at which the reflectance spectra are given. */
  private static final double[] WAVELENGTHS = ArrayUtil.range(400.0e-9, 700.0e-9, 76);

  /** The reflectance spectrum for the white walls. */
  private static final Function1 white = new PiecewiseLinearFunction1(WAVELENGTHS, new double[]{

      0.343,0.445,0.551,0.624,0.665,0.687,0.708,0.723,0.715,0.710, /* 400 - 436 */
      0.745,0.758,0.739,0.767,0.777,0.765,0.751,0.745,0.748,0.729, /* 440 - 476 */
      0.745,0.757,0.753,0.750,0.746,0.747,0.735,0.732,0.739,0.734, /* 480 - 516 */
      0.725,0.721,0.733,0.725,0.732,0.743,0.744,0.748,0.728,0.716, /* 520 - 556 */
      0.733,0.726,0.713,0.740,0.754,0.764,0.752,0.736,0.734,0.741, /* 560 - 596 */
      0.740,0.732,0.745,0.755,0.751,0.744,0.731,0.733,0.744,0.731, /* 600 - 636 */
      0.712,0.708,0.729,0.730,0.727,0.707,0.703,0.729,0.750,0.760, /* 640 - 676 */
      0.751,0.739,0.724,0.730,0.740,0.737                          /* 680 - 700 */

  });

  /** The reflectance spectrum for the green wall. */
  private static final Function1 green = new PiecewiseLinearFunction1(WAVELENGTHS, new double[]{

      0.092,0.096,0.098,0.097,0.098,0.095,0.095,0.097,0.095,0.094, /* 400 - 436 */
      0.097,0.098,0.096,0.101,0.103,0.104,0.107,0.109,0.112,0.115, /* 440 - 476 */
      0.125,0.140,0.160,0.187,0.229,0.285,0.343,0.390,0.435,0.464, /* 480 - 516 */
      0.472,0.476,0.481,0.462,0.447,0.441,0.426,0.406,0.373,0.347, /* 520 - 556 */
      0.337,0.314,0.285,0.277,0.266,0.250,0.230,0.207,0.186,0.171, /* 560 - 596 */
      0.160,0.148,0.141,0.136,0.130,0.126,0.123,0.121,0.122,0.119, /* 600 - 636 */
      0.114,0.115,0.117,0.117,0.118,0.120,0.122,0.128,0.132,0.139, /* 640 - 676 */
      0.144,0.146,0.150,0.152,0.157,0.159                          /* 680 - 700 */

  });

  /** The reflectance spectrum for the red wall. */
  private static final Function1 red = new PiecewiseLinearFunction1(WAVELENGTHS, new double[]{

      0.040,0.046,0.048,0.053,0.049,0.050,0.053,0.055,0.057,0.056, /* 400 - 436 */
      0.059,0.057,0.061,0.061,0.060,0.062,0.062,0.062,0.061,0.062, /* 440 - 476 */
      0.060,0.059,0.057,0.058,0.058,0.058,0.056,0.055,0.056,0.059, /* 480 - 516 */
      0.057,0.055,0.059,0.059,0.058,0.059,0.061,0.061,0.063,0.063, /* 520 - 556 */
      0.067,0.068,0.072,0.080,0.090,0.099,0.124,0.154,0.192,0.255, /* 560 - 596 */
      0.287,0.349,0.402,0.443,0.487,0.513,0.558,0.584,0.620,0.606, /* 600 - 636 */
      0.609,0.651,0.612,0.610,0.650,0.638,0.627,0.620,0.630,0.628, /* 640 - 676 */
      0.642,0.639,0.657,0.639,0.635,0.642                          /* 680 - 700 */

  });

  /** The emission spectrum for the light box. */
  private static final Function1 emission = new PiecewiseLinearFunction1(
      new double[]{ 400.0e-9, 500.0e-9, 600.0e-9, 700.0e-9 },
      new double[]{   0.0   ,   8.0   ,  15.6   ,  18.4    }
  );

  /** The <code>Lens</code> to use to view the box. */
  private static final Lens lens = createLens();

  /**
   * The <code>SceneElement</code> for the geometry
   *
   * The following indicates the layout of the vertices in this scene.
   * <pre>
   *              1                                         2
   *             o-----------------------------------------o
   *             |                                         |
   *             |                Back Wall                |
   *      1      |11                                       |10     2
   *     o-------o-----------------------------------------o-------o
   *     |       |                                         |       |
   *     |       |                                         |       |
   *     |       |                                         |       |
   *     |       |                                         |       |
   *     |       |                                         |   R   |
   *     |   L   |                                         |   i   |
   *     |   e   |                                         |   g   |
   *     |   f   |                                         |   h   |
   *     |   t   |                                         |   t   |
   *     |       |                  Floor                  |       |
   *     |   W   |                                         |   W   |
   *     |   a   |                                         |   a   |
   *     |   l   |                                         |   l   |
   *     |   l   |                                         |   l   |
   *     |       |                                         | ^     |
   *     |       |                                         | |     |
   *     |       |                                         | |     |
   *     |       |                                         | z     |
   *     |0      |8                                        |9      |3
   *     o-------o-----------------------------------------o-------o
   *                                          <---x  ORIGIN^  y--->
   *
   *
   *              1                                         2
   *             o-----------------------------------------o
   *             |                                         |
   *             |                                         |
   *             |                 Ceiling                 |
   *             |                                         |
   *             |                                         |
   *             |              5             6            |
   *             |             o             o             |
   *             |                                         |
   *             |                                         |
   *             |                  Light                  |
   *             |                                         |
   *             |              4             7            |
   *             |             o             o             |
   *             |                                         |
   *             |                                         |
   *             |                                         |  ^
   *             |                 Ceiling                 |  |
   *             |                                         |  |
   *             |0                                        |3 z
   *             o-----------------------------------------o
   *                                                 <---x
   * </pre>
   */
  private static final SceneElement geometry = new PolyhedronGeometry(
      new Point3[]{
          new Point3(556.0, 548.8,   0.0), // ceiling (0-3)
          new Point3(556.0, 548.8, 559.2),
          new Point3(  0.0, 548.8, 559.2),
          new Point3(  0.0, 548.8,   0.0),

          new Point3(343.0, 548.8, 227.0), // light (4-7)
          new Point3(343.0, 548.8, 332.0),
          new Point3(213.0, 548.8, 332.0),
          new Point3(213.0, 548.8, 227.0),

          new Point3(552.8, 0.0,   0.0), // floor (8-11)
          new Point3(  0.0, 0.0,   0.0),
          new Point3(  0.0, 0.0, 559.2),
          new Point3(549.6, 0.0, 559.2),
      },
      new int[][]{
          new int[]{  0,  1,  5,  4 }, // ceiling
          new int[]{  1,  2,  6,  5 },
          new int[]{  2,  3,  7,  6 },
          new int[]{  3,  0,  4,  7 },

          new int[]{  4,  5,  6,  7 }, // light
          new int[]{  8,  9, 10, 11 }, // floor
          new int[]{  2,  1, 11, 10 }, // back wall
          new int[]{  3,  2, 10,  9 }, // right wall
          new int[]{  1,  0,  8, 11 }, // left wall
      }
  );

  /** The <code>SceneElement</code> for the light box. */
  private final Light light;

  /** The root <code>SceneElement</code> for the Cornell box scene. */
  private final SceneElement cornellBox;

  /**
   * Creates a new <code>CornellBoxModel</code>.
   * @param colorModel The <code>ColorModel</code> to use.
   */
  public EmptyCornellBoxScene(ColorModel colorModel) {
    Material matteWhite = new LambertianMaterial(colorModel.getContinuous(white));
    Material matteGreen = new LambertianMaterial(colorModel.getContinuous(green));
    Material matteRed = new LambertianMaterial(colorModel.getContinuous(red));
    Material matteEmissive = new LambertianMaterial(colorModel.getGray(0.78), colorModel.getContinuous(emission));

    SceneElement materialMap = new MaterialMapSceneElement(geometry)
        .addMaterial("white", matteWhite)
        .addMaterial("red", matteRed)
        .addMaterial("green", matteGreen)
        .addMaterial("emissive", matteEmissive)
        .setMaterialRange(0, 4, "white")    // ceiling
        .setMaterialRange(4, 1, "emissive")    // light
        .setMaterialRange(5, 1, "white")    // floor
        .setMaterialRange(6, 1, "white")     // back wall
        .setMaterialRange(7, 1, "green")     // right wall
        .setMaterialRange(8, 1, "red");      // left wall

    this.cornellBox = materialMap;
    this.light = cornellBox.createLight();
  }

  @Override
  public SceneElement getRoot() {
    return cornellBox;
  }

  @Override
  public Light getLight() {
    return light;
  }

  @Override
  public Lens getLens() {
    return lens;
  }

  /**
   * Creates the <code>Lens</code> used in the Cornell Box.
   * @return The <code>Lens</code> used in the Cornell Box.
   */
  private static Lens createLens() {
    TransformableLens lens = new TransformableLens(
        PinholeLens.fromHfovAndAspect(2.0 * Math.atan2(0.25 / 2.0, 0.35), 1.0));
    lens.rotateY(Math.PI);
    lens.translate(new Vector3(278.0, 273.0, -800.0));
    return lens;
  }

}

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
package ca.eandb.jmist.framework.color;

import ca.eandb.jmist.math.MathUtil;
import ca.eandb.jmist.math.Tuple3;

/**
 * An RGB color triple.
 * @author Brad Kimmel
 */
public final class RGB extends Tuple3 implements Spectrum {

  /** Serialization version ID. */
  private static final long serialVersionUID = -4621493353796327474L;

  public static final RGB ZERO = new RGB(0.0, 0.0, 0.0);

  public static final RGB BLACK = new RGB(0.0, 0.0, 0.0);
  public static final RGB WHITE = new RGB(1.0, 1.0, 1.0);
  public static final RGB RED = new RGB(1.0, 0.0, 0.0);
  public static final RGB GREEN = new RGB(0.0, 1.0, 0.0);
  public static final RGB BLUE = new RGB(0.0, 0.0, 1.0);
  public static final RGB YELLOW = new RGB(1.0, 1.0, 0.0);
  public static final RGB MAGENTA = new RGB(1.0, 0.0, 1.0);
  public static final RGB CYAN = new RGB(0.0, 1.0, 1.0);
  public static final RGB GREY50 = new RGB(0.50, 0.50, 0.50);
  public static final RGB GREY25 = new RGB(0.25, 0.25, 0.25);
  public static final RGB GREY75 = new RGB(0.75, 0.75, 0.75);

  public RGB(double r, double g, double b) {
    super(r, g, b);
  }

  public double r() {
    return x;
  }

  public double g() {
    return y;
  }

  public double b() {
    return z;
  }

  public final RGB plus(RGB other) {
    return new RGB(x + other.x, y + other.y, z + other.z);
  }

  public final RGB minus(RGB other) {
    return new RGB(x - other.x, y - other.y, z - other.z);
  }

  public final RGB divide(RGB other) {
    return new RGB(x / other.x, y / other.y, z / other.z);
  }

  public final RGB divide(double c) {
    return new RGB(x / c, y / c, z / c);
  }

  public final RGB times(RGB other) {
    return new RGB(x * other.x, y * other.y, z * other.z);
  }

  public final RGB times(double c) {
    return new RGB(x * c, y * c, z * c);
  }

  public final RGB clamp(double max) {
    return clamp(0.0, max);
  }

  public final RGB clamp(double min, double max) {
    return new RGB(
        MathUtil.clamp(x, min, max),
        MathUtil.clamp(y, min, max),
        MathUtil.clamp(z, min, max));
  }

  public final double luminance() {
    return ColorUtil.convertRGB2Luminance(this);
  }

  public final int toR8G8B8() {
    return
        (MathUtil.clamp((int) Math.floor(256.0 * x), 0, 255) << 16) |
        (MathUtil.clamp((int) Math.floor(256.0 * y), 0, 255) <<  8) |
        (MathUtil.clamp((int) Math.floor(256.0 * z), 0, 255) <<  0);
  }

  public final int toRGBE() {
    double v = (x > y && x > z) ? x : (y > z ? y : z);
    if (v < 1e-32) {
      return 0;
    }
    long bits = Double.doubleToRawLongBits(v);
    int e = (int) (((bits & 0x7ff0000000000000L)) >>> 52L) - 0x3fe;
    v = Double.longBitsToDouble((bits & 0x800fffffffffffffL) | 0x3fe0000000000000L) * 256.0 / v;

    return
        (MathUtil.clamp((int) Math.floor(x * v), 0, 255) << 24) |
        (MathUtil.clamp((int) Math.floor(y * v), 0, 255) << 16) |
        (MathUtil.clamp((int) Math.floor(z * v), 0, 255) <<  8) |
        MathUtil.clamp(e + 128, 0, 255);
  }

  public static RGB fromR8G8B8(int rgb) {
    double r = ((double) ((rgb & 0x00ff0000) >> 16)) / 255.0;
    double g = ((double) ((rgb & 0x0000ff00) >>  8)) / 255.0;
    double b = ((double) ((rgb & 0x000000ff) >>  0)) / 255.0;
    return new RGB(r, g, b);
  }

  public static RGB fromRGBE(int rgbe) {
    int e = (rgbe & 0x000000ff);
    if (e > 0) {
      int r = (rgbe & 0xff000000) >>> 24;
      int g = (rgbe & 0x00ff0000) >>> 16;
      int b = (rgbe & 0x0000ff00) >>>  8;
      long bits = ((long) (e - (128 + 8) + 0x3ff)) << 52;
      double f = Double.longBitsToDouble(bits);
      return new RGB(r * f, g * f, b * f);
    } else {
      return RGB.ZERO;
    }
  }

  public CIEXYZ toXYZ() {
    return ColorUtil.convertRGB2XYZ(this);
  }

  public static RGB fromXYZ(double X, double Y, double Z) {
    return ColorUtil.convertXYZ2RGB(X, Y, Z);
  }

  public static RGB fromXYZ(CIEXYZ xyz) {
    return ColorUtil.convertXYZ2RGB(xyz);
  }

  @Override
  public Color sample(WavelengthPacket lambda) {
    ColorModel cm = lambda.getColorModel();
    return cm.fromRGB(this).sample(lambda);
  }

}

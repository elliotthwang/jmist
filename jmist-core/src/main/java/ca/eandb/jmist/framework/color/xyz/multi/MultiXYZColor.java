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
package ca.eandb.jmist.framework.color.xyz.multi;

import ca.eandb.jmist.framework.color.CIEXYZ;
import ca.eandb.jmist.framework.color.Color;
import ca.eandb.jmist.framework.color.ColorUtil;
import ca.eandb.jmist.framework.color.RGB;
import ca.eandb.jmist.framework.color.WavelengthPacket;

/* package */ final class MultiXYZColor implements Color {

  /** Serialization version ID. */
  private static final long serialVersionUID = 6038043872164333783L;

  private final double[] values;

  private final MultiXYZWavelengthPacket lambda;

  public MultiXYZColor(double[] values, MultiXYZWavelengthPacket lambda) {
    this.values = values;
    this.lambda = lambda;
  }

  @Override
  public CIEXYZ toXYZ() {
    return new CIEXYZ(X(), Y(), Z());
  }

  public XYZColor toXYZColor() {
    MultiXYZColorModel owner = getColorModel();
    return new XYZColor(X(), Y(), Z(), owner);
  }

  public double X() {
    MultiXYZColorModel owner = getColorModel();
    int n = owner.getChannelsX();
    double x = 0.0;
    for (int i = 0, ofs = owner.getOffsetX(); i < n; i++) {
      x += values[ofs + i];
    }
    return x / (double) n;
  }

  public double Y() {
    MultiXYZColorModel owner = getColorModel();
    int n = owner.getChannelsY();
    double y = 0.0;
    for (int i = 0, ofs = owner.getOffsetY(); i < n; i++) {
      y += values[ofs + i];
    }
    return y / (double) n;
  }

  public double Z() {
    MultiXYZColorModel owner = lambda.getColorModel();
    int n = owner.getChannelsZ();
    double z = 0.0;
    for (int i = 0, ofs = owner.getOffsetZ(); i < n; i++) {
      z += values[ofs + i];
    }
    return z / (double) n;
  }

  @Override
  public RGB toRGB() {
    return ColorUtil.convertXYZ2RGB(X(), Y(), Z());
  }

  private Color create(double[] values, Color compat) {
    MultiXYZColor color = new MultiXYZColor(values, lambda);
    if (lambda == compat.getWavelengthPacket()) {
      return color;
    } else {
      return color.toXYZColor();
    }
  }

  @Override
  public Color abs() {
    double[] x = values.clone();
    for (int i = 0; i < x.length; i++) {
      x[i] = Math.abs(x[i]);
    }
    return new MultiXYZColor(x, lambda);
  }

  @Override
  public Color clamp(double max) {
    double[] x = values.clone();
    for (int i = 0; i < x.length; i++) {
      x[i] = Math.min(x[i], max);
    }
    return new MultiXYZColor(x, lambda);
  }

  @Override
  public Color clamp(double min, double max) {
    double[] x = values.clone();
    for (int i = 0; i < x.length; i++) {
      x[i] = Math.min(Math.max(x[i], min), max);
    }
    return new MultiXYZColor(x, lambda);
  }

  @Override
  public Color disperse(int channel) {
    double[] x = values.clone();
    for (int i = 0; i < x.length; i++) {
      if (i != channel) {
        x[i] = 0.0;
      }
    }
    return new MultiXYZColor(x, lambda);
  }

  @Override
  public Color divide(Color other) {
    if (other instanceof XYZColor) {
      return toXYZColor().divide((XYZColor) other);
    } else {
      return divide((MultiXYZColor) other);
    }
  }

  public Color divide(MultiXYZColor other) {
    double[] x = values.clone();
    for (int i = 0; i < x.length; i++) {
      x[i] /= other.values[i];
    }
    return create(x, other);
  }

  @Override
  public Color divide(double c) {
    double[] x = values.clone();
    for (int i = 0; i < x.length; i++) {
      x[i] /= c;
    }
    return new MultiXYZColor(x, lambda);
  }

  @Override
  public Color exp() {
    double[] x = values.clone();
    for (int i = 0; i < x.length; i++) {
      x[i] = Math.exp(x[i]);
    }
    return new MultiXYZColor(x, lambda);
  }

  @Override
  public MultiXYZColorModel getColorModel() {
    return lambda.getColorModel();
  }

  @Override
  public double getValue(int channel) {
    return values[channel];
  }

  @Override
  public WavelengthPacket getWavelengthPacket() {
    return lambda;
  }

  @Override
  public Color invert() {
    double[] x = values.clone();
    for (int i = 0; i < x.length; i++) {
      x[i] = 1.0 / x[i];
    }
    return new MultiXYZColor(x, lambda);
  }

  @Override
  public double luminance() {
    return Y();
  }

  @Override
  public Color minus(Color other) {
    if (other instanceof XYZColor) {
      return toXYZColor().minus((XYZColor) other);
    } else {
      return minus((MultiXYZColor) other);
    }
  }

  public Color minus(MultiXYZColor other) {
    double[] x = values.clone();
    for (int i = 0; i < x.length; i++) {
      x[i] -= other.values[i];
    }
    return create(x, other);
  }

  @Override
  public Color negative() {
    double[] x = values.clone();
    for (int i = 0; i < x.length; i++) {
      x[i] = -x[i];
    }
    return new MultiXYZColor(x, lambda);
  }

  @Override
  public Color plus(Color other) {
    if (other instanceof XYZColor) {
      return toXYZColor().plus((XYZColor) other);
    } else {
      return plus((MultiXYZColor) other);
    }
  }

  public Color plus(MultiXYZColor other) {
    double[] x = values.clone();
    for (int i = 0; i < x.length; i++) {
      x[i] += other.values[i];
    }
    return create(x, other);
  }

  @Override
  public Color pow(Color other) {
    if (other instanceof XYZColor) {
      return toXYZColor().pow((XYZColor) other);
    }
    return pow((MultiXYZColor) other);
  }

  public Color pow(MultiXYZColor other) {
    double[] x = values.clone();
    for (int i = 0; i < x.length; i++) {
      x[i] = Math.pow(x[i], other.values[i]);
    }
    return create(x, other);
  }

  @Override
  public Color pow(double e) {
    double[] x = values.clone();
    for (int i = 0; i < x.length; i++) {
      x[i] = Math.pow(x[i], e);
    }
    return new MultiXYZColor(x, lambda);
  }

  @Override
  public Color sqrt() {
    double[] x = values.clone();
    for (int i = 0; i < x.length; i++) {
      x[i] = Math.sqrt(x[i]);
    }
    return new MultiXYZColor(x, lambda);
  }

  @Override
  public Color times(Color other) {
    if (other instanceof XYZColor) {
      return toXYZColor().times((XYZColor) other);
    } else {
      return times((MultiXYZColor) other);
    }
  }

  public Color times(MultiXYZColor other) {
    double[] x = values.clone();
    for (int i = 0; i < x.length; i++) {
      x[i] *= other.values[i];
    }
    return create(x, other);
  }

  @Override
  public Color times(double c) {
    double[] x = values.clone();
    for (int i = 0; i < x.length; i++) {
      x[i] *= c;
    }
    return new MultiXYZColor(x, lambda);
  }

  @Override
  public double[] toArray() {
    return values.clone();
  }

}

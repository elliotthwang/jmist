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
package ca.eandb.jmist.framework.color.xyz;

import ca.eandb.jmist.framework.Function1;
import ca.eandb.jmist.framework.ProbabilityDensityFunction;
import ca.eandb.jmist.framework.Random;
import ca.eandb.jmist.framework.Raster;
import ca.eandb.jmist.framework.color.CIEXYZ;
import ca.eandb.jmist.framework.color.Color;
import ca.eandb.jmist.framework.color.ColorModel;
import ca.eandb.jmist.framework.color.ColorUtil;
import ca.eandb.jmist.framework.color.DoubleRaster;
import ca.eandb.jmist.framework.color.Spectrum;
import ca.eandb.jmist.framework.color.WavelengthPacket;
import ca.eandb.jmist.framework.pdf.PiecewiseLinearProbabilityDensityFunction;
import ca.eandb.jmist.math.MathUtil;

public final class XYZColorModel implements ColorModel {

  /** Serialization version ID. */
  private static final long serialVersionUID = 1316752091233635632L;

  private static final ProbabilityDensityFunction X_PDF = new PiecewiseLinearProbabilityDensityFunction(ColorUtil.XYZ_WAVELENGTHS, ColorUtil.X_BAR);
  private static final ProbabilityDensityFunction Y_PDF = new PiecewiseLinearProbabilityDensityFunction(ColorUtil.XYZ_WAVELENGTHS, ColorUtil.Y_BAR);
  private static final ProbabilityDensityFunction Z_PDF = new PiecewiseLinearProbabilityDensityFunction(ColorUtil.XYZ_WAVELENGTHS, ColorUtil.Z_BAR);

  private static final double X_CONST = ColorUtil.LUMENS_PER_WATT
      * MathUtil.trapz(ColorUtil.XYZ_WAVELENGTHS, ColorUtil.X_BAR);

  private static final double Y_CONST = ColorUtil.LUMENS_PER_WATT
      * MathUtil.trapz(ColorUtil.XYZ_WAVELENGTHS, ColorUtil.Y_BAR);

  private static final double Z_CONST = ColorUtil.LUMENS_PER_WATT
      * MathUtil.trapz(ColorUtil.XYZ_WAVELENGTHS, ColorUtil.Z_BAR);

  private static final XYZColorModel instance = new XYZColorModel();

  /** The channel names for this <code>ColorModel</code>. */
  private static final String[] CHANNEL_NAMES = { "X", "Y", "Z" };

  public static XYZColorModel getInstance() {
    return instance;
  }

  /**
   * This constructor is private because this class is a singleton.
   */
  private XYZColorModel() {
    /* nothing to do. */
  }

  @Override
  public Spectrum fromRGB(double r, double g, double b) {
    CIEXYZ xyz = ColorUtil.convertRGB2XYZ(r, g, b);
    return new XYZColor(xyz.X(), xyz.Y(), xyz.Z(), null);
  }

  @Override
  public Spectrum fromXYZ(double x, double y, double z) {
    return new XYZColor(x, y, z, null);
  }

  @Override
  public Spectrum getBlack() {
    return XYZColor.BLACK;
  }

  @Override
  public Color getBlack(WavelengthPacket lambda) {
    return getBlack((XYZWavelengthPacket) lambda);
  }

  public Color getBlack(XYZWavelengthPacket lambda) {
    return new XYZColor(0, 0, 0, lambda);
  }

  @Override
  public Spectrum getContinuous(Function1 spectrum) {
    return new XYZContinuousSpectrum(spectrum);
  }

  @Override
  public Spectrum getGray(double value) {
    return new XYZColor(value, value, value, null);
  }

  @Override
  public Color getGray(double value, WavelengthPacket lambda) {
    return getGray(value, (XYZWavelengthPacket) lambda);
  }

  public Color getGray(double value, XYZWavelengthPacket lambda) {
    return new XYZColor(value, value, value, lambda);
  }

  @Override
  public int getNumChannels() {
    return 3;
  }

  @Override
  public Color fromArray(double[] values, WavelengthPacket lambda) {
    if (values.length < 3) {
      throw new IllegalArgumentException("values.length < 3");
    }
    return new XYZColor(values[0], values[1], values[2], (XYZWavelengthPacket) lambda);
  }

  @Override
  public Spectrum getWhite() {
    return XYZColor.WHITE;
  }

  @Override
  public Color getWhite(WavelengthPacket lambda) {
    return getWhite((XYZWavelengthPacket) lambda);
  }

  public Color getWhite(XYZWavelengthPacket lambda) {
    return new XYZColor(1, 1, 1, lambda);
  }

  @Override
  public Color sample(Random random) {
    XYZWavelengthPacket lambda = new XYZWavelengthPacket(X_PDF.sample(random), Y_PDF.sample(random), Z_PDF.sample(random));
    return new XYZColor(X_CONST, Y_CONST, Z_CONST, lambda);
  }

  @Override
  public Raster createRaster(int width, int height) {
    return new DoubleRaster(width, height, 3) {
      private static final long serialVersionUID = 538645330465767339L;
      protected Color getPixel(double[] raster, int index) {
        return new XYZColor(raster[index], raster[index + 1], raster[index + 2], null);
      }
    };
  }

  @Override
  public String getChannelName(int channel) {
    if (channel < 0 || channel >= 3) {
      throw new IllegalArgumentException("Invalid channel");
    }
    return CHANNEL_NAMES[channel];
  }

}

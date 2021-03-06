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
package ca.eandb.jmist.framework.loader.radiance;

import ca.eandb.jmist.framework.color.CIEXYZ;
import ca.eandb.jmist.framework.color.RGB;

/**
 * Represents the RGBE pixel format for a <code>RadiancePicture</code>.
 * This class is a singleton.
 * @see #INSTANCE
 * @author Brad Kimmel
 */
final class RGBEPixelFormat implements PixelFormat {

  /** Serialization version ID. */
  private static final long serialVersionUID = 8026681085259479612L;

  /** The single <code>RGBEPixelFormat</code> instance. */
  public static final RGBEPixelFormat INSTANCE = new RGBEPixelFormat();

  /**
   * Creates a new <code>RGBEPixelFormat</code>.
   * This constructor is private because this class is a singleton.
   */
  private RGBEPixelFormat() {}

  @Override
  public RGB toRGB(int raw) {
    return RGB.fromRGBE(raw);
  }

  @Override
  public int toRaw(RGB rgb) {
    return rgb.toRGBE();
  }

  @Override
  public int toRaw(CIEXYZ xyz) {
    return xyz.toRGB().toRGBE();
  }

  @Override
  public CIEXYZ toXYZ(int raw) {
    return toRGB(raw).toXYZ();
  }

}

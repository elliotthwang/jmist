/**
 *
 */
package org.jmist.packages;

import org.jmist.toolkit.Pixel;
import org.jmist.toolkit.Point2;
import org.jmist.framework.IImageShader;
import org.jmist.framework.IPixelShader;

/**
 * Represents a pixel shader that rasterizes an image represented by
 * an image shader.  The inheriting pixel shader's sole responsibility
 * will be anti-aliasing.
 * @author bkimmel
 */
public abstract class ImageRasterizingPixelShader implements IPixelShader {

	/**
	 * Initializes the image shader to use for this pixel shader.
	 * @param shader The image shader to use for this pixel shader.
	 */
	protected ImageRasterizingPixelShader(IImageShader shader) {
		this.shader = shader;
	}

	/**
	 * Shades the specified pixel using this shader's image shader.
	 * @param p The point on the image plane to shade.
	 * @param pixel The pixel to write the shading to.
	 */
	protected void shadeAt(Point2 p, Pixel pixel) {
		this.shader.shadeAt(p, pixel);
	}

	/* (non-Javadoc)
	 * @see org.jmist.framework.IPixelFactory#createPixel()
	 */
	public Pixel createPixel() {
		return this.shader.createPixel();
	}

	/** The image shader to use for shading points. */
	private final IImageShader shader;

}